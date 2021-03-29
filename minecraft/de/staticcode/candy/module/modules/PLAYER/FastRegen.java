package de.staticcode.candy.module.modules.PLAYER;

import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;

public class FastRegen extends Module {

    public FastRegen ( ) {
        super ( "FastRegen" , Category.PLAYER );
    }

    private boolean shouldHeal;

    @Override
    public void onUpdate ( ) {
        if (Minecraft.thePlayer.getHealth ( ) < Minecraft.thePlayer.getMaxHealth ( ) / 2F) {
            this.shouldHeal = true;
        }

        if (Minecraft.thePlayer.getHealth ( ) >= Minecraft.thePlayer.getMaxHealth ( )) {
            this.shouldHeal = false;
        }

        if (this.shouldHeal) {
            for ( int i = 0; i < 10; i++ )
                Minecraft.thePlayer.sendQueue.addToSendQueue ( new C03PacketPlayer ( ) );
        }

        super.onUpdate ( );
    }

}
