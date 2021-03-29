package de.staticcode.candy.module.modules.MOVEMENT;

import de.staticcode.candy.gui.components.GuiComponent;
import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;

import java.util.Arrays;

public class NoSlowdown extends Module {

    private final GuiComponent guiComponent = new GuiComponent ( "Slowdown Mode" , this , Arrays.asList ( "Normal" , "AAC" ) , "Normal" );

    public NoSlowdown ( ) {
        super ( "NoSlowdown" , Category.MOVEMENT );
    }

    @Override
    public void onUpdate ( ) {
        this.displayName = "NoSlowDown §a" + this.guiComponent.getActiveMode ( );
        super.onUpdate ( );
    }
}
