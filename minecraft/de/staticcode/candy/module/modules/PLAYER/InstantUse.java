package de.staticcode.candy.module.modules.PLAYER;

import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C03PacketPlayer;

public class InstantUse extends Module {

    public InstantUse ( ) {
        super ( "InstantUse" , Category.PLAYER );
    }

    @Override
    public void onUpdate ( ) {

        if (Minecraft.thePlayer.getCurrentEquippedItem ( ) != null && ( Minecraft.thePlayer.getCurrentEquippedItem ( ).getItem ( ) instanceof ItemBow || Minecraft.thePlayer.getCurrentEquippedItem ( ).getItem ( ) instanceof ItemSword ))
            return;

        if (Minecraft.thePlayer.isUsingItem ( )) {

            for ( int i = 0; i < 10; i++ ) {
                Minecraft.thePlayer.sendQueue.addToSendQueue ( new C03PacketPlayer ( ) );
            }
        }
        super.onUpdate ( );
    }

}
