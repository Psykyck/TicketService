package com.clementlu.ticketservice;

import java.util.Scanner;

import com.clementlu.ticketservice.Constant.SeatStatus;
import com.clementlu.ticketservice.Exceptions.InvalidVenueException;
import com.clementlu.ticketservice.Models.SeatHold;
import com.clementlu.ticketservice.Models.Venue;

public class App 
{
    public static void main( String[] args )
    {   
        try {
            Scanner in = new Scanner(System.in);
            Venue venue = new Venue(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
            
            System.out.println("Total Seats: " + venue.getTotalSeats());
            System.out.println("Vacant Seats: " + venue.getNumSeats(SeatStatus.VACANT));
            System.out.println("Hold Seats: " + venue.getNumSeats(SeatStatus.HOLD));
            System.out.println("Reserved Seats: " + venue.getNumSeats(SeatStatus.RESERVED));
            
            TicketService service = new PerformanceVenueTicketService(venue);

            try {
                while(true) {
                    System.out.println("*********Start Request*********");
                    System.out.print("Enter seats to hold: ");
                    String numSeats = in.nextLine();

                    System.out.print("Enter e-mail address: ");
                    String email = in.nextLine();

                    SeatHold seatHold = service.findAndHoldSeats(Integer.parseInt(numSeats), email);

                    System.out.println("Seats Available: " + service.numSeatsAvailable());
                    System.out.println("Vacant Seats: " + venue.getNumSeats(SeatStatus.VACANT));
                    System.out.println("Hold Seats: " + venue.getNumSeats(SeatStatus.HOLD));
                    System.out.println("Reserved Seats: " + venue.getNumSeats(SeatStatus.RESERVED));

                    System.out.print("\nYou have 5 seconds to confirm your reservation, press ENTER to confirm...\n");
                    in.nextLine();

                    String confId = service.reserveSeats(seatHold.getId(), email);

                    if (confId != null) {
                        System.out.println("Reservation successful!");
                        System.out.println("Confirmation Code: " + confId);
                    } else {
                        System.out.println("Hold expired, please try again.");
                    }

                    System.out.println("Seats Available: " + service.numSeatsAvailable());
                    System.out.println("Vacant Seats: " + venue.getNumSeats(SeatStatus.VACANT));
                    System.out.println("Hold Seats: " + venue.getNumSeats(SeatStatus.HOLD));
                    System.out.println("Reserved Seats: " + venue.getNumSeats(SeatStatus.RESERVED));
                    System.out.println("*********End Request*********");
                }
            } catch (Exception e) {
                System.out.println("Restarting...");
            }
            
        } catch (InvalidVenueException ex) {
            System.out.println("Try again with valid venue dimensions: " + ex);
        }  
    }
}
