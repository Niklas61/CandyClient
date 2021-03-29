package de.staticcode.candy.module.modules.PLAYER;

import de.staticcode.candy.gui.components.GuiComponent;
import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;

public class NoRotation extends Module {

    private final GuiComponent setBackButton = new GuiComponent ( "SetBack" , this , false );
    private final GuiComponent setBackSlider = new GuiComponent ( "Set Back Ticks" , this , 10d , 1d , 1d );

    public NoRotation ( ) {
        super ( "NoRotation" , Category.PLAYER );
    }

}
