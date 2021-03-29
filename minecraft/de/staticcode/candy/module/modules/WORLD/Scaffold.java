package de.staticcode.candy.module.modules.WORLD;

import de.staticcode.candy.gui.components.GuiComponent;
import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import de.staticcode.candy.module.modules.COMBAT.Killaura;
import de.staticcode.candy.utils.RotationUtils;
import de.staticcode.candy.utils.Timings;
import de.staticcode.ui.BlickWinkel3D;
import de.staticcode.ui.Location3D;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;

public class Scaffold extends Module {

    public GuiComponent mode = null;
    public GuiComponent intervalStop = new GuiComponent ( "Stop After" , this , 7D , 1D , 1D );
    private int slotId;
    private final Timings timings = new Timings ( );

    private final GuiComponent rotationSpeed = new GuiComponent ( "Rotation-Speed" , this , 90D , 20D , 20D );

    private boolean foundBlock = false;
    private float previousYaw;
    private float previousPitch;

    private float lastYaw;

    public Scaffold ( ) {
        super ( "Scaffold" , Keyboard.KEY_X , Category.WORLD );

        mode = new GuiComponent ( "Scaffold Mode" , this , Arrays.asList ( "aac" , "normal" ) , "normal" );
    }

    @Override
    public void onEnable ( ) {
        this.previousYaw = Minecraft.thePlayer.rotationYaw;
        this.previousPitch = Minecraft.thePlayer.rotationPitch;
        this.foundBlock = false;
        this.lastSlot = Minecraft.thePlayer.inventory.currentItem;
        this.hasAttacked = false;
        this.lastYaw = 0.0F;
        this.doneRotated = false;
        super.onEnable ( );
    }

    int look = 0;
    private int blocksPlaced = 0;
    private long waited = 0L;

    private int lastSlot;
    private boolean doneRotated, doneYaw = false;

    private boolean hasAttacked = false;


    @Override
    public void onDisable ( ) {
        Minecraft.thePlayer.setSneaking ( false );
        mc.gameSettings.keyBindSneak.pressed = false;
        this.blocksPlaced = 0;
        this.waited = 0L;

        if (this.foundBlock)
            this.resetSlot ( );

        super.onDisable ( );
    }

    private int findBlocks ( ) {
        for ( int i = 0; i < 9; ++i ) {
            if (Minecraft.thePlayer.inventoryContainer.getSlot ( 36 + i ).getHasStack ( ) && Minecraft.thePlayer.inventoryContainer.getSlot ( 36 + i ).getStack ( ).getItem ( ) instanceof ItemBlock) {
                return 36 + i;
            }
        }

        return -1;
    }

    private void resetSlot ( ) {
        mc.getNetHandler ( ).addToSendQueue ( new C09PacketHeldItemChange ( Minecraft.thePlayer.inventory.currentItem ) );
    }


    @Override
    public void onUpdate ( ) {
        if (Killaura.underAttack != null) {
            this.hasAttacked = true;
            return;
        }

        if (!this.foundBlock) {
            int blockSlot = this.findBlocks ( );
            if (blockSlot != -1) {
                mc.getNetHandler ( ).addToSendQueue ( new C09PacketHeldItemChange ( blockSlot - 36 ) );
                this.foundBlock = true;
                this.slotId = blockSlot;
            }
            return;
        } else {
            if (this.timings.hasReached ( 500L )) {
                int blockSlot = this.findBlocks ( );
                if (blockSlot != -1 && this.slotId != blockSlot) {
                    mc.getNetHandler ( ).addToSendQueue ( new C09PacketHeldItemChange ( blockSlot - 36 ) );
                    this.slotId = blockSlot;
                }
            }
        }

        if (this.lastSlot != Minecraft.thePlayer.inventory.currentItem || ( this.hasAttacked && Killaura.underAttack == null )) {
            int blockSlot = this.findBlocks ( );
            if (blockSlot != -1) {
                mc.getNetHandler ( ).addToSendQueue ( new C09PacketHeldItemChange ( blockSlot - 36 ) );
                this.slotId = blockSlot;
            }

            if (this.hasAttacked)
                this.hasAttacked = false;
        }

        BlockPos place = new BlockPos ( Minecraft.thePlayer.posX , Minecraft.thePlayer.posY - 0.5 , Minecraft.thePlayer.posZ );
        BlockPos at = at ( place );

        Minecraft.thePlayer.setSprinting ( false );
        mc.gameSettings.keyBindSprint.pressed = false;

        if (at == null)
            return;

        int faceing = lastRot;

        boolean shouldLook = shouldLook ( Minecraft.thePlayer.posX , Minecraft.thePlayer.posY , Minecraft.thePlayer.posZ );

        mc.gameSettings.keyBindSneak.pressed = false;

        double random = Math.sin ( System.currentTimeMillis ( ) / 150D ) / 7D;

        if (this.blocksPlaced >= this.intervalStop.getCurrent ( )) {

            if (this.waited < 8L) {
                this.waited++;
                mc.gameSettings.keyBindSneak.pressed = true;
            } else {
                mc.gameSettings.keyBindSneak.pressed = false;
                this.waited = 0L;
                this.blocksPlaced = 0;
            }

            RotationUtils.server_pitch = ( float ) ( 80F + random );
            if (this.lastYaw == 0.0F)
                RotationUtils.server_yaw = Minecraft.thePlayer.rotationYaw;
            else
                RotationUtils.server_yaw = this.lastYaw + ( float ) random;

            Minecraft.thePlayer.motionX = 0.0D;
            Minecraft.thePlayer.motionZ = 0.0D;

            return;
        }

        if (shouldLook) {
            Location3D end = null;

            Location3D start = new Location3D ( Minecraft.thePlayer.posX , Minecraft.thePlayer.posY + 1.6 , Minecraft.thePlayer.posZ );

            if (faceing == 4) {
                end = new Location3D ( at.getX ( ) , at.getY ( ) + 0.5 , at.getZ ( ) + 0.5 );
            } else if (faceing == 5) {
                end = new Location3D ( at.getX ( ) + 1 , at.getY ( ) + 0.5 , at.getZ ( ) + 0.5 );
            } else if (faceing == 2) {
                end = new Location3D ( at.getX ( ) + 0.5 , at.getY ( ) + 0.5 , at.getZ ( ) );
            } else if (faceing == 3) {
                end = new Location3D ( at.getX ( ) + 0.5 , at.getY ( ) + 0.5 , at.getZ ( ) + 1 );
            }

            BlickWinkel3D bl = new BlickWinkel3D ( start , end );


            float yaw = this.computeNextYaw ( RotationUtils.server_yaw , this.previousYaw , ( float ) bl.getYaw ( ) );
            float pitch = this.computeNextPitch ( RotationUtils.server_pitch , this.previousPitch , ( float ) bl.getPitch ( ) );
            // max 81
            RotationUtils.server_pitch = pitch + ( float ) random;
            RotationUtils.server_yaw = ( yaw + ( float ) random );
            look = 15;

            this.previousYaw = yaw;
            this.previousPitch = pitch;

            if (this.mode.getActiveMode ( ).equalsIgnoreCase ( "aac" )) {
                Minecraft.thePlayer.motionX /= 2.5f;
                Minecraft.thePlayer.motionZ /= 2.5f;
            }
            this.lastYaw = yaw;

        } else {
            if (this.lastYaw == 0.0F)
                RotationUtils.server_yaw = Minecraft.thePlayer.rotationYaw;
            else
                RotationUtils.server_yaw = this.lastYaw + ( float ) random;
        }

        if (look > 0)
            look--;

        try {

            if (!this.doneRotated) {
                Minecraft.thePlayer.motionX = 0.0D;
                Minecraft.thePlayer.motionZ = 0.0D;
                return;
            }

            if (shouldPlace ( Minecraft.thePlayer.posX , Minecraft.thePlayer.posY , Minecraft.thePlayer.posZ )) {
                Vec3 v = new Vec3 ( place.getX ( ) , place.getY ( ) , place.getZ ( ) );

                mc.gameSettings.keyBindSneak.pressed = true;

                Minecraft.thePlayer.swingItem ( );

                IBlockState ibl = ( ( ItemBlock ) Minecraft.thePlayer.inventoryContainer.getSlot ( this.slotId ).getStack ( ).getItem ( ) ).getBlock ( )
                        .getDefaultState ( );
                mc.theWorld.setBlockState ( place , ibl );

                C08PacketPlayerBlockPlacement bl = new C08PacketPlayerBlockPlacement ( at , faceing ,
                        Minecraft.thePlayer.inventoryContainer.getSlot ( this.slotId ).getStack ( ) , 0 , 0 , 0 );

                this.blocksPlaced++;

                Minecraft.thePlayer.sendQueue.addToSendQueue ( bl );
                if (!this.mode.getActiveMode ( ).equalsIgnoreCase ( "AAC" )) {
                    Minecraft.move ( Minecraft.thePlayer.rotationYaw , 0.3f );
                } else {
                    Minecraft.move ( Minecraft.thePlayer.rotationYaw , 0.11F );
                }

            }
        } catch ( Exception e ) {

        }

        super.onUpdate ( );
    }

    int lastRot = 0;

    public BlockPos at ( BlockPos want ) {
        BlockPos o1 = new BlockPos ( want.getX ( ) + 1 , want.getY ( ) , want.getZ ( ) );
        BlockPos o2 = new BlockPos ( want.getX ( ) - 1 , want.getY ( ) , want.getZ ( ) );
        BlockPos o3 = new BlockPos ( want.getX ( ) , want.getY ( ) , want.getZ ( ) + 1 );
        BlockPos o4 = new BlockPos ( want.getX ( ) , want.getY ( ) , want.getZ ( ) - 1 );

        if (mc.theWorld.getBlockState ( o1 ).getBlock ( ) != Blocks.air) {
            lastRot = 4;
            return o1;
        }

        if (mc.theWorld.getBlockState ( o2 ).getBlock ( ) != Blocks.air) {
            lastRot = 5;
            return o2;
        }

        if (mc.theWorld.getBlockState ( o3 ).getBlock ( ) != Blocks.air) {
            lastRot = 2;
            return o3;
        }

        if (mc.theWorld.getBlockState ( o4 ).getBlock ( ) != Blocks.air) {
            lastRot = 3;
            return o4;
        }

        return null;
    }

    public boolean shouldPlace ( double x , double y , double z ) {
        BlockPos p1 = new BlockPos ( x - 0.25F , y - 0.5 , z - 0.25F );
        BlockPos p2 = new BlockPos ( x - 0.25F , y - 0.5 , z + 0.25F );
        BlockPos p3 = new BlockPos ( x + 0.25F , y - 0.5 , z + 0.25F );
        BlockPos p4 = new BlockPos ( x + 0.25F , y - 0.5 , z - 0.25F );

        if (Minecraft.thePlayer.worldObj.getBlockState ( p1 ).getBlock ( ) == Blocks.air)
            if (Minecraft.thePlayer.worldObj.getBlockState ( p2 ).getBlock ( ) == Blocks.air)
                if (Minecraft.thePlayer.worldObj.getBlockState ( p3 ).getBlock ( ) == Blocks.air)
                    return Minecraft.thePlayer.worldObj.getBlockState ( p4 ).getBlock ( ) == Blocks.air;
        return false;
    }

    // LOOK have to be between 80 and 83.3

    public boolean shouldLook ( double x , double y , double z ) {
        BlockPos p1 = new BlockPos ( x - 0.05F , y - 0.5 , z - 0.05F );
        BlockPos p2 = new BlockPos ( x - 0.05F , y - 0.5 , z + 0.05F );
        BlockPos p3 = new BlockPos ( x + 0.05F , y - 0.5 , z + 0.05F );
        BlockPos p4 = new BlockPos ( x + 0.05F , y - 0.5 , z - 0.05F );

        if (Minecraft.thePlayer.worldObj.getBlockState ( p1 ).getBlock ( ) == Blocks.air)
            if (Minecraft.thePlayer.worldObj.getBlockState ( p2 ).getBlock ( ) == Blocks.air)
                if (Minecraft.thePlayer.worldObj.getBlockState ( p3 ).getBlock ( ) == Blocks.air)
                    return Minecraft.thePlayer.worldObj.getBlockState ( p4 ).getBlock ( ) == Blocks.air;
        return false;
    }


    private float computeNextYaw ( float currentYaw , float previousYaw , float targetYaw ) {

        float snappyness = ( float ) this.rotationSpeed.getCurrent ( );
        float friction = 3F;
        float prevmotion = this.getAbsolutePath ( currentYaw - previousYaw );
        float delta = this.getRotation ( currentYaw , targetYaw );

        float absDelta = Math.abs ( delta );
        float x = absDelta / 180;
        float accel = ( -( 2 * x - 1 ) * ( 2 * x - 1 ) + 1 ) * snappyness * delta / absDelta;
        float motion = prevmotion / friction + accel;
        if (Math.abs ( motion ) > 0.01D) {
            currentYaw += motion;
        }

        this.doneYaw = absDelta <= 5D;
        return currentYaw;
    }


    private float computeNextPitch ( float currentPitch , float previousPitch , float targetPitch ) {

        float snappyness = ( float ) this.rotationSpeed.getCurrent ( ) / 2F;
        float friction = 3F;
        float prevmotion = this.getAbsolutePath ( currentPitch - previousPitch );
        float delta = this.getRotation ( currentPitch , targetPitch );

        float absDelta = Math.abs ( delta );
        float x = absDelta / 180;
        float accel = ( -( 2 * x - 1 ) * ( 2 * x - 1 ) + 1 ) * snappyness * delta / absDelta;
        float motion = prevmotion / friction + accel;

        if (Math.abs ( motion ) > 0.01D) {
            currentPitch += motion;
        }

        this.doneRotated = absDelta <= 3D && this.doneYaw;
        return currentPitch;
    }


    private float getRotation ( float current , float absolute ) {
        float delta = absolute - current;
        float fixedDelta = this.getAbsolutePath ( delta );
        return fixedDelta;
    }


    private float getAbsolutePath ( float rotation ) {
        rotation = rotation % 360;
        if (rotation > 180) {
            rotation -= 360;
        } else if (rotation <= -180) {
            rotation += 360;
        }
        return rotation;
    }

}