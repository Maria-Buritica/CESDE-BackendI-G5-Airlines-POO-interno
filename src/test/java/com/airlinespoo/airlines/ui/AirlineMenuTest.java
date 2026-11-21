package com.airlinespoo.airlines.ui;

import com.airlinespoo.airlines.model.Airline;
import com.airlinespoo.airlines.repository.AirlineRepository;
import com.airlinespoo.airlines.service.AirlineService;
import com.airlinespoo.core.ui.ConsoleInput;
import com.airlinespoo.core.ui.Menu;
import com.airlinespoo.core.util.IdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class AirlineMenuTest {
    private AirlineRepository repository;
    private IdGenerator ids;
    private AirlineService service;

    @BeforeEach
    void setUp() {
        repository = new AirlineRepository();
        ids = new IdGenerator(100, 10);
        service = new AirlineService(repository, ids);
    }

    private String runMenu(String script) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);
        ConsoleInput input = new ConsoleInput(
            new ByteArrayInputStream(script.getBytes(StandardCharsets.UTF_8)), out);
        Menu menu = new AirlineMenu(service, input, out);
        assertEquals("Aerolíneas", menu.title());
        menu.run();
        return buffer.toString(StandardCharsets.UTF_8);
    }

    @Test
    void completesRegistrationConsultationAndRename() {
        String output = runMenu("1\nAvianca\n2\n100\n3\n100\nCopa\n2\n100\n0\n");
        assertEquals(1, service.countAirlines());
        assertEquals("Copa", service.getAirline(100).getName());
        assertTrue(output.contains("Aerolínea registrada."));
        assertTrue(output.contains("Id: 100 | Nombre: Avianca"));
        assertTrue(output.contains("Aerolínea renombrada."));
        assertTrue(output.contains("Id: 100 | Nombre: Copa"));
    }

    @Test
    void recoversFromInvalidOptionsIdsAndBlankName() {
        String output = runMenu("texto\n9\n1\n\nAvianca\n2\ncien\n-1\n100\n0\n");
        assertEquals(1, service.countAirlines());
        assertTrue(output.contains("Escribe un número entero válido."));
        assertTrue(output.contains("El número debe estar entre 0 y 6."));
        assertTrue(output.contains("Este dato es obligatorio."));
        assertTrue(output.contains("Id: 100 | Nombre: Avianca"));
    }

    @Test
    void continuesAfterDuplicateMissingIdAndLongName() {
        String output = runMenu("1\nAvianca\n1\nAVIANCA\n2\n999\n1\n"
            + "x".repeat(51) + "\n1\nLATAM\n0\n");
        assertEquals(2, service.countAirlines());
        assertEquals("LATAM", service.getAirline(110).getName());
        assertEquals(3, output.lines()
            .filter(line -> line.contains("No se pudo completar la operación:")).count());
        assertTrue(output.contains("Ya existe una aerolínea"));
        assertTrue(output.contains("No existe un registro"));
    }

    @Test
    void failedRenameKeepsDataAndReturnsToMenu() {
        service.createAirline("Avianca");
        service.createAirline("LATAM");
        String output = runMenu("3\n110\navianca\n2\n110\n0\n");
        assertEquals("LATAM", service.getAirline(110).getName());
        assertTrue(output.contains("No se pudo completar la operación:"));
        assertTrue(output.contains("Id: 110 | Nombre: LATAM"));
    }

    @Test
    void listsSortedAndSearchesPartialNames() {
        service.createAirline("Wingo");
        service.createAirline("Avianca");
        service.createAirline("LATAM");
        String listing = runMenu("4\n0\n");
        assertTrue(listing.indexOf("Nombre: Avianca") < listing.indexOf("Nombre: LATAM"));
        assertTrue(listing.indexOf("Nombre: LATAM") < listing.indexOf("Nombre: Wingo"));
        assertTrue(listing.contains("Total de aerolíneas: 3"));
        String search = runMenu("5\nAV\n0\n");
        assertTrue(search.contains("Nombre: Avianca"));
        assertFalse(search.contains("Nombre: LATAM"));
        assertFalse(search.contains("Nombre: Wingo"));
    }

    @Test
    void reportsEmptyListsAndSearchResults() {
        String output = runMenu("4\n5\nzzz\n0\n");
        assertTrue(output.contains("No se encontraron aerolíneas."));
        assertTrue(output.contains("Total de aerolíneas: 0"));
    }

    @Test
    void cancellationPreservesAirlineAndConfirmationDeletesIt() {
        service.createAirline("Avianca");
        String cancelled = runMenu("6\n100\nno\n0\n");
        assertEquals(1, service.countAirlines());
        assertTrue(cancelled.contains("Eliminación cancelada."));
        String deleted = runMenu("6\n100\ns\n0\n");
        assertEquals(0, service.countAirlines());
        assertTrue(deleted.contains("Aerolínea eliminada."));
    }

    @Test
    void seedIsNotDuplicatedAndNextRegistrationUses130() {
        AirlineSeed.load(service, ids);
        AirlineSeed.load(service, ids);
        assertEquals(3, service.countAirlines());
        assertEquals("Avianca", service.getAirline(100).getName());
        assertEquals("LATAM", service.getAirline(110).getName());
        assertEquals("Wingo", service.getAirline(120).getName());
        String output = runMenu("1\nCopa\n0\n");
        assertEquals("Copa", service.getAirline(130).getName());
        assertTrue(output.contains("Id: 130 | Nombre: Copa"));
    }

    @Test
    void seedPreservesExistingDataAndSynchronizesGenerator() {
        repository.create(new Airline(200, "Existente"));
        AirlineSeed.load(service, ids);
        assertEquals(1, service.countAirlines());
        assertEquals("Existente", service.getAirline(200).getName());
        assertEquals(210, service.createAirline("Nueva").getId());
    }

    @Test
    void returningLeavesSharedInputAvailableToCaller() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);
        ConsoleInput input = new ConsoleInput(
            new ByteArrayInputStream("0\n42\n".getBytes(StandardCharsets.UTF_8)), out);
        new AirlineMenu(service, input, out).run();
        assertEquals(42, input.readInt("Siguiente menú", 0, 100));
        assertEquals(0, service.countAirlines());
    }
}
