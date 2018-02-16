package com.clementlu.ticketservice;

import com.clementlu.ticketservice.Constant.SeatStatus;
import com.clementlu.ticketservice.Models.Seat;
import com.clementlu.ticketservice.Models.SeatHold;
import com.clementlu.ticketservice.Models.Venue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author      Clement Lu <clementlu@live.com>
 * @version     1.0
 * @since       1.0
 */
public class PerformanceVenueTicketService implements TicketService {

    private Venue venue;
    private Map<Integer, SeatHold> seatHolds;

    private CountDownLatch done; 
    final int holdExpiry = 5;

    public PerformanceVenueTicketService(Venue venue) {
        this.venue = venue; 
        seatHolds = new ConcurrentHashMap<Integer, SeatHold>();
        done = new CountDownLatch(1);
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
    * contiguous {@code numSeats} seats in the venue. The venue 
    * is filled with a "first fit" algorithm, starting in at (0, 0) 
    * and moving left to right and then starting over in the first 
    * element of the next row if there is any overflow. For example, 
    * in a 2x2 venue, a request for a 3 seat hold would return 
    * the following seats: [(0,0), (1,0), (0,1)].
    * <p>
    * A successful hold will add the {@code SeatHold} with a 
    * list of the held seats to {@code seatHolds}. The timer for
    * hold expiry is also started at this time on a seperate thread. 
    * If hold was unsuccessful, a null value will be returned.
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
                    final int seatHoldId = seatHold.getId();
                    
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if(!done.await(holdExpiry, TimeUnit.SECONDS)) {
                                    System.out.println("Seat hold expired.");
                                    expiryHandler(seatHoldId);
                                }
                            } catch (InterruptedException iE) {
                                System.out.println("Hold expiry thread interrupted!");
                            }
                        }
                    }).start();
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
            seatHolds.remove(seatHoldId);
            done.countDown();
        }
        return confirmationId;
    }

    /**
    * Handles the expiration of a SeatHold.
    * <p>
    * Given {@code seatHoldId}, this method will attempt to find a
    * {@code SeatHold} with given id and change the status of the
    * list of seats from HOLD to VACANT. The {@code SeatHold} will
    * also be removed from the active list of seat holds.
    *
    * @param seatHoldId Id of the SeatHold containing the list of seats.
    */
    public void expiryHandler(int seatHoldId) {
        if (seatHolds.containsKey(seatHoldId)) {
            Arrays.asList(seatHolds).toString();
            List<Seat> seats = seatHolds.get(seatHoldId).getSeats();
            seatHolds.remove(seatHoldId);
            venue.changeSeatStatus(seats, SeatStatus.VACANT);
            System.out.println("Seat hold " + seatHoldId + " was removed.");
        }
    }
}