package de.staticcode.candy.module.modules.MOVEMENT;

import de.staticcode.candy.gui.components.GuiComponent;
import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;

public class AirJumpFly extends Module {

    public AirJumpFly ( ) {
        super ( "AirJumpFly" , Category.MOVEMENT );
    }

    private double startY;
    private final GuiComponent boostSpeed = new GuiComponent ( "Boost Speed" , this , 10d , 1d , 1d );
    private final GuiComponent boostButton = new GuiComponent ( "Boost" , this , false );
    private final GuiComponent damageButton = new GuiComponent ( "Damage" , this , false );

    @Override
    public void onEnable ( ) {
        if (Minecraft.thePlayer.onGround)
            Minecraft.thePlayer.motionY = 0.42D;

        this.startY = Minecraft.thePlayer.posY - 1d;
        super.onEnable ( );
    }

    @Override
    public void onDisable ( ) {
        Minecraft.thePlayer.onGround = false;
        mc.timer.timerSpeed = 1.0F;
        super.onDisable ( );
    }

    @Override
    public void onUpdate ( ) {
        if (this.boostButton.isToggled ( ) && ( Minecraft.thePlayer.moveForward != 0.0F || Minecraft.thePlayer.moveStrafing != 0.0F )) {
            Minecraft.move ( Minecraft.thePlayer.rotationYaw , ( float ) ( this.boostSpeed.getCurrent ( ) / 10D ) );
        }

        if (this.startY >= Minecraft.thePlayer.posY) {
            mc.timer.timerSpeed = 0.5F;

            if (this.damageButton.isToggled ( )) {
                Minecraft.thePlayer.onGround = true;
                Minecraft.thePlayer.sendQueue.addToSendQueue ( new C03PacketPlayer.C04PacketPlayerPosition ( Minecraft.thePlayer.posX ,
                        Minecraft.thePlayer.posY + 2.2d , Minecraft.thePlayer.posZ , true ) );
                Minecraft.thePlayer.sendQueue.addToSendQueue ( new C03PacketPlayer.C04PacketPlayerPosition ( Minecraft.thePlayer.posX ,
                        Minecraft.thePlayer.posY , Minecraft.thePlayer.posZ , false ) );
            }

            Minecraft.thePlayer.onGround = true;
            mc.gameSettings.keyBindJump.pressed = true;
        } else {
            mc.timer.timerSpeed = 1.0F;
            Minecraft.thePlayer.onGround = false;
            mc.gameSettings.keyBindJump.pressed = false;
        }
        super.onUpdate ( );
    }

}
