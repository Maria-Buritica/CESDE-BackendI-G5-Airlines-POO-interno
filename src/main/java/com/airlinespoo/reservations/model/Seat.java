package com.airlinespoo.reservations.model;

import com.airlinespoo.core.exception.ValidationException;
import com.airlinespoo.core.util.Validator;

import java.util.Objects;

public class Seat {
    private static final int MAX_ROW_LENGTH = 2;
    private static final int MAX_SEAT_NUMBER = 99;
    private final String rowNumber;
    private final int seatNumber;

    public Seat(String rowNumber, int seatNumber) {
        String cleanRow = Validator.requireText(rowNumber, "número de fila", MAX_ROW_LENGTH);
        String upperRow = cleanRow.toUpperCase();
        for (char c : cleanRow.toCharArray()) {
            if (!Character.isLetter(c)) {
                throw new ValidationException("La fila solo puede tener letras.");
            }
        }
        this.rowNumber = upperRow;
        Validator.requireInRange(seatNumber, 1, MAX_SEAT_NUMBER, "número de la silla");
        this.seatNumber = seatNumber;

    }

    public String getRowNumber() {
        return rowNumber;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Seat other = (Seat) o;
        return seatNumber == other.seatNumber && rowNumber.equals(other.rowNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rowNumber, seatNumber);
    }

    @Override
    public String toString() {
        return rowNumber + seatNumber;
    }
}


