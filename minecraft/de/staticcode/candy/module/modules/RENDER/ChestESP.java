package de.staticcode.candy.module.modules.RENDER;

import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import de.staticcode.candy.utils.RenderUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;

public class ChestESP extends Module {

    public ChestESP ( ) {
        super ( "ChestESP" , Category.RENDER );
    }


    @Override
    public void onDisable ( ) {
        RenderUtils.shouldRender = false;
        RenderUtils.clearAllWithID ( 54 );
//        RenderUtils.clearAllWithID ( 146 );
        super.onDisable ( );
    }

    @Override
    public void onUpdate ( ) {
        for ( TileEntity e : mc.theWorld.loadedTileEntityList ) {
            if (e instanceof TileEntityChest) {
                RenderUtils.shouldRender = true;
                if (!RenderUtils.renderList.containsKey ( e.getPos ( ) )) {
                    RenderUtils.renderList.put ( e.getPos ( ) , 54 );
//                    RenderUtils.renderList.put ( e.getPos ( ) , 146 );
                }
            }
        }

        super.onUpdate ( );
    }

}
