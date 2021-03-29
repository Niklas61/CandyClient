package de.staticcode.candy.module.modules.MOVEMENT;

import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import de.staticcode.ui.Line3D;
import de.staticcode.ui.Location3D;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;

public class NoFall extends Module {

    public NoFall ( ) {
        super ( "NoFall" , Category.MOVEMENT );
    }

    double startY = 0;

    @Override
    public void onUpdate ( ) {
        this.onAAC321 ( );
        super.onUpdate ( );
    }

    public void onAAC321 ( ) {
        if (Minecraft.thePlayer.fallDistance > 2) {

            startY += -Minecraft.thePlayer.motionY;

            if (startY > 2.5) {

                Line3D line = new Line3D ( new Location3D ( Minecraft.thePlayer.posX , Minecraft.thePlayer.posY , Minecraft.thePlayer.posZ ) , 0 ,
                        -90 , 99 );

                for ( Location3D point : line.getPointsOn ( 0.5f ) ) {

                    BlockPos pos = new BlockPos ( point.getX ( ) , point.getY ( ) , point.getZ ( ) );

                    if (mc.theWorld.getBlockState ( pos ).getBlock ( ) != Blocks.air) {

                        double top = mc.theWorld.getBlockState ( pos ).getBlock ( ).getBlockBoundsMaxY ( ) + pos.getY ( );

                        if (Minecraft.thePlayer.posY - top < 11) {

                            Minecraft.thePlayer.sendQueue.addToSendQueue ( new C03PacketPlayer.C04PacketPlayerPosition (
                                    Minecraft.thePlayer.posX , top , Minecraft.thePlayer.posZ , true ) );
                        }
                        break;
                    }
                }
                startY = 0;
            }
        } else {
            startY = 99f;
        }
    }

}
