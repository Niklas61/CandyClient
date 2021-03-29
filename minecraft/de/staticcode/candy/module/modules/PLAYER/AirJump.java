package de.staticcode.candy.module.modules.PLAYER;

import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import net.minecraft.client.Minecraft;

public class AirJump extends Module {

    public AirJump ( ) {
        super ( "AirJump" , Category.PLAYER );
    }

    @Override
    public void onUpdate ( ) {
        Minecraft.thePlayer.onGround = true;
        super.onUpdate ( );
    }

    @Override
    public void onDisable ( ) {
        Minecraft.thePlayer.onGround = false;
        super.onDisable ( );
    }

}
