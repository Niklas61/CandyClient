package de.staticcode.candy.module.modules.RENDER;

import de.staticcode.candy.gui.components.GuiComponent;
import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;

public class ESP extends Module {

    private final GuiComponent guiComponent = new GuiComponent ( "ESP Mode" , this , Arrays.asList ( "Shine" , "Outline" ) , "Shine" );

    public ESP ( ) {
        super ( "ESP" , Keyboard.KEY_P , Category.RENDER );
    }

    @Override
    public void onUpdate ( ) {
        this.displayName = "ESP §7" + this.guiComponent.getActiveMode ( );
        super.onUpdate ( );
    }
}
