package de.staticcode.candy.module.modules.PLAYER;

import de.staticcode.candy.Candy;
import de.staticcode.candy.friend.FriendManager;
import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;

public class MidClick extends Module {
    public MidClick ( ) {
        super ( "MidClick" , Category.PLAYER );
    }

    @Override
    public void onUpdate ( ) {
        if (mc.objectMouseOver != null && mc.objectMouseOver.entityHit != null) {
            if (Mouse.isButtonDown ( 2 )) {
                try {
                    if (!FriendManager.friends.contains ( mc.objectMouseOver.entityHit.getName ( ) )) {
                        FriendManager.friends.add ( mc.objectMouseOver.entityHit.getName ( ) );
                        Candy.sendChat ( "Friend " + mc.objectMouseOver.entityHit.getName ( ) + " has benn added!" );
                    } else {
                        FriendManager.friends.remove ( mc.objectMouseOver.entityHit.getName ( ) );
                        Candy.sendChat ( "Friend " + mc.objectMouseOver.entityHit.getName ( ) + " has been remove!" );
                    }
                    Mouse.destroy ( );
                    Mouse.create ( );
                } catch ( LWJGLException e ) {
                    e.printStackTrace ( );
                }
            }
        }
        super.onUpdate ( );
    }
}
