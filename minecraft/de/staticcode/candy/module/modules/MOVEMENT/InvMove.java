package de.staticcode.candy.module.modules.MOVEMENT;

import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import net.minecraft.client.gui.GuiChat;
import org.lwjgl.input.Keyboard;

public class InvMove extends Module {

    public InvMove ( ) {
        super ( "InvMove" , Category.MOVEMENT );
    }

    @Override
    public void onUpdate ( ) {
        if (this.isToggled ( )) {
            if (mc.currentScreen != null && !( mc.currentScreen instanceof GuiChat )) {
                mc.gameSettings.keyBindForward.pressed = Keyboard.isKeyDown ( mc.gameSettings.keyBindForward.getKeyCode ( ) );
                mc.gameSettings.keyBindBack.pressed = Keyboard.isKeyDown ( mc.gameSettings.keyBindBack.getKeyCode ( ) );
                mc.gameSettings.keyBindRight.pressed = Keyboard.isKeyDown ( mc.gameSettings.keyBindRight.getKeyCode ( ) );
                mc.gameSettings.keyBindLeft.pressed = Keyboard.isKeyDown ( mc.gameSettings.keyBindLeft.getKeyCode ( ) );
                mc.gameSettings.keyBindJump.pressed = Keyboard.isKeyDown ( mc.gameSettings.keyBindJump.getKeyCode ( ) );

            }
        }
        super.onUpdate ( );
    }

}
