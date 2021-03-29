package de.staticcode.candy.gui.ui;

import de.staticcode.candy.Candy;
import de.staticcode.candy.gui.ClickGUI;
import de.staticcode.candy.gui.components.GuiComponent;
import de.staticcode.candy.gui.components.GuiComponent.ComponentType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class GuiConsole {

    private int xPos, yPos;
    private final List< String > lastCommands = new ArrayList<> ( );

    private final Minecraft mc = Minecraft.getMinecraft ( );
    private boolean dragging;
    public boolean selected;

    private boolean pointing = false;

    private int dragX, dragY;

    private String currentText;
    private String dotString;
    private String lastCommand;

    public GuiConsole ( int x , int y ) {
        this.xPos = x;
        this.yPos = y;
    }

    public void onRender ( int mouseX , int mouseY ) {

        if (this.dragging) {
            this.xPos = mouseX - this.dragX;
            this.yPos = mouseY - this.dragY;

            Candy.getGuiPos ( ).setString ( "Console" , this.xPos + ":" + this.yPos );
            Candy.getGuiPos ( ).save ( );
        }

        boolean hovered = ClickGUI.isHovered ( mouseX , mouseY , this.xPos , this.yPos + 70 ,
                Minecraft.fontRendererObj.getStringWidth ( "Component Console" ) + 50 , 10 );

        if (this.lastCommands.size ( ) > 15) {
            this.lastCommands.remove ( 0 );
        }

        if (this.selected) {
            this.dotString = this.pointing ? "." : "";
            this.pointing = !this.pointing;
        }

        Gui.drawRect ( this.xPos - 10 , this.yPos - 5 ,
                this.xPos + Minecraft.fontRendererObj.getStringWidth ( "Component Console" ) + 50 , this.yPos + 5 ,
                this.dragging ? Candy.RGBtoHEX ( 0 , 130 , 170 , 255 ) : Candy.RGBtoHEX ( 0 , 0 , 0 , 255 ) );

        Gui.drawRect ( this.xPos - 10 , this.yPos + 5 ,
                this.xPos + Minecraft.fontRendererObj.getStringWidth ( "Component Console" ) + 50 , this.yPos + 70 ,
                Candy.RGBtoHEX ( 0 , 0 , 0 , 150 ) );

        Gui.drawRect ( this.xPos - 10 , this.yPos + 70 ,
                this.xPos + Minecraft.fontRendererObj.getStringWidth ( "Component Console" ) + 50 , this.yPos + 80 ,
                hovered ? Candy.RGBtoHEX ( 64 , 64 , 64 , 255 ) : Candy.RGBtoHEX ( 0 , 0 , 0 , 255 ) );

        if (this.selected) {
            Minecraft.fontRendererObj.drawString ( this.dotString ,
                    this.xPos + Minecraft.fontRendererObj.getStringWidth ( this.currentText ) - 8 , this.yPos + 71 , -1 );
        }

        int yCount = this.yPos + 65;

        GL11.glPushMatrix ( );
        GL11.glScalef ( 0.5F , 0.5F , 0.5F );
        for ( String strings : this.lastCommands ) {
            if (strings.length ( ) > 55)
                strings = strings.substring ( 0 , 55 );

            Minecraft.fontRendererObj.drawStringWithShadow ( strings , this.xPos * 2F - 15F , yCount * 2F ,
                    Candy.RGBtoHEX ( 0 , 128 , 0 , 255 ) );
            yCount -= 4;
        }
        GL11.glPopMatrix ( );

        mc.ingameGUI.drawCenteredString ( Minecraft.fontRendererObj , "Component Console" ,
                this.xPos + Minecraft.fontRendererObj.getStringWidth ( "Component Console" ) / 2 + 20 , this.yPos - 4 , -1 );

        Minecraft.fontRendererObj.drawString ( this.currentText , this.xPos - 9 , this.yPos + 71 , -1 );

    }

    public void keyTyped ( int keyCode ) {
        if (!this.selected)
            return;

        String keyName = Keyboard.getKeyName ( keyCode );

        if (keyName == null)
            return;

        if (keyName.contains ( "RETURN" )) {

            String[] splits = this.currentText.split ( " " );

            if (splits[ 0 ] != null && splits[ 0 ].length ( ) < 1) {
                this.currentText = "";
                return;
            }

            this.lastCommand = this.currentText;


            for ( GuiComponent guiComponent : GuiComponent.getComponents ( ) ) {
                if (guiComponent.getName ( ).replace ( " " , "" ).equalsIgnoreCase ( splits[ 0 ] )) {
                    if (guiComponent.getComponentType ( ) == ComponentType.BUTTON) {
                        try {
                            boolean toggled = splits[ 1 ].equalsIgnoreCase ( "on" ) || splits[ 1 ].equalsIgnoreCase ( "true" )
                                    || splits[ 1 ].equalsIgnoreCase ( "toggle" );
                            guiComponent.setToggled ( toggled );
                            this.lastCommands.add ( "Component " + guiComponent.getName ( ) + " of module "
                                    + guiComponent.getModule ( ).getName ( ) + " was set to " + toggled );
                            this.currentText = "";
                            return;
                        } catch ( Exception e ) {
                            this.lastCommands.add ( "§cPlease enter a valid type!" );
                        }
                    } else if (guiComponent.getComponentType ( ) == ComponentType.SLIDER) {
                        try {
                            double value = Double.valueOf ( splits[ 1 ] );
                            if (value > guiComponent.getMax ( )) {
                                this.lastCommands.add ( "§cThe value " + value + " is bigger then the maximum" );
                                this.currentText = "";
                                return;
                            }

                            if (value < guiComponent.getMin ( )) {
                                this.lastCommands.add ( "§cThe value " + value + " is lower then the minimum" );
                                this.currentText = "";
                                return;
                            }

                            guiComponent.setConfigCurrent ( value );
                            this.lastCommands.add ( "Component " + guiComponent.getName ( ) + " of module "
                                    + guiComponent.getModule ( ).getName ( ) + " was set to " + value );
                            this.currentText = "";
                            return;
                        } catch ( Exception e ) {
                            this.lastCommands.add ( "§cPlease enter a valid value!" );
                        }
                    } else if (guiComponent.getComponentType ( ) == ComponentType.MODE) {
                        String active = splits[ 1 ];
                        List< String > modes = new ArrayList<> ( );
                        for ( String m : guiComponent.getModes ( ) )
                            modes.add ( m.toUpperCase ( ).replace ( " " , "" ) );

                        if (!modes.contains ( active.toUpperCase ( ) )) {
                            this.lastCommands.add ( "Mode " + active + " was not found!" );
                            return;
                        }

                        int id = 0;
                        for ( String ms : modes ) {
                            if (!modes.get ( id ).equalsIgnoreCase ( active.toUpperCase ( ) ))
                                id++;
                        }

                        guiComponent.setActiveMode ( guiComponent.getModes ( ).get ( id ) );
                        this.lastCommands.add ( "Component " + guiComponent.getName ( ) + " of module "
                                + guiComponent.getModule ( ).getName ( ) + " was set to " + guiComponent.getActiveMode ( ) );
                        this.currentText = "";
                        return;
                    }
                }
            }

            this.currentText = "";

            this.lastCommands.add ( "§cComponent " + splits[ 0 ] + " was not found!" );
            return;
        }

        if (keyName.equalsIgnoreCase ( "PERIOD" )) {
            this.currentText += ".";
            return;
        }

        if (keyName.equalsIgnoreCase ( "BACK" )) {
            if (this.currentText != null && this.currentText.length ( ) > 0) {
                this.currentText = this.currentText.substring ( 0 , ( this.currentText.length ( ) - 1 ) );
            }
            return;
        }

        if (keyName.equalsIgnoreCase ( "SPACE" )) {
            this.currentText += " ";
            return;
        }

        if (keyName.equalsIgnoreCase ( "UP" )) {
            if (this.lastCommand != null) {
                this.currentText = this.lastCommand;
                return;
            }
        }

        if (keyName.equalsIgnoreCase ( "DOWN" )) {
            this.currentText = "";
            return;
        }
        if (keyName.length ( ) > 1 && !keyName.equalsIgnoreCase ( "PERIOD" ))
            return;

        if (this.currentText != null && this.currentText.length ( ) > 24)
            return;

        if (this.currentText == null || this.currentText.equalsIgnoreCase ( "" )) {
            this.currentText += keyName;
        } else {
            this.currentText += keyName.toLowerCase ( );
        }

        this.currentText = this.currentText.replace ( "null" , "" );
    }

    public void mouseReleased ( ) {
        if (!this.selected && !this.dragging)
            return;

        this.dragging = false;
    }

    public void mouseClicked ( int mouseX , int mouseY ) {
        this.selected = ClickGUI.isHovered ( mouseX , mouseY , this.xPos , this.yPos + 70 ,
                Minecraft.fontRendererObj.getStringWidth ( "Component Console" ) + 50 , 10 );

        if (ClickGUI.isHovered ( mouseX , mouseY , this.xPos , this.yPos ,
                Minecraft.fontRendererObj.getStringWidth ( "Component Console" ) + 50 , 10 )) {
            this.dragging = true;

            this.dragX = mouseX - this.xPos;
            this.dragY = mouseY - this.yPos;
        }
    }

    public int getxPos ( ) {
        return xPos;
    }

    public int getyPos ( ) {
        return yPos;
    }

    public List< String > getLastCommands ( ) {
        return lastCommands;
    }

}
