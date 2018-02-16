package com.clementlu.ticketservice.Models;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import com.clementlu.ticketservice.Constant.SeatStatus;
import com.clementlu.ticketservice.Exceptions.InvalidVenueException;

/**
 * @author      Clement Lu <clementlu@live.com>
 * @version     1.0
 * @since       1.0
 */
public class Venue {
    private int totalSeats;
    private List<List<Seat>> seatMap;

    public Venue(int rows, int columns) throws InvalidVenueException {
        setTotalSeats(rows*columns);
        initializeSeatMap(rows, columns);
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    private void setTotalSeats(int totalSeats) throws InvalidVenueException {
        if (totalSeats > 0) {
            this.totalSeats = totalSeats;
        } else {
            throw new InvalidVenueException("Venue must have at least one seat!");
        } 
    }

    /**
    * Counts number of seats of a certain status.
    * <p>
    * This method filters each row to include only {@code status} seats. Each
    * row is then reduced to its size as a single integer. The total number 
    * of reserved seats in the venue is then the sum of all of the sizes.
    *
    * @param status Status to filter the search by.
    * @return Total number of {@code status} seats in the venue.
    */
    public int getNumSeats(SeatStatus status) {
        return seatMap.stream()
            .map(x -> (x.stream()
                .filter(s -> s.getSeatStatus() == status))
                .collect(Collectors.toList()))
            .mapToInt(List::size)
            .sum();
    }

    /**
    * Initializes SeatMap object.
    * <p>
    * Given the number of rows and columns in a venue, this method
    * will initialize the seat map with new, vacant {@code Seats}.
    * <p>
    * This method will throw a {@code InvalidVenueException} if given a row
    * or column number that is non-positive.
    *
    * @param  row Number of rows in venue.
    * @param  columns Number of columns in venue.
    */
    private void initializeSeatMap(int rows, int columns) throws InvalidVenueException {
        if (rows > 0 && columns > 0) {
            seatMap = new CopyOnWriteArrayList<List<Seat>>();
            for (int y = 0; y < columns; y++) {
                seatMap.add(new CopyOnWriteArrayList<Seat>());
                for (int x = 0; x < rows; x++) {
                    seatMap.get(y).add(new Seat(x,y));
                }
            }
        } else {
            throw new InvalidVenueException("Seat map must have a positive number of" +
            " rows and columns!");
        }
    }

    public List<List<Seat>> getSeatMap() {
        return seatMap;
    };

    /**
    * Changes the status of each seat in a list to {@code status}
    *
    * @param seatList List of seats to change.
    * @param status Changes seat to this status.
    * @return List of seats.
    */
    public List<Seat> changeSeatStatus(List<Seat> seatList, SeatStatus status) {
        for (Seat seat : seatList) {
            int x = (int) seat.getSeatNo().getX();
            int y = (int) seat.getSeatNo().getY();

            seatMap.get(y)
                .get(x)
                .setSeatStatus(status);
        }
        return seatList;
    }

}