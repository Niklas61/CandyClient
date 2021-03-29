package de.staticcode.candy.utils;

import net.minecraft.client.Minecraft;

public class Timings {

    private long lastMS;

    public Timings ( ) {
        this.lastMS = Minecraft.getSystemTime ( );
    }

    public boolean hasReached ( long ms ) {
        return Math.abs ( Minecraft.getSystemTime ( ) - this.lastMS ) >= ms;
    }

    public void resetTimings ( ) {
        this.lastMS = Minecraft.getSystemTime ( );
    }

}
