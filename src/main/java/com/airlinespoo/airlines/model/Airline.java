package com.airlinespoo.airlines.model;

import com.airlinespoo.core.model.Identifiable;
import com.airlinespoo.core.util.Validator;

/**
 * Aerolínea con identidad fija y nombre válido.
 * La generación del id y la unicidad del nombre corresponden al servicio.
 */
public final class Airline implements Identifiable<Integer> {

    private static final int MAX_NAME_LENGTH = 50;

    private final int id;
    private String name;

    /**
     * Crea una aerolínea con un identificador previamente asignado.
     *
     * @param id identificador positivo; el generador usará 100, 110, 120...
     * @param name nombre obligatorio, de máximo 50 caracteres una vez normalizado
     * @throws com.airlinespoo.core.exception.ValidationException si algún dato es inválido
     */
    public Airline(int id, String name) {
        Validator.requirePositive(id, "id de la aerolínea");
        this.id = id;
        setName(name);
    }

    /** @return identificador inmutable de la aerolínea */
    @Override
    public Integer getId() {
        return id;
    }

    /** @return nombre normalizado de la aerolínea */
    public String getName() {
        return name;
    }

    /**
     * Cambia el nombre; el servicio debe comprobar antes que no esté repetido.
     *
     * @param name nuevo nombre obligatorio, de máximo 50 caracteres
     * @throws com.airlinespoo.core.exception.ValidationException si el nombre es inválido
     */
    public void setName(String name) {
        this.name = Validator.requireText(name, "nombre de la aerolínea", MAX_NAME_LENGTH);
    }

    /** @return si ambos objetos representan la misma identidad */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Airline)) {
            return false;
        }
        Airline airline = (Airline) other;
        return id == airline.id;
    }

    /** @return código hash basado únicamente en el identificador */
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    /** @return representación con el identificador y el nombre */
    @Override
    public String toString() {
        return "Airline{id=" + id + ", name='" + name + "'}";
    }
}
