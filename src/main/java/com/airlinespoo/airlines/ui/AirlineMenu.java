package com.airlinespoo.airlines.ui;

import com.airlinespoo.airlines.model.Airline;
import com.airlinespoo.airlines.service.AirlineService;
import com.airlinespoo.core.exception.AirlinesException;
import com.airlinespoo.core.ui.ConsoleInput;
import com.airlinespoo.core.ui.Menu;
import com.airlinespoo.core.util.Validator;

import java.io.PrintStream;
import java.util.List;

/** Interfaz de consola: lee datos y presenta resultados; el servicio aplica las reglas. */
public class AirlineMenu implements Menu {
    private final AirlineService service;
    private final ConsoleInput input;
    private final PrintStream out;

    public AirlineMenu(AirlineService service, ConsoleInput input) {
        this(service, input, System.out);
    }

    /** Permite capturar la salida en pruebas usando el mismo destino que ConsoleInput. */
    public AirlineMenu(AirlineService service, ConsoleInput input, PrintStream out) {
        this.service = Validator.requireNotNull(service, "servicio de aerolíneas");
        this.input = Validator.requireNotNull(input, "lector de consola");
        this.out = Validator.requireNotNull(out, "salida de consola");
    }

    @Override
    public String title() {
        return "Aerolíneas";
    }

    @Override
    public void run() {
        while (true) {
            out.println("\n=== " + title() + " ===");
            out.println("1. Registrar");
            out.println("2. Consultar por id");
            out.println("3. Renombrar");
            out.println("4. Listar");
            out.println("5. Buscar por nombre");
            out.println("6. Eliminar");
            out.println("0. Volver");
            int option = input.readInt("Elige una opción", 0, 6);
            if (option == 0) {
                return;
            }
            try {
                switch (option) {
                    case 1 -> register();
                    case 2 -> showAirline(service.getAirline(readId()));
                    case 3 -> rename();
                    case 4 -> {
                        showList(service.listAirlines());
                        out.println("Total de aerolíneas: " + service.countAirlines());
                    }
                    case 5 -> showList(service.searchAirlinesByName(
                        input.readRequiredText("Nombre o parte del nombre")));
                    case 6 -> delete();
                    default -> throw new IllegalStateException("Opción fuera del rango validado");
                }
            } catch (AirlinesException e) {
                out.println("No se pudo completar la operación: " + e.getMessage());
            }
        }
    }

    private void register() {
        Airline airline = service.createAirline(input.readRequiredText("Nombre"));
        out.println("Aerolínea registrada.");
        showAirline(airline);
    }

    private void rename() {
        int id = readId();
        showAirline(service.getAirline(id));
        Airline airline = service.renameAirline(id, input.readRequiredText("Nuevo nombre"));
        out.println("Aerolínea renombrada.");
        showAirline(airline);
    }

    private void delete() {
        int id = readId();
        Airline airline = service.getAirline(id);
        if (input.confirm("¿Eliminar la aerolínea " + airline.getName() + " (id " + id + ")?")) {
            service.deleteAirline(id);
            out.println("Aerolínea eliminada.");
        } else {
            out.println("Eliminación cancelada.");
        }
    }

    private int readId() {
        return input.readInt("Id de la aerolínea", 1, Integer.MAX_VALUE);
    }

    private void showList(List<Airline> airlines) {
        if (airlines.isEmpty()) {
            out.println("No se encontraron aerolíneas.");
            return;
        }
        for (Airline airline : airlines) {
            showAirline(airline);
        }
    }

    private void showAirline(Airline airline) {
        out.printf("Id: %d | Nombre: %s%n", airline.getId(), airline.getName());
    }
}
