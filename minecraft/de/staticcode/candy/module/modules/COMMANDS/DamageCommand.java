package de.staticcode.candy.module.modules.COMMANDS;

import de.staticcode.candy.Candy;
import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;

public class DamageCommand extends Module {

    public DamageCommand ( ) {
        super ( "Damage" , Category.COMMANDS );
    }

    @Override
    public void onCommand ( String[] args ) {
        if (args.length < 2) {
            Candy.sendChat ( "Please enter a valid value." );
            return;
        }
        try {
            double damage = Double.valueOf ( args[ 1 ] );
            damage += 2.2d;

            Minecraft.thePlayer.sendQueue.addToSendQueue ( new C03PacketPlayer.C04PacketPlayerPosition ( Minecraft.thePlayer.posX ,
                    Minecraft.thePlayer.posY + damage , Minecraft.thePlayer.posZ , false ) );
            Minecraft.thePlayer.sendQueue.addToSendQueue ( new C03PacketPlayer.C04PacketPlayerPosition ( Minecraft.thePlayer.posX ,
                    Minecraft.thePlayer.posY , Minecraft.thePlayer.posZ , false ) );
            Candy.sendChat ( "Damage succesfull!" );
        } catch ( Exception e ) {
            Candy.sendChat ( "§cDamage failed." );
        }
        super.onCommand ( args );
    }

}
