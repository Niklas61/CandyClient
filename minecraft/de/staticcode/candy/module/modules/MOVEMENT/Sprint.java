package de.staticcode.candy.module.modules.MOVEMENT;

import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import org.lwjgl.input.Keyboard;

public class Sprint extends Module {

    public Sprint ( ) {
        super ( "Sprint" , Keyboard.KEY_G , Category.MOVEMENT );
    }

    @Override
    public void onDisable ( ) {
        mc.gameSettings.keyBindSprint.pressed = false;
        super.onDisable ( );
    }

    @Override
    public void onUpdate ( ) {
        mc.gameSettings.keyBindSprint.pressed = true;
        super.onUpdate ( );
    }

}
