package de.staticcode.candy.module.modules.MOVEMENT;

import de.staticcode.candy.Candy;
import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import de.staticcode.candy.utils.Timings;
import de.staticcode.ui.Line3D;
import de.staticcode.ui.Location3D;
import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.List;

public class Teleport extends Module {

    public Teleport ( ) {
        super ( "Teleport" , Category.MOVEMENT );
    }

    public static BlockPos pos;
    public static BlockPos toTP;
    public static double x, y, z;
    private boolean finished;
    private final List< Location3D > toSwitch = new ArrayList<> ( );

    private final Timings timings = new Timings ( );

    public static boolean flagged;

    public static Location3D current;

    @Override
    public void onDisable ( ) {
        pos = null;
        mc.timer.timerSpeed = 1.0F;
        super.onDisable ( );
    }

    @Override
    public void onUpdate ( ) {

        if (Minecraft.thePlayer.isSneaking ( )) {
            pos = null;
            this.finished = false;
            current = null;
            this.toSwitch.clear ( );
            flagged = false;
            toTP = null;
            mc.timer.timerSpeed = 1.0f;
            return;
        }

        if (Mouse.isButtonDown ( 2 )
                && !( mc.theWorld.getBlockState ( mc.objectMouseOver.getBlockPos ( ) ).getBlock ( ) instanceof BlockAir )) {

            current = null;
            this.finished = false;
            this.toSwitch.clear ( );

            Minecraft.thePlayer.setPosition ( Minecraft.thePlayer.posX , Minecraft.thePlayer.posY , Minecraft.thePlayer.posZ );

            try {
                Candy.sendChat ( "Teleporting to: " + mc.objectMouseOver.getBlockPos ( ) );
                pos = mc.objectMouseOver.getBlockPos ( ).add ( 0.0D , 1D , 0.0D );
                toTP = pos;
                Mouse.destroy ( );
                Mouse.create ( );
            } catch ( Exception e ) {
            }
        }

        if (flagged && toTP != null) {

            this.finished = false;
            current = null;
            this.toSwitch.clear ( );
            flagged = false;
            pos = toTP;
        }

        if (pos != null && !this.finished) {

            mc.timer.timerSpeed = 0.1F;
            Line3D to = new Line3D ( new Location3D ( Minecraft.thePlayer.posX , Minecraft.thePlayer.posY , Minecraft.thePlayer.posZ ) ,
                    new Location3D ( pos.getX ( ) , pos.getY ( ) , pos.getZ ( ) ) );

            if (this.toSwitch.isEmpty ( )) {
                for ( Location3D locs : to.getPointsOn ( 0.1d ) ) {
                    this.toSwitch.add ( locs );
                }
                this.finished = true;
            }
        } else {

            if (!this.toSwitch.isEmpty ( )) {
                this.teleport ( );

            } else {
                mc.timer.timerSpeed = 1.0F;
                pos = null;
                toTP = null;
                this.finished = false;
                current = null;
                this.toSwitch.clear ( );
                mc.timer.timerSpeed = 1.0f;
            }

        }
        super.onUpdate ( );
    }

    private boolean lastY;

    private void teleport ( ) {
        if (!this.toSwitch.isEmpty ( )) {
            Location3D loc = this.toSwitch.get ( 0 );
            current = loc;

            Minecraft.thePlayer.sendQueue
                    .addToSendQueue ( new C03PacketPlayer.C04PacketPlayerPosition ( x , y , z , true ) );

            x = loc.getX ( );
            y = loc.getY ( );
            z = loc.getZ ( );

            this.lastY = !this.lastY;

            mc.gameSettings.keyBindForward.pressed = false;
            mc.gameSettings.keyBindBack.pressed = false;

            mc.gameSettings.keyBindLeft.pressed = false;
            mc.gameSettings.keyBindRight.pressed = false;
            mc.gameSettings.keyBindJump.pressed = false;

            this.toSwitch.remove ( loc );
            this.timings.resetTimings ( );

        }
    }

}
