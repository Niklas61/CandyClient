package de.staticcode.candy.module.modules.RENDER;

import de.staticcode.candy.gui.components.GuiComponent;
import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;

public class MainMenuScale extends Module {

    private final GuiComponent scaleSlider = new GuiComponent ( "Main Scale" , this , 10d , 1d , 5d );


    public MainMenuScale ( ) {
        super ( "MainMenuScale" , Category.RENDER );
    }

}
