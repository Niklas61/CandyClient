package de.staticcode.candy.module.modules.MOVEMENT;

import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import net.minecraft.client.Minecraft;

public class Glide extends Module {

    public Glide ( ) {
        super ( "Glide" , Category.MOVEMENT );
    }

    @Override
    public void onUpdate ( ) {
        Minecraft.thePlayer.motionY = -0.01d;
        super.onUpdate ( );
    }

}
