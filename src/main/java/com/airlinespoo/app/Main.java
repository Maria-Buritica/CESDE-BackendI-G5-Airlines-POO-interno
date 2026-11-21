package com.airlinespoo.app;

import com.airlinespoo.airlines.repository.AirlineRepository;
import com.airlinespoo.airlines.service.AirlineService;
import com.airlinespoo.airlines.ui.AirlineMenu;
import com.airlinespoo.airlines.ui.AirlineSeed;
import com.airlinespoo.core.ui.ConsoleInput;
import com.airlinespoo.core.ui.Menu;
import com.airlinespoo.core.util.IdGenerator;

/**
 * Arranque provisional del módulo de aerolíneas.
 * La integración final agregará MainMenu y los demás módulos.
 */
public final class Main {
    private Main() {
    }

    /** Ensambla dependencias; las reglas de negocio permanecen en el servicio. */
    public static void main(String[] args) {
        ConsoleInput input = new ConsoleInput();
        IdGenerator airlineIds = new IdGenerator(100, 10);
        AirlineRepository repository = new AirlineRepository();
        AirlineService service = new AirlineService(repository, airlineIds);
        AirlineSeed.load(service, airlineIds);
        Menu menu = new AirlineMenu(service, input);

        System.out.println("=== Airlines-poo ===");
        System.out.println("Módulo de aerolíneas. Datos en memoria: se reinician al cerrar.");
        menu.run();
        System.out.println("Programa finalizado.");
    }
}
