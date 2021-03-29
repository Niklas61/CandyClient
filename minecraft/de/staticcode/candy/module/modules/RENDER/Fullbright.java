package de.staticcode.candy.module.modules.RENDER;

import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;

public class Fullbright extends Module {

    public Fullbright ( ) {
        super ( "Fullbright" , Category.RENDER );
    }


    @Override
    public void onUpdate ( ) {
        mc.gameSettings.gammaSetting = 10F;
        super.onUpdate ( );
    }

    @Override
    public void onDisable ( ) {
        mc.gameSettings.gammaSetting = 1.0F;
        super.onDisable ( );
    }

}
