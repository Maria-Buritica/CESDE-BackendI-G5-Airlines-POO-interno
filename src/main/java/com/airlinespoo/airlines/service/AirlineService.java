package com.airlinespoo.airlines.service;

import com.airlinespoo.airlines.model.Airline;
import com.airlinespoo.core.exception.DuplicateException;
import com.airlinespoo.core.exception.NotFoundException;
import com.airlinespoo.core.repository.Repository;
import com.airlinespoo.core.util.IdGenerator;
import com.airlinespoo.core.util.Validator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Reglas de negocio de aerolíneas. Recibe sus dependencias desde Main.
 * El generador debe configurarse con inicio 100 y paso 10.
 */
public class AirlineService {
    private final Repository<Airline, Integer> repository;
    private final IdGenerator idGenerator;

    public AirlineService(Repository<Airline, Integer> repository, IdGenerator idGenerator) {
        this.repository = Validator.requireNotNull(repository, "repositorio de aerolíneas");
        this.idGenerator = Validator.requireNotNull(idGenerator, "generador de ids");
    }

    /** Valida antes de consumir un id o guardar la nueva aerolínea. */
    public Airline createAirline(String name) {
        String normalizedName = Validator.requireText(name, "nombre de la aerolínea", 50);
        requireUniqueName(normalizedName, null);
        return repository.create(new Airline(idGenerator.next(), normalizedName));
    }

    public Airline getAirline(int id) {
        Validator.requirePositive(id, "id de la aerolínea");
        return repository.findById(id)
            .orElseThrow(() -> new NotFoundException("aerolínea", id));
    }

    /** Devuelve una lista independiente, ordenada por nombre sin distinguir mayúsculas. */
    public List<Airline> listAirlines() {
        List<Airline> airlines = new ArrayList<>(repository.findAll());
        airlines.sort(Comparator.comparing(Airline::getName, String.CASE_INSENSITIVE_ORDER));
        return airlines;
    }

    /** Conserva el id y la referencia que pueden compartir otros módulos. */
    public Airline renameAirline(int id, String name) {
        Airline airline = getAirline(id);
        String normalizedName = Validator.requireText(name, "nombre de la aerolínea", 50);
        requireUniqueName(normalizedName, id);
        airline.setName(normalizedName);
        return repository.update(airline);
    }

    /**
     * Elimina una aerolínea existente.
     * La restricción de vuelos asociados se incorporará al integrar el módulo de vuelos.
     */
    public void deleteAirline(int id) {
        getAirline(id);
        repository.deleteById(id);
    }

    /** Busca coincidencias parciales; el texto no puede estar vacío. */
    public List<Airline> searchAirlinesByName(String text) {
        String query = Validator.requireText(text, "búsqueda de aerolínea")
            .toLowerCase(Locale.ROOT);
        List<Airline> matches = new ArrayList<>();
        for (Airline airline : listAirlines()) {
            if (airline.getName().toLowerCase(Locale.ROOT).contains(query)) {
                matches.add(airline);
            }
        }
        return matches;
    }

    public long countAirlines() {
        return repository.count();
    }

    private void requireUniqueName(String name, Integer excludedId) {
        for (Airline airline : repository.findAll()) {
            if (!airline.getId().equals(excludedId)
                && airline.getName().equalsIgnoreCase(name)) {
                throw new DuplicateException("Ya existe una aerolínea con el nombre '" + name + "'.");
            }
        }
    }
}
