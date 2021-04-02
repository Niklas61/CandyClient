package de.staticcode.candy.module.modules.COMBAT;

import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import de.staticcode.ui.BlickWinkel3D;
import de.staticcode.ui.Location3D;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldSettings;

public class SmoothAim extends Module {

    protected static SmoothAim smoothAim;
    protected EntityLivingBase triggeredEntity;
    private boolean triggered;

    public SmoothAim ( ) {
        super ( "SmoothAim" , Category.COMBAT );
        smoothAim = this;
    }

    public static SmoothAim getSmoothAim ( ) {
        return smoothAim;
    }

    @Override
    public void onEnable ( ) {
        this.releaseTrigger ( );
        super.onEnable ( );
    }

    @Override
    public void onUpdate ( ) {
        if (mc.thePlayer.isSwingInProgress) {
            if (!this.isTriggered ( )) {
                EntityLivingBase closetEntity = this.getCloset ( );
                if (this.getCloset ( ) != null)
                    this.onTrigger ( closetEntity );
            } else {
                BlickWinkel3D blickWinkel3D = this.getRotationsToEntity ( this.getTriggeredEntity ( ) );

                final float yawDist = Math.abs ( MathHelper.wrapAngleTo180_float ( mc.thePlayer.rotationYaw ) - MathHelper.wrapAngleTo180_float ( ( float ) blickWinkel3D.getYaw ( ) ) );
                final float pitchDist = Math.abs ( mc.thePlayer.rotationPitch - ( float ) blickWinkel3D.getPitch ( ) );

                mc.thePlayer.rotationYaw = ( float ) ( blickWinkel3D.getYaw ( ) - ( yawDist / 2F ) );
                mc.thePlayer.rotationPitch = ( float ) ( blickWinkel3D.getPitch ( ) - ( pitchDist / 2F ) );
                this.releaseTrigger ( );
            }
        }

    }

    private EntityLivingBase getCloset ( ) {
        EntityLivingBase closetEntity = null;
        double closetRotation = 90D;

        for ( Entity entities : mc.theWorld.loadedEntityList ) {

            if (entities.isDead)
                continue;

            if (entities.isInvisible ( ))
                continue;

            if (entities instanceof EntityLivingBase) {
                EntityLivingBase entityLivingBase = ( EntityLivingBase ) entities;
                final double distanceToPlayer = entityLivingBase.getDistance ( mc.thePlayer.posX , mc.thePlayer.posY , mc.thePlayer.posZ );

                if (distanceToPlayer > ( mc.playerController.getCurrentGameType ( ) == WorldSettings.GameType.CREATIVE ? 6D : 4.3D ))
                    continue;

                final BlickWinkel3D blickWinkel3D = this.getRotationsToEntity ( entityLivingBase );
                final float playerYaw = MathHelper.wrapAngleTo180_float ( mc.thePlayer.rotationYaw );
                final float entityYaw = MathHelper.wrapAngleTo180_float ( ( float ) blickWinkel3D.getYaw ( ) );

                final float entityPitch = ( float ) blickWinkel3D.getPitch ( );
                final float playerPitch = mc.thePlayer.rotationPitch;

                final float distYaw = Math.abs ( playerYaw - entityYaw );
                final float distPitch = Math.abs ( playerPitch - entityPitch );

                final double rotationDist = Math.sqrt ( distYaw * distYaw + distPitch * distPitch );

                if (rotationDist < closetRotation) {
                    closetEntity = entityLivingBase;
                    closetRotation = rotationDist;
                }
            }
        }

        return closetEntity;
    }

    private BlickWinkel3D getRotationsToEntity ( EntityLivingBase entityLivingBase ) {
        Location3D playerLoc = new Location3D ( mc.thePlayer.posX , mc.thePlayer.posY + mc.thePlayer.getEyeHeight ( ) , mc.thePlayer.posZ );
        Location3D entityLoc = new Location3D ( entityLivingBase.posX , entityLivingBase.posY + entityLivingBase.getEyeHeight ( ) / 2 , entityLivingBase.posZ );
        return new BlickWinkel3D ( playerLoc , entityLoc );
    }

    public void onTrigger ( EntityLivingBase entityLivingBase ) {
        this.setTriggered ( true );
        this.setTriggeredEntity ( entityLivingBase );
    }

    public void releaseTrigger ( ) {
        this.setTriggered ( false );
        this.setTriggeredEntity ( null );
    }

    protected EntityLivingBase getTriggeredEntity ( ) {
        return triggeredEntity;
    }

    protected void setTriggeredEntity ( EntityLivingBase triggeredEntity ) {
        this.triggeredEntity = triggeredEntity;
    }

    public boolean isTriggered ( ) {
        return triggered;
    }

    public void setTriggered ( boolean triggered ) {
        this.triggered = triggered;
    }
}
