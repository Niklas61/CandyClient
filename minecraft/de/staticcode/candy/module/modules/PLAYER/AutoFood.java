package de.staticcode.candy.module.modules.PLAYER;

import de.staticcode.candy.gui.components.GuiComponent;
import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.network.play.client.C03PacketPlayer;

public class AutoFood extends Module {

    public AutoFood ( ) {
        super ( "AutoFood" , Category.PLAYER );
    }

    private final GuiComponent instantUseButton = new GuiComponent ( "Instant" , this , false );

    @Override
    public void onUpdate ( ) {
        if (Minecraft.thePlayer.getFoodStats ( ).getFoodLevel ( ) < 15F) {
            if (Minecraft.thePlayer.getHeldItem ( ) != null && Minecraft.thePlayer.getHeldItem ( ).getItem ( ).getCreativeTab ( ) != null
                    && Minecraft.thePlayer.getHeldItem ( ).getItem ( ).getCreativeTab ( ) == CreativeTabs.tabFood) {
                Minecraft.thePlayer.setItemInUse ( Minecraft.thePlayer.getHeldItem ( ) ,
                        Minecraft.thePlayer.getHeldItem ( ).getMaxItemUseDuration ( ) );

                if (this.instantUseButton.isToggled ( )) {
                    for ( int i = 0; i < 10; i++ ) {
                        Minecraft.thePlayer.sendQueue.addToSendQueue ( new C03PacketPlayer ( ) );
                    }
                    Minecraft.thePlayer.setItemInUse ( Minecraft.thePlayer.getHeldItem ( ) , 0 );
                }
            }
        }
        super.onUpdate ( );
    }

}
