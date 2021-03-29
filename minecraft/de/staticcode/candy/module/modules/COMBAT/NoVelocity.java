package de.staticcode.candy.module.modules.COMBAT;

import de.staticcode.candy.gui.components.GuiComponent;
import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;

public class NoVelocity extends Module {

    private final GuiComponent percentVelocitySlider = new GuiComponent ( "Percent" , this , 100d , 1d , 100d );
    private final GuiComponent gommeButton = new GuiComponent ( "Gomme" , this , false );
    private final GuiComponent jumpButton = new GuiComponent ( "Only Jump" , this , false );
    private final GuiComponent aac4 = new GuiComponent ( "AAC4" , this , false );

    public NoVelocity ( ) {
        super ( "NoVelocity" , Category.COMBAT );
    }

}
