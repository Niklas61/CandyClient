package de.staticcode.candy.module.modules.MOVEMENT;

import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;

public class WaterSpeed extends Module {

    public WaterSpeed ( ) {
        super ( "WaterSpeed" , Category.MOVEMENT );
    }

    private boolean wasOnGround;

    @Override
    public void onUpdate ( ) {
        if (Minecraft.thePlayer.isInWater ( )) {
            if (Minecraft.thePlayer.onGround)
                this.wasOnGround = true;

            if (mc.theWorld.getBlockState ( Minecraft.thePlayer.getPosition ( ) ).getBlock ( ) instanceof BlockLiquid
                    && this.wasOnGround) {
                Minecraft.thePlayer.motionY = 0.167d;
                Minecraft.thePlayer.motionX *= 1.21d;
                Minecraft.thePlayer.motionZ *= 1.21d;
            }

            if (!this.wasOnGround) {
                Minecraft.thePlayer.motionX *= 1.025d;
                Minecraft.thePlayer.motionZ *= 1.025d;


                Minecraft.thePlayer.motionY -= 0.01d;
            }

            if (mc.theWorld.getBlockState ( Minecraft.thePlayer.getPosition ( ) ).getBlock ( ) instanceof BlockAir) {
                Minecraft.thePlayer.motionX *= 1.04d;
                Minecraft.thePlayer.motionZ *= 1.04d;

                this.wasOnGround = false;
            }

        } else {
            this.wasOnGround = false;
        }
        super.onUpdate ( );
    }

}
