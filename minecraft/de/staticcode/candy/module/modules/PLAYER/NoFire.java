package de.staticcode.candy.module.modules.PLAYER;

import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;

public class NoFire extends Module {
    public NoFire ( ) {
        super ( "NoFire" , Category.PLAYER );
    }

    @Override
    public void onUpdate ( ) {
        if (Minecraft.thePlayer.isBurning ( )) {
            Minecraft.thePlayer.setFire ( 0 );

            mc.gameSettings.keyBindSprint.pressed = false;
            Minecraft.thePlayer.setSprinting ( false );

            for ( int i = 0; i < 7; i++ )
                Minecraft.thePlayer.sendQueue.addToSendQueue ( new C03PacketPlayer ( true ) );
        }
        super.onUpdate ( );
    }

}
