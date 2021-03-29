package de.staticcode.candy.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;

public class ThreadedPacketQuery implements Runnable {

    private final Packet packet;
    private final INetHandler iNetHandler;
    protected Minecraft mc = Minecraft.getMinecraft ( );

    public ThreadedPacketQuery ( Packet packet , INetHandler iNetHandler ) {
        this.packet = packet;
        this.iNetHandler = iNetHandler;
    }

    @Override
    public void run ( ) {
        if (this.getPacket ( ) != null && this.getINetHandler ( ) != null) {
            this.getPacket ( ).processPacket ( this.getINetHandler ( ) );
        }
    }

    public Packet getPacket ( ) {
        return packet;
    }

    public INetHandler getINetHandler ( ) {
        return iNetHandler;
    }
}
