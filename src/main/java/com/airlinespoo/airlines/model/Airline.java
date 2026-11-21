package com.airlinespoo.airlines.model;

import com.airlinespoo.core.exception.ValidationException;
import com.airlinespoo.core.model.Identifiable;
import com.airlinespoo.core.util.Validator;

/**
 * Representa una aerolínea con un identificador fijo y un nombre válido.
 * El servicio se encargará de generar los ids y evitar nombres repetidos.
 */
public final class Airline implements Identifiable<Integer> {

    private static final int MAX_NAME_LENGTH = 50;

    private final int id;
    private String name;

    /**
     * Construye una aerolínea con los datos recibidos.
     *
     * @param id identificador positivo asignado por el programa
     * @param name nombre obligatorio de máximo 50 caracteres
     * @throws ValidationException si el id o el nombre no son válidos
     */
    public Airline(int id, String name) {
        Validator.requirePositive(id, "id de la aerolínea");
        this.id = id;
        setName(name);
    }

    /**
     * @return identificador que no cambia durante la vida del objeto
     */
    @Override
    public Integer getId() {
        return id;
    }

    /**
     * @return nombre sin espacios sobrantes
     */
    public String getName() {
        return name;
    }

    /**
     * Actualiza el nombre después de validarlo.
     * Si la validación falla, se conserva el nombre anterior.
     *
     * @param name nuevo nombre; el servicio debe verificar su unicidad
     * @throws ValidationException si está vacío o supera 50 caracteres
     */
    public void setName(String name) {
        this.name = Validator.requireText(name, "nombre de la aerolínea", MAX_NAME_LENGTH);
    }

    /**
     * Compara la identidad, aunque los nombres sean diferentes.
     *
     * @param other objeto con el que se compara
     * @return true si es una aerolínea con el mismo id
     */
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

    /**
     * @return código hash de la identidad, estable al renombrar
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    /**
     * @return texto con el identificador y el nombre
     */
    @Override
    public String toString() {
        return "Airline{id=" + id + ", name='" + name + "'}";
    }
}
