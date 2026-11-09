package com.airlinespoo.reservations.model;

import com.airlinespoo.core.exception.ValidationException;
import com.airlinespoo.core.util.Validator;

import java.util.Objects;

public class ReservationDetail {
    private static final int MAX_PASSENGER_FULL_NAME_LENGTH = 50;
    private static final int MAX_PASSENGER_DOCUMENT_LENGTH = 20;
    private final int id;
    private String passengerFullName;
    private String passengerDocument;
    private Seat seat;

    public ReservationDetail(int id, String passengerFullName, String passengerDocument, Seat seat) {
        Validator.requirePositive(id, "número del detalle");
        this.id = id;
        this.passengerFullName = Validator.requireText(passengerFullName, "nombre del pasajero", MAX_PASSENGER_FULL_NAME_LENGTH);

        this.passengerDocument = validateDocument(passengerDocument);
        this.seat = Validator.requireNotNull(seat, "silla del pasajero");
    }

    public void setPassengerFullName(String passengerFullName) {
        this.passengerFullName = Validator.requireText(passengerFullName, "nombre del pasajero", MAX_PASSENGER_FULL_NAME_LENGTH);
    }

    public void setSeat(Seat seat) {
        this.seat = Validator.requireNotNull(seat, "silla del pasajero");
    }

    private static String validateDocument(String passengerDocument) {
        String cleanDocument = Validator.requireText(passengerDocument, "documento del pasajero", MAX_PASSENGER_DOCUMENT_LENGTH);
        String upperDocument = cleanDocument.toUpperCase();
        for (char c : upperDocument.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                throw new ValidationException("El documento solo puede tener letras y números.");
            }
        }
        return upperDocument;
    }

    public void setPassengerDocument(String passengerDocument) {
        this.passengerDocument = validateDocument(passengerDocument);
    }

    public int getId() {
        return id;
    }

    public String getPassengerFullName() {
        return passengerFullName;
    }

    public String getPassengerDocument() {
        return passengerDocument;
    }

    public Seat getSeat() {
        return seat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ReservationDetail other = (ReservationDetail) o;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id + ": " + passengerFullName + ", " + seat;
    }
}
