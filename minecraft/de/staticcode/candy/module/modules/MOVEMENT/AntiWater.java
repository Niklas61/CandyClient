package de.staticcode.candy.module.modules.MOVEMENT;

import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

public class AntiWater extends Module {

    public AntiWater ( ) {
        super ( "AntiWater" , Category.MOVEMENT );
    }

    @Override
    public void onUpdate ( ) {
        BlockPos pos = new BlockPos ( Minecraft.thePlayer.posX , Minecraft.thePlayer.posY + Minecraft.thePlayer.motionY , Minecraft.thePlayer.posZ );
        if (mc.theWorld.getBlockState ( pos ).getBlock ( ) == Blocks.water) {
            Minecraft.thePlayer.motionY = 0.42f;
        }
        super.onUpdate ( );
    }

}
