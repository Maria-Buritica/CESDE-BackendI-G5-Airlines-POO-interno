package com.airlinespoo.airlines.service;

import com.airlinespoo.airlines.model.Airline;
import com.airlinespoo.airlines.repository.AirlineRepository;
import com.airlinespoo.core.exception.DuplicateException;
import com.airlinespoo.core.exception.NotFoundException;
import com.airlinespoo.core.exception.ValidationException;
import com.airlinespoo.core.util.IdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AirlineServiceTest {
    private AirlineRepository repository;
    private IdGenerator ids;
    private AirlineService service;

    @BeforeEach
    void setUp() {
        repository = new AirlineRepository();
        ids = new IdGenerator(100, 10);
        service = new AirlineService(repository, ids);
    }

    @Test
    void createsSequentialIdsAndNormalizesName() {
        Airline first = service.createAirline("  Viva   Air  ");
        Airline second = service.createAirline("Avianca");
        Airline third = service.createAirline("LATAM");
        assertEquals(List.of(100, 110, 120),
            List.of(first.getId(), second.getId(), third.getId()));
        assertEquals("Viva Air", first.getName());
        assertSame(first, service.getAirline(100));
        assertEquals(3, service.countAirlines());
    }

    @Test
    void rejectsDuplicateNameWithoutConsumingId() {
        service.createAirline("Viva Air");
        assertThrows(DuplicateException.class, () -> service.createAirline(" viva   AIR "));
        assertEquals(1, service.countAirlines());
        assertEquals(110, service.createAirline("Avianca").getId());
    }

    @Test
    void rejectsInvalidNamesWithoutSavingOrConsumingId() {
        assertThrows(ValidationException.class, () -> service.createAirline(null));
        assertThrows(ValidationException.class, () -> service.createAirline("   "));
        assertThrows(ValidationException.class, () -> service.createAirline("a".repeat(51)));
        assertEquals(0, service.countAirlines());
        assertEquals(100, ids.peekNext());
        assertEquals("a".repeat(50), service.createAirline("a".repeat(50)).getName());
    }

    @Test
    void missingIdsFailForReadRenameAndDelete() {
        assertThrows(NotFoundException.class, () -> service.getAirline(999));
        assertThrows(NotFoundException.class, () -> service.renameAirline(999, "Avianca"));
        assertThrows(NotFoundException.class, () -> service.deleteAirline(999));
    }

    @Test
    void rejectsNonPositiveIds() {
        assertThrows(ValidationException.class, () -> service.getAirline(0));
        assertThrows(ValidationException.class, () -> service.renameAirline(-1, "Avianca"));
        assertThrows(ValidationException.class, () -> service.deleteAirline(0));
    }

    @Test
    void renameAllowsOwnNameAndPreservesIdentity() {
        Airline airline = service.createAirline("Avianca");
        assertSame(airline, service.renameAirline(100, " AVIANCA "));
        assertEquals("AVIANCA", airline.getName());
        assertSame(airline, service.renameAirline(100, "Nueva Aerolinea"));
        assertEquals(100, airline.getId());
        assertEquals("Nueva Aerolinea", service.getAirline(100).getName());
        assertEquals(110, ids.peekNext());
    }

    @Test
    void duplicateRenameLeavesBothAirlinesUnchanged() {
        Airline first = service.createAirline("Avianca");
        Airline second = service.createAirline("LATAM");
        assertThrows(DuplicateException.class, () -> service.renameAirline(110, " avianca "));
        assertEquals("Avianca", first.getName());
        assertEquals("LATAM", second.getName());
        assertEquals(2, service.countAirlines());
    }

    @Test
    void invalidRenameLeavesOriginalName() {
        Airline airline = service.createAirline("Avianca");
        assertThrows(ValidationException.class, () -> service.renameAirline(100, null));
        assertThrows(ValidationException.class, () -> service.renameAirline(100, " "));
        assertThrows(ValidationException.class, () -> service.renameAirline(100, "x".repeat(51)));
        assertEquals("Avianca", airline.getName());
    }

    @Test
    void listsAlphabeticallyWithoutChangingRepositoryOrder() {
        service.createAirline("LATAM");
        service.createAirline("avianca");
        service.createAirline("Wingo");
        List<Airline> sorted = service.listAirlines();
        assertEquals(List.of("avianca", "LATAM", "Wingo"),
            sorted.stream().map(Airline::getName).toList());
        assertEquals(100, repository.findAll().get(0).getId());
        sorted.clear();
        assertEquals(3, service.countAirlines());
    }

    @Test
    void searchesPartialNamesIgnoringCaseAndReturnsIndependentList() {
        service.createAirline("Naviera Air");
        service.createAirline("LATAM");
        service.createAirline("Avianca");
        List<Airline> matches = service.searchAirlinesByName(" AV ");
        assertEquals(List.of("Avianca", "Naviera Air"),
            matches.stream().map(Airline::getName).toList());
        matches.clear();
        assertEquals(3, service.countAirlines());
        assertTrue(service.searchAirlinesByName("zzz").isEmpty());
    }

    @Test
    void rejectsEmptySearch() {
        assertThrows(ValidationException.class, () -> service.searchAirlinesByName(null));
        assertThrows(ValidationException.class, () -> service.searchAirlinesByName("  "));
    }

    @Test
    void deletesOnlyRequestedAirlineAndDoesNotReuseId() {
        service.createAirline("Avianca");
        Airline remaining = service.createAirline("LATAM");
        service.deleteAirline(100);
        assertThrows(NotFoundException.class, () -> service.getAirline(100));
        assertSame(remaining, service.getAirline(110));
        assertEquals(1, service.countAirlines());
        assertEquals(120, service.createAirline("Avianca").getId());
    }

    @Test
    void synchronizedGeneratorContinuesAfterSeedIds() {
        repository.create(new Airline(100, "Avianca"));
        repository.create(new Airline(110, "LATAM"));
        repository.create(new Airline(120, "Wingo"));
        ids.syncWith(120);
        assertEquals(130, service.createAirline("Copa").getId());
        assertEquals(4, service.countAirlines());
    }

    @Test
    void startsEmptyAndRejectsMissingDependencies() {
        assertTrue(service.listAirlines().isEmpty());
        assertEquals(0, service.countAirlines());
        assertThrows(ValidationException.class, () -> new AirlineService(null, ids));
        assertThrows(ValidationException.class, () -> new AirlineService(repository, null));
    }
}
