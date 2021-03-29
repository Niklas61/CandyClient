package de.staticcode.candy.module.modules.RENDER;

import de.staticcode.candy.gui.components.GuiComponent;
import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;

public class ItemPhysics extends Module {

    private final GuiComponent rotateX = new GuiComponent ( "Rotate X" , this , true );
    private final GuiComponent rotateY = new GuiComponent ( "Rotate Y" , this , true );
    private final GuiComponent rotateZ = new GuiComponent ( "Rotate Z" , this , true );

    public ItemPhysics ( ) {
        super ( "ItemPhysics" , Category.RENDER );
    }

}
