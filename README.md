# Ticket Service

This project implements a simple ticket service that allows a user to find, hold, and reserve seats for a given venue.

## Assumptions

1. Venue is rectangular and is defined by giving the number of rows and columns.
2. `0 < rows <= 1000` and `0 < columns <= 1000`
3. Seat holds expire after 5 seconds (for the purpose of a shorter demo).
4. "Best" seats are closest to the front of the venue.
5. If a seat hold request needs more seats than the venue currently has available, no seats will be held.

## Implementation

The venue is represented as a 2D array, with the outer array as the leftmost column that determines the total number of rows in the venue and each element as another array containing Seats, representing each row in the venue. During `Venue` creation, a `seatMap` is initialized through the `initializeSeatMap` method which creates a 2D array and populates each "seat" with a `Seat` object that is `VACANT`. So, for example, venue.seatMap[0][0] would be the seat in the upper left corner in the very first row closest to the stage. 

Each `Seat` object maintains its `SeatStatus` which can be `VACANT`, `HOLD`, or `RESERVED` as well as its seat number which is represented as a `Point` with x and y coordinates, corresponding to its column and row, respectively. 

The venue is filled with a "first fit" algorithm, starting in at `(0, 0)` and moving left to right and then starting over in the first element of the next row if there is any overflow. For example, in a 2x2 venue, a request for a 3 seat hold would return the following seats: `[(0,0), (1,0), (0,1)]`.

A `seatHolds` map inside the `Venue` object maintains the current list of active reservation holds. The map holds the `seatHoldId` as the key and the `SeatHold` object itself as the value to allow for easy retrieval when the `reserveSeats` method is called. After creating the `SeatHold` and adding it to the list, a new thread is started to wait for either a notify from the main thread that `reserveSeats` has been called, or a timeout based on the `holdExpiry` variable has occured. If the hold expires, the thread access both the seat hold list and the venue's seatmap in a thread-safe way to remove the `SeatHold` from the active list and to change the `SeatStatus` of all the `Seat` objects from `HOLD` to `VACANT`. Otherwise, if `reserveSeats` is called, then the `SeatStatus` of each of the seats in the `SeatHold` are changed from `HOLD` to `RESERVED`. The `SeatHold` is then removed from the active seat hold list after a successful reservation confirmation.

## Build
```bash
$ mvn compile
```

## Test
```bash
mvn test
```

## Execution
To run the main class for demo and interactive purposes, run the following where `x` is the number of rows and `y` is the number of columns:
```bash
$ mvn exec:java -Dexec.args="x y"
```

## Package
The following command will create a .jar file in the target/ folder:
```bash
mvn clean install
```

## Future Work/Improvements

1. Need to work on isolation in unit tests with added mocking of outside method calls.
2. Seat people starting from the center of a row and work outwards.
