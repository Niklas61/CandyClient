package de.staticcode.candy.module.modules.MOVEMENT;

import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import net.minecraft.block.BlockSlime;
import net.minecraft.client.Minecraft;

public class SlimeJump extends Module {

    public SlimeJump ( ) {
        super ( "SlimeJump" , Category.MOVEMENT );
    }

    private final boolean hasJumped = false;

    @Override
    public void onUpdate ( ) {

        if (mc.theWorld.getBlockState ( Minecraft.thePlayer.getPosition ( ).add ( 0.0d , -0.5d , 0.0d ) )
                .getBlock ( ) instanceof BlockSlime) {
            Minecraft.thePlayer.motionY = Math.random ( ) * 1.2d;
        }
        super.onUpdate ( );
    }

}
