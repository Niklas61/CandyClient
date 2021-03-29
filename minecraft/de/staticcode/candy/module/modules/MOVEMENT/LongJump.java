package de.staticcode.candy.module.modules.MOVEMENT;

import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import net.minecraft.client.Minecraft;

public class LongJump extends Module {

    public LongJump ( ) {
        super ( "LongJump" , Category.MOVEMENT );
    }

    @Override
    public void onDisable ( ) {
        Minecraft.thePlayer.motionX = 0.0d;
        Minecraft.thePlayer.motionZ = 0.0d;
        mc.timer.timerSpeed = 1.0F;
        super.onDisable ( );
    }

    private boolean isDoing = false;

    @Override
    public void onUpdate ( ) {
        if (mc.gameSettings.keyBindJump.pressed) {

            if (Minecraft.thePlayer.onGround) {
                Minecraft.thePlayer.motionY = 0.42d;
                Minecraft.move ( Minecraft.thePlayer.rotationYaw , 0.3f );
                isDoing = false;
                return;
            }

            mc.timer.timerSpeed = 0.55f;

            if (Math.abs ( Minecraft.thePlayer.motionY - 0.3332 ) < 0.0001) {
                Minecraft.thePlayer.motionY += 0.015f;
                isDoing = true;
            }

            if (!isDoing)
                return;

            if (Minecraft.thePlayer.motionY < 0.3f && Minecraft.thePlayer.motionY > 0.2) {
                Minecraft.thePlayer.motionX *= 4.05f;
                Minecraft.thePlayer.motionZ *= 4.05f;

            } else {
                Minecraft.thePlayer.motionX *= 1.05f;
                Minecraft.thePlayer.motionZ *= 1.05f;

            }


        } else {
            Minecraft.thePlayer.motionX = 0.0d;
            Minecraft.thePlayer.motionZ = 0.0d;
            mc.timer.timerSpeed = 1.0f;
        }
        super.onUpdate ( );
    }

}
