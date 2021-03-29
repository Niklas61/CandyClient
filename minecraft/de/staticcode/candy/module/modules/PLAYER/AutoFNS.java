package de.staticcode.candy.module.modules.PLAYER;

import de.staticcode.candy.friend.FriendManager;
import de.staticcode.candy.gui.components.GuiComponent;
import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import de.staticcode.candy.utils.Timings;
import de.staticcode.ui.Location3D;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;

public class AutoFNS extends Module {

    public AutoFNS ( ) {
        super ( "AutoFNS" , Category.PLAYER );
    }

    private final Timings timings = new Timings ( );

    @Override
    public void onUpdate ( ) {

        if (this.getNearest ( ) != null) {
            if (Minecraft.thePlayer.getHeldItem ( ) != null
                    && Minecraft.thePlayer.getHeldItem ( ).getItem ( ) instanceof ItemFlintAndSteel) {
                if (this.timings.hasReached ( 600L ) && this.getNearest ( ).onGround) {
                    Minecraft.thePlayer.sendQueue.addToSendQueue ( new C08PacketPlayerBlockPlacement (
                            new BlockPos ( this.getNearest ( ).posX , this.getNearest ( ).posY - 0.5d , this.getNearest ( ).posZ ) ,
                            1 , Minecraft.thePlayer.getHeldItem ( ) , 0f , 0f , 0f ) );
                    this.timings.resetTimings ( );
                }
            }
        }
        super.onUpdate ( );
    }

    private String getPrefix ( Entity e ) {
        return e.getDisplayName ( ).getFormattedText ( ).substring ( 1 , 2 ).replace ( "§" , "" );
    }

    private boolean checkEntity ( Entity e ) {

        if (!e.canAttackWithItem ( ))
            return false;

        if (Module.getByName ( "AntiBots" ).isToggled ( )) {
            if (GuiComponent.getByName ( "New Detection" ).isToggled ( )) {
                if (e.wasSprinting == e.isSprinting ( ))
                    return false;
            } else {
                if (!e.wasHurting)
                    return false;
            }
        }

        if (!Module.getByName ( "NoFriends" ).isToggled ( )) {
            if (FriendManager.friends.contains ( e.getName ( ) ))
                return false;
        }

        if (e == Minecraft.thePlayer)
            return false;

        if (e instanceof EntityLivingBase) {
            if (( ( EntityLivingBase ) e ).getHealth ( ) <= 0.0F) {
                return false;
            }
        }

        if (Module.getByName ( "Teams" ).isToggled ( )) {
            if (this.getPrefix ( Minecraft.thePlayer ).equalsIgnoreCase ( this.getPrefix ( e ) ))
                return false;

            return !Minecraft.thePlayer.isOnSameTeam ( ( ( EntityLivingBase ) e ) );
        }

        return true;
    }

    private EntityPlayer getNearest ( ) {
        Location3D p = new Location3D ( Minecraft.thePlayer.posX , Minecraft.thePlayer.posY , Minecraft.thePlayer.posZ );

        EntityPlayer best = null;
        double distance = 10d;

        for ( int i = 0; i < mc.theWorld.playerEntities.size ( ); i++ ) {
            EntityPlayer ep = mc.theWorld.playerEntities.get ( i );

            if (ep.equals ( Minecraft.thePlayer ))
                continue;

            if (ep.getName ( ).equalsIgnoreCase ( "§6Dealer" ))
                continue;

            if (!this.checkEntity ( ep ))
                continue;

            Location3D epl = new Location3D ( ep.posX , ep.posY , ep.posZ );
            double newDis = epl.distance ( p );
            if (newDis <= distance) {
                distance = newDis;
                best = ep;
            }
        }

        return best;
    }
}
