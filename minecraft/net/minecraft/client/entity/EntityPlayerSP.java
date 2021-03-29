package net.minecraft.client.entity;

import de.staticcode.candy.gui.components.GuiComponent;
import de.staticcode.candy.math.LaggTime;
import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.modules.COMBAT.Killaura;
import de.staticcode.candy.utils.RenderUtils;
import de.staticcode.candy.utils.RotationUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSoundMinecartRiding;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.*;
import net.minecraft.potion.Potion;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.*;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;

public class EntityPlayerSP extends AbstractClientPlayer {
    public final NetHandlerPlayClient sendQueue;
    private final StatFileWriter statWriter;

    /**
     * The last X position which was transmitted to the server, used to determine
     * when the X position changes and needs to be re-trasmitted
     */
    private double lastReportedPosX;

    /**
     * The last Y position which was transmitted to the server, used to determine
     * when the Y position changes and needs to be re-transmitted
     */
    private double lastReportedPosY;

    /**
     * The last Z position which was transmitted to the server, used to determine
     * when the Z position changes and needs to be re-transmitted
     */
    private double lastReportedPosZ;

    /**
     * The last yaw value which was transmitted to the server, used to determine
     * when the yaw changes and needs to be re-transmitted
     */
    private float lastReportedYaw;

    /**
     * The last pitch value which was transmitted to the server, used to determine
     * when the pitch changes and needs to be re-transmitted
     */
    private float lastReportedPitch;

    /**
     * the last sneaking state sent to the server
     */
    private boolean serverSneakState;

    /**
     * the last sprinting state sent to the server
     */
    private boolean serverSprintState;

    /**
     * Reset to 0 every time position is sent to the server, used to send periodic
     * updates every 20 ticks even when the player is not moving.
     */
    private int positionUpdateTicks;
    private boolean hasValidHealth;
    private String clientBrand;
    public MovementInput movementInput;
    protected Minecraft mc;

    /**
     * Used to tell if the player pressed forward twice. If this is at 0 and it's
     * pressed (And they are allowed to sprint, aka enough food on the ground etc)
     * it sets this to 7. If it's pressed and it's greater than 0 enable sprinting.
     */
    public int sprintToggleTimer;

    /**
     * Ticks left before sprinting is disabled.
     */
    public int sprintingTicksLeft;
    public float renderArmYaw;
    public float renderArmPitch;
    public float prevRenderArmYaw;
    public float prevRenderArmPitch;
    private int horseJumpPowerCounter;
    private float horseJumpPower;

    /**
     * The amount of time an entity has been in a Portal
     */
    public float timeInPortal;

    /**
     * The amount of time an entity has been in a Portal the previous tick
     */
    public float prevTimeInPortal;

    public EntityPlayerSP ( Minecraft mcIn , World worldIn , NetHandlerPlayClient netHandler , StatFileWriter statFile ) {
        super ( worldIn , netHandler.getGameProfile ( ) );
        this.sendQueue = netHandler;
        this.statWriter = statFile;
        this.mc = mcIn;
        this.dimension = 0;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom ( DamageSource source , float amount ) {
        return false;
    }

    /**
     * Heal living entity (param: amount of half-hearts)
     */
    public void heal ( float healAmount ) {
    }

    /**
     * Called when a player mounts an entity. e.g. mounts a pig, mounts a boat.
     */
    public void mountEntity ( Entity entityIn ) {
        super.mountEntity ( entityIn );

        if (entityIn instanceof EntityMinecart) {
            this.mc.getSoundHandler ( ).playSound ( new MovingSoundMinecartRiding ( this , ( EntityMinecart ) entityIn ) );
        }
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate ( ) {
        if (this == Minecraft.thePlayer) {
            if (Killaura.underAttack == null && !Killaura.isTurnOffRotation && !Module.getByName ( "Scaffold" ).isToggled ( )) {
                RotationUtils.server_pitch = this.rotationPitch;
                RotationUtils.server_yaw = this.rotationYaw;
            }

            RenderUtils.shouldRender = false;

            LaggTime.lastMs = System.currentTimeMillis ( );

            Module.updateAllModules ( );
        }

        if (this.worldObj.isBlockLoaded ( new BlockPos ( this.posX , 0.0D , this.posZ ) )) {
            super.onUpdate ( );

            if (this.isRiding ( )) {
                this.sendQueue.addToSendQueue ( new C03PacketPlayer.C05PacketPlayerLook ( RotationUtils.server_yaw ,
                        RotationUtils.server_pitch , this.onGround ) );
                this.sendQueue.addToSendQueue ( new C0CPacketInput ( this.moveStrafing , this.moveForward ,
                        this.movementInput.jump , this.movementInput.sneak ) );
            } else {
                this.onUpdateWalkingPlayer ( );
            }
        }
    }

    /**
     * called every tick when the player is on foot. Performs all the things that
     * normally happen during movement.
     */

    public double addY;

    public void onUpdateWalkingPlayer ( ) {
        boolean flag = this.isSprinting ( ) || Module.getByName ( "Teleport" ).isToggled ( );


        if (flag != this.serverSprintState) {
            if (flag) {
                this.sendQueue
                        .addToSendQueue ( new C0BPacketEntityAction ( this , C0BPacketEntityAction.Action.START_SPRINTING ) );
            } else {
                this.sendQueue
                        .addToSendQueue ( new C0BPacketEntityAction ( this , C0BPacketEntityAction.Action.STOP_SPRINTING ) );
            }

            this.serverSprintState = flag;
        }

        boolean flag1 = this.isSneaking ( );

        if (flag1 != this.serverSneakState) {
            if (flag1) {
                this.sendQueue
                        .addToSendQueue ( new C0BPacketEntityAction ( this , C0BPacketEntityAction.Action.START_SNEAKING ) );
            } else {
                this.sendQueue
                        .addToSendQueue ( new C0BPacketEntityAction ( this , C0BPacketEntityAction.Action.STOP_SNEAKING ) );
            }

            this.serverSneakState = flag1;
        }

        if (this.isCurrentViewEntity ( ) && !Module.getByName ( "Teleport" ).isToggled ( )) {
            double d0 = this.posX - this.lastReportedPosX;
            double d1 = this.getEntityBoundingBox ( ).minY - this.lastReportedPosY;
            double d2 = this.posZ - this.lastReportedPosZ;
            double d3 = RotationUtils.server_yaw - this.lastReportedYaw;
            double d4 = RotationUtils.server_pitch - this.lastReportedPitch;
            boolean flag2 = d0 * d0 + d1 * d1 + d2 * d2 > 9.0E-4D || this.positionUpdateTicks >= 20;
            boolean flag3 = d3 != 0.0D || d4 != 0.0D;

            double y = this.getEntityBoundingBox ( ).minY;

            if (this.ridingEntity == null) {
                if (flag2 && flag3) {
                    this.sendQueue.addToSendQueue ( new C03PacketPlayer.C06PacketPlayerPosLook ( this.posX , y , this.posZ ,
                            RotationUtils.server_yaw , RotationUtils.server_pitch , this.onGround ) );
                } else if (flag2) {
                    this.sendQueue.addToSendQueue (
                            new C03PacketPlayer.C04PacketPlayerPosition ( this.posX , y , this.posZ , this.onGround ) );
                } else if (flag3) {
                    this.sendQueue.addToSendQueue ( new C03PacketPlayer.C05PacketPlayerLook ( RotationUtils.server_yaw ,
                            RotationUtils.server_pitch , this.onGround ) );
                } else {
                    this.sendQueue.addToSendQueue ( new C03PacketPlayer ( this.onGround ) );
                }
            } else {
                this.sendQueue.addToSendQueue ( new C03PacketPlayer.C06PacketPlayerPosLook ( this.motionX , -999.0D ,
                        this.motionZ , RotationUtils.server_yaw , RotationUtils.server_pitch , this.onGround ) );
                flag2 = false;
            }

            ++this.positionUpdateTicks;

            if (flag2) {
                this.lastReportedPosX = this.posX;
                this.lastReportedPosY = y;
                this.lastReportedPosZ = this.posZ;
                this.positionUpdateTicks = 0;
            }

            this.lastReportedYaw = RotationUtils.server_yaw;
            this.lastReportedPitch = RotationUtils.server_pitch;

        }
    }

    @Override
    public void moveFlying ( float strafe , float forward , float friction ) {
        String movementMode = GuiComponent.getByName ( "Movement Mode" ).getActiveMode ( );

        if (movementMode.equalsIgnoreCase ( "Server-Side" )) {
            this.strafeSilent ( friction );
            return;
        }

        float f = strafe * strafe + forward * forward;

        if (f >= 1.0E-4F) {
            f = MathHelper.sqrt_float ( f );

            if (f < 1.0F) {
                f = 1.0F;
            }

            f = friction / f;
            strafe = strafe * f;
            forward = forward * f;

            float yaw = RotationUtils.server_yaw;

            if (movementMode.equalsIgnoreCase ( "Normal" ) || Module.getByName ( "Scaffold" ).isToggled ( ))
                yaw = Minecraft.thePlayer.rotationYaw;

            if (Killaura.underAttack != null) {
                mc.gameSettings.keyBindSprint.pressed = false;
                Minecraft.thePlayer.setSprinting ( false );
            }

            float f1 = MathHelper.sin ( yaw * ( float ) Math.PI / 180.0F );
            float f2 = MathHelper.cos ( yaw * ( float ) Math.PI / 180.0F );

            double motionX = strafe * f2 - forward * f1;
            double motionZ = forward * f2 + strafe * f1;

            if (Killaura.underAttack != null && Minecraft.thePlayer.getDistanceToEntity ( Killaura.underAttack ) < 0.9D && movementMode.equalsIgnoreCase ( "Client-Side" ))
                return;

            this.motionX += strafe * f2 - forward * f1;
            this.motionZ += forward * f2 + strafe * f1;
        }

    }

    private void strafeSilent ( float friction ) {

        if (Minecraft.thePlayer == null)
            return;

        if (Killaura.underAttack != null) {
            this.setSprinting ( false );
            mc.gameSettings.keyBindSprint.pressed = false;
        }

        int difference = ( int ) ( ( MathHelper.wrapAngleTo180_float ( this.rotationYaw - RotationUtils.server_yaw - 23.5F - 135 ) + 180 ) / 45 );
        float strafe = this.moveStrafing;
        float forward = this.moveForward;
        double forwardMot = 0D;
        double strafeMot = 0D;
        switch ( difference ) {
            case 0: {
                forwardMot = forward;
                strafeMot = strafe;
                break;
            }
            case 1: {
                forwardMot += forward;
                strafeMot -= forward;
                forwardMot += strafe;
                strafeMot += strafe;
                break;
            }
            case 2: {
                forwardMot = strafe;
                strafeMot = -forward;
                break;
            }
            case 3: {
                forwardMot -= forward;
                strafeMot -= forward;
                forwardMot += strafe;
                strafeMot -= strafe;
                break;
            }
            case 4: {
                forwardMot = -forward;
                strafeMot = -strafe;
                break;
            }
            case 5: {
                forwardMot -= forward;
                strafeMot += forward;
                forwardMot -= strafe;
                strafeMot -= strafe;
                break;
            }
            case 6: {
                forwardMot = -strafe;
                strafeMot = forward;
                break;
            }
            case 7: {
                forwardMot += forward;
                strafeMot += forward;
                forwardMot -= strafe;
                strafeMot += strafe;
                break;
            }
        }
        if (forwardMot > 1.0F || ( forwardMot < 0.9F && forwardMot > 0.3F ) || forwardMot < -1.0F || ( forwardMot > -0.9F && forwardMot < -0.3F )) {
            forwardMot *= 0.5F;
        }
        if (strafeMot > 1.0F || ( strafeMot < 0.9F && strafeMot > 0.3F ) || strafeMot < -1.0F || ( strafeMot > -0.9F && strafeMot < -0.3F )) {
            strafeMot *= 0.5F;
        }

        float f = ( float ) ( strafeMot * strafeMot + forwardMot * forwardMot );
        if (f >= 1.0E-4F) {
            f = MathHelper.sqrt_float ( f );
            if (f < 1.0F) {
                f = 1.0F;
            }
            f = friction / f;
            strafeMot *= f;
            forwardMot *= f;
            float yawSin = MathHelper.sin ( ( float ) ( RotationUtils.server_yaw * Math.PI / 180.0f ) );
            float yawCos = MathHelper.cos ( ( float ) ( RotationUtils.server_yaw * Math.PI / 180.0f ) );
            this.motionX += strafeMot * yawCos - forwardMot * ( double ) yawSin;
            this.motionZ += forwardMot * yawCos + strafeMot * ( double ) yawSin;
        }
    }

    /**
     * Called when player presses the drop item key
     */
    public EntityItem dropOneItem ( boolean dropAll ) {
        C07PacketPlayerDigging.Action c07packetplayerdigging$action = dropAll
                ? C07PacketPlayerDigging.Action.DROP_ALL_ITEMS
                : C07PacketPlayerDigging.Action.DROP_ITEM;
        this.sendQueue.addToSendQueue (
                new C07PacketPlayerDigging ( c07packetplayerdigging$action , BlockPos.ORIGIN , EnumFacing.DOWN ) );
        return null;
    }

    /**
     * Joins the passed in entity item with the world. Args: entityItem
     */
    protected void joinEntityItemWithWorld ( EntityItem itemIn ) {
    }

    /**
     * Sends a chat message from the player. Args: chatMessage
     */
    public void sendChatMessage ( String message ) {
        if (message.startsWith ( "." )) {
            String[] split = message.split ( " " );
            if (Module.getByName ( split[ 0 ].replace ( "." , "" ) ) != null) {
                Module.getByName ( split[ 0 ].replace ( "." , "" ) ).onCommand ( split );
            }
            return;
        }
        this.sendQueue.addToSendQueue ( new C01PacketChatMessage ( message ) );
    }

    /**
     * Swings the item the player is holding.
     */
    public void swingItem ( ) {
        super.swingItem ( );
        this.sendQueue.addToSendQueue ( new C0APacketAnimation ( ) );
    }

    public void respawnPlayer ( ) {
        this.sendQueue.addToSendQueue ( new C16PacketClientStatus ( C16PacketClientStatus.EnumState.PERFORM_RESPAWN ) );
    }

    /**
     * Deals damage to the entity. If its a EntityPlayer then will take damage from
     * the armor first and then health second with the reduced value. Args:
     * damageAmount
     */
    protected void damageEntity ( DamageSource damageSrc , float damageAmount ) {
        if (!this.isEntityInvulnerable ( damageSrc )) {
            this.setHealth ( this.getHealth ( ) - damageAmount );
        }
    }

    /**
     * set current crafting inventory back to the 2x2 square
     */
    public void closeScreen ( ) {
        this.sendQueue.addToSendQueue ( new C0DPacketCloseWindow ( this.openContainer.windowId ) );
        this.closeScreenAndDropStack ( );
    }

    public void closeScreenAndDropStack ( ) {
        this.inventory.setItemStack ( null );
        super.closeScreen ( );
        this.mc.displayGuiScreen ( null );
    }

    /**
     * Updates health locally.
     */
    public void setPlayerSPHealth ( float health ) {
        if (this.hasValidHealth) {
            float f = this.getHealth ( ) - health;

            if (f <= 0.0F) {
                this.setHealth ( health );

                if (f < 0.0F) {
                    this.hurtResistantTime = this.maxHurtResistantTime / 2;
                }
            } else {
                this.lastDamage = f;
                this.setHealth ( this.getHealth ( ) );
                this.hurtResistantTime = this.maxHurtResistantTime;
                this.damageEntity ( DamageSource.generic , f );

                this.hurtTimeNoCam = 10;
                if (Module.getByName ( "NoHurtcam" ).isToggled ( ) && Minecraft.thePlayer != null
                        && this.getName ( ).equalsIgnoreCase ( Minecraft.thePlayer.getName ( ) ))
                    this.hurtTime = 0;
                else
                    this.hurtTime = this.maxHurtTime = 10;
            }
        } else {
            this.setHealth ( health );
            this.hasValidHealth = true;
        }
    }

    /**
     * Adds a value to a statistic field.
     */
    public void addStat ( StatBase stat , int amount ) {
        if (stat != null) {
            if (stat.isIndependent) {
                super.addStat ( stat , amount );
            }
        }
    }

    /**
     * Sends the player's abilities to the server (if there is one).
     */
    public void sendPlayerAbilities ( ) {
        this.sendQueue.addToSendQueue ( new C13PacketPlayerAbilities ( this.capabilities ) );
    }

    /**
     * returns true if this is an EntityPlayerSP, or the logged in player.
     */
    public boolean isUser ( ) {
        return true;
    }

    protected void sendHorseJump ( ) {
        this.sendQueue.addToSendQueue ( new C0BPacketEntityAction ( this , C0BPacketEntityAction.Action.RIDING_JUMP ,
                ( int ) ( this.getHorseJumpPower ( ) * 100.0F ) ) );
    }

    public void sendHorseInventory ( ) {
        this.sendQueue.addToSendQueue ( new C0BPacketEntityAction ( this , C0BPacketEntityAction.Action.OPEN_INVENTORY ) );
    }

    public void setClientBrand ( String brand ) {
        this.clientBrand = brand;
    }

    public String getClientBrand ( ) {
        return this.clientBrand;
    }

    public StatFileWriter getStatFileWriter ( ) {
        return this.statWriter;
    }

    public void addChatComponentMessage ( IChatComponent chatComponent ) {
        this.mc.ingameGUI.getChatGUI ( ).printChatMessage ( chatComponent );
    }

    protected boolean pushOutOfBlocks ( double x , double y , double z ) {
        if (this.noClip) {
            return false;
        } else {
            BlockPos blockpos = new BlockPos ( x , y , z );
            double d0 = x - ( double ) blockpos.getX ( );
            double d1 = z - ( double ) blockpos.getZ ( );

            if (!this.isOpenBlockSpace ( blockpos )) {
                int i = -1;
                double d2 = 9999.0D;

                if (this.isOpenBlockSpace ( blockpos.west ( ) ) && d0 < d2) {
                    d2 = d0;
                    i = 0;
                }

                if (this.isOpenBlockSpace ( blockpos.east ( ) ) && 1.0D - d0 < d2) {
                    d2 = 1.0D - d0;
                    i = 1;
                }

                if (this.isOpenBlockSpace ( blockpos.north ( ) ) && d1 < d2) {
                    d2 = d1;
                    i = 4;
                }

                if (this.isOpenBlockSpace ( blockpos.south ( ) ) && 1.0D - d1 < d2) {
                    d2 = 1.0D - d1;
                    i = 5;
                }

                float f = 0.1F;

                if (i == 0) {
                    this.motionX = -f;
                }

                if (i == 1) {
                    this.motionX = f;
                }

                if (i == 4) {
                    this.motionZ = -f;
                }

                if (i == 5) {
                    this.motionZ = f;
                }
            }

            return false;
        }
    }

    /**
     * Returns true if the block at the given BlockPos and the block above it are
     * NOT full cubes.
     */
    private boolean isOpenBlockSpace ( BlockPos pos ) {
        return !this.worldObj.getBlockState ( pos ).getBlock ( ).isNormalCube ( )
                && !this.worldObj.getBlockState ( pos.up ( ) ).getBlock ( ).isNormalCube ( );
    }

    /**
     * Set sprinting switch for Entity.
     */
    public void setSprinting ( boolean sprinting ) {
        super.setSprinting ( sprinting );
        this.sprintingTicksLeft = sprinting ? 600 : 0;
    }

    /**
     * Sets the current XP, total XP, and level number.
     */
    public void setXPStats ( float currentXP , int maxXP , int level ) {
        this.experience = currentXP;
        this.experienceTotal = maxXP;
        this.experienceLevel = level;
    }

    /**
     * Send a chat message to the CommandSender
     */
    public void addChatMessage ( IChatComponent component ) {
        this.mc.ingameGUI.getChatGUI ( ).printChatMessage ( component );
    }

    /**
     * Returns {@code true} if the CommandSender is allowed to execute the command,
     * {@code false} if not
     */
    public boolean canCommandSenderUseCommand ( int permLevel , String commandName ) {
        return permLevel <= 0;
    }

    /**
     * Get the position in the world. <b>{@code null} is not allowed!</b> If you are
     * not an entity in the world, return the coordinates 0, 0, 0
     */
    public BlockPos getPosition ( ) {
        return new BlockPos ( this.posX + 0.5D , this.posY + 0.5D , this.posZ + 0.5D );
    }

    public void playSound ( String name , float volume , float pitch ) {
        this.worldObj.playSound ( this.posX , this.posY , this.posZ , name , volume , pitch , false );
    }

    /**
     * Returns whether the entity is in a server world
     */
    public boolean isServerWorld ( ) {
        return true;
    }

    public boolean isRidingHorse ( ) {
        return this.ridingEntity != null && this.ridingEntity instanceof EntityHorse
                && ( ( EntityHorse ) this.ridingEntity ).isHorseSaddled ( );
    }

    public float getHorseJumpPower ( ) {
        return this.horseJumpPower;
    }

    public void openEditSign ( TileEntitySign signTile ) {
        this.mc.displayGuiScreen ( new GuiEditSign ( signTile ) );
    }

    public void openEditCommandBlock ( CommandBlockLogic cmdBlockLogic ) {
        this.mc.displayGuiScreen ( new GuiCommandBlock ( cmdBlockLogic ) );
    }

    /**
     * Displays the GUI for interacting with a book.
     */
    public void displayGUIBook ( ItemStack bookStack ) {
        Item item = bookStack.getItem ( );

        if (item == Items.writable_book) {
            this.mc.displayGuiScreen ( new GuiScreenBook ( this , bookStack , true ) );
        }
    }

    /**
     * Displays the GUI for interacting with a chest inventory. Args: chestInventory
     */
    public void displayGUIChest ( IInventory chestInventory ) {
        String s = chestInventory instanceof IInteractionObject ? ( ( IInteractionObject ) chestInventory ).getGuiID ( )
                : "minecraft:container";

        if ("minecraft:chest".equals ( s )) {
            this.mc.displayGuiScreen ( new GuiChest ( this.inventory , chestInventory ) );
        } else if ("minecraft:hopper".equals ( s )) {
            this.mc.displayGuiScreen ( new GuiHopper ( this.inventory , chestInventory ) );
        } else if ("minecraft:furnace".equals ( s )) {
            this.mc.displayGuiScreen ( new GuiFurnace ( this.inventory , chestInventory ) );
        } else if ("minecraft:brewing_stand".equals ( s )) {
            this.mc.displayGuiScreen ( new GuiBrewingStand ( this.inventory , chestInventory ) );
        } else if ("minecraft:beacon".equals ( s )) {
            this.mc.displayGuiScreen ( new GuiBeacon ( this.inventory , chestInventory ) );
        } else if (!"minecraft:dispenser".equals ( s ) && !"minecraft:dropper".equals ( s )) {
            this.mc.displayGuiScreen ( new GuiChest ( this.inventory , chestInventory ) );
        } else {
            this.mc.displayGuiScreen ( new GuiDispenser ( this.inventory , chestInventory ) );
        }
    }

    public void displayGUIHorse ( EntityHorse horse , IInventory horseInventory ) {
        this.mc.displayGuiScreen ( new GuiScreenHorseInventory ( this.inventory , horseInventory , horse ) );
    }

    public void displayGui ( IInteractionObject guiOwner ) {
        String s = guiOwner.getGuiID ( );

        if ("minecraft:crafting_table".equals ( s )) {
            this.mc.displayGuiScreen ( new GuiCrafting ( this.inventory , this.worldObj ) );
        } else if ("minecraft:enchanting_table".equals ( s )) {
            this.mc.displayGuiScreen ( new GuiEnchantment ( this.inventory , this.worldObj , guiOwner ) );
        } else if ("minecraft:anvil".equals ( s )) {
            this.mc.displayGuiScreen ( new GuiRepair ( this.inventory , this.worldObj ) );
        }
    }

    public void displayVillagerTradeGui ( IMerchant villager ) {
        this.mc.displayGuiScreen ( new GuiMerchant ( this.inventory , villager , this.worldObj ) );
    }

    /**
     * Called when the player performs a critical hit on the Entity. Args: entity
     * that was hit critically
     */
    public void onCriticalHit ( Entity entityHit ) {
        this.mc.effectRenderer.emitParticleAtEntity ( entityHit , EnumParticleTypes.CRIT );
    }

    public void onEnchantmentCritical ( Entity entityHit ) {
        this.mc.effectRenderer.emitParticleAtEntity ( entityHit , EnumParticleTypes.CRIT_MAGIC );
    }

    /**
     * Returns if this entity is sneaking.
     */
    public boolean isSneaking ( ) {
        boolean flag = this.movementInput != null && this.movementInput.sneak;
        return flag && !this.sleeping;
    }

    public void updateEntityActionState ( ) {
        super.updateEntityActionState ( );

        if (this.isCurrentViewEntity ( )) {
            this.moveStrafing = this.movementInput.moveStrafe;
            this.moveForward = this.movementInput.moveForward;
            this.isJumping = this.movementInput.jump;
            this.prevRenderArmYaw = this.renderArmYaw;
            this.prevRenderArmPitch = this.renderArmPitch;
            this.renderArmPitch = ( float ) ( ( double ) this.renderArmPitch
                    + ( double ) ( this.rotationPitch - this.renderArmPitch ) * 0.5D );
            this.renderArmYaw = ( float ) ( ( double ) this.renderArmYaw
                    + ( double ) ( this.rotationYaw - this.renderArmYaw ) * 0.5D );
        }
    }

    protected boolean isCurrentViewEntity ( ) {
        return this.mc.getRenderViewEntity ( ) == this;
    }

    /**
     * Called frequently so the entity can update its state every tick as required.
     * For example, zombies and skeletons use this to react to sunlight and start to
     * burn.
     */
    int aacSlowDownTimer = 0;

    public void onLivingUpdate ( ) {
        if (this.sprintingTicksLeft > 0) {
            --this.sprintingTicksLeft;

            if (this.sprintingTicksLeft == 0) {
                this.setSprinting ( false );
            }
        }

        if (this.sprintToggleTimer > 0) {
            --this.sprintToggleTimer;
        }

        this.prevTimeInPortal = this.timeInPortal;

        if (this.inPortal) {
            if (this.mc.currentScreen != null && !this.mc.currentScreen.doesGuiPauseGame ( )) {
                this.mc.displayGuiScreen ( null );
            }

            if (this.timeInPortal == 0.0F) {
                this.mc.getSoundHandler ( ).playSound ( PositionedSoundRecord.create ( new ResourceLocation ( "portal.trigger" ) ,
                        this.rand.nextFloat ( ) * 0.4F + 0.8F ) );
            }

            this.timeInPortal += 0.0125F;

            if (this.timeInPortal >= 1.0F) {
                this.timeInPortal = 1.0F;
            }

            this.inPortal = false;
        } else if (this.isPotionActive ( Potion.confusion )
                && this.getActivePotionEffect ( Potion.confusion ).getDuration ( ) > 60) {
            this.timeInPortal += 0.006666667F;

            if (this.timeInPortal > 1.0F) {
                this.timeInPortal = 1.0F;
            }
        } else {
            if (this.timeInPortal > 0.0F) {
                this.timeInPortal -= 0.05F;
            }

            if (this.timeInPortal < 0.0F) {
                this.timeInPortal = 0.0F;
            }
        }

        if (this.timeUntilPortal > 0) {
            --this.timeUntilPortal;
        }

        boolean flag = this.movementInput.jump;
        boolean flag1 = this.movementInput.sneak;
        float f = 0.8F;
        boolean flag2 = this.movementInput.moveForward >= f;
        this.movementInput.updatePlayerMoveState ( );

        if (this.isUsingItem ( ) && !this.isRiding ( )) {

            boolean slowdown = true;
            if (GuiComponent.getByName ( "Slowdown Mode" ).getActiveMode ( ).equalsIgnoreCase ( "Normal" ))
                slowdown = false;

            if (!Module.getByName ( "NoSlowdown" ).isToggled ( ))
                slowdown = true;

            if (GuiComponent.getByName ( "Slowdown Mode" ).getActiveMode ( ).equalsIgnoreCase ( "AAC" ) && Module.getByName ( "NoSlowdown" ).isToggled ( )) {
                this.aacSlowDownTimer++;

                if (this.aacSlowDownTimer > 3) {
                    this.movementInput.moveStrafe *= 0.16F;
                    this.movementInput.moveForward *= 0.16F;
                    this.sprintToggleTimer = 0;
                    this.aacSlowDownTimer = 0;
                } else {
                    Minecraft.thePlayer.motionX /= 1.08D;
                    Minecraft.thePlayer.motionZ /= 1.08D;
                }

                slowdown = false;
            }


            if (slowdown) {
                this.movementInput.moveStrafe *= 0.2F;
                this.movementInput.moveForward *= 0.2F;
                this.sprintToggleTimer = 0;
            }
        }

        this.pushOutOfBlocks ( this.posX - ( double ) this.width * 0.35D , this.getEntityBoundingBox ( ).minY + 0.5D ,
                this.posZ + ( double ) this.width * 0.35D );
        this.pushOutOfBlocks ( this.posX - ( double ) this.width * 0.35D , this.getEntityBoundingBox ( ).minY + 0.5D ,
                this.posZ - ( double ) this.width * 0.35D );
        this.pushOutOfBlocks ( this.posX + ( double ) this.width * 0.35D , this.getEntityBoundingBox ( ).minY + 0.5D ,
                this.posZ - ( double ) this.width * 0.35D );
        this.pushOutOfBlocks ( this.posX + ( double ) this.width * 0.35D , this.getEntityBoundingBox ( ).minY + 0.5D ,
                this.posZ + ( double ) this.width * 0.35D );
        boolean flag3 = ( float ) this.getFoodStats ( ).getFoodLevel ( ) > 6.0F || this.capabilities.allowFlying;

        if (this.onGround && !flag1 && !flag2 && this.movementInput.moveForward >= f && !this.isSprinting ( ) && flag3
                && !this.isUsingItem ( ) && !this.isPotionActive ( Potion.blindness )) {
            if (this.sprintToggleTimer <= 0 && !this.mc.gameSettings.keyBindSprint.isKeyDown ( )) {
                this.sprintToggleTimer = 7;
            } else {
                this.setSprinting ( true );
            }
        }

        if (!this.isSprinting ( ) && this.movementInput.moveForward >= f && flag3 && !this.isUsingItem ( )
                && !this.isPotionActive ( Potion.blindness ) && this.mc.gameSettings.keyBindSprint.isKeyDown ( )) {
            this.setSprinting ( true );
        }

        if (this.isSprinting ( ) && ( this.movementInput.moveForward < f || this.isCollidedHorizontally || !flag3 )) {
            this.setSprinting ( false );
        }

        if (this.capabilities.allowFlying) {
            if (this.mc.playerController.isSpectatorMode ( )) {
                if (!this.capabilities.isFlying) {
                    this.capabilities.isFlying = true;
                    this.sendPlayerAbilities ( );
                }
            } else if (!flag && this.movementInput.jump) {
                if (this.flyToggleTimer == 0) {
                    this.flyToggleTimer = 7;
                } else {
                    this.capabilities.isFlying = !this.capabilities.isFlying;
                    this.sendPlayerAbilities ( );
                    this.flyToggleTimer = 0;
                }
            }
        }

        if (this.capabilities.isFlying && this.isCurrentViewEntity ( )) {
            if (this.movementInput.sneak) {
                this.motionY -= this.capabilities.getFlySpeed ( ) * 3.0F;
            }

            if (this.movementInput.jump) {
                this.motionY += this.capabilities.getFlySpeed ( ) * 3.0F;
            }
        }

        if (this.isRidingHorse ( )) {
            if (this.horseJumpPowerCounter < 0) {
                ++this.horseJumpPowerCounter;

                if (this.horseJumpPowerCounter == 0) {
                    this.horseJumpPower = 0.0F;
                }
            }

            if (flag && !this.movementInput.jump) {
                this.horseJumpPowerCounter = -10;
                this.sendHorseJump ( );
            } else if (!flag && this.movementInput.jump) {
                this.horseJumpPowerCounter = 0;
                this.horseJumpPower = 0.0F;
            } else if (flag) {
                ++this.horseJumpPowerCounter;

                if (this.horseJumpPowerCounter < 10) {
                    this.horseJumpPower = ( float ) this.horseJumpPowerCounter * 0.1F;
                } else {
                    this.horseJumpPower = 0.8F + 2.0F / ( float ) ( this.horseJumpPowerCounter - 9 ) * 0.1F;
                }
            }
        } else {
            this.horseJumpPower = 0.0F;
        }

        super.onLivingUpdate ( );

        if (this.onGround && this.capabilities.isFlying && !this.mc.playerController.isSpectatorMode ( )) {
            this.capabilities.isFlying = false;
            this.sendPlayerAbilities ( );
        }
    }
}
