package de.staticcode.candy.module.modules.COMBAT;


import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import de.staticcode.candy.friend.FriendManager;
import de.staticcode.candy.gui.components.GuiComponent;
import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import de.staticcode.candy.module.modules.PLAYER.AutoPotion;
import de.staticcode.candy.utils.RotationUtils;
import de.staticcode.candy.utils.Timings;
import de.staticcode.ui.BlickWinkel3D;
import de.staticcode.ui.Location3D;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Killaura extends Module {


    private static long attackTime;
    public static boolean isTurnOffRotation;
    public static EntityLivingBase underAttack;
    static Killaura killaura;

    private final Raycast raycast = new Raycast ( );

    public static float previousYaw;
    public static float previousPitch;
    private boolean doneRotatedYaw;
    private boolean doneRotatedPitch;
    private boolean isBlocking;

    private final Timings timings = new Timings ( );
    private final Timings hitAnimaton = new Timings ( );

    private long delaySwitch = 0L;
    private boolean overPitched;

    private Entity lastEntity;

    private boolean sniperCursered;
    private final Timings sniperTimings = new Timings ( );

    private final GuiComponent preAimSlider = new GuiComponent ( "Pre-Aim Range" , this , 2.2D , 0.1D , 0.1D );
    private final GuiComponent rangeSlider = new GuiComponent ( "Attack Reach" , this , 8D , 1D , 1D );
    private final GuiComponent delaySlider = new GuiComponent ( "Ticks" , this , 700D , 10D , 80D );
    private final GuiComponent autoblockMode = new GuiComponent ( "AutoBlock Mode" , this , Arrays.asList ( "None" , "Smart-Block" , "Block always" ) , "None" );
    private final GuiComponent movementMode = new GuiComponent ( "Movement Mode" , this , Arrays.asList ( "Normal" , "Server-Side" , "Client-Side" ) , "Normal" );
    private final GuiComponent hurtTimeButton = new GuiComponent ( "Hurttime" , this , false );
    private final GuiComponent rayTraceButton = new GuiComponent ( "Raytrace" , this , false );
    private final GuiComponent preAimButton = new GuiComponent ( "Pre-Aim" , this , false );
    private final GuiComponent smoothHitButton = new GuiComponent ( "Smooth-Hits" , this , false );
    private final GuiComponent smoothAimButton = new GuiComponent ( "Smooth-Aim" , this , false );
    private final GuiComponent autoMissButton = new GuiComponent ( "Auto-Miss" , this , false );
    private final GuiComponent smoothRotationsButton = new GuiComponent ( "Smooth-Rotations" , this , false );
    private final GuiComponent throughWallsButton = new GuiComponent ( "Through Walls" , this , false );
    private final GuiComponent sniperCursorButton = new GuiComponent ( "Sniper-Cursor" , this , false );
    private final GuiComponent particlesButton = new GuiComponent ( "Particles" , this , false );
    private final GuiComponent targetInfoButton = new GuiComponent ( "TargetInfo" , this , false );
    private final GuiComponent slowHitAnimation = new GuiComponent ( "Slow-Hitanimation" , this , false );

    public Killaura ( ) {
        super ( "Killaura" , Category.COMBAT );
        killaura = this;
    }

    public static long getAttackTime ( ) {
        return attackTime;
    }

    @Override
    public void onUpdate ( ) {

        if (underAttack == null)
            this.displayName = "Killaura §a(None)";
        else
            this.displayName = "Killaura §a(" + Math.round ( underAttack.getDistanceToEntity ( Minecraft.thePlayer ) * 10D ) / 10D + ")";

        if (Minecraft.thePlayer == null)
            return;

        if (Minecraft.thePlayer.ticksExisted < 50)
            return;

        if (Minecraft.thePlayer.posX == Double.NaN || Minecraft.thePlayer.posZ == Double.NaN || Minecraft.thePlayer.posY == Double.NaN)
            return;

        if (mc.currentScreen != null)
            return;

        EntityLivingBase entityLivingBase = this.getNearest ( );

        if (entityLivingBase != null) {
            underAttack = entityLivingBase;
            this.sendLooks ( entityLivingBase );
        } else {
            underAttack = null;
            this.delaySwitch = 0L;
            this.lastEntity = null;
            this.hitAnimaton.resetTimings ( );
            this.sniperTimings.resetTimings ( );
            attackTime = 0L;

            if (this.smoothRotationsButton.isToggled ( )) {
                this.updateTurnoffRotation ( );
            }

            if (this.isBlocking) {
                this.setUnblock ( );
            }

            ItemRenderer.isBlocking = false;
        }
        super.onUpdate ( );
    }

    @Override
    public void onEnable ( ) {
        previousYaw = Minecraft.thePlayer.rotationYaw;
        previousPitch = Minecraft.thePlayer.rotationPitch;
        this.lastEntity = null;
        isTurnOffRotation = false;
        this.doneRotatedYaw = false;
        this.doneRotatedPitch = false;
        this.timings.resetTimings ( );
        this.hitAnimaton.resetTimings ( );
        attackTime = 0L;
        super.onEnable ( );
    }

    public void updateTurnoffRotation ( ) {
        isTurnOffRotation = true;

        float yaw = this.computeNextYaw ( null , RotationUtils.server_yaw , previousYaw , Minecraft.thePlayer.rotationYaw , Minecraft.thePlayer.rotationYaw );
        float pitch = this.computeNextPitch ( null , RotationUtils.server_pitch , previousPitch , Minecraft.thePlayer.rotationPitch );

        if (this.doneRotatedYaw && this.doneRotatedPitch) {
            this.doneRotatedYaw = false;
            this.doneRotatedPitch = false;
            isTurnOffRotation = false;
        }

        RotationUtils.server_yaw = yaw;
        RotationUtils.server_pitch = pitch;

        previousYaw = yaw;
        previousPitch = pitch;
    }

    private void sendLooks ( EntityLivingBase entityLivingBase ) {

        float[] rotations = this.getRotations ( entityLivingBase );
        float yaw = rotations[ 0 ];
        float pitch = rotations[ 1 ];

        //Check if pitch is over 90 Degress
        this.checkOverpitch ( pitch );

        if (this.overPitched) {
            if (pitch > -90F)
                pitch = -90F;

            if (pitch < 90F)
                pitch = 90F;
        }

        if (this.smoothRotationsButton.isToggled ( )) {
            yaw = this.computeNextYaw ( entityLivingBase , RotationUtils.server_yaw , previousYaw , yaw , rotations[ 0 ] );
            pitch = this.computeNextPitch ( entityLivingBase , RotationUtils.server_pitch , previousPitch , pitch );
        }

        RotationUtils.server_yaw = yaw;

        if (!AutoPotion.isFakeRotations ( ))
            RotationUtils.server_pitch = pitch;

        if (this.canHit ( entityLivingBase )) {
            if (!this.smoothHit ( entityLivingBase ))
                this.attackEntity ( entityLivingBase );
        }

        if (this.isBlocking && this.checkDistance ( entityLivingBase , this.rangeSlider.getCurrent ( ) ))
            this.setUnblock ( );

        previousYaw = yaw;
        previousPitch = pitch;
    }

    private double getModifiedRotSpeed ( EntityLivingBase entityLivingBase ) {
        final double lastTargetX = entityLivingBase.lastTickPosX;
        final double currentTargetX = entityLivingBase.posX;
        final double distTargetX = Math.abs ( lastTargetX - currentTargetX );

        final double lastTargetZ = entityLivingBase.lastTickPosZ;
        final double currentTargetZ = entityLivingBase.posZ;
        final double distTargetZ = Math.abs ( lastTargetZ - currentTargetZ );

        final double distTarget = Math.sqrt ( distTargetX * distTargetX + distTargetZ * distTargetZ );

        final double lastPlayerX = mc.thePlayer.lastTickPosX;
        final double currentPlayerX = mc.thePlayer.posX;
        final double distPlayerX = Math.abs ( lastPlayerX - currentPlayerX );

        final double lastPlayerZ = mc.thePlayer.lastTickPosZ;
        final double currentPlayerZ = mc.thePlayer.posZ;
        final double distPlayerZ = Math.abs ( lastPlayerZ - currentPlayerZ );

        final double distPlayer = Math.sqrt ( distPlayerX * distPlayerX + distPlayerZ * distPlayerZ );

        final double rotationSpeed = ( distTarget + distPlayer ) / 1.5D;

        return rotationSpeed;
    }


    private void checkOverpitch ( float pitch ) {
        float correctly = Math.abs ( pitch );
        if (correctly > 90F) {
            this.overPitched = true;
        } else if (correctly <= 90F) {
            this.overPitched = false;
        }
    }

    @Override
    public void onDisable ( ) {
        if (this.smoothRotationsButton.isToggled ( ))
            this.updateTurnoffRotation ( );

        underAttack = null;
        this.delaySwitch = 0L;
        this.lastEntity = null;
        attackTime = 0L;

        if (this.isBlocking)
            this.setUnblock ( );

        ItemRenderer.isBlocking = false;
        super.onDisable ( );
    }

    private void swingItem ( ) {
        if (!this.slowHitAnimation.isToggled ( )) {
            Minecraft.thePlayer.swingItem ( );
            return;
        }

        if (this.hitAnimaton.hasReached ( 250L )) {
            Minecraft.thePlayer.swingItem ( );
            this.hitAnimaton.resetTimings ( );
        } else {
            mc.getNetHandler ( ).addToSendQueue ( new C0APacketAnimation ( ) );
        }
    }

    private boolean shouldBlock ( EntityLivingBase entityLivingBase ) {
        boolean shouldBlock = false;
        String blockMode = this.autoblockMode.getActiveMode ( );

        if (blockMode.equalsIgnoreCase ( "None" ))
            return false;

        if (blockMode.equalsIgnoreCase ( "Block always" ))
            return true;

        if (!this.checkDistance ( entityLivingBase , 2D ))
            shouldBlock = true;

        if (Minecraft.thePlayer.motionX == 0.0D && Minecraft.thePlayer.motionZ == 0D)
            shouldBlock = true;

        return shouldBlock;
    }

    private void setBlocking ( ) {

        if (this.isBlocking)
            return;

        if (!Minecraft.thePlayer.isInWater ( )) {
            if (Minecraft.thePlayer.getCurrentEquippedItem ( ) != null
                    && Minecraft.thePlayer.getCurrentEquippedItem ( ).getItem ( ) instanceof ItemSword) {
                C08PacketPlayerBlockPlacement block = new C08PacketPlayerBlockPlacement ( new BlockPos ( Minecraft.thePlayer.posX , Minecraft.thePlayer.posY , Minecraft.thePlayer.posZ ) , 255 ,
                        Minecraft.thePlayer.getCurrentEquippedItem ( ) , 0.0F , 0.0F , 0.0F );
                mc.gameSettings.keyBindUseItem.pressed = true;
                Minecraft.thePlayer.sendQueue.addToSendQueue ( block );
                this.isBlocking = true;
                ItemRenderer.isBlocking = true;
            }

        }
    }

    private void setUnblock ( ) {
        if (Minecraft.thePlayer.getCurrentEquippedItem ( ) != null
                && Minecraft.thePlayer.getCurrentEquippedItem ( ).getItem ( ) instanceof ItemSword) {
            C07PacketPlayerDigging unblock = new C07PacketPlayerDigging ( C07PacketPlayerDigging.Action.RELEASE_USE_ITEM ,
                    new BlockPos ( Minecraft.thePlayer.posX , Minecraft.thePlayer.posY , Minecraft.thePlayer.posZ ) , EnumFacing.DOWN );
            mc.gameSettings.keyBindUseItem.pressed = false;
            Minecraft.thePlayer.sendQueue.addToSendQueue ( unblock );
            this.isBlocking = false;
            ItemRenderer.isBlocking = false;
        }
    }

    private EntityLivingBase getRaytrace ( EntityLivingBase entityLivingBase ) {
        this.raycast.getMouseOver ( System.nanoTime ( ) );
        return this.raycast.getRayCastEntity ( );
    }

    private EntityLivingBase getNearest ( ) {
        double distance = this.rangeSlider.getCurrent ( );
        EntityLivingBase entityLivingBase = null;

        for ( EntityLivingBase entities : this.getEntityToPlayer ( ) ) {

            Location3D startLoc = new Location3D ( Minecraft.thePlayer.posX , Minecraft.thePlayer.posY + ( Minecraft.thePlayer.getEyeHeight ( ) / 2 ) , Minecraft.thePlayer.posZ );
            Location3D endLoc = new Location3D ( entities.posX , entities.posY + ( entities.getEyeHeight ( ) / 2 ) , entities.posZ );
            double dist = startLoc.distance ( endLoc );

            if (entityLivingBase == null) {
                distance = dist;
                entityLivingBase = entities;
            }

            if (distance > dist) {
                entityLivingBase = entities;
                distance = dist;
            }
        }

        if (entityLivingBase != null && this.rayTraceButton.isToggled ( )) {
            EntityLivingBase raytrace = this.getRaytrace ( entityLivingBase );
            if (raytrace != null)
                entityLivingBase = raytrace;
        }

        return entityLivingBase;
    }

    private List< EntityLivingBase > getEntityToPlayer ( ) {
        List< EntityLivingBase > entityList = new ArrayList<> ( );

        try {
            if (mc.theWorld.loadedEntityList != null) {
                for ( Entity entities : mc.theWorld.loadedEntityList ) {
                    if (entities instanceof EntityLivingBase) {

                        if (entities instanceof EntityArmorStand)
                            continue;

                        EntityLivingBase entityLivingBase = ( EntityLivingBase ) entities;
                        if (this.checkEntity ( entityLivingBase )) {

                            double currentRange = this.rangeSlider.getCurrent ( );
                            double preAimRange = this.preAimSlider.getCurrent ( );
                            if (this.checkDistance ( entityLivingBase , ( this.preAimButton.isToggled ( ) ? currentRange + preAimRange : currentRange ) ))
                                continue;

                            entityList.add ( entityLivingBase );
                        }
                    }
                }
            }
        } catch ( ConcurrentModificationException exception ) {
        }

        return entityList;
    }

    private boolean checkEntity ( EntityLivingBase entityLivingBase ) {
        if (Minecraft.thePlayer != null)
            if (entityLivingBase.getEntityId ( ) == Minecraft.thePlayer.getEntityId ( ))
                return false;

        if (entityLivingBase.isDead || entityLivingBase.deathTime > 0)
            return false;

        if (entityLivingBase.isInvisible ( ))
            return false;

        if (FriendManager.friends.contains ( entityLivingBase.getName ( ) ) && !getByName ( "NoFriends" ).isToggled ( ))
            return false;

        if (getByName ( "Teams" ).isToggled ( )) {
            if (entityLivingBase.getTeam ( ) != null && Minecraft.thePlayer.getTeam ( ) != null) {
                return !entityLivingBase.getTeam ( ).isSameTeam ( Minecraft.thePlayer.getTeam ( ) );
            }
        }

        if (!entityLivingBase.isInvisible ( ) && !this.throughWallsButton.isToggled ( ) && !entityLivingBase.canEntityBeSeen ( Minecraft.thePlayer ))
            return false;

        if (Module.getByName ( "AntiBots" ).isToggled ( )) {
            if (GuiComponent.getByName ( "New Detection" ).isToggled ( )) {
                return Entity.sendedStatus.contains ( entityLivingBase.getName ( ) );
            } else if (GuiComponent.getByName ( "Swing Detection" ).isToggled ( )) {
                return entityLivingBase.wasSwinged;
            } else if (GuiComponent.getByName ( "Ultra Detection" ).isToggled ( )) {
                return entityLivingBase.stepSoundChecked;
            }
        }

        return true;
    }

    private boolean canHit ( EntityLivingBase entityLivingBase ) {

        return this.checkRotations ( entityLivingBase ) && !AutoPotion.isFakeRotations ( );
    }

    private boolean smoothHit ( EntityLivingBase entityLivingBase ) {
        if (!this.smoothHitButton.isToggled ( ))
            return false;

        double lastX = entityLivingBase.lastTickPosX;
        double lastZ = entityLivingBase.lastTickPosZ;

        double posX = entityLivingBase.posX + Math.abs ( lastX - entityLivingBase.prevPosX );
        double posZ = entityLivingBase.posZ + Math.abs ( lastX - entityLivingBase.prevPosX );

        double distX = Math.abs ( posX - lastX );
        double distZ = Math.abs ( posZ - lastZ );

        double diff = Math.sqrt ( distX * distX + distZ + distZ );
        return diff > 0.7D;
    }

    private boolean checkDistance ( EntityLivingBase entityLivingBase , double dist ) {
        Location3D startLoc = new Location3D ( Minecraft.thePlayer.posX , Minecraft.thePlayer.posY + ( Minecraft.thePlayer.getEyeHeight ( ) / 2 ) , Minecraft.thePlayer.posZ );
        Location3D endLoc = new Location3D ( entityLivingBase.posX , entityLivingBase.posY + ( entityLivingBase.getEyeHeight ( ) / 2 ) , entityLivingBase.posZ );
        return startLoc.distance ( endLoc ) > dist;
    }

    private boolean autoMiss ( ) {
        if (!this.autoMissButton.isToggled ( ))
            return false;

        if (Minecraft.thePlayer.hurtTimeNoCam > 0 && Minecraft.thePlayer.hurtTimeNoCam < 7)
            return false;

        return ThreadLocalRandom.current ( ).nextDouble ( 1 , 330 ) > 300D;
    }

    private boolean checkDelay ( EntityLivingBase entityLivingBase ) {

        if (this.hurtTimeButton.isToggled ( )) {

            if (entityLivingBase.hurtTimeNoCam == 0F)
                return this.timings.hasReached ( 250L );

            return entityLivingBase.hurtTimeNoCam <= 3F;
        }
        long random = ThreadLocalRandom.current ( ).nextLong ( 1 , 130 );
        return this.timings.hasReached ( ( long ) this.delaySlider.getCurrent ( ) + random );
    }

    private void attackEntity ( EntityLivingBase entityLivingBase ) {

        if (this.checkDelay ( entityLivingBase ) && !this.checkDistance ( entityLivingBase , this.rangeSlider.getCurrent ( ) )) {

            if (this.autoMiss ( ) || this.overPitched)
                return;

            if (this.isBlocking)
                this.setUnblock ( );

            //Swing item before automiss or pitch is over 90 Degress
            this.swingItem ( );

            boolean shouldAttack = true;

            if (this.sniperCursered) {
                shouldAttack = false;
                this.sniperTimings.resetTimings ( );
                this.sniperCursered = false;
            }


            if (shouldAttack) {
                if (!Module.getByName ( "NoSlowdown" ).isToggled ( ))
                    mc.playerController.attackEntity ( Minecraft.thePlayer , entityLivingBase );
                else {
                    mc.getNetHandler ( ).addToSendQueue ( new C02PacketUseEntity ( entityLivingBase , C02PacketUseEntity.Action.ATTACK ) );
                }
                attackTime++;
            }

            if (this.particlesButton.isToggled ( )) {
                Minecraft.thePlayer.onCriticalHit ( entityLivingBase );
                Minecraft.thePlayer.onEnchantmentCritical ( entityLivingBase );
            }

            this.timings.resetTimings ( );

            if (this.lastEntity == null)
                this.lastEntity = entityLivingBase;

            if (this.shouldBlock ( entityLivingBase ))
                this.setBlocking ( );
            else {
                if (ItemRenderer.isBlocking && this.isBlocking)
                    this.setUnblock ( );
            }
        }

    }

    private float getRotation ( float current , float absolute ) {
        final float delta = absolute - current;
        final float fixedDelta = this.getAbsolutePath ( delta );
        return fixedDelta;
    }


    private float getAbsolutePath ( float rotation ) {
        rotation = rotation % 360F;
        if (rotation > 180F) {
            rotation -= 360F;
        } else if (rotation <= -180F) {
            rotation += 360F;
        }
        return rotation;
    }

    private float computeNextYaw ( EntityLivingBase entityLivingBase , float currentYaw , float previousYaw , float targetYaw , float finalYaw ) {

        float snappyness = 20F;
        float friction = 2.5F;

        if (entityLivingBase != null) {
            double distance = this.getModifiedRotSpeed ( entityLivingBase );

            if (distance >= 1.0D)
                distance /= 4.5D;

            if (distance < 0.16D)
                distance = 0.16D;

            if (distance > 1D)
                distance = 1D;

            snappyness = ( float ) ( distance * 100D );
            friction = ( float ) ( distance * 10D ) / 1.3F;
        }

        final float wrappedFinalYaw = MathHelper.wrapAngleTo180_float ( finalYaw );
        final float wrappedCurrentYaw = MathHelper.wrapAngleTo180_float ( currentYaw );
        final boolean isTargetForward = Math.abs ( wrappedFinalYaw - wrappedCurrentYaw ) < 90F;

        if (!isTargetForward) {
            snappyness *= 1.6D;
            friction *= 1.6D;
        }

        final float prevmotion = this.getAbsolutePath ( currentYaw - previousYaw );
        final float delta = this.getRotation ( currentYaw , targetYaw );

        final float absDelta = Math.abs ( delta );
        final float x = absDelta / 180;
        final float accel = ( -( 2 * x - 1 ) * ( 2 * x - 1 ) + 1 ) * snappyness * delta / absDelta;
        final float motion = prevmotion / friction + accel;
        if (Math.abs ( motion ) > 0.01D) {
            currentYaw += motion;
        }

        float maxMotion = 10F;
        if (entityLivingBase != null)
            maxMotion = entityLivingBase.width * ( entityLivingBase.width * 100F );

        this.doneRotatedYaw = absDelta <= maxMotion;
        return currentYaw;
    }


    private boolean checkRotations ( Entity e ) {
        if (!this.smoothRotationsButton.isToggled ( ))
            return true;

        if (this.lastEntity != null && this.lastEntity != e)
            return false;

        return this.doneRotatedYaw && this.doneRotatedPitch;
    }

    private float[] getRotations ( Entity e ) {
        double x = e.posX;
        int y = ( int ) e.posY;
        double z = e.posZ;
        double fX = Minecraft.thePlayer.posX;
        double fY = Minecraft.thePlayer.posY;
        double fZ = Minecraft.thePlayer.posZ;

        if (this.smoothAimButton.isToggled ( )) {
            if (Minecraft.thePlayer.posY - Minecraft.thePlayer.lastTickPosY < -0.225D)
                fY -= ( Minecraft.thePlayer.posY - Minecraft.thePlayer.lastTickPosY );

            if (e.posY - e.lastTickPosY < -0.225D)
                y -= ( e.prevPosY - e.lastTickPosY );
        }

        Location3D startLoc = new Location3D ( fX , fY + ( Minecraft.thePlayer.getEyeHeight ( ) / 2D ) , fZ );
        Location3D endLoc = new Location3D ( x , y + ( e.getEyeHeight ( ) / 4D ) , z );
        BlickWinkel3D blickWinkel3D = new BlickWinkel3D ( startLoc , endLoc );

        //Jitter rotations -> yaw & pitch
        double distance = startLoc.distance ( endLoc );
        double maxRndm = distance * 2D;

        if (maxRndm < 6D)
            maxRndm = 6D;

        double randomYaw = ThreadLocalRandom.current ( ).nextDouble ( 1D , maxRndm );
        double randomPitch = ThreadLocalRandom.current ( ).nextDouble ( 1D , maxRndm );

        float yaw = ( float ) blickWinkel3D.getYaw ( ) - ( float ) randomYaw;
        float pitch = ( float ) blickWinkel3D.getPitch ( ) + ( float ) randomPitch;

        if (( this.sniperTimings.hasReached ( 6000L ) || ( Minecraft.thePlayer.hurtTimeNoCam > 2 && Minecraft.thePlayer.hurtTimeNoCam < 4 ) ) && this.doneRotatedPitch && this.doneRotatedYaw && this.sniperCursorButton.isToggled ( )) {
            double maxSnipe = Minecraft.thePlayer.hurtTimeNoCam > 0 ? 20D : 15D;
            double randomSnipe = ThreadLocalRandom.current ( ).nextDouble ( 5D , maxSnipe );
            int snipeDirection = ThreadLocalRandom.current ( ).nextBoolean ( ) ? 1 : -1;

            if (snipeDirection == 1)
                yaw += randomSnipe;
            else if (snipeDirection == -1)
                yaw -= randomSnipe;

            float newPitch = ( float ) ( pitch - ( randomSnipe * 1.6D ) );

            if (newPitch < 90F)
                pitch = newPitch;

            this.sniperCursered = true;
        }

        if (this.smoothAimButton.isToggled ( )) {
            if (fY >= y && startLoc.distance ( endLoc ) <= 2D) {
                pitch = 35F;
            }

            if (fY >= y && Minecraft.thePlayer.hurtTimeNoCam > 0)
                pitch -= 20F;
        }

        return new float[]{ yaw , pitch };
    }

    public static Killaura getKillaura ( ) {
        return killaura;
    }

    private float computeNextPitch ( EntityLivingBase entityLivingBase , float currentPitch , float previousPitch , float targetPitch ) {

        float snappyness = 20F;
        float friction = 2.5F;

        if (entityLivingBase != null) {
            double distance = this.getModifiedRotSpeed ( entityLivingBase );

            if (distance >= 1.0D)
                distance /= 4.5D;


            if (distance < 0.16D)
                distance = 0.16D;

            if (distance > 1D)
                distance = 1D;

            snappyness = ( float ) ( distance * 100D ) / 2F;
            friction = ( float ) ( distance * 10D ) / 2F;
        }

        final float prevmotion = this.getAbsolutePath ( currentPitch - previousPitch );
        final float delta = this.getRotation ( currentPitch , targetPitch );

        final float absDelta = Math.abs ( delta );
        final float x = absDelta / 180;
        final float accel = ( -( 2 * x - 1 ) * ( 2 * x - 1 ) + 1 ) * snappyness * delta / absDelta;
        final float motion = prevmotion / friction + accel;

        if (Math.abs ( motion ) > 0.01D) {
            currentPitch += motion;
        }

        float maxMotion = 10F;
        if (entityLivingBase != null)
            maxMotion = entityLivingBase.height * ( entityLivingBase.height * 10F );

        if (this.lastEntity != null && this.lastEntity != underAttack) {
            if (this.doneRotatedYaw && this.doneRotatedPitch)
                this.lastEntity = null;
        }

        this.doneRotatedPitch = absDelta <= maxMotion;
        return currentPitch;
    }
}

class Raycast {

    private EntityLivingBase rayCastEntity = null;
    private Entity pointedEntity;
    private final Minecraft mc = Minecraft.getMinecraft ( );
    private MovingObjectPosition movingObjectPosition;

    Raycast ( ) {
        this.pointedEntity = null;
        this.movingObjectPosition = null;
    }

    public void getMouseOver ( float partialTicks ) {
        Entity entity = this.mc.getRenderViewEntity ( );

        if (entity != null) {
            if (this.mc.theWorld != null) {
                this.rayCastEntity = null;
                double d0 = GuiComponent.getByName ( "Attack Reach" ).getCurrent ( );
                this.movingObjectPosition = this.rayTrace ( d0 , partialTicks );
                double d1 = d0;
                Vec3 vec3 = this.getPositionEyes ( partialTicks );

                if (this.movingObjectPosition != null) {
                    d1 = this.movingObjectPosition.hitVec.distanceTo ( vec3 );
                }

                Vec3 vec31 = this.getLook ( partialTicks );
                Vec3 vec32 = vec3.addVector ( vec31.xCoord * d0 , vec31.yCoord * d0 , vec31.zCoord * d0 );
                this.pointedEntity = null;
                Vec3 vec33 = null;
                float f = 1.0F;
                List< Entity > list = this.mc.theWorld.getEntitiesInAABBexcluding ( entity ,
                        entity.getEntityBoundingBox ( ).addCoord ( vec31.xCoord * d0 , vec31.yCoord * d0 , vec31.zCoord * d0 )
                                .expand ( f , f , f ) ,
                        Predicates.and ( EntitySelectors.NOT_SPECTATING , new Predicate< Entity > ( ) {
                            public boolean apply ( Entity p_apply_1_ ) {
                                return p_apply_1_.canBeCollidedWith ( );
                            }
                        } ) );
                double d2 = d1;

                for ( int j = 0; j < list.size ( ); ++j ) {
                    Entity entity1 = list.get ( j );
                    float f1 = entity1.getCollisionBorderSize ( );
                    AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox ( ).expand ( f1 , f1 ,
                            f1 );
                    MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept ( vec3 , vec32 );

                    if (axisalignedbb.isVecInside ( vec3 )) {
                        if (d2 >= 0.0D) {
                            this.pointedEntity = entity1;
                            vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                            d2 = 0.0D;
                        }
                    } else if (movingobjectposition != null) {
                        double d3 = vec3.distanceTo ( movingobjectposition.hitVec );

                        if (d3 < d2 || d2 == 0.0D) {
                            if (entity1 == entity.ridingEntity) {
                                if (d2 == 0.0D) {
                                    this.pointedEntity = entity1;
                                    vec33 = movingobjectposition.hitVec;
                                }
                            } else {
                                this.pointedEntity = entity1;
                                vec33 = movingobjectposition.hitVec;
                                d2 = d3;
                            }
                        }
                    }
                }

                if (this.pointedEntity != null && ( d2 < d1 || this.movingObjectPosition == null )) {
                    this.movingObjectPosition = new MovingObjectPosition ( this.pointedEntity , vec33 );

                    if (this.pointedEntity instanceof EntityLivingBase) {

                        if (this.pointedEntity instanceof EntityArmorStand)
                            return;

                        if (this.pointedEntity.isDead || ( ( EntityLivingBase ) this.pointedEntity ).deathTime > 0)
                            return;

                        this.rayCastEntity = ( EntityLivingBase ) this.pointedEntity;
                    }
                }
            }
        }
    }

    private MovingObjectPosition rayTrace ( double blockReachDistance , float partialTicks ) {
        Vec3 vec3 = this.getPositionEyes ( partialTicks );
        Vec3 vec31 = this.getLook ( partialTicks );
        Vec3 vec32 = vec3.addVector ( vec31.xCoord * blockReachDistance , vec31.yCoord * blockReachDistance ,
                vec31.zCoord * blockReachDistance );
        return this.mc.theWorld.rayTraceBlocks ( vec3 , vec32 , false , false , true );
    }

    private Vec3 getPositionEyes ( float partialTicks ) {
        double x = Minecraft.thePlayer.posX;
        double y = Minecraft.thePlayer.posY;
        double z = Minecraft.thePlayer.posZ;

        float eyeHeight = Minecraft.thePlayer.getEyeHeight ( );
        return new Vec3 ( x , y + ( double ) eyeHeight , z );

    }

    private Vec3 getLook ( float partialTicks ) {
        float pitch = RotationUtils.server_pitch;
        float yaw = RotationUtils.server_yaw;

        if (partialTicks == 1.0F) {
            return this.getVectorForRotation ( pitch , yaw );
        } else {
            float f = Killaura.previousPitch + ( pitch - Killaura.previousPitch ) * partialTicks;
            float f1 = Killaura.previousYaw + ( yaw - Killaura.previousYaw ) * partialTicks;
            return this.getVectorForRotation ( f , f1 );
        }
    }

    private Vec3 getVectorForRotation ( float pitch , float yaw ) {
        float f = MathHelper.cos ( -yaw * 0.017453292F - ( float ) Math.PI );
        float f1 = MathHelper.sin ( -yaw * 0.017453292F - ( float ) Math.PI );
        float f2 = -MathHelper.cos ( -pitch * 0.017453292F );
        float f3 = MathHelper.sin ( -pitch * 0.017453292F );
        return new Vec3 ( f1 * f2 , f3 , f * f2 );
    }

    public EntityLivingBase getRayCastEntity ( ) {
        return rayCastEntity;
    }
}