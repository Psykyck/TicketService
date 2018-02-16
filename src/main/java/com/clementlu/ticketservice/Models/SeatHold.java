package com.clementlu.ticketservice.Models;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author      Clement Lu <clementlu@live.com>
 * @version     1.0
 * @since       1.0
 */
public class SeatHold {
    private int id;
    private String confirmationId;
    private List<Seat> seats;
    private String customerEmail;

    public SeatHold(List<Seat> seats, String customerEmail) {
        id = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);
        this.seats = seats;
        this.customerEmail = customerEmail;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public int getId() {
        return id;
    }

    public void setConfirmationId(String confirmationId) {
        this.confirmationId = confirmationId;
    }

    public String getConfirmationId() {
        return confirmationId;
    }

    @Override
    public String toString() {
        return "Seats: " + 
            seats.toString() +
            "Reservation Holder: " +
            getCustomerEmail();
    }
}