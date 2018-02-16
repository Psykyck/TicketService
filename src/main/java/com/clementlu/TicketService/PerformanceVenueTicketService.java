package com.clementlu.ticketservice;

import com.clementlu.ticketservice.Constant.SeatStatus;
import com.clementlu.ticketservice.Models.Seat;
import com.clementlu.ticketservice.Models.SeatHold;
import com.clementlu.ticketservice.Models.Venue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author      Clement Lu <clementlu@live.com>
 * @version     1.0
 * @since       1.0
 */
public class PerformanceVenueTicketService implements TicketService {

    private Venue venue;
    private Map<Integer, SeatHold> seatHolds;

    public PerformanceVenueTicketService(Venue venue) {
        this.venue = venue;
        seatHolds = new HashMap<Integer, SeatHold>();
    }
    
    /**
    * Returns number of seat available in venue.
    * <p>
    * An available seat is a seat that is VACANT and not
    * HOLD or RESERVED.
    *
    * @return Number of VACANT seats.
    */
    public int numSeatsAvailable() {
        return venue.getTotalSeats() - 
            (venue.getNumSeats(SeatStatus.HOLD) + 
            venue.getNumSeats(SeatStatus.RESERVED));
    }

    /**
    * Finds best available seats in the venue and holds them.
    * <p>
    * Given {@code numSeats}, this method will attempt to hold 
    * contiguous {@code numSeats} seats in the venue.
    * <p>
    * A successful hold will return a {@code SeatHold} with a 
    * list of the held seats. If hold was unsuccessful, a null
    * value will be returned.
    *
    * @param numSeats Number of seats to hold.
    * @param customerEmail Customer's email address.
    * @return Number of VACANT seats.
    */
    public SeatHold findAndHoldSeats(int numSeats, String customerEmail) {
        SeatHold seatHold = null;
        if (numSeats > 0 && numSeats <= venue.getTotalSeats()) {
            List<Seat> seatList = new ArrayList<>();
            if (numSeats <= numSeatsAvailable()) {
                int x = 0;
                for (List<Seat> row : venue.getSeatMap()) {
                    Iterator<Seat> rowIterator = row.iterator();
                    while (rowIterator.hasNext() && x != numSeats) {
                        Seat seat = rowIterator.next();
                        if (seat.getSeatStatus() == SeatStatus.VACANT) {
                            seatList.add(seat);
                            x++;
                        } 
                    }
                    if ((seatList.size() == numSeats)) {
                        break;
                    }
                }
                if (!seatList.isEmpty() && x == numSeats){
                    venue.changeSeatStatus(seatList, SeatStatus.HOLD);
                    seatHold = new SeatHold(seatList, customerEmail);
                    seatHolds.put(seatHold.getId(), seatHold);
                }
            }        
        }
        return seatHold;
    }

    /**
    * Reserves a list of seats.
    * <p>
    * Given {@code seatHoldId}, this method will attempt to find a
    * {@code SeatHold} with given id and change the status of the
    * list of seats from HOLD to RESERVED.
    * <p>
    * A successful reservation will return {@code confirmationId},
    * whereas if the {@code seatHoldId} does not exist, then a null
    * value will be returned.
    *
    * @param seatHoldId Id of the SeatHold containing the list of seats.
    * @param customerEmail Customer's email address.
    * @return Confirmation id.
    */
    public String reserveSeats(int seatHoldId, String customerEmail) {
        String confirmationId = null;
        if (!seatHolds.isEmpty() && seatHolds.containsKey(seatHoldId)) {
            SeatHold seatHold = seatHolds.get(seatHoldId);
            confirmationId = UUID.randomUUID().toString();
            venue.changeSeatStatus(seatHold.getSeats(), SeatStatus.RESERVED);
            seatHold.setConfirmationId(confirmationId);
        }
        return confirmationId;
    }

    private void expiryHandler(SeatHold seatHold, int numSeconds) {

    }
}