package de.staticcode.candy.module.modules.PLAYER;

import de.staticcode.candy.gui.components.GuiComponent;
import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import de.staticcode.candy.module.modules.COMBAT.Killaura;
import de.staticcode.candy.utils.RotationUtils;
import de.staticcode.candy.utils.Timings;
import de.staticcode.ui.BlickWinkel3D;
import de.staticcode.ui.Location3D;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Fucker extends Module {
    public static int blockId = 0;
    private final Timings timings = new Timings ( );
    private final GuiComponent sTries = new GuiComponent ( "Tries" , this , 10.0D , 1.0D , 1.0D );
    private final GuiComponent sDelay = new GuiComponent ( "Delay" , this , 2000.0D , 2.0D , 3.0D );
    private final GuiComponent bFailureFix = new GuiComponent ( "Failure Fix" , this , false );
    private long lastFail;
    private boolean failure;
    private BlockPos failureBlock;
    private int trys;
    private BlockPos lastArround;
    private Block lastArroundBlock;

    public Fucker ( ) {
        super ( "Fucker" , Category.WORLD );
    }

    @Override
    public void onEnable ( ) {
        blockId = 26;
    }

    public void onUpdate ( ) {
        super.onUpdate ( );
        if (!this.bFailureFix.isToggled ( ) || this.failureBlock != null && this.getBlocksArround ( this.failureBlock ).isEmpty ( )) {
            this.lastArround = null;
            this.lastArroundBlock = null;
            this.failure = false;
        }

        if (Killaura.underAttack == null) {
            ( new Thread ( new Runnable ( ) {
                public void run ( ) {
                    try {
                        if (Fucker.this.failure && !Fucker.this.getBlocksArround ( Fucker.this.failureBlock ).isEmpty ( )) {
                            Fucker.this.checkFailure ( );
                            return;
                        }

                        for ( int y = -5; y < 5; ++y ) {
                            for ( int x = -5; x < 5; ++x ) {
                                for ( int z = -5; z < 5; ++z ) {
                                    BlockPos blockPos = Minecraft.thePlayer.getPosition ( ).add ( x , y , ( double ) z );
                                    Block block = Fucker.this.mc.theWorld.getBlockState ( blockPos ).getBlock ( );
                                    if (block == Block.getBlockById ( Fucker.blockId )) {
                                        Fucker.this.sendLooksToPos ( blockPos );
                                        if (Fucker.this.timings.hasReached ( ( long ) Fucker.this.sDelay.getCurrent ( ) )) {
                                            Fucker.this.destroyBlock ( blockPos );
                                            Fucker.this.trys = Fucker.this.trys + 1;
                                            Fucker.this.timings.resetTimings ( );
                                            Thread.sleep ( 250L );
                                            if (Fucker.this.failedDestroy ( blockPos , block ) && !Fucker.this.getBlocksArround ( blockPos ).isEmpty ( ) && ( double ) Fucker.this.trys > Fucker.this.sTries.getCurrent ( )) {
                                                Fucker.this.lastFail = System.currentTimeMillis ( );
                                                Fucker.this.failure = true;
                                                Fucker.this.failureBlock = blockPos;
                                                Fucker.this.trys = 0;
                                            }
                                        }

                                        return;
                                    }
                                }
                            }
                        }
                    } catch ( Exception var6 ) {
                    }

                }
            } ) ).start ( );
        }
    }

    private void checkFailure ( ) {
        long sinceLastFail = System.currentTimeMillis ( ) - this.lastFail;
        if (!this.getBlocksArround ( this.failureBlock ).isEmpty ( )) {
            BlockPos blockPos = this.getClosetNearby ( this.getBlocksArround ( this.failureBlock ) );
            this.sendLooksToPos ( blockPos );
            if (sinceLastFail > 150L) {
                this.lastArround = blockPos;
                this.lastArroundBlock = this.mc.theWorld.getBlockState ( blockPos ).getBlock ( );
                this.destroyBlock ( blockPos );
            }
        }

        if (this.lastArround != null) {
            this.lastArround = null;
            this.lastArroundBlock = null;
            this.failure = false;
        }

    }

    private void sendLooksToPos ( BlockPos blockPos ) {
        Location3D startLoc = new Location3D ( Minecraft.thePlayer.posX , Minecraft.thePlayer.posY + 1.6D , Minecraft.thePlayer.posZ );
        Location3D endLoc = new Location3D ( blockPos.getX ( ) , blockPos.getY ( ) , blockPos.getZ ( ) );
        BlickWinkel3D blickWinkel3D = new BlickWinkel3D ( startLoc , endLoc );
        RotationUtils.server_pitch = ( float ) blickWinkel3D.getPitch ( );
        RotationUtils.server_yaw = ( float ) blickWinkel3D.getYaw ( );
    }

    private List getBlocksArround ( BlockPos mainPos ) {
        ArrayList arroundBlocks = new ArrayList ( );
        Block mainBlock = this.mc.theWorld.getBlockState ( mainPos ).getBlock ( );

        for ( int y = 0; y < 2; ++y ) {
            for ( int x = -5; x < 5; ++x ) {
                for ( int z = -5; z < 5; ++z ) {
                    BlockPos blockPos = mainPos.add ( x , y , z );
                    Block block = this.mc.theWorld.getBlockState ( blockPos ).getBlock ( );
                    if (block == Blocks.sandstone || block == Blocks.iron_block || block == Blocks.end_stone || block == Blocks.glass || block == Blocks.glowstone || block == Blocks.chest || block.getLocalizedName ( ).contains ( "Chiseled Sandstone" )) {
                        arroundBlocks.add ( blockPos );
                    }
                }
            }
        }

        return arroundBlocks;
    }

    private BlockPos getClosetNearby ( List list ) {
        BlockPos currentPos = null;
        double currentRange = Double.MAX_VALUE;
        Iterator var6 = list.iterator ( );

        while ( var6.hasNext ( ) ) {
            BlockPos blockPos = ( BlockPos ) var6.next ( );
            if (Minecraft.thePlayer.getDistance ( blockPos.getX ( ) , blockPos.getY ( ) , blockPos.getZ ( ) ) < currentRange) {
                currentRange = Minecraft.thePlayer.getDistance ( blockPos.getX ( ) , blockPos.getY ( ) , blockPos.getZ ( ) );
                currentPos = blockPos;
            }
        }

        return currentPos;
    }

    private void destroyBlock ( BlockPos blockPos ) {
        Minecraft.thePlayer.sendQueue.addToSendQueue ( new C0APacketAnimation ( ) );
        Minecraft.thePlayer.sendQueue.addToSendQueue ( new C07PacketPlayerDigging ( C07PacketPlayerDigging.Action.START_DESTROY_BLOCK , blockPos , EnumFacing.DOWN ) );
        Minecraft.thePlayer.sendQueue.addToSendQueue ( new C07PacketPlayerDigging ( C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK , blockPos , EnumFacing.DOWN ) );
    }

    private boolean failedDestroy ( BlockPos blockPos , Block block ) {
        return this.mc.theWorld.getBlockState ( blockPos ).getBlock ( ) == block;
    }
}
