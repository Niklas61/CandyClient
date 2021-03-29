package de.staticcode.candy.module.modules.MOVEMENT;

import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import net.minecraft.client.Minecraft;

public class Fly extends Module {

    public Fly ( ) {
        super ( "Fly" , Category.MOVEMENT );
    }


    @Override
    public void onUpdate ( ) {
        Minecraft.thePlayer.capabilities.allowFlying = true;
        Minecraft.thePlayer.capabilities.isFlying = true;
        super.onUpdate ( );
    }

    @Override
    public void onDisable ( ) {
        Minecraft.thePlayer.capabilities.allowFlying = false;
        Minecraft.thePlayer.capabilities.isFlying = false;
        super.onDisable ( );
    }


}
