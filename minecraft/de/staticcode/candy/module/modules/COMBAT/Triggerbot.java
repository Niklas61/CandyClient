package de.staticcode.candy.module.modules.COMBAT;

import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import de.staticcode.candy.utils.Timings;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;

public class Triggerbot extends Module {

    public Triggerbot ( ) {
        super ( "Triggerbot" , Category.COMBAT );
    }

    private final Timings timings = new Timings ( );

    @Override
    public void onUpdate ( ) {
        if (mc.objectMouseOver != null && mc.objectMouseOver.entityHit != null) {
            if (this.timings.hasReached ( 100L )) {
                Minecraft.thePlayer.onCriticalHit ( mc.objectMouseOver.entityHit );
                Minecraft.thePlayer.swingItem ( );
                Minecraft.thePlayer.sendQueue
                        .addToSendQueue ( new C02PacketUseEntity ( mc.objectMouseOver.entityHit , Action.ATTACK ) );
                this.timings.resetTimings ( );
            }
        }
        super.onUpdate ( );
    }

}
