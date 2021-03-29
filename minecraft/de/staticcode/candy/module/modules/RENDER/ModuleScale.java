package de.staticcode.candy.module.modules.RENDER;

import de.staticcode.candy.gui.components.GuiComponent;
import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;

public class ModuleScale extends Module {

    private final GuiComponent scaleSlider = new GuiComponent ( "Scale" , this , 2.2d , 1d , 1d );

    public ModuleScale ( ) {
        super ( "ModuleScale" , Category.RENDER );
    }

}
