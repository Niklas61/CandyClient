package de.staticcode.candy.math;

public class LaggTime {

    public static long lastMs = System.currentTimeMillis ( );

    public static long getTPS ( ) {
        return Math.abs ( System.currentTimeMillis ( ) - LaggTime.lastMs );
    }


}
