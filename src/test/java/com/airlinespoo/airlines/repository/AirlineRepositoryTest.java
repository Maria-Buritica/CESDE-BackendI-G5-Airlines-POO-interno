package com.airlinespoo.airlines.repository;

import com.airlinespoo.airlines.model.Airline;
import com.airlinespoo.core.exception.DuplicateException;
import com.airlinespoo.core.exception.NotFoundException;
import com.airlinespoo.core.exception.ValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class AirlineRepositoryTest {

    @Test
    void findsPartialNamesIgnoringCaseAndNormalizingSpaces() {
        AirlineRepository repository = new AirlineRepository();
        Airline avianca = repository.create(new Airline(100, "Avianca"));
        Airline avior = repository.create(new Airline(110, "Avior Airlines"));
        repository.create(new Airline(120, "LATAM"));
        assertEquals(List.of(avianca, avior), repository.findByName("  AV  "));
        assertEquals(List.of(avior), repository.findByName("avior   AIR"));
        assertTrue(repository.findByName("inexistente").isEmpty());
    }

    @Test
    void existenceUsesFullNameRatherThanPartialMatch() {
        AirlineRepository repository = new AirlineRepository();
        repository.create(new Airline(100, "Aero Colombia"));
        assertTrue(repository.existsByName("  AERO   colombia "));
        assertFalse(repository.existsByName("Aero"));
        assertFalse(repository.existsByName("LATAM"));
    }

    @Test
    void emptyRepositoryReturnsNoMatches() {
        AirlineRepository repository = new AirlineRepository();
        assertTrue(repository.findByName("av").isEmpty());
        assertFalse(repository.existsByName("Avianca"));
    }

    @Test
    void rejectsMissingSearchText() {
        AirlineRepository repository = new AirlineRepository();
        for (String text : new String[]{null, "", "   "}) {
            assertThrows(ValidationException.class, () -> repository.findByName(text));
            assertThrows(ValidationException.class, () -> repository.existsByName(text));
        }
    }

    @Test
    void changingReturnedListsDoesNotRemoveStoredAirlines() {
        AirlineRepository repository = new AirlineRepository();
        repository.create(new Airline(100, "Avianca"));
        repository.findByName("av").clear();
        repository.findAll().clear();
        assertEquals(1, repository.count());
        assertTrue(repository.existsById(100));
        assertTrue(repository.existsByName("Avianca"));
    }

    @Test
    void nameQueriesReflectUpdatesAndDeletions() {
        AirlineRepository repository = new AirlineRepository();
        repository.create(new Airline(100, "Avianca"));
        repository.update(new Airline(100, "LATAM"));
        assertFalse(repository.existsByName("Avianca"));
        assertTrue(repository.findByName("av").isEmpty());
        assertTrue(repository.existsByName("latam"));
        assertTrue(repository.deleteById(100));
        assertFalse(repository.existsByName("LATAM"));
    }

    @Test
    void inheritedCreateAndUpdateKeepTheirDifferentContracts() {
        AirlineRepository repository = new AirlineRepository();
        repository.create(new Airline(100, "Avianca"));
        assertThrows(DuplicateException.class,
            () -> repository.create(new Airline(100, "LATAM")));
        assertThrows(NotFoundException.class,
            () -> repository.update(new Airline(110, "LATAM")));
        assertEquals("Avianca", repository.findById(100).orElseThrow().getName());
        assertEquals(1, repository.count());
    }

    @Test
    void partialSearchDoesNotDependOnComputerLocale() {
        Locale previous = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            AirlineRepository repository = new AirlineRepository();
            repository.create(new Airline(100, "AVIANCA"));
            assertEquals(1, repository.findByName("avi").size());
        } finally {
            Locale.setDefault(previous);
        }
    }
}
