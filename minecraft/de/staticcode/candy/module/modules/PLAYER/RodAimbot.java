package de.staticcode.candy.module.modules.PLAYER;

import de.staticcode.candy.friend.FriendManager;
import de.staticcode.candy.gui.components.GuiComponent;
import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import de.staticcode.candy.utils.RotationUtils;
import de.staticcode.ui.BlickWinkel3D;
import de.staticcode.ui.Location3D;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFishingRod;

public class RodAimbot extends Module {

    public RodAimbot ( ) {
        super ( "RodAimbot" , Category.PLAYER );
    }

    @Override
    public void onUpdate ( ) {

        if (this.getNearest ( ) != null && Minecraft.thePlayer.getHeldItem ( ) != null
                && Minecraft.thePlayer.getHeldItem ( ).getItem ( ) instanceof ItemFishingRod) {
            Entity e = this.getNearest ( );
            if (Minecraft.thePlayer.getDistanceToEntity ( e ) > 50D || !this.checkEntity ( e ))
                return;
            Location3D start = new Location3D ( Minecraft.thePlayer.posX , Minecraft.thePlayer.posY + 1.6d , Minecraft.thePlayer.posZ );

            double x = e.posX - ( e.posX - e.prevPosX );
            double z = e.posZ - ( e.posZ - e.prevPosZ );
            double y = e.posY + ( e.height / 1.5F );
            y += Minecraft.thePlayer.motionY * 5D;


            Location3D end = new Location3D ( x , y , z );
            BlickWinkel3D blickWinkel3D = new BlickWinkel3D ( start , end );

            RotationUtils.server_yaw = ( float ) blickWinkel3D.getYaw ( );
            RotationUtils.server_pitch = ( float ) blickWinkel3D.getPitch ( );

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
        double distance = 50;

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
