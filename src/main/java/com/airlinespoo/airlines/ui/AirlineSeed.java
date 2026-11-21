package com.airlinespoo.airlines.ui;

import com.airlinespoo.airlines.model.Airline;
import com.airlinespoo.airlines.service.AirlineService;
import com.airlinespoo.core.util.IdGenerator;
import com.airlinespoo.core.util.Validator;

/** Datos de demostración para el almacenamiento en memoria. */
public final class AirlineSeed {
    private AirlineSeed() {
    }

    /**
     * Carga ejemplos solo si el módulo está vacío y sincroniza el generador compartido.
     * Debe recibir el mismo generador que usa el servicio.
     */
    public static void load(AirlineService service, IdGenerator ids) {
        Validator.requireNotNull(service, "servicio de aerolíneas");
        Validator.requireNotNull(ids, "generador de ids");
        if (service.countAirlines() == 0) {
            service.createAirline("Avianca");
            service.createAirline("LATAM");
            service.createAirline("Wingo");
        }
        for (Airline airline : service.listAirlines()) {
            ids.syncWith(airline.getId());
        }
    }
}
