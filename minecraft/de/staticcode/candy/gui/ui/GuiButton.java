package de.staticcode.candy.gui.ui;

import de.staticcode.candy.Candy;
import de.staticcode.candy.gui.ClickGUI;
import de.staticcode.candy.gui.components.GuiComponent;
import de.staticcode.candy.gui.components.GuiComponent.ComponentType;
import de.staticcode.candy.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class GuiButton {

    private final GuiFrame guiFrame;
    private final Module module;
    private final int width;
    private final int height;
    private final int yCount;
    private boolean isExtend = false;
    private boolean isHovered;

    private final Minecraft mc = Minecraft.getMinecraft ( );
    public List< GuiExtend > extendList = new ArrayList<> ( );

    // Changeable
    private final int R = 0;
    private final int G = 70;
    private final int B = 170;
    private final boolean fadeAlpha = true;

    private float fadeDown;

    public GuiButton ( GuiFrame guiFrame , Module module , int yCount , int width , int height ) {
        this.guiFrame = guiFrame;
        this.module = module;
        this.yCount = yCount;
        this.width = width;
        this.height = height;

        int componentY = 10;
        for ( GuiComponent guiComponent : GuiComponent.getComponents ( ) ) {
            if (guiComponent.getModule ( ) == this.module) {
                this.extendList.add ( new GuiExtend ( this , guiComponent , componentY , this.getMaxComponentWidth ( ) ,
                        guiComponent.getComponentType ( ) == ComponentType.SLIDER ? 16 : 11 ) );
                componentY += 10;
            }
        }

    }

    public int getMaxComponentWidth ( ) {
        int maxWidth = 0;

        for ( GuiComponent guiComponent : GuiComponent.getComponents ( ) ) {
            if (guiComponent.getModule ( ) == this.module) {
                if (Minecraft.fontRendererObj.getStringWidth ( guiComponent.getName ( ) ) > maxWidth)
                    maxWidth = Minecraft.fontRendererObj.getStringWidth ( guiComponent.getName ( ) ) + 10;

                if (guiComponent.getComponentType ( ) == ComponentType.MODE) {
                    int maxOptionsWidth = 0;
                    for ( String options : guiComponent.getModes ( ) ) {
                        if (Minecraft.fontRendererObj.getStringWidth ( options ) > maxOptionsWidth)
                            maxOptionsWidth = ( Minecraft.fontRendererObj.getStringWidth ( options ) ) - 34;
                    }
                    maxWidth += maxOptionsWidth;
                } else if (guiComponent.getComponentType ( ) == ComponentType.SLIDER) {
                    maxWidth += Minecraft.fontRendererObj.getStringWidth ( guiComponent.getMax ( ) + "" ) / 2 - 10;
                }

            }
        }

        return maxWidth;
    }

    public GuiFrame getGuiFrame ( ) {
        return guiFrame;
    }

    public Module getModule ( ) {
        return module;
    }

    public int xPos ( ) {
        return this.guiFrame.xPos ( );
    }

    public int yPos ( ) {
        return this.guiFrame.yPos ( ) + this.yCount;
    }

    public int getWidth ( ) {
        return width;
    }

    public int getHeight ( ) {
        return height;
    }

    public boolean isExtend ( ) {
        return isExtend;
    }

    private void setPositions ( ) {
        int yCount = 0;
        for ( GuiExtend extend : this.extendList ) {
            extend.setyPos ( this.yPos ( ) + yCount );
            extend.setxPos ( this.xPos ( ) + this.getWidth ( ) + 10 );

            if (!extend.getGuiComponent ( ).isRender ( ))
                continue;

            if (extend.getGuiComponent ( ).getComponentType ( ) == ComponentType.BUTTON)
                yCount += 11;
            else if (extend.getGuiComponent ( ).getComponentType ( ) == ComponentType.SLIDER)
                yCount += 16;
            else if (extend.getGuiComponent ( ).getComponentType ( ) == ComponentType.MODE) {
                if (!extend.getGuiComponent ( ).isExtend ( )) {
                    yCount += 11;
                } else {
                    yCount += 10;
                    for ( String options : extend.getGuiComponent ( ).getModes ( ) ) {
                        yCount += 10;
                    }
                }
            }
        }
    }

    public void onRender ( int mouseX , int mouseY ) {
        this.setPositions ( );

        this.isHovered = ClickGUI.isHovered ( mouseX , mouseY , this.xPos ( ) , this.yPos ( ) , this.getWidth ( ) + 6 ,
                this.getHeight ( ) / 2 + 4 );

        double showAlpha = Math.abs ( ( Math.sin ( System.currentTimeMillis ( ) / 650d ) * 255d ) );

        if (showAlpha < 175d)
            showAlpha = 175d;

        if (showAlpha > 255d)
            showAlpha = 255d;

        int moduleColor = ClickGUI.RGBtoHEX ( this.R , this.G , this.B , 255 );

        if (this.fadeAlpha) {
            moduleColor = ClickGUI.RGBtoHEX ( this.R , this.G , this.B , ( int ) showAlpha );
        }

        if (this.fadeDown > 0)
            this.fadeDown -= 0.30f;

        if (this.module.isToggled ( )) {

            Gui.drawRect ( this.xPos ( ) - 2 , this.yPos ( ) - this.height / 20 - 5 , this.xPos ( ) ,
                    this.yPos ( ) + this.height + 1 , ClickGUI.RGBtoHEX ( 0 , 128 , 170 , 255 ) );

            Gui.drawRect ( this.xPos ( ) + this.width + 9 , this.yPos ( ) - this.height / 20 - 5 ,
                    this.xPos ( ) + this.width + 7 , this.yPos ( ) + this.height + 1 , ClickGUI.RGBtoHEX ( 0 , 128 , 170 , 255 ) );

            Gui.drawRect ( this.xPos ( ) , this.yPos ( ) + this.height / 20 - 5 , this.xPos ( ) + this.width + 7 ,
                    ( int ) ( this.yPos ( ) + this.height - this.fadeDown + 1 ) , Candy.RGBtoHEX ( 0 , 0 , 0 , 200 ) );

            Gui.drawRect ( this.xPos ( ) + 5 , this.yPos ( ) - this.height / 20 - 5 + 1 ,
                    this.xPos ( ) + this.width + 2 , ( int ) ( this.yPos ( ) + this.height - this.fadeDown ) , moduleColor );
        } else {

            Gui.drawRect ( this.xPos ( ) - 2 , this.yPos ( ) - this.height / 20 - 5 , this.xPos ( ) ,
                    this.yPos ( ) + this.height , ClickGUI.RGBtoHEX ( 0 , 128 , 170 , 255 ) );

            Gui.drawRect ( this.xPos ( ) + this.width + 9 , this.yPos ( ) - this.height / 20 - 5 ,
                    this.xPos ( ) + this.width + 7 , this.yPos ( ) + this.height , ClickGUI.RGBtoHEX ( 0 , 128 , 170 , 255 ) );

            Gui.drawRect ( this.xPos ( ) , this.yPos ( ) - this.height / 20 - 5 , this.xPos ( ) + this.width + 7 ,
                    this.yPos ( ) + this.height , ClickGUI.RGBtoHEX ( 0 , 0 , 0 , 150 ) );
        }


        GL11.glPushMatrix ( );
        mc.currentScreen.drawCenteredString ( Minecraft.fontRendererObj ,
                this.isHovered ? "§8" + this.module.getName ( ) : this.module.getName ( ) , this.xPos ( ) + this.width / 2 + 3 ,
                this.yPos ( ) - 1 , this.module.isToggled ( ) ? ClickGUI.RGBtoHEX ( 128 , 128 , 128 , 255 )
                        : ClickGUI.RGBtoHEX ( 255 , 255 , 255 , 255 ) );

        if (!this.extendList.isEmpty ( )) {
            Minecraft.fontRendererObj.drawStringWithShadow ( this.isExtend ? ">" : "<" , this.xPos ( ) + this.width + 2 ,
                    this.yPos ( ) - 1 , ClickGUI.RGBtoHEX ( 255 , 255 , 255 , 255 ) );
        }

        GL11.glPopMatrix ( );

        if (this.isExtend) {

            for ( GuiExtend extend : this.extendList ) {
                if (!extend.getGuiComponent ( ).isRender ( ))
                    continue;

                extend.onRender ( mouseX , mouseY );
            }
        }

    }

    public void onMouseReleased ( ) {
        if (this.isExtend) {
            for ( GuiExtend extendGuis : this.extendList ) {
                extendGuis.onMouseReleased ( );
            }
        }
    }

    public void onClick ( int mouseX , int mouseY , int mouseButton ) {
        if (this.isHovered) {
            if (mouseButton == 0) {
                this.module.setToggled ( !this.module.isToggled ( ) );
                Minecraft.thePlayer.playSound ( "random.click" , 0.5f , 1.0f );

                if (this.module.isToggled ( ))
                    this.fadeDown = 10F;
                else
                    this.fadeDown = 0F;
            } else if (mouseButton == 1) {
                this.isExtend = !this.isExtend;
                if (!this.extendList.isEmpty ( ))
                    Minecraft.thePlayer.playSound ( "random.bow" , 0.5f , 1f );
            } else if (mouseButton == 2) {
                ClickGUI.waitingForKeyPress = true;
                ClickGUI.waitModule = this.module;
            }
        }

        if (this.isExtend) {
            for ( GuiExtend extend : this.extendList ) {
                extend.onClick ( mouseX , mouseY );
            }
        }
    }

}
