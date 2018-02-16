# Ticket Service

This project implements a simple ticket service that allows a user to find, hold, and reserve seats for a given venue.

## Assumptions

1. Venue is rectangular and is defined by giving the number of rows and columns.
2. `0 < rows <= 1000` and `0 < columns <= 1000`
3. Seat holds expire after 90 seconds.
4. "Best" seats are closest to the front of the venue.
5. If a seat hold request needs more seats than the venue currently has available, no seats will be held.

## Implementation

The venue is represented as a 2D array, with the outer array as the leftmost column that determines the total number of rows in the venue and each element as another array containing Seats, representing each row in the venue. During `Venue` creation, a `seatMap` is initialized through the `initializeSeatMap` method which creates a 2D array and populates each "seat" with a `Seat` object that is `VACANT`. So, for example, venue.seatMap[0][0] would be the seat in the upper left corner in the very first row closest to the stage. 

Each `Seat` object maintains its `SeatStatus` which can be `VACANT`, `HOLD`, or `RESERVED` as well as its seat number which is represented as a `Point` with x and y coordinates, corresponding to its column and row, respectively. 

The venue is filled with a "first fit" algorithm, starting in at `(0, 0)` and moving left to right and then starting over in the first element of the next row if there is any overflow. For example, in a 2x2 venue, a request for a 3 seat hold would return the following seats: `[(0,0), (1,0), (0,1)]`.

A `seatHolds` map inside the `Venue` object maintains the current list of active reservation holds. The map holds the `seatHoldId` as the key and the `SeatHold` object itself as the value to allow for easy retrieval when the `reserveSeats` method is called to finalize the reservation and change the `SeatStatus` of each of the seats in the `SeatHold` from `HOLD` to `RESERVED`. The `SeatHold` is then removed from the active seat hold list after a successful reservation confirmation.

## Build and Package
```bash
$ mvn clean install
```

## Test
```bash
mvn test
```

## Execution
To run the main class for demo purposes and interaction, run the following:
```bash
$ mvn exec:java
```
