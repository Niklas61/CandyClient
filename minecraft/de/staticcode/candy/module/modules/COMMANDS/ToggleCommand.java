package de.staticcode.candy.module.modules.COMMANDS;

import de.staticcode.candy.Candy;
import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;

public class ToggleCommand extends Module {

    public ToggleCommand ( ) {
        super ( "T" , Category.COMMANDS );
    }

    @Override
    public void onCommand ( String[] args ) {
        if (args.length < 2) {
            Candy.sendChat ( ".t <Module>" );
            return;
        }
        String module = args[ 1 ];
        if (Module.getByName ( module ) != null) {
            Module.getByName ( module ).setToggled ( !Module.getByName ( module ).isToggled ( ) );
            Candy.sendChat ( "Module " + Module.getByName ( module ).getName ( ) + " was "
                    + ( Module.getByName ( module ).isToggled ( ) ? "§aactivated" : "§cdeactivated" ) );
        } else {
            Candy.sendChat ( "§cModule " + module + " was not found!" );
        }
        super.onCommand ( args );
    }

}
