package de.staticcode.candy.module.modules.COMMANDS;

import de.staticcode.candy.Candy;
import de.staticcode.candy.friend.FriendManager;
import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;

public class FriendCommand extends Module {

    public FriendCommand ( ) {
        super ( "Friend" , Category.COMMANDS );
    }

    @Override
    public void onCommand ( String[] args ) {
        if (args.length < 3) {
            Candy.sendChat ( ".friend <Username> <Alias>" );
            return;
        }

        String username = args[ 1 ];
        String alias = args[ 2 ];

        if (!FriendManager.friends.contains ( username )) {
            Candy.sendChat ( "Friend has been added!" );
            FriendManager.friends.add ( username );
            FriendManager.aliases.put ( username , alias );
            return;
        } else {
            Candy.sendChat ( "Friend has been removed." );
            FriendManager.friends.remove ( username );
            FriendManager.aliases.remove ( username );
            return;
        }

    }

}
