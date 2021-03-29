package de.staticcode.candy.module.modules.COMMANDS;

import de.staticcode.candy.Candy;
import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;

public class GuiScaleCommand extends Module {

    public GuiScaleCommand ( ) {
        super ( "guiscale" , Category.COMMANDS );
    }

    @Override
    public void onCommand ( String[] args ) {
        if (args.length < 2) {
            Candy.sendChat ( "Please enter a scale." );
            return;
        }

        try {
            int guiScale = Integer.parseInt ( args[ 1 ] );

            if (guiScale > 4) {
                Candy.sendChat ( "Scale " + guiScale + " is to big!" );
                return;
            } else if (guiScale < 1) {
                Candy.sendChat ( "You minimum need scale 1!" );
                return;
            }

            Candy.sendChat ( "Scale set to " + guiScale );

            if (guiScale == 4)
                guiScale = 0;

            mc.gameSettings.guiScale = guiScale;
        } catch ( NumberFormatException exception ) {
            Candy.sendChat ( "Please enter a valid value" );
        }

    }

}
