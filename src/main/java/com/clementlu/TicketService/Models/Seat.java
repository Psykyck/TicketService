package com.clementlu.ticketservice.Models;

import com.clementlu.ticketservice.Constant.SeatStatus;
import java.awt.Point;

/**
 * @author      Clement Lu <clementlu@live.com>
 * @version     1.0
 * @since       1.0
 */
public class Seat {
    private SeatStatus seatStatus;
    private Point seatNo;

    public Seat(int row, int column) {
        this.seatStatus = SeatStatus.VACANT;
        this.seatNo = new Point(row, column);
    }

    public void setSeatStatus(SeatStatus status) {
        this.seatStatus = status;
    }

    public SeatStatus getSeatStatus() {
        return seatStatus;
    }

    public void setSeatNo(int x, int y) {
        seatNo.setLocation(x, y);
    }

    public Point getSeatNo() {
        return seatNo;
    }

    @Override
    public String toString() {
        return "Seat Number: " +
            seatNo.toString() +
            " Seat Status: " +
            seatStatus.toString();
    }
}