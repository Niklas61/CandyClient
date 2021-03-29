package de.staticcode.candy.module.modules.MOVEMENT;

import de.staticcode.candy.gui.components.GuiComponent;
import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import de.staticcode.candy.module.modules.COMBAT.Killaura;
import de.staticcode.candy.utils.Timings;
import net.minecraft.block.BlockIce;
import net.minecraft.block.BlockPackedIce;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovementInput;

import java.util.Arrays;

public class Speed extends Module {

    private final GuiComponent speedMode = new GuiComponent ( "Speed Mode" , this ,
            Arrays.asList ( "AAC" , "AAC Bhop 3.2.0" , "AAC Strafe Bhop 3.2.0" , "AAC Lowhop 3.2.0" , "Motion Jump" ,
                    "Fast Jump Down" , "Hop Fast" , "AAC Strafe Bhop 3.2.1" , "MineSucht Strafe Bhop" , "AAC Bhop 3.2.1" ,
                    "Hive Bhop" , "NCP Lowhop" , "Boost" , "Redesky Test" ) ,
            "AAC" );

    public Speed ( ) {
        super ( "Speed" , Category.MOVEMENT );
    }

    public static boolean isSpeeding;
    private float lastYaw;

    private final Timings timings = new Timings ( );

    @Override
    public void onDisable ( ) {
        Minecraft.thePlayer.motionX = 0.0d;
        Minecraft.thePlayer.motionZ = 0.0d;

        mc.timer.timerSpeed = 1.0f;
        Minecraft.thePlayer.speedInAir = 0.02f;
        isSpeeding = false;
        this.settedAir = false;
        this.speedingTime = 0;
        this.speedingDouble = 0d;
        super.onDisable ( );
    }

    @Override
    public void onUpdate ( ) {
        if (this.isToggled ( )) {

            this.displayName = "Speed §2" + this.speedMode.getActiveMode ( );

            if (Minecraft.thePlayer.moveForward == 0.0f && Minecraft.thePlayer.moveStrafing == 0.0f
                    || mc.gameSettings.keyBindJump.pressed || Minecraft.thePlayer.isInWater ( )
                    || mc.theWorld.getBlockState ( Minecraft.thePlayer.getPosition ( ).add ( 0.0d , -0.5d , 0.0d ) )
                    .getBlock ( ) instanceof BlockIce
                    || mc.theWorld.getBlockState ( Minecraft.thePlayer.getPosition ( ).add ( 0.0d , -0.5d , 0.0d ) )
                    .getBlock ( ) instanceof BlockPackedIce) {
                isSpeeding = false;
                mc.timer.timerSpeed = 1.0f;
                this.settedAir = false;
                this.speedingTime = 0;
                this.speedingDouble = 0d;
                if (!mc.gameSettings.keyBindJump.pressed) {
                    Minecraft.thePlayer.motionX = 0.0d;
                    Minecraft.thePlayer.motionZ = 0.0d;
                }
                return;
            }

            isSpeeding = !Minecraft.thePlayer.onGround;

            String mode = this.speedMode.getActiveMode ( );
            if (mode.equalsIgnoreCase ( "AAC" )) {
                if (Minecraft.thePlayer.onGround) {
                    Minecraft.thePlayer.jump ( );

                    Minecraft.thePlayer.motionX *= 1.013d;
                    Minecraft.thePlayer.motionZ *= 1.013d;

                    mc.timer.timerSpeed = 1.15f;
                } else {
                    mc.timer.timerSpeed = 1.0f;
                    if (Minecraft.thePlayer.fallDistance < 0.1f) {
                        Minecraft.thePlayer.speedInAir = 0.0215f;
                    } else if (Minecraft.thePlayer.fallDistance >= 0.15f) {
                        Minecraft.thePlayer.speedInAir = 0.0216f;
                        Minecraft.thePlayer.motionY += 0.014d;
                    } else if (Minecraft.thePlayer.fallDistance > 0.025f) {
                        mc.timer.timerSpeed = 1.0f;
                        Minecraft.thePlayer.speedInAir = 0.024f;
                    }
                }
            } else if (mode.equalsIgnoreCase ( "AAC Bhop 3.2.0" )) {
                if (Minecraft.thePlayer.onGround) {
                    mc.timer.timerSpeed = 1.0f;
                    Minecraft.thePlayer.jump ( );
                } else if (Minecraft.thePlayer.fallDistance <= 0.15f) {
                    Minecraft.thePlayer.speedInAir = 0.02f;
                } else if (Minecraft.thePlayer.fallDistance >= 0.15f && Minecraft.thePlayer.fallDistance <= 0.025f) {
                    mc.timer.timerSpeed = 1.0f;
                    Minecraft.thePlayer.speedInAir = 0.02f;
                } else {
                    mc.timer.timerSpeed = 1.05f;
                }
            } else if (mode.equalsIgnoreCase ( "AAC Lowhop 3.2.0" )) {
                this.onAAC320 ( );
            } else if (mode.equalsIgnoreCase ( "AAC Strafe Bhop 3.2.0" )) {

                if (Killaura.underAttack != null) {
                    if (Minecraft.thePlayer.onGround) {
                        Minecraft.thePlayer.motionY = 0.41d;
                    }
                    return;
                }

                if (Minecraft.thePlayer.onGround) {
                    Minecraft.thePlayer.jump ( );
                    this.speedingDouble = 0d;
                    this.speedingTime++;
                    this.moveSpeed = 0.356d;
                } else {
                    this.moveSpeed = this.getBaseMoveSpeed ( ) / 1.04d;

                    if (this.speedingTime > 1) {
                        if (Minecraft.thePlayer.fallDistance >= 0.1f) {
                            this.moveSpeed = 0.25D;
                        }
                        if (Minecraft.thePlayer.fallDistance >= 0.125f) {
                            this.moveSpeed = 0.274D;
                        }

                    } else {
                        this.moveSpeed = 0.1d;
                    }
                }
                this.shouldSpeed ( );
            } else if (mode.equalsIgnoreCase ( "Motion Jump" )) {

                if (Killaura.underAttack != null || Minecraft.thePlayer.isCollidedHorizontally) {
                    if (Minecraft.thePlayer.onGround) {
                        Minecraft.thePlayer.motionY = 0.4d;
                    }
                    return;
                }

                if (Minecraft.thePlayer.onGround) {
                    Minecraft.thePlayer.motionY = 0.35d;
                } else {
                    this.moveSpeed = this.getBaseMoveSpeed ( ) / 1.05d;

                    if (Minecraft.thePlayer.fallDistance > 0.1f) {
                        Minecraft.thePlayer.motionY -= 0.02d;
                    }

                    if (Minecraft.thePlayer.fallDistance >= 0.2f && Minecraft.thePlayer.fallDistance < 0.5f) {
                        Minecraft.thePlayer.motionY -= 0.01d;
                    }
                    this.shouldSpeed ( );
                }

            } else if (mode.equalsIgnoreCase ( "Fast Jump Down" )) {
                if (Minecraft.thePlayer.motionY < -0.1d)
                    return;

                if (Minecraft.thePlayer.onGround) {
                    Minecraft.thePlayer.motionY = 0.1d;
                    this.moveSpeed = 0.4d;
                } else {
                    this.moveSpeed = this.getBaseMoveSpeed ( );
                }
                this.shouldSpeed ( );
            } else if (mode.equalsIgnoreCase ( "Hop Fast" )) {
                if (Minecraft.thePlayer.motionY < -0.1d)
                    return;

                if (Minecraft.thePlayer.onGround) {
                    Minecraft.thePlayer.motionY = 0.15d;
                    this.moveSpeed = 0.5d;
                } else {
                    this.moveSpeed = this.getBaseMoveSpeed ( ) + Minecraft.thePlayer.fallDistance + 0.3f;
                }
                this.shouldSpeed ( );
            } else if (mode.equalsIgnoreCase ( "AAC Strafe Bhop 3.2.1" )) {

                if (Minecraft.thePlayer.onGround) {
                    mc.timer.timerSpeed = 1.3F;
                    if (this.speedingDouble < 0.7d) {
                        this.speedingDouble += 0.15d;
                    } else {
                        Minecraft.thePlayer.fallDistance = 0.018f;
                    }
                    Minecraft.thePlayer.jump ( );

                    this.moveSpeed = this.speedingDouble;
                } else {
                    mc.timer.timerSpeed = 1.05F;
                    Minecraft.thePlayer.speedInAir = 0.034F;
                    this.moveSpeed = this.getBaseMoveSpeed ( ) + Math.abs ( this.speedingDouble / 1.1d ) + ( Math.random ( ) - 0.2 );
                }
                this.shouldSpeed ( );
            } else if (mode.equalsIgnoreCase ( "MineSucht Strafe Bhop" )) {
                if (Minecraft.thePlayer.onGround) {
                    mc.timer.timerSpeed = 1.0f;
                    Minecraft.thePlayer.jump ( );
                    this.moveSpeed = 0.4d;
                } else {
                    this.moveSpeed = 0.26d;

                    if (Minecraft.thePlayer.fallDistance > 0.01f) {
                        mc.timer.timerSpeed = 2f;
                    }
                    if (Minecraft.thePlayer.fallDistance >= 0.23f) {
                        mc.timer.timerSpeed = 1.0f;
                    }

                }

                this.shouldSpeed ( );
            } else if (mode.equalsIgnoreCase ( "AAC Bhop 3.2.1" )) {

                if (Minecraft.thePlayer.onGround) {
                    Minecraft.thePlayer.motionY = 0.39D;

                    mc.timer.timerSpeed = 1.0f;
                    Minecraft.thePlayer.speedInAir = 0.02f;

                    if (this.speedingDouble < 0.4d) {
                        this.speedingDouble += 0.1d;
                    }
                    this.moveSpeed = Minecraft.thePlayer.getAIMoveSpeed ( ) + this.speedingDouble / 1.2d + 0.011d;
                    this.shouldSpeed ( );
                } else {
                    this.moveSpeed = Minecraft.thePlayer.getAIMoveSpeed ( ) + this.speedingDouble / 3.1d;

                    if (Minecraft.thePlayer.fallDistance >= 0.03f) {
                        Minecraft.thePlayer.motionY += 0.015d;
                    }
                    mc.timer.timerSpeed = 1.0f;

                    this.shouldSpeed ( );
                    Minecraft.thePlayer.jumpMovementFactor = 0.03f;

                    if (Minecraft.thePlayer.fallDistance >= 0.25f) {
                        Minecraft.thePlayer.motionX *= 1.036d;
                        Minecraft.thePlayer.motionZ *= 1.036d;
                        mc.timer.timerSpeed = 1.18f;
                    }

                    if (Minecraft.thePlayer.fallDistance >= 0.3f) {
                        Minecraft.thePlayer.speedInAir = 0.0232f;
                        mc.timer.timerSpeed = 1.0f;
                    }
                }

            } else if (mode.equalsIgnoreCase ( "Hive Bhop" )) {

                if (Minecraft.thePlayer.onGround) {
                    this.moveSpeed = this.getBaseMoveSpeed ( );
                    mc.timer.timerSpeed = 1F;
                    Minecraft.thePlayer.motionY = 0.399d;
                    this.settedAir = false;
                    this.speedingTime++;

                    if (this.speedingTime > 1) {
                        this.moveSpeed = 0.2726d;
                    } else {
                        this.moveSpeed = 0.1d;
                    }
                } else {
                    if (this.speedingTime < 2)
                        return;

                    if (Minecraft.thePlayer.hurtTimeNoCam > 0)
                        return;

                    if (!this.settedAir) {
                        this.moveSpeed = 0.3721d;
                        this.settedAir = true;
                    }

                    this.moveSpeed -= 0.00247d;

                    if (this.distLastYaw ( ) > 10d) {
                        this.moveSpeed -= 0.0019d;
                    }

                }
                this.shouldSpeed ( );
            } else if (mode.equalsIgnoreCase ( "NCP Lowhop" )) {
                if (Minecraft.thePlayer.onGround) {
                    if (!this.settedAir) {
                        Minecraft.thePlayer.motionY = 0.05d;
                    } else {
                        Minecraft.thePlayer.motionY = 0.035d;
                    }
                    this.settedAir = !this.settedAir;
                    this.moveSpeed = 0.23d;
                } else {
                    if (this.settedAir) {
                        this.moveSpeed = 0.25d;
                    } else {
                        this.moveSpeed = 0.15d;
                    }
                }

                this.shouldSpeed ( );
            } else if (mode.equalsIgnoreCase ( "Boost" )) {
                if (Minecraft.thePlayer.onGround) {
                    this.speedingTime++;

                    if (this.speedingTime >= 15) {
                        if (this.speedingTime >= 20)
                            this.speedingTime = 0;
                        return;
                    }
                    mc.timer.timerSpeed = ( float ) ( Math.random ( ) + 0.4F );
                    if (mc.timer.timerSpeed < 1F)
                        mc.timer.timerSpeed = 1F;
                    if (this.timings.hasReached ( 250L )) {
                        this.settedAir = false;
                        Minecraft.thePlayer.motionY = 0.03d;
                        Minecraft.move ( Minecraft.thePlayer.rotationYaw , 0.215f );
                        this.timings.resetTimings ( );
                    } else {
                        Minecraft.move ( Minecraft.thePlayer.rotationYaw , 0.15f );
                    }
                } else {
                    Minecraft.move ( Minecraft.thePlayer.rotationYaw , 0.25f );
                    if (!this.settedAir) {
                        Minecraft.thePlayer.jumpMovementFactor = 0.03F;
                        this.settedAir = true;
                    }
                }
            } else if (mode.equalsIgnoreCase ( "Redesky Test" )) {
                if (Minecraft.thePlayer.onGround) {
                    this.moveSpeed = this.getBaseMoveSpeed ( ) - 0.03D;
                    Minecraft.thePlayer.motionY = 0.4D;
                    mc.timer.timerSpeed = 1.0F;
                } else {
                    double fallDistance = Minecraft.thePlayer.fallDistance;

                    if (fallDistance >= 0.7F && fallDistance < 0.9F) {
                        mc.timer.timerSpeed = 1.1F;
                        this.moveSpeed = this.getBaseMoveSpeed ( ) + 0.1D;
                        Minecraft.thePlayer.speedInAir = 0.2F;
                    }

                    if (fallDistance < 0.7F) {
                        mc.timer.timerSpeed = 0.834534F;
                        Minecraft.thePlayer.speedInAir = 0.1F;
                        this.moveSpeed = this.getBaseMoveSpeed ( );
                    }
                }

                this.shouldSpeed ( );
            }

        }
        this.lastYaw = Minecraft.thePlayer.rotationYaw;
        super.onUpdate ( );
    }

    private boolean settedAir;
    private int speedingTime;
    private double speedingDouble;

    private double distLastYaw ( ) {
        return Math.abs ( Minecraft.thePlayer.rotationYaw - this.lastYaw );
    }

    private void onAAC320 ( ) {
        // AAC 3.2.0
        BlockPos pos = new BlockPos ( Minecraft.thePlayer.posX , Minecraft.thePlayer.posY - 1.0 , Minecraft.thePlayer.posZ );

        if (!mc.gameSettings.keyBindForward.isKeyDown ( ) || mc.theWorld.getBlockState ( pos ).getBlock ( ) == Blocks.air) {
            mc.timer.timerSpeed = 1f;
            return;
        }

        mc.timer.timerSpeed = 1.03f;

        if (Minecraft.thePlayer.onGround) {
            Minecraft.thePlayer.jump ( );
            Minecraft.thePlayer.motionY = 0.3851f;

            Minecraft.thePlayer.motionX *= 1.0135f;
            Minecraft.thePlayer.motionZ *= 1.0135f;

        } else {

            if (Minecraft.thePlayer.motionY > 0) {
                Minecraft.thePlayer.motionY -= 0.015f;

                Minecraft.thePlayer.motionX *= 1.011f;
                Minecraft.thePlayer.motionZ *= 1.011f;

            } else {

                Minecraft.thePlayer.motionY -= 0.01499999f;

                if (Minecraft.thePlayer.motionY <= -0.282) {
                    Minecraft.thePlayer.motionY -= 0.5f;

                    Minecraft.thePlayer.motionX *= 1.005f;
                    Minecraft.thePlayer.motionZ *= 1.005f;
                }
            }
        }
    }

    private double moveSpeed;

    public void shouldSpeed ( ) {

        MovementInput movementInput = Minecraft.thePlayer.movementInput;
        float forward = movementInput.moveForward;
        float strafe = movementInput.moveStrafe;
        float yaw = Minecraft.thePlayer.rotationYaw;
        if (forward == 0.0F && strafe == 0.0F) {
            Minecraft.thePlayer.motionX = 0.0D;
            Minecraft.thePlayer.motionZ = 0.0D;
        } else if (forward != 0.0F) {
            if (strafe > 0.0F) {
                yaw += ( float ) ( forward > 0.0F ? -45 : 45 );
                strafe = 0.0F;
            } else if (strafe < 0.0F) {
                yaw += ( float ) ( forward > 0.0F ? 45 : -45 );
                strafe = 0.0F;
            }

            if (forward > 0.0F) {
                forward = 1.0F;
            } else if (forward < 0.0F) {
                forward = -1.0F;
            }
        }

        double mx = Math.cos ( Math.toRadians ( yaw + 90.0F ) );
        double mz = Math.sin ( Math.toRadians ( yaw + 90.0F ) );
        Minecraft.thePlayer.motionX = ( ( double ) forward * this.moveSpeed * mx + ( double ) strafe * this.moveSpeed * mz );
        Minecraft.thePlayer.motionZ = ( ( double ) forward * this.moveSpeed * mz - ( double ) strafe * this.moveSpeed * mx );
        Minecraft.thePlayer.stepHeight = 0.6F;
        if (forward == 0.0F && strafe == 0.0F) {
            Minecraft.thePlayer.motionX = 0.0D;
            Minecraft.thePlayer.motionZ = 0.0D;
        }
    }

    private double getBaseMoveSpeed ( ) {
        double baseSpeed = 0.2873D;
        if (Minecraft.thePlayer.isPotionActive ( Potion.moveSpeed )) {
            int amplifier = Minecraft.thePlayer.getActivePotionEffect ( Potion.moveSpeed ).getAmplifier ( );
            baseSpeed *= 1.0D + 0.2D * ( double ) ( amplifier + 1 );
        }

        return baseSpeed;
    }

}
