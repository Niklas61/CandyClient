package de.staticcode.candy.gui;

import de.staticcode.candy.Candy;
import de.staticcode.candy.balls.Ball;
import de.staticcode.candy.gui.ui.GuiConsole;
import de.staticcode.candy.gui.ui.GuiFrame;
import de.staticcode.candy.gui.ui.GuiMenu;
import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;

public class ClickGUI extends GuiScreen {

    public static ArrayList< GuiFrame > frameList = new ArrayList<> ( );
    public static boolean waitingForKeyPress = false;

    public static GuiConsole guiConsole;
    public static GuiMenu guiMenu;

    public static boolean isHovered ( int mouseX , int mouseY , int x , int y , int width , int height ) {
        return ( mouseX >= x && mouseX - width <= x && mouseY + height / 2 >= y && mouseY - height <= y );
    }

    public static int RGBtoHEX ( int r , int g , int b , int a ) {
        return ( a << 24 ) + ( r << 16 ) + ( g << 8 ) + b;
    }

    public int getMaxModuleWidth ( Category category ) {
        int maxWidth = 0;

        for ( Module modules : Module.getModuleList ( ) ) {
            if (modules.getCategory ( ) == category) {
                if (Minecraft.fontRendererObj.getStringWidth ( modules.getName ( ) ) > maxWidth)
                    maxWidth = Minecraft.fontRendererObj.getStringWidth ( modules.getName ( ) );
            }
        }

        return maxWidth;
    }

    private void savePositions ( Category category , int x ) {
        Candy.getGuiPos ( ).setString ( category.name ( ) , x + ":" + 15 );
        Candy.getGuiPos ( ).save ( );
    }

    @Override
    public void onGuiClosed ( ) {

        if (this.mc.entityRenderer.getShaderGroup ( ) != null) {
            this.mc.entityRenderer.getShaderGroup ( ).deleteShaderGroup ( );
            this.mc.entityRenderer.setTheShaderGroup ( null );
        }

        mc.gameSettings.guiScale = guiScale;

        super.onGuiClosed ( );
    }


    int guiScale;

    @Override
    public void initGui ( ) {

        this.guiScale = mc.gameSettings.guiScale;
        mc.gameSettings.guiScale = 2;

        this.buttonList.clear ( );
        Ball.instances.clear ( );

        for ( int i = 0; i < 100; i++ ) {
            new Ball ( this.width * Math.random ( ) , this.height * Math.random ( ) );
        }

        Keyboard.enableRepeatEvents ( true );

        if (guiConsole == null) {
            if (!Candy.getGuiPos ( ).containsValue ( "Console" )) {
                guiConsole = new GuiConsole ( ScaledResolution.getScaledWidth ( ) / 2 ,
                        ScaledResolution.getScaledHeight ( ) / 2 );
            } else {
                int xPos = Integer.valueOf ( Candy.getGuiPos ( ).getString ( "Console" ).split ( ":" )[ 0 ] );
                int yPos = Integer.valueOf ( Candy.getGuiPos ( ).getString ( "Console" ).split ( ":" )[ 1 ] );
                guiConsole = new GuiConsole ( xPos , yPos );
            }
        } else {
            guiConsole.selected = false;
        }

        if (guiMenu == null) {
            guiMenu = new GuiMenu ( 5 , ScaledResolution.getScaledHeight ( ) - 16 );
        }

        if (guiMenu != null) {
            if (guiMenu.getyPos ( ) != ScaledResolution.getScaledHeight ( ) - 16) {
                guiMenu = new GuiMenu ( 5 , ScaledResolution.getScaledHeight ( ) - 16 );
            }
        }

        if (OpenGlHelper.shadersSupported) {
            if (this.mc.entityRenderer.getShaderGroup ( ) != null) {
                this.mc.entityRenderer.getShaderGroup ( ).deleteShaderGroup ( );
            }
            this.mc.entityRenderer.loadShader ( new ResourceLocation ( "shaders/post/blur.json" ) );
        }

        if (frameList.isEmpty ( )) {
            int width = 10;

            for ( Category category : Category.values ( ) ) {

                if (category == Category.COMMANDS)
                    continue;

                if (!Candy.getGuiPos ( ).containsValue ( category.name ( ) )) {
                    GuiFrame frame = new GuiFrame ( category , width , 15 );

                    if (Candy.getGuiPos ( ).containsValue ( category.name ( ) + ".Show" )) {
                        frame.show = Candy.getGuiPos ( ).getBoolean ( category.name ( ) + ".Show" );
                        frame.setExtend ( Candy.getGuiPos ( ).getBoolean ( category.name ( ) + ".Show" ) );
                    }

                    frameList.add ( frame );

                    this.savePositions ( category , width );
                } else {
                    int posWidth = Integer.valueOf ( Candy.getGuiPos ( ).getString ( category.name ( ) ).split ( ":" )[ 0 ] );
                    int posHeight = Integer.valueOf ( Candy.getGuiPos ( ).getString ( category.name ( ) ).split ( ":" )[ 1 ] );
                    GuiFrame frame = new GuiFrame ( category , posWidth , posHeight );

                    if (Candy.getGuiPos ( ).containsValue ( category.name ( ) + ".Show" )) {
                        frame.show = Candy.getGuiPos ( ).getBoolean ( category.name ( ) + ".Show" );
                        frame.setExtend ( Candy.getGuiPos ( ).getBoolean ( category.name ( ) + ".Show" ) );
                    }
                    frameList.add ( frame );
                }

                if (Minecraft.fontRendererObj.getStringWidth ( category.name ( ) ) > 60 + this.getMaxModuleWidth ( category ) / 2)
                    width += Minecraft.fontRendererObj.getStringWidth ( category.name ( ) );
                else
                    width += 60 + this.getMaxModuleWidth ( category ) / 2;

            }
        }
        super.initGui ( );
        this.mc.func_181537_a ( false );
    }

    private String waitString = ".";
    public static Module waitModule = null;

    @Override
    public void drawScreen ( int mouseX , int mouseY , float partialTicks ) {
        GlStateManager.pushMatrix ( );
        GlStateManager.disableTexture2D ( );
        for ( Ball balls : Ball.instances ) {
            balls.render ( );
        }
        GlStateManager.enableTexture2D ( );
        GlStateManager.popMatrix ( );

        for ( GuiFrame frame : frameList ) {
            frame.onRender ( mouseX , mouseY );
        }

        guiConsole.onRender ( mouseX , mouseY );
        guiMenu.onRender ( mouseX , mouseY );

        if (waitingForKeyPress) {
            this.drawDefaultBackground ( );

            if (this.waitString.equalsIgnoreCase ( "." )) {
                this.waitString = "..";
            } else if (this.waitString.equalsIgnoreCase ( ".." )) {
                this.waitString = "...";
            } else if (this.waitString.equalsIgnoreCase ( "..." )) {
                this.waitString = ".";
            }

            GL11.glPushMatrix ( );
            this.drawString ( Minecraft.fontRendererObj , "Waiting" + this.waitString , ScaledResolution.getScaledWidth ( ) / 2 ,
                    ScaledResolution.getScaledHeight ( ) / 2 , RGBtoHEX ( 255 , 255 , 255 , 255 ) );
            this.drawString ( Minecraft.fontRendererObj , "Press any key to bind the module" ,
                    ScaledResolution.getScaledWidth ( ) / 2
                            - Minecraft.fontRendererObj.getStringWidth ( "Press any key to bind the module" ) / 3 - 10 ,
                    ScaledResolution.getScaledHeight ( ) / 2 + 10 , RGBtoHEX ( 255 , 255 , 255 , 255 ) );
            this.drawString ( Minecraft.fontRendererObj , "Press RETURN to unbind the module" ,
                    ScaledResolution.getScaledWidth ( ) / 2
                            - Minecraft.fontRendererObj.getStringWidth ( "Press RETURN to unbind the module" ) / 3 - 10 ,
                    ScaledResolution.getScaledHeight ( ) / 2 + 20 , RGBtoHEX ( 255 , 255 , 255 , 255 ) );
            GL11.glPopMatrix ( );
        }

        super.drawScreen ( mouseX , mouseY , partialTicks );
    }

    @Override
    protected void mouseClicked ( int mouseX , int mouseY , int mouseButton ) throws IOException {

        for ( GuiFrame frame : ( ArrayList< GuiFrame > ) frameList.clone ( ) ) {
            frame.onClick ( mouseX , mouseY , mouseButton );
        }

        guiMenu.onClick ( mouseX , mouseY , mouseButton );
        guiConsole.mouseClicked ( mouseX , mouseY );
    }

    @Override
    protected void keyTyped ( char typedChar , int keyCode ) throws IOException {
        if (waitingForKeyPress) {
            waitingForKeyPress = false;
            if (Keyboard.getKeyName ( keyCode ).contains ( "RETURN" )) {
                waitModule.setKey ( 0 );
                waitModule = null;
                return;
            }

            waitModule.setKey ( keyCode );
            waitModule = null;
        }

        guiConsole.keyTyped ( keyCode );
        super.keyTyped ( typedChar , keyCode );
    }

    @Override
    protected void mouseReleased ( int mouseX , int mouseY , int state ) {

        for ( GuiFrame frame : frameList ) {
            frame.mouseReleased ( mouseX , mouseY );
        }

        guiConsole.mouseReleased ( );
    }

}
