package de.staticcode.candy.math;

public class ServerLaggTime {

    public static long lastReceivedPacketTime = System.currentTimeMillis ( );

    public static long distLastPacket ( ) {
        return Math.abs ( System.currentTimeMillis ( ) - lastReceivedPacketTime );
    }

}
