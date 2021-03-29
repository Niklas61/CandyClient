package de.staticcode.candy.module.modules.COMMANDS;

import de.staticcode.candy.Candy;
import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import org.lwjgl.input.Keyboard;

public class BindCommand extends Module {

    public BindCommand ( ) {
        super ( "Bind" , Category.COMMANDS );
    }

    @Override
    public void onCommand ( String[] args ) {

        if (args.length < 3) {
            Candy.sendChat ( ".bind <Module> <Key>" );
            return;
        }
        Module mod = Module.getByName ( args[ 1 ] );
        int keyCode = Keyboard.getKeyIndex ( args[ 2 ].toUpperCase ( ) );

        if (mod == null) {
            Candy.sendChat ( "Module " + args[ 1 ] + " not found!" );
            return;
        }
        mod.setKey ( keyCode );

        Candy.sendChat ( mod.getName ( ) + " was binded to " + args[ 2 ].toUpperCase ( ) );
        return;
    }

}
