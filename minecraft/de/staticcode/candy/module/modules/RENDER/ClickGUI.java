package de.staticcode.candy.module.modules.RENDER;

import de.staticcode.candy.gui.components.GuiComponent;
import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;

public class ClickGUI extends Module {

    private final GuiComponent surroundedButton = new GuiComponent ( "Surrounded" , this , false );
    private final GuiComponent rectButton2 = new GuiComponent ( "Rect2" , this , false );

    private final GuiComponent imageBackgroundButton = new GuiComponent ( "Image Background" , this , false );

    private final GuiComponent HotbarButton = new GuiComponent ( "Hotbar Info" , this , false );

    private final GuiComponent rotateImageButton = new GuiComponent ( "Rotate Image" , this , false );
    private final GuiComponent hudThem = new GuiComponent ( "Hud Theme" , this , Arrays.asList ( "Simple" , "Image" , "New" , "Compact" ) ,
            "Simple" );

    public ClickGUI ( ) {
        super ( "ClickGUI" , Keyboard.KEY_RSHIFT , Category.RENDER );
    }

    @Override
    public void onEnable ( ) {
        mc.displayGuiScreen ( new de.staticcode.candy.gui.ClickGUI ( ) );
        this.setToggled ( false );
        super.onEnable ( );
    }

}
