package de.staticcode.candy.module.modules.COMBAT;

import de.staticcode.candy.gui.components.GuiComponent;
import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import net.minecraft.client.Minecraft;

public class Criticals extends Module {

    private final GuiComponent strafeButton = new GuiComponent ( "Crit on Strafe" , this , false );

    public Criticals ( ) {
        super ( "Criticals" , Category.COMBAT );
    }

    @Override
    public void onUpdate ( ) {
        if (Killaura.underAttack != null && Minecraft.thePlayer.swingProgress > 0.0F) {

            if (!this.strafeButton.isToggled ( ) && ( Minecraft.thePlayer.moveStrafing > 0.5F || Minecraft.thePlayer.moveForward > 0.7F ))
                return;

            if (mc.gameSettings.keyBindJump.pressed)
                return;

            if (Minecraft.thePlayer.onGround)
                Minecraft.thePlayer.motionY = 0.3167426842375634D;
        }

        super.onUpdate ( );
    }
}
