package de.staticcode.candy.module.modules.WORLD;

import de.staticcode.candy.gui.components.GuiComponent;
import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import de.staticcode.candy.module.modules.COMBAT.Killaura;
import de.staticcode.candy.utils.RotationUtils;
import de.staticcode.ui.BlickWinkel3D;
import de.staticcode.ui.Line3D;
import de.staticcode.ui.Location3D;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Iterator;

public class ChestAura extends Module {
    BlockPos breaking;
    int state = 0;
    int delayc = 0;
    private final GuiComponent rangeSlider = new GuiComponent ( "Range" , this , 7.0 , 2.0 , 4.0 );
    private final GuiComponent CPSSlider = new GuiComponent ( "Delay" , this , 400D , 100D , 200 );
    private final GuiComponent lookButton = new GuiComponent ( "Look" , this , true );
    private final GuiComponent throughBlocksButton = new GuiComponent ( "Trough Blocks" , this , true );
    public static int chest, trapped;
    public BlockPos openNextTick = null;
    public static ArrayList opened;

    static {
        chest = Block.getIdFromBlock ( Blocks.chest );
        trapped = Block.getIdFromBlock ( Blocks.trapped_chest );
        opened = new ArrayList ( );
    }

    public ChestAura ( ) {
        super ( "ChestAura" , Category.WORLD );
    }

    public void onUpdate ( ) {
        if (!Minecraft.thePlayer.isUsingItem ( )) {
            if (Killaura.underAttack == null) {
                if (!( this.mc.currentScreen instanceof GuiInventory )) {
                    try {
                        if (this.mc.currentScreen instanceof GuiChest) {
                            this.mc.gameSettings.keyBindForward.pressed = Keyboard.isKeyDown ( this.mc.gameSettings.keyBindForward.getKeyCode ( ) );
                            this.mc.gameSettings.keyBindBack.pressed = Keyboard.isKeyDown ( this.mc.gameSettings.keyBindBack.getKeyCode ( ) );
                            this.mc.gameSettings.keyBindRight.pressed = Keyboard.isKeyDown ( this.mc.gameSettings.keyBindRight.getKeyCode ( ) );
                            this.mc.gameSettings.keyBindLeft.pressed = Keyboard.isKeyDown ( this.mc.gameSettings.keyBindLeft.getKeyCode ( ) );
                            this.mc.gameSettings.keyBindJump.pressed = Keyboard.isKeyDown ( this.mc.gameSettings.keyBindJump.getKeyCode ( ) );
                            this.mc.gameSettings.keyBindSneak.pressed = Keyboard.isKeyDown ( this.mc.gameSettings.keyBindSneak.getKeyCode ( ) );
                            return;
                        }

                        if (this.mc.currentScreen == null) {
                            this.mc.gameSettings.keyBindForward.pressed = Keyboard.isKeyDown ( this.mc.gameSettings.keyBindForward.getKeyCode ( ) );
                            this.mc.gameSettings.keyBindBack.pressed = Keyboard.isKeyDown ( this.mc.gameSettings.keyBindBack.getKeyCode ( ) );
                            this.mc.gameSettings.keyBindRight.pressed = Keyboard.isKeyDown ( this.mc.gameSettings.keyBindRight.getKeyCode ( ) );
                            this.mc.gameSettings.keyBindLeft.pressed = Keyboard.isKeyDown ( this.mc.gameSettings.keyBindLeft.getKeyCode ( ) );
                            this.mc.gameSettings.keyBindJump.pressed = Keyboard.isKeyDown ( this.mc.gameSettings.keyBindJump.getKeyCode ( ) );
                            this.mc.gameSettings.keyBindSneak.pressed = Keyboard.isKeyDown ( this.mc.gameSettings.keyBindSneak.getKeyCode ( ) );
                        }
                    } catch ( Exception var11 ) {
                    }

                    if (this.openNextTick != null) {
                        this.sendClick ( this.openNextTick );
                        this.openNextTick = null;
                    }

                    if (( double ) this.delayc < ( this.CPSSlider.getCurrent ( ) ) / 12) {
                        ++this.delayc;
                    } else {
                        this.delayc = 0;

                        for ( double x = -this.rangeSlider.getCurrent ( ); x < this.rangeSlider.getCurrent ( ); ++x ) {
                            for ( double y = -this.rangeSlider.getCurrent ( ); y < this.rangeSlider.getCurrent ( ); ++y ) {
                                for ( double z = -this.rangeSlider.getCurrent ( ); z < this.rangeSlider.getCurrent ( ); ++z ) {
                                    final BlockPos pos = new BlockPos ( Minecraft.thePlayer.posX + x , Minecraft.thePlayer.posY + y , Minecraft.thePlayer.posZ + z );
                                    if (!opened.contains ( pos ) && ( Block.getIdFromBlock ( this.mc.theWorld.getBlockState ( pos ).getBlock ( ) ) == chest || Block.getIdFromBlock ( this.mc.theWorld.getBlockState ( pos ).getBlock ( ) ) == trapped ) && Math.sqrt ( Minecraft.thePlayer.getDistanceSqToCenter ( pos ) ) < this.rangeSlider.getCurrent ( ) && ( this.throughBlocksButton.isToggled ( ) || !this.isBlockBetween ( pos ) )) {
                                        Minecraft.thePlayer.swingItem ( );
                                        if (this.lookButton.isToggled ( )) {
                                            Location3D player = new Location3D ( Minecraft.thePlayer.posX , Minecraft.thePlayer.posY + 1.7000000476837158D , Minecraft.thePlayer.posZ );
                                            Location3D block = new Location3D ( ( double ) pos.getX ( ) + 0.5D , ( double ) pos.getY ( ) + 0.5D , ( double ) pos.getZ ( ) + 0.5D );
                                            BlickWinkel3D look = new BlickWinkel3D ( player , block );
                                            RotationUtils.server_pitch = ( float ) look.getPitch ( );
                                            RotationUtils.server_yaw = ( float ) look.getYaw ( );
                                        }

                                        this.openNextTick = pos;
                                        opened.add ( pos );
                                        ( new Thread ( new Runnable ( ) {
                                            public void run ( ) {
                                                try {
                                                    Thread.sleep ( 30000L );
                                                } catch ( Exception var5 ) {
                                                } finally {
                                                    ChestAura.opened.remove ( pos );
                                                }

                                            }
                                        } ) ).start ( );
                                        return;
                                    }
                                }
                            }
                        }

                        super.onUpdate ( );
                    }
                }
            }
        }
    }

    public boolean isBlockBetween ( BlockPos pos ) {
        Location3D chestL = new Location3D ( ( double ) pos.getX ( ) + 0.5D , ( double ) pos.getY ( ) + 0.5D , ( double ) pos.getZ ( ) + 0.5D );
        Location3D player = new Location3D ( Minecraft.thePlayer.posX , Minecraft.thePlayer.posY + 1.7D , Minecraft.thePlayer.posZ );
        if (player.distance ( chestL ) < 0.5D) {
            return true;
        } else {
            Line3D line = new Line3D ( player , chestL );
            Iterator var6 = line.getPointsOn ( 0.5D ).iterator ( );

            while ( var6.hasNext ( ) ) {
                Location3D point = ( Location3D ) var6.next ( );
                if (this.mc.theWorld.getBlockState ( new BlockPos ( point.getX ( ) , point.getY ( ) , point.getZ ( ) ) ).getBlock ( ) == this.mc.theWorld.getBlockState ( pos )) {
                    return false;
                }
            }

            return false;
        }
    }

    public void onEnable ( ) {
        opened.clear ( );
        super.onEnable ( );
    }

    public void sendClick ( BlockPos pos ) {
        C08PacketPlayerBlockPlacement packet = new C08PacketPlayerBlockPlacement ( pos , ( double ) pos.getY ( ) + 0.5D < Minecraft.thePlayer.posY + 1.7D ? 1 : 0 , Minecraft.thePlayer.getCurrentEquippedItem ( ) , 0.0F , 0.0F , 0.0F );
        Minecraft.thePlayer.sendQueue.addToSendQueue ( packet );
    }
}
