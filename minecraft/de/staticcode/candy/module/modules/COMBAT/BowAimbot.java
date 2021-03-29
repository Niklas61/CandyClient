package de.staticcode.candy.module.modules.COMBAT;

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
import net.minecraft.init.Items;

public class BowAimbot extends Module {

    public BowAimbot ( ) {
        super ( "BowAimbot" , Category.COMBAT );
    }

    private final GuiComponent predictTime = new GuiComponent ( "Predict" , this , 20d , 1d , 12d );


    @Override
    public void onUpdate ( ) {

        if (!this.isToggled ( ))
            return;

        EntityPlayer target = getNearest ( );

        if (target == null)
            return;

        if (Minecraft.thePlayer.getCurrentEquippedItem ( ) == null
                || Minecraft.thePlayer.getCurrentEquippedItem ( ).getItem ( ) != Items.bow)
            return;

        if (Minecraft.thePlayer.getItemInUseDuration ( ) < 5)
            return;

        updateLook ( target );

        super.onUpdate ( );
    }

    public void updateLook ( Entity target ) {

        Location3D from = new Location3D ( Minecraft.thePlayer.posX , Minecraft.thePlayer.posY + 1.6 , Minecraft.thePlayer.posZ );
        Location3D toNormal = new Location3D ( target.posX , target.posY + 1 , target.posZ );

        double movesAmount = from.distance ( toNormal ) + this.predictTime.getCurrent ( );
        movesAmount /= 3;

        double xOffSet = ( target.posX - target.prevPosX ) * movesAmount;
        double zOffSet = ( target.posZ - target.prevPosZ ) * movesAmount;

        Location3D to = new Location3D ( toNormal.getX ( ) + xOffSet , toNormal.getY ( ) , toNormal.getZ ( ) + zOffSet );

        BlickWinkel3D bl = new BlickWinkel3D ( from , to );

        double bowUse = Minecraft.thePlayer.getItemInUseDuration ( );

        if (bowUse > 20)
            bowUse = 20;

        double pitchOffSet = ( Math.abs ( bl.getPitch ( ) ) - 90 ) * from.distance ( to ) / ( bowUse * 10 ) / 2.5;

        if (bl.getPitch ( ) < -90 || bl.getPitch ( ) > 90 || bl.getYaw ( ) > 999 || bl.getYaw ( ) < -999)
            return;

        RotationUtils.server_yaw = ( float ) bl.getYaw ( );
        RotationUtils.server_pitch = ( float ) ( bl.getPitch ( ) + pitchOffSet );
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
