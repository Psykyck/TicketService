package com.clementlu.ticketservice;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import com.clementlu.ticketservice.Constant.SeatStatus;
import com.clementlu.ticketservice.Exceptions.InvalidVenueException;
import com.clementlu.ticketservice.Models.Seat;
import com.clementlu.ticketservice.Models.Venue;
import org.junit.Test;
import org.junit.Before;

/**
 * Unit tests for Venue class.
 */
public class VenueTest {
    private Venue testVenue;
    int x;
    int y;

    @Before
    public void setUp() throws InvalidVenueException {
        x = 100;
        y = 100;
        testVenue = new Venue(x, y);
    }

    @Test(expected = InvalidVenueException.class)
    public void testInvalidVenue() throws InvalidVenueException {
        testVenue = new Venue(0, -23);
    }

    @Test
    public void testGetVacantSeats() {
        assertEquals(x*y, testVenue.getNumSeats(SeatStatus.VACANT));
    }

    @Test
    public void testGetHoldSeats() {
        SeatStatus status = SeatStatus.HOLD;
        List<Seat> seats = new ArrayList<Seat>();
        seats.add(new Seat(0,0));
        testVenue.changeSeatStatus(seats, status);
        assertEquals(seats.size(), testVenue.getNumSeats(status));
    }

    @Test
    public void testGetReservedSeats() {
        SeatStatus status = SeatStatus.RESERVED;
        List<Seat> seats = new ArrayList<Seat>();
        seats.add(new Seat(0,0));
        testVenue.changeSeatStatus(seats, status);
        assertEquals(seats.size(), testVenue.getNumSeats(status));
    }

    @Test
    public void testChangeSeatStatusHold() {
        int w = 0;
        int z = 0;
        SeatStatus status = SeatStatus.HOLD;
        List<Seat> seats = new ArrayList<Seat>();
        seats.add(new Seat(w,z));
        testVenue.changeSeatStatus(seats, status);
        assertEquals(status, testVenue.getSeatMap().get(z).get(w).getSeatStatus());
    }

    @Test
    public void testChangeSeatStatusReserved() {
        int w = 0;
        int z = 0;
        SeatStatus status = SeatStatus.RESERVED;
        List<Seat> seats = new ArrayList<Seat>();
        seats.add(new Seat(w,z));
        testVenue.changeSeatStatus(seats, status);
        assertEquals(status, testVenue.getSeatMap().get(z).get(w).getSeatStatus());
    }
}