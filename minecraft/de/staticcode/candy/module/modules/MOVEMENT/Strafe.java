package de.staticcode.candy.module.modules.MOVEMENT;

import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import net.minecraft.client.Minecraft;

public class Strafe extends Module {

    public Strafe ( ) {
        super ( "Strafe" , Category.MOVEMENT );
    }

    @Override
    public void onUpdate ( ) {

        if (getByName ( "Speed" ).isToggled ( ))
            return;
        float moveYaw = Minecraft.thePlayer.rotationYaw;

        if (mc.gameSettings.keyBindLeft.pressed) {
            moveYaw -= 90F;
        }

        if (mc.gameSettings.keyBindRight.pressed) {
            moveYaw += 90F;
        }

        if (mc.gameSettings.keyBindBack.pressed) {
            moveYaw -= 180F;
            moveYaw = moveYaw / 2F + ( Minecraft.thePlayer.rotationYaw - 180 ) / 2F;
        }

        if (mc.gameSettings.keyBindForward.pressed) {
            moveYaw = moveYaw / 2F + Minecraft.thePlayer.rotationYaw / 2F;
        }

        if (Minecraft.thePlayer.moveForward != 0.0F || Minecraft.thePlayer.moveStrafing != 0.0F) {
            if (!Minecraft.thePlayer.onGround) {
                Minecraft.move ( moveYaw , 0.256F );
            } else {
                Minecraft.move ( moveYaw , 0.15F );
            }
        }

        super.onUpdate ( );
    }

}
