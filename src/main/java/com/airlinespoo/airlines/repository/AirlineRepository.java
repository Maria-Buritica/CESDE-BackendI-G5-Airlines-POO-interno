package com.airlinespoo.airlines.repository;

import com.airlinespoo.airlines.model.Airline;
import com.airlinespoo.core.repository.InMemoryRepository;
import com.airlinespoo.core.util.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Guarda aerolíneas en memoria y permite consultarlas por nombre.
 * La decisión de rechazar nombres repetidos corresponde al servicio.
 */
public class AirlineRepository extends InMemoryRepository<Airline, Integer> {

    /** @return nombre de la entidad usado por el kit en sus mensajes */
    @Override
    protected String entityName() {
        return "aerolínea";
    }

    /**
     * Busca coincidencias parciales sin distinguir mayúsculas.
     *
     * @param text fragmento del nombre; se normalizan los espacios
     * @return lista nueva de coincidencias, en orden de inserción
     * @throws com.airlinespoo.core.exception.ValidationException si el texto está vacío
     */
    public List<Airline> findByName(String text) {
        String query = Validator.requireText(text, "búsqueda de aerolínea")
            .toLowerCase(Locale.ROOT);
        List<Airline> matches = new ArrayList<>();
        for (Airline airline : values()) {
            if (airline.getName().toLowerCase(Locale.ROOT).contains(query)) {
                matches.add(airline);
            }
        }
        return matches;
    }

    /**
     * Comprueba un nombre completo, sin distinguir mayúsculas.
     *
     * @param name nombre a consultar; se normalizan los espacios
     * @return true si alguna aerolínea tiene ese nombre completo
     * @throws com.airlinespoo.core.exception.ValidationException si el nombre está vacío
     */
    public boolean existsByName(String name) {
        String normalizedName = Validator.requireText(name, "nombre de la aerolínea");
        for (Airline airline : values()) {
            if (airline.getName().equalsIgnoreCase(normalizedName)) {
                return true;
            }
        }
        return false;
    }
}
