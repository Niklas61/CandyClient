package de.staticcode.candy.module.modules.RENDER;

import de.staticcode.candy.Candy;
import de.staticcode.candy.friend.FriendManager;
import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;

public class MidClick extends Module {
    public MidClick ( ) {
        super ( "MidClick" , Category.RENDER );
    }

    @Override
    public void onUpdate ( ) {
        if (mc.objectMouseOver.entityHit != null) {
            if (Mouse.isButtonDown ( 2 )) {
                try {
                    if (!FriendManager.friends.contains ( mc.objectMouseOver.entityHit.getName ( ) )) {
                        FriendManager.friends.add ( mc.objectMouseOver.entityHit.getName ( ) );
                        Candy.sendChat ( "Friend " + mc.objectMouseOver.entityHit.getName ( ) + " has added!" );
                    } else {
                        FriendManager.friends.remove ( mc.objectMouseOver.entityHit.getName ( ) );
                        Candy.sendChat ( "Friend " + mc.objectMouseOver.entityHit.getName ( ) + " has remove!" );
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
