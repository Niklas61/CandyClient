package de.staticcode.candy.module.modules.MOVEMENT;

import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

public class Jesus extends Module {

    public Jesus ( ) {
        super ( "Jesus" , Category.MOVEMENT );
    }

    boolean goUp = false;
    boolean ready = false;

    @Override
    public void onUpdate ( ) {
        this.onAAC321 ( );
        super.onUpdate ( );
    }

    public void onAAC321 ( ) {

        if (!Minecraft.thePlayer.isInWater ( )) {
            ready = false;
            return;
        }

        BlockPos pos = new BlockPos ( Minecraft.thePlayer.posX , Minecraft.thePlayer.posY + 0.5 , Minecraft.thePlayer.posZ );
        BlockPos pos2 = new BlockPos ( Minecraft.thePlayer.posX , Minecraft.thePlayer.posY + 0.55 , Minecraft.thePlayer.posZ );

        if (goUp == true)
            if (mc.theWorld.getBlockState ( pos ).getBlock ( ) != Blocks.water) {
                goUp = false;
            }

        if (goUp == false)
            if (mc.theWorld.getBlockState ( pos2 ).getBlock ( ) == Blocks.water) {
                goUp = true;
                ready = true;
            }

        if (ready)
            if (goUp) {
                Minecraft.thePlayer.motionY += 0.03f;

                Minecraft.thePlayer.motionX *= 1.2f;
                Minecraft.thePlayer.motionZ *= 1.2f;
            } else {
                Minecraft.thePlayer.motionY += -0.06f;
                Minecraft.thePlayer.motionX *= 1.2f;
                Minecraft.thePlayer.motionZ *= 1.2f;
            }
    }
}
