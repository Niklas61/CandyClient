package de.staticcode.candy.module.modules.MOVEMENT;

import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;

public class GroundFly extends Module {

    public GroundFly ( ) {
        super ( "GroundFly" , Category.MOVEMENT );
    }

    private BlockPos lastPos;
    private double startY;
    private IBlockState oldBlockState;

    @Override
    public void onDisable ( ) {
        this.lastPos = null;
        super.onDisable ( );
    }

    @Override
    public void onEnable ( ) {
        Minecraft.thePlayer.motionY = 0.42d;
        this.startY = Minecraft.thePlayer.posY - 0.5d;
        super.onEnable ( );
    }

    @Override
    public void onUpdate ( ) {
        if (this.lastPos != null) {
            mc.theWorld.setBlockState ( this.lastPos , this.oldBlockState );
        }

        mc.gameSettings.keyBindSprint.pressed = false;
        Minecraft.thePlayer.setSprinting ( false );
        Minecraft.thePlayer.sendQueue.addToSendQueue ( new C03PacketPlayer ( true ) );

        mc.theWorld.setBlockState ( new BlockPos ( Minecraft.thePlayer.posX , this.startY , Minecraft.thePlayer.posZ ) ,
                Blocks.barrier.getDefaultState ( ) );


        this.lastPos = new BlockPos ( Minecraft.thePlayer.posX , this.startY , Minecraft.thePlayer.posZ );
        this.oldBlockState = mc.theWorld.getBlockState ( this.lastPos );

        super.onUpdate ( );
    }

}
