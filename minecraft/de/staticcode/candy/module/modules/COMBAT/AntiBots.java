package de.staticcode.candy.module.modules.COMBAT;

import de.staticcode.candy.gui.components.GuiComponent;
import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;

public class AntiBots extends Module {

    private final GuiComponent newDetectionButton = new GuiComponent ( "New Detection" , this , false );
    private final GuiComponent swingDetectionButton = new GuiComponent ( "Swing Detection" , this , false );
    private final GuiComponent ultraDetection = new GuiComponent ( "Ultra Detection" , this , false );

    public AntiBots ( ) {
        super ( "AntiBots" , Category.COMBAT );
    }
}
