package de.staticcode.candy.balls;

import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class Ball {
    public static List< Ball > instances = new ArrayList ( );
    private double x;
    private double y;
    private double motionX;
    private double motionY;
    private double radius = 7.0D;
    public static double maxMotion = 0.8D;

    private final int blink = 0;

    public double getX ( ) {
        return this.x;
    }

    public double getY ( ) {
        return this.y;
    }

    public Ball ( double x , double y ) {
        this.x = x;
        this.y = y;

        maxMotion = 1.5f;

        this.motionY = ( Math.random ( ) * maxMotion );
        this.motionX = ( Math.random ( ) * maxMotion );

        instances.add ( this );

        this.radius = Math.random ( ) + 0.1;
    }

    public double distance ( double x , double y ) {
        double a = this.x - x;
        double b = this.y - y;
        return Math.sqrt ( a * a + b * b );
    }

    public void render ( ) {
        animate ( );

        GL11.glColor4f ( 1 , 1 , 1 , 1 );

        GL11.glPushMatrix ( );
        GL11.glBegin ( 6 );
        for ( double d = 0.0D; d <= 300.0D; d += 1.0D ) {
            double Angle = d * 0.06283185307179587D;
            double Y = radius * Math.cos ( Angle ) + this.y;
            double X = radius * Math.sin ( Angle ) + this.x;
            GL11.glVertex2d ( X , Y );
        }
        GL11.glEnd ( );
        GL11.glPopMatrix ( );
    }

    public void drawLineTo ( Ball ball ) {
        GL11.glPushMatrix ( );
        GL11.glBegin ( 1 );
        GL11.glColor4f ( 255.0F , 255.0F , 255.0F , 255.0F );
        GL11.glVertex2d ( getX ( ) , getY ( ) );
        GL11.glVertex2d ( ball.getX ( ) , ball.getY ( ) );
        GL11.glEnd ( );
        GL11.glPopMatrix ( );
    }

    private void checkOutOfFrame ( ) {

        if (this.x > ScaledResolution.getScaledWidth ( ) + radius) {
            this.x = ( -radius );
            this.y = ( ScaledResolution.getScaledHeight ( ) - this.y );
        } else if (this.x <= -radius) {
            this.x = ( ScaledResolution.getScaledWidth ( ) + radius );
            this.y = ( ScaledResolution.getScaledHeight ( ) - this.y );
        }
        if (this.y > ScaledResolution.getScaledHeight ( ) + radius) {
            this.y = ( -radius );
            this.x = ( ScaledResolution.getScaledWidth ( ) - this.x );
        } else if (this.y <= -radius) {
            this.y = ( ScaledResolution.getScaledHeight ( ) + radius );
            this.x = ( ScaledResolution.getScaledWidth ( ) - this.x );
        }
    }

    private void animate ( ) {
        checkOutOfFrame ( );

        this.motionY += Math.random ( ) / 20.0D - 0.025D;
        this.motionX += Math.random ( ) / 20.0D - 0.025D;

        if (this.motionY > maxMotion) {
            this.motionY = maxMotion;
        } else if (this.motionY < 0.0D) {
            this.motionY = 0.0D;
        }
        if (this.motionX > maxMotion) {
            this.motionX = maxMotion;
        } else if (this.motionX < 0.0D) {
            this.motionX = 0.0D;
        }
        this.y -= this.motionY;
        this.x -= this.motionX;
    }
}