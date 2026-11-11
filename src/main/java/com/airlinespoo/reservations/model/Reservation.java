package com.airlinespoo.reservations.model;

import com.airlinespoo.core.exception.ValidationException;
import com.airlinespoo.core.util.Validator;

import java.util.ArrayList;
import java.util.List;

public class Reservation {
    private static final int MAX_PASSENGER = 10;
    private final int id;
    private final List<ReservationDetail> details = new ArrayList<>();

    public Reservation(int id, ReservationDetail firstDetail){
        Validator.requirePositive(id, "número de la reserva.");
        this.id = id;
        addDetail(firstDetail);

    }

    public List<ReservationDetail> getDetails() {
        return List.copyOf(details);
    }

    public void addDetail(ReservationDetail detail) {
        if (details.size() >= MAX_PASSENGER) {
            throw new ValidationException("La reserva no puede tener más de " + MAX_PASSENGER + " pasajeros.");
        }
        for (ReservationDetail existing : details) {
            if (existing.getPassengerDocument().equals(detail.getPassengerDocument())) {
                throw new ValidationException("Ya exíste un pasajero con ese documento.");
            }
        }
        for (ReservationDetail existing : details) {
            if (existing.getSeat().equals(detail.getSeat())) {
                throw new ValidationException("La silla ya está ocupada.");
            }
        }
        details.add(detail);
    }

    public void removeDetail(ReservationDetail detail) {
        if (details.size() <= 1) {
            throw new ValidationException("La reserva debe tener por lo menos un pasajero.");
        }
        details.remove(detail);
    }
}
