package de.staticcode.candy.module.modules.MOVEMENT;

import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import de.staticcode.candy.utils.Timings;
import net.minecraft.client.Minecraft;

public class RedeskyFly extends Module {


    private final Timings timings = new Timings ( );
    private float startSpeed;

    public RedeskyFly ( ) {
        super ( "RedeskyFly" , Category.MOVEMENT );
    }

    @Override
    public void onEnable ( ) {
        this.startSpeed = 0.123281273F;
        this.timings.resetTimings ( );
        super.onEnable ( );
    }

    @Override
    public void onUpdate ( ) {

        if (!mc.gameSettings.keyBindForward.pressed)
            return;

        if (Minecraft.thePlayer.onGround) {
            Minecraft.thePlayer.motionY = 0.4D;
        } else {
            this.startSpeed += 0.08342456342F;
            float rotationYaw = Minecraft.thePlayer.rotationYaw;

            Minecraft.move ( rotationYaw , this.startSpeed );
            Minecraft.thePlayer.motionY = 0.0243242D;
        }

        super.onUpdate ( );
    }
}
