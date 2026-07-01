package com.rescue.model;

public class Victim extends GameObject {

    private boolean rescued;

    public Victim(int row, int col) {
        super(row, col);
        rescued = false;
    }

    public boolean isRescued() {
        return rescued;
    }

    public void rescue() {
        rescued = true;
    }

}