package de.staticcode.candy.module.modules.WORLD;

import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import de.staticcode.candy.utils.RotationUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Keyboard;

public class Tower extends Module {

    public Tower ( ) {
        super ( "Tower" , Keyboard.KEY_H , Category.WORLD );
    }

    double groundy = 0;

    @Override
    public void onUpdate ( ) {

        if (!Keyboard.isKeyDown ( Keyboard.KEY_SPACE ))
            return;

        mc.gameSettings.keyBindJump.pressed = false;

        BlockPos at = new BlockPos ( Minecraft.thePlayer.posX , Minecraft.thePlayer.posY - 2 , Minecraft.thePlayer.posZ );
        ItemStack is = Minecraft.thePlayer.getCurrentEquippedItem ( );

        // check for block in hand
        if (is == null || !( is.getItem ( ) instanceof ItemBlock )) {
            return;
        }

        // check for block under player
        if (mc.theWorld.getBlockState ( at ).getBlock ( ) == Blocks.air)
            if (Minecraft.thePlayer.onGround)
                Minecraft.thePlayer.jump ( );
            else
                return;

        // set silent player look
        RotationUtils.server_pitch = ( float ) ( 90F - Math.random ( ) );

        // jump if onground
        if (Minecraft.thePlayer.onGround) {
            groundy = Minecraft.thePlayer.posY;
            jump ( );
        }

        // place block if possible & jump
        if (Minecraft.thePlayer.posY > groundy + 0.9) {
            Minecraft.thePlayer.setPosition ( Minecraft.thePlayer.posX , ( int ) ( Minecraft.thePlayer.posY ) , Minecraft.thePlayer.posZ );
            placeBlock ( );
            jump ( );
            groundy = Minecraft.thePlayer.posY;
        }
        super.onUpdate ( );
    }

    public void placeBlock ( ) {

        BlockPos at = new BlockPos ( Minecraft.thePlayer.posX , Minecraft.thePlayer.posY - 2 , Minecraft.thePlayer.posZ );
        ItemStack is = Minecraft.thePlayer.getCurrentEquippedItem ( );

        // check for block to place at
        if (mc.theWorld.getBlockState ( at.add ( 0 , 1 , 0 ) ).getBlock ( ) != Blocks.air) {
            return;
        }

        // play arm animation
        Minecraft.thePlayer.sendQueue.addToSendQueue ( new C0APacketAnimation ( ) );

        // place block for client
        IBlockState ibl = ( ( ItemBlock ) is.getItem ( ) ).getBlock ( ).getDefaultState ( );
        mc.theWorld.setBlockState ( at.add ( 0 , 1 , 0 ) , ibl );

        // place block for server
        C08PacketPlayerBlockPlacement bl = new C08PacketPlayerBlockPlacement ( at , 1 ,
                Minecraft.thePlayer.getCurrentEquippedItem ( ) , 0 , 0 , 0 );
        Minecraft.thePlayer.sendQueue.addToSendQueue ( bl );
    }

    public void jump ( ) {
        Minecraft.thePlayer.motionY = 0.42f;
    }
}