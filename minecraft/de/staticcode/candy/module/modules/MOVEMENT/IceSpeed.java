package de.staticcode.candy.module.modules.MOVEMENT;

import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import net.minecraft.block.BlockIce;
import net.minecraft.block.BlockPackedIce;
import net.minecraft.client.Minecraft;

public class IceSpeed extends Module {

    public IceSpeed ( ) {
        super ( "IceSpeed" , Category.MOVEMENT );
    }

    private boolean wasBoosted;

    @Override
    public void onUpdate ( ) {
        if (mc.theWorld.getBlockState ( Minecraft.thePlayer.getPosition ( ).add ( 0.0d , -0.5d , 0.0d ) ).getBlock ( ) instanceof BlockIce
                || mc.theWorld.getBlockState ( Minecraft.thePlayer.getPosition ( ).add ( 0.0d , -0.5d , 0.0d ) )
                .getBlock ( ) instanceof BlockPackedIce) {

            if (!this.wasBoosted) {
                if (Minecraft.thePlayer.onGround)
                    Minecraft.thePlayer.motionY = 0.4d;
                this.wasBoosted = true;
            }

            if (Minecraft.thePlayer.onGround && this.wasBoosted) {
                Minecraft.thePlayer.distanceWalkedModified = 0.0F;
                Minecraft.portMove ( Minecraft.thePlayer.rotationYaw , 0.2f , 0.0f );

                Minecraft.thePlayer.motionX *= 1.1d;
                Minecraft.thePlayer.motionZ *= 1.1d;

                mc.timer.timerSpeed = 1f;
            }
        } else {
            if (this.wasBoosted) {
                if (Minecraft.thePlayer.onGround)
                    Minecraft.thePlayer.motionY = 0.4d;

                Minecraft.thePlayer.motionX = 0.0d;
                Minecraft.thePlayer.motionZ = 0.0d;

                mc.timer.timerSpeed = 1.0f;
                this.wasBoosted = false;
            }
        }
        super.onUpdate ( );
    }

}
