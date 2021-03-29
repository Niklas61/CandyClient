package de.staticcode.candy.gui.ui;

import de.staticcode.candy.gui.ClickGUI;
import de.staticcode.candy.gui.components.GuiComponent;
import de.staticcode.candy.gui.components.GuiComponent.ComponentType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiExtend {

    private final GuiButton guiButton;
    private final GuiComponent guiComponent;

    private int xPos, yPos;
    private final int width;
    private final int height;
    private int yCount;

    private long lastClick;

    protected static GuiComponent currentSlider;
    protected static GuiButton currentButton;
    protected static boolean isUpdating;

    private final Minecraft mc = Minecraft.getMinecraft ( );

    public GuiExtend ( GuiButton guiButton , GuiComponent guiComponent , int yCount , int width , int height ) {
        this.guiButton = guiButton;
        this.guiComponent = guiComponent;
        this.yCount = yCount;
        this.width = width;
        this.height = height;
        this.yPos = this.guiButton.getGuiFrame ( ).yPos ( ) + this.yCount;
        this.xPos = this.guiButton.xPos ( ) + this.guiButton.getWidth ( );
        this.guiComponent.setGuiButton ( this.guiButton );
        this.lastClick = Minecraft.getSystemTime ( );
    }

    public int xPos ( ) {
        return this.xPos;
    }

    public int yPos ( ) {
        return this.yPos;
    }

    public int getyCount ( ) {
        return yCount;
    }

    public void setyCount ( int yCount ) {
        this.yCount = yCount;
    }

    public void setxPos ( int xPos ) {
        this.xPos = xPos;
    }

    public void setyPos ( int yPos ) {
        this.yPos = yPos;
    }

    public int getWidth ( ) {
        return width;
    }

    public int getHeight ( ) {
        return height;
    }

    public GuiButton getGuiButton ( ) {
        return guiButton;
    }

    public GuiComponent getGuiComponent ( ) {
        return guiComponent;
    }

    public void onRender ( int mouseX , int mouseY ) {

        if (this.guiComponent.getComponentType ( ) == ComponentType.BUTTON) {

            this.guiComponent.setHovered ( ClickGUI.isHovered ( mouseX , mouseY , this.xPos ( ) + 1 , this.yPos ( ) ,
                    this.getWidth ( ) + 18 , this.getHeight ( ) / 2 + 2 ) );

            Gui.drawRect ( this.xPos ( ) + 1 , this.yPos ( ) - this.height / 15 , this.xPos ( ) + this.width + 20 ,
                    this.yPos ( ) + this.height , ClickGUI.RGBtoHEX ( 0 , 0 , 0 , 200 ) );

            Minecraft.fontRendererObj.drawStringWithShadow (
                    this.guiComponent.isHovered ( ) ? "§7" + this.guiComponent.getName ( ) : this.guiComponent.getName ( ) ,
                    this.xPos ( ) + 17 , this.yPos ( ) + 2 , ClickGUI.RGBtoHEX ( 255 , 255 , 255 , 255 ) );

            GlStateManager.pushMatrix ( );
            GlStateManager.disableTexture2D ( );
            if (this.guiComponent.isToggled ( ))
                GlStateManager.colorDiv ( 0f , 128f , 170f , 255f );
            GL11.glBegin ( 6 );
            for ( double d = 0.0D; d <= 100.0D; d += 1.0D ) {
                double Angle = d * 0.06283185307179587D;
                double Y = 5d * Math.cos ( Angle ) + this.yPos + 5.5d;
                double X = 6d * Math.sin ( Angle ) + this.xPos ( ) + 8;
                GL11.glVertex2d ( X , Y );
            }
            GL11.glEnd ( );
            GlStateManager.enableTexture2D ( );
            GlStateManager.popMatrix ( );

        } else if (this.guiComponent.getComponentType ( ) == ComponentType.SLIDER) {

            boolean updateSlider = true;
            if (currentButton != null && currentSlider != null && currentSlider != this.guiComponent
                    && currentButton == this.guiButton && isUpdating)
                updateSlider = false;

            if (ClickGUI.isHovered ( mouseX , mouseY , this.xPos ( ) + 1 , this.yPos ( ) , this.width + 20 , this.height / 2 + 2 )
                    && updateSlider) {
                this.guiComponent.setHovered ( true );
                if (Mouse.isButtonDown ( 0 ))
                    this.sliderUpdate ( mouseX , this.guiComponent );
            } else {
                this.guiComponent.setHovered ( false );
            }

            Gui.drawRect ( this.xPos ( ) + 1 , this.yPos ( ) - this.height / 15 + 1 ,
                    this.xPos ( ) + this.width + 20 , this.yPos ( ) + this.height , ClickGUI.RGBtoHEX ( 0 , 0 , 0 , 200 ) );

            Gui.drawRect ( this.xPos ( ) + 1 , this.yPos ( ) + 9 ,
                    ( int ) ( this.xPos ( )
                            + ( this.guiComponent.getCurrent ( ) / this.guiComponent.getMax ( ) * this.getWidth ( ) + 21 ) ) ,
                    this.yPos ( ) + this.height , ClickGUI.RGBtoHEX ( 0 , 0 , 0 , 255 ) );

            Gui.drawRect ( this.xPos ( ) + 1 , this.yPos ( ) + 10 ,
                    ( int ) ( this.xPos ( )
                            + ( this.guiComponent.getCurrent ( ) / this.guiComponent.getMax ( ) * this.getWidth ( ) + 20 ) ) ,
                    this.yPos ( ) + this.height - 1 , ClickGUI.RGBtoHEX ( 0 , 128 , 170 , 255 ) );

            Minecraft.fontRendererObj.drawStringWithShadow (
                    this.guiComponent.isHovered ( ) ? "§8" + this.guiComponent.getName ( ) : this.guiComponent.getName ( ) ,
                    this.xPos ( ) + 2 , this.yPos ( ) + 1 , ClickGUI.RGBtoHEX ( 255 , 255 , 255 , 255 ) );

            Minecraft.fontRendererObj.drawStringWithShadow ( " " + this.guiComponent.getCurrent ( ) ,
                    this.xPos ( ) + Minecraft.fontRendererObj.getStringWidth ( this.guiComponent.getName ( ) ) , this.yPos ( ) + 1 ,
                    ClickGUI.RGBtoHEX ( 128 , 128 , 128 , 255 ) );

            GlStateManager.pushMatrix ( );
            GlStateManager.disableTexture2D ( );
            if (this.guiComponent.isHovered ( ))
                GlStateManager.colorDiv ( 255f , 255f , 255f , 255f );
            GL11.glBegin ( 6 );
            for ( double d = 0.0D; d <= 100.0D; d += 1.0D ) {
                double Angle = d * 0.06283185307179587D;
                double Y = 4d * Math.cos ( Angle ) + this.yPos + 12;
                double X = 4d * Math.sin ( Angle ) + this.xPos ( )
                        + ( this.guiComponent.getCurrent ( ) / this.guiComponent.getMax ( ) * this.getWidth ( ) + 20 );
                GL11.glVertex2d ( X , Y );
            }
            GL11.glEnd ( );
            GlStateManager.enableTexture2D ( );
            GlStateManager.popMatrix ( );

        } else if (this.guiComponent.getComponentType ( ) == ComponentType.MODE) {

            this.guiComponent.setHovered (
                    ClickGUI.isHovered ( mouseX , mouseY , this.xPos ( ) , this.yPos ( ) , this.width , this.height / 2 ) );

            Gui.drawRect ( this.xPos ( ) + 1 , this.yPos ( ) - this.height / 30 , this.xPos ( ) + this.width + 20 ,
                    this.yPos ( ) + this.height , ClickGUI.RGBtoHEX ( 0 , 0 , 0 , 200 ) );

            mc.currentScreen.drawCenteredString ( Minecraft.fontRendererObj ,
                    this.guiComponent.isHovered ( ) ? "§7" + this.guiComponent.getName ( ) : this.guiComponent.getName ( ) ,
                    this.xPos ( ) + this.width / 2 + 15 , this.yPos ( ) + 1 , ClickGUI.RGBtoHEX ( 255 , 255 , 255 , 255 ) );

            if (this.guiComponent.isExtend ( )) {
                int yCount = 10;

                for ( String options : this.guiComponent.getModes ( ) ) {

                    this.guiComponent.setModeHovered ( ClickGUI.isHovered ( mouseX , mouseY , this.xPos ( ) + 1 ,
                            this.yPos ( ) + yCount + 1 , this.width + 18 , 5 ) );

                    Gui.drawRect ( this.xPos ( ) + 1 , ( this.yPos ( ) + yCount ) - this.height / 15 + 1 ,
                            this.xPos ( ) + this.width + 20 , this.yPos ( ) + yCount + this.height ,
                            ClickGUI.RGBtoHEX ( 0 , 0 , 0 , 220 ) );

                    Gui.drawRect ( this.xPos ( ) + this.width + 20 ,
                            ( this.yPos ( ) + yCount ) - this.height / 15 + 1 , this.xPos ( ) + this.width + 21 ,
                            this.yPos ( ) + yCount + this.height , ClickGUI.RGBtoHEX ( 0 , 128 , 170 , 220 ) );


                    mc.currentScreen.drawCenteredString ( Minecraft.fontRendererObj ,
                            this.guiComponent.isModeHovered ( )
                                    ? "§7" + options.substring ( 0 , 1 ).toUpperCase ( ) + options.substring ( 1 ).toLowerCase ( )
                                    : options.substring ( 0 , 1 ).toUpperCase ( ) + options.substring ( 1 ).toLowerCase ( ) ,
                            this.xPos ( ) + this.width / 2 + 15 , this.yPos ( ) + yCount + 1 ,
                            this.guiComponent.getActiveMode ( ).equalsIgnoreCase ( options )
                                    ? ClickGUI.RGBtoHEX ( 128 , 128 , 128 , 255 ) : ClickGUI.RGBtoHEX ( 255 , 255 , 255 , 255 ) );

                    if (this.guiComponent.getActiveMode ( ).equalsIgnoreCase ( options )) {
                        GlStateManager.pushMatrix ( );
                        GlStateManager.disableTexture2D ( );

                        GlStateManager.colorDiv ( 0f , 128f , 170f , 255f );
                        GL11.glBegin ( 6 );
                        for ( double d = 0.0D; d <= 100.0D; d += 1.0D ) {
                            double Angle = d * 0.06283185307179587D;
                            double Y = 5d * Math.cos ( Angle ) + ( this.yPos + yCount + 5 );
                            double X = 5d * Math.sin ( Angle ) + this.xPos ( ) + 7;
                            GL11.glVertex2d ( X , Y );
                        }
                        GL11.glEnd ( );
                        GlStateManager.enableTexture2D ( );
                        GlStateManager.popMatrix ( );
                    }
                    yCount += 10;
                }
            }
        }
    }

    private void sliderUpdate ( double mouseX , GuiComponent slider ) {
        double startX = this.xPos + 19;
        double endX = this.xPos + this.width + 20;
        double betweens = mouseX - startX;
        if (betweens < 0.0D)
            return;

        betweens /= this.getWidth ( ) / slider.getMax ( );

        if (betweens > slider.getMax ( ))
            betweens = slider.getMax ( );

        if (slider.getMin ( ) > betweens)
            betweens = slider.getMin ( );

        betweens = ( Math.round ( betweens * 10d ) ) / 10d;
        slider.setCurrent ( betweens );

        currentSlider = slider;
        currentButton = this.guiButton;
        isUpdating = true;
    }

    public void onMouseReleased ( ) {
        if (isUpdating) {
            currentButton = null;
            currentSlider = null;
            isUpdating = false;
        }
    }

    public void onClick ( int mouseX , int mouseY ) {
        if (this.guiComponent.isHovered ( )) {
            if (this.guiComponent.getComponentType ( ) == ComponentType.BUTTON) {
                if (Math.abs ( this.lastClick - Minecraft.getSystemTime ( ) ) > 100d) {
                    this.guiComponent.setToggled ( !this.guiComponent.isToggled ( ) );
                    Minecraft.thePlayer.playSound ( "note.hat" , 0.5f , 0.3f );

                    this.lastClick = Minecraft.getSystemTime ( );
                }

            } else if (this.guiComponent.getComponentType ( ) == ComponentType.MODE) {
                if (this.guiComponent.isHovered ( )) {
                    this.guiComponent.setExtend ( !this.guiComponent.isExtend ( ) );
                    if (!this.guiComponent.isExtend ( )) {
                        Minecraft.thePlayer.playSound ( "liquid.lavapop" , 0.5f , 0.5f );
                    } else {
                        Minecraft.thePlayer.playSound ( "liquid.lavapop" , 0.5f , 0.5f );
                    }
                }
            }
        }

        if (this.guiComponent.isExtend ( )) {
            int yCount = 10;
            for ( String options : this.guiComponent.getModes ( ) ) {

                if (ClickGUI.isHovered ( mouseX , mouseY , this.xPos ( ) + 1 , this.yPos ( ) + yCount + 1 , this.width + 18 , 5 )
                        && !this.guiComponent.getActiveMode ( ).equalsIgnoreCase ( options )) {
                    this.guiComponent.setActiveMode ( options );
                    Minecraft.thePlayer.playSound ( "random.click" , 0.5f , 1f );
                }
                yCount += 10;
            }
        }

    }

}
