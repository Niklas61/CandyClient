package de.staticcode.candy.module.modules.COMMANDS;

import de.staticcode.candy.Candy;
import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;

public class NameCommand extends Module {

    public NameCommand ( ) {
        super ( "nickname" , Category.COMMANDS );
    }

    @Override
    public void onCommand ( String[] args ) {
        if (args.length < 2) {
            Candy.sendChat ( "Please enter a nickname." );
            return;
        }
        String nick = args[ 1 ];
        Candy.getIrcConfig ( ).setString ( "nickName" , nick );
        Candy.getIrcConfig ( ).save ( );
        Candy.sendChat ( "§eYour nickname is now §7" + nick );
        super.onCommand ( args );
    }

}
