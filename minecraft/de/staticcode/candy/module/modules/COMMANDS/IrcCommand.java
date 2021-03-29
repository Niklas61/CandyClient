package de.staticcode.candy.module.modules.COMMANDS;

import de.staticcode.candy.Candy;
import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;

public class IrcCommand extends Module {

    public IrcCommand ( ) {
        super ( "irc" , Category.COMMANDS );
    }

    @Override
    public void onCommand ( String[] args ) {
        if (args.length < 2) {
            Candy.sendChat ( "Please enter a message" );
            return;
        }

        StringBuilder stringBuilder = new StringBuilder ( );
        boolean asMessage = false;

        for ( int i = 1; i < args.length; i++ ) {
            if (args[ i ].equalsIgnoreCase ( "c" )) {
                asMessage = true;
                continue;
            }

            if (i != args.length)
                stringBuilder.append ( args[ i ] + " " );
            else
                stringBuilder.append ( args[ i ] );
        }
        String message = stringBuilder.toString ( );

        if (stringBuilder.toString ( ).length ( ) < 1) {
            Candy.sendChat ( "Please enter a message" );
            return;
        }
            
        if (!Candy.getIrcHandler ( ).executeCommand ( message , !asMessage )) {
            Candy.sendChat ( "§cIrc servers are currently offline." );
        }
        return;

    }
}
