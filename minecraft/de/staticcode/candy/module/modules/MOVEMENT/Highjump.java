package de.staticcode.candy.module.modules.MOVEMENT;

import de.staticcode.candy.Candy;
import de.staticcode.candy.gui.components.GuiComponent;
import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import net.minecraft.client.Minecraft;

import java.util.Arrays;

public class Highjump extends Module {

    public GuiComponent mode = null;

    public Highjump ( ) {
        super ( "Highjump" , -1 , Category.MOVEMENT );

        mode = new GuiComponent ( "HighJump Mode" , this , Arrays.asList ( "normal" , "aac317" , "aac321" ) , "normal" );
    }

    @Override
    public void onUpdate ( ) {

        if (this.mode.getActiveMode ( ).equalsIgnoreCase ( "aac317" )) {
            this.onAAC317 ( );
        } else if (this.mode.getActiveMode ( ).equalsIgnoreCase ( "aac321" )) {
            this.onAAC321 ( );
        } else {
            if (mc.gameSettings.keyBindJump.isKeyDown ( )
                    && Math.abs ( Minecraft.thePlayer.motionY - 0.33319999363422365 ) < 0.000001)
                Minecraft.thePlayer.motionY = 1.0f;

        }

        super.onUpdate ( );
    }

    GuiComponent aac321Slider = new GuiComponent ( "AAC 3.2.1 Height" , this , 4.0 , 0.42f , 2.0f );

    int state = 0;
    double boost = 0f;
    double startY = 0f;

    public void onAAC321 ( ) {
        if (state > 6 && Minecraft.thePlayer.posY < startY) {
            state = 0;
            Candy.sendChat ( "�cHighjump failed?" );
            return;
        }

        if (Minecraft.thePlayer.isSneaking ( ) && state == 0 && Minecraft.thePlayer.onGround) {
            state = 1;

            boost = aac321Slider.getCurrent ( );

            startY = Minecraft.thePlayer.posY + boost;
        }

        if (state > 0) {
            state++;
            if (boost <= 0) {
                state = 0;
                return;
            }

            Minecraft.thePlayer.motionY = boost;
            boost = ( boost / 1.02f ) - 0.08f;
        }
    }

    @Override
    public void onEnable ( ) {
        doing = false;

        if (this.mode.getActiveMode ( ).equalsIgnoreCase ( "aac317" )) {
            Minecraft.thePlayer.motionY = 1.0d;
            doing = true;

            Minecraft.thePlayer.motionX *= 2.45f;
            Minecraft.thePlayer.motionZ *= 2.45f;
        }

        super.onEnable ( );
    }

    boolean doing = false;

    public void onAAC317 ( ) {

        if (Minecraft.thePlayer.onGround && Minecraft.thePlayer.motionY < 0) {
            doing = false;
            return;
        }

        if (!doing)
            return;

        if (Minecraft.thePlayer.motionY < 1) {
            Minecraft.thePlayer.motionY += 0.00999998f;

            Minecraft.thePlayer.motionX *= 1.0062f;
            Minecraft.thePlayer.motionZ *= 1.0062f;
        }
    }
}