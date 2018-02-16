package com.clementlu.ticketservice;

import static org.junit.Assert.*;

import com.clementlu.ticketservice.Exceptions.InvalidVenueException;
import com.clementlu.ticketservice.Models.SeatHold;
import com.clementlu.ticketservice.Models.Venue;
import org.junit.Test;
import org.junit.Before;

/**
 * Unit tests for PerformanceVenueTicketService class.
 */
public class PerformanceVenueTicketServiceTest {

    private PerformanceVenueTicketService testService;
    private Venue venue;
    private int x;
    private int y;
    private String customerEmail;

    @Before
    public void setUp() throws InvalidVenueException {
        x = 100;
        y = 100;
        venue = new Venue(x, y);
        testService = new PerformanceVenueTicketService(venue);
        customerEmail = "test@gmail.com";
    }

    @Test
    public void testNumSeatAllVacant(){
        int avail = testService.numSeatsAvailable();
        assertEquals(x*y, avail);
    }

    @Test
    public void testNumSeatWithHolds(){
        int hold = 7480;
        testService.findAndHoldSeats(hold, customerEmail);
        int avail = testService.numSeatsAvailable();
        assertEquals(x*y-hold, avail);
    }

    @Test
    public void testNumSeatWithHoldsAndReservations(){
        int hold = 2352;
        int hold2 = 6434;
        SeatHold seat = testService.findAndHoldSeats(hold, customerEmail);
        testService.reserveSeats(seat.getId(), customerEmail);
        testService.findAndHoldSeats(hold2, customerEmail);
        int avail = testService.numSeatsAvailable();
        assertEquals(x*y-hold-hold2, avail);
    }

    @Test
    public void testHoldOverCapacity(){
        int hold = 10001;
        SeatHold seatHold = testService.findAndHoldSeats(hold, customerEmail);
        assertNull(seatHold);
    }

    @Test
    public void testInvalidHold(){
        int hold = -1;
        SeatHold seatHold = testService.findAndHoldSeats(hold, customerEmail);
        assertNull(seatHold);
    }

    @Test
    public void testValidHold(){
        int hold = 10000;
        SeatHold seatHold = testService.findAndHoldSeats(hold, customerEmail);
        assertNotNull(seatHold);
        assertEquals(hold, seatHold.getSeats().size());
    }

    @Test
    public void testMissingSeatHoldIdReservation(){
        int hold = 2352;
        testService.findAndHoldSeats(hold, customerEmail);
        String confId = testService.reserveSeats(-1, customerEmail);
        assertNull(confId);
    }
}