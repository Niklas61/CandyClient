package de.staticcode.candy.module.modules.PLAYER;

import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;

public class FastPlace extends Module {

    public FastPlace ( ) {
        super ( "FastPlace" , Category.PLAYER );
    }

    @Override
    public void onEnable ( ) {
        mc.rightClickDelayTimer = 0;
        super.onEnable ( );
    }

    @Override
    public void onUpdate ( ) {
        mc.rightClickDelayTimer = 0;
        super.onUpdate ( );
    }

    @Override
    public void onDisable ( ) {
        mc.rightClickDelayTimer = 6;
        super.onDisable ( );
    }

}
