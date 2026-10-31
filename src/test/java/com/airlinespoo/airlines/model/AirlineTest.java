package com.airlinespoo.airlines.model;

import com.airlinespoo.core.exception.ValidationException;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AirlineTest {

    @Test
    void normalizesNameAndPreservesAssignedId() {
        Airline airline = new Airline(100, "  Aero   Colombia  ");
        assertEquals(100, airline.getId());
        assertEquals("Aero Colombia", airline.getName());
    }

    @Test
    void rejectsMissingNames() {
        for (String name : new String[]{null, "", "   "}) {
            assertThrows(ValidationException.class, () -> new Airline(100, name));
        }
    }

    @Test
    void acceptsFiftyCharactersAndRejectsFiftyOne() {
        assertEquals("a".repeat(50), new Airline(100, "a".repeat(50)).getName());
        assertThrows(ValidationException.class, () -> new Airline(100, "a".repeat(51)));
    }

    @Test
    void rejectsNonPositiveIds() {
        assertThrows(ValidationException.class, () -> new Airline(0, "Avianca"));
        assertThrows(ValidationException.class, () -> new Airline(-10, "Avianca"));
    }

    @Test
    void invalidRenamePreservesPreviousState() {
        Airline airline = new Airline(100, "Avianca");
        for (String name : new String[]{null, "", "   ", "a".repeat(51)}) {
            assertThrows(ValidationException.class, () -> airline.setName(name));
            assertEquals("Avianca", airline.getName());
        }
        airline.setName("  Nuevo   Nombre ");
        assertEquals("Nuevo Nombre", airline.getName());
        assertEquals(100, airline.getId());
    }

    @Test
    void identityRemainsStableAfterRenameInHashSet() {
        Airline airline = new Airline(100, "Avianca");
        Set<Airline> airlines = new HashSet<>();
        airlines.add(airline);
        airline.setName("Otro nombre");
        Airline sameIdentity = new Airline(100, "Nombre anterior");
        assertEquals(airline, sameIdentity);
        assertEquals(airline.hashCode(), sameIdentity.hashCode());
        assertTrue(airlines.contains(sameIdentity));
        assertNotEquals(airline, new Airline(110, "Otro nombre"));
        assertNotEquals(null, airline);
        assertNotEquals("Otro nombre", airline);
    }
}
