package de.staticcode.candy.module.modules.MOVEMENT;

import de.staticcode.candy.Candy;
import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import net.minecraft.client.Minecraft;

public class NoLadder extends Module {

    public NoLadder ( ) {
        super ( "NoLadder" , Category.MOVEMENT );
    }

    private boolean lastBoosted = true;
    private float timeOnLadder;

    @Override
    public void onUpdate ( ) {
        if (Minecraft.thePlayer.isOnLadder ( )) {
            this.timeOnLadder++;

            Minecraft.thePlayer.setPosition ( Minecraft.thePlayer.posX , Minecraft.thePlayer.posY , Minecraft.thePlayer.posZ );

            if (this.timeOnLadder > 70F) {
                Minecraft.thePlayer.motionY = 0.15d;
                return;
            }

            Minecraft.thePlayer.motionY = 0.17D;
            this.lastBoosted = true;

        } else {

            if (this.lastBoosted) {
                Candy.sendChat ( "Done in " + ( this.timeOnLadder / 20F ) + " seconds." );
                this.lastBoosted = false;
            }

            this.timeOnLadder = 0.0F;
        }
        super.onUpdate ( );
    }

}
