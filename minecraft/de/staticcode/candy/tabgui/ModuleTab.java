package de.staticcode.candy.tabgui;

import de.staticcode.candy.Candy;
import de.staticcode.candy.gui.components.GuiComponent;
import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ModuleTab {

    private final int x;
    private final int y;

    public static int current = 0;

    public static int old = -1;
    private static float fadeAnimation;

    private final Category category;

    private final Minecraft mc = Minecraft.getMinecraft ( );

    public ModuleTab ( int x , int y , Category category ) {
        this.x = x - 1;
        this.y = y;
        this.category = category;
    }

    public List< Module > getSortedModuleList ( ) {
        List< Module > modules = new ArrayList<> ( );
        for ( Module mods : Module.getModuleList ( ) ) {
            if (mods.getCategory ( ) == this.category)
                modules.add ( mods );
        }
        modules.sort ( new Comparator< Module > ( ) {

            @Override
            public int compare ( Module o1 , Module o2 ) {
                if (GuiComponent.getByName ( "Hud Theme" ).getActiveMode ( ).equalsIgnoreCase ( "Compact" ))
                    return ( Candy.getVerdanaFont ( ).getStringWidth ( o2.getName ( ) ) - Candy.getVerdanaFont ( ).getStringWidth ( o1.getName ( ) ) );
                else
                    return ( Minecraft.fontRendererObj.getStringWidth ( o2.getName ( ) )
                            - Minecraft.fontRendererObj.getStringWidth ( o1.getName ( ) ) );
            }
        } );
        return modules;
    }

    public int getMaxModuleWidth ( ) {
        if (GuiComponent.getByName ( "Hud Theme" ).getActiveMode ( ).equalsIgnoreCase ( "Compact" ))
            return Candy.getVerdanaFont ( ).getStringWidth ( this.getSortedModuleList ( ).get ( 0 ).getName ( ) );
        else
            return Minecraft.fontRendererObj.getStringWidth ( this.getSortedModuleList ( ).get ( 0 ).getName ( ) );
    }

    public void drawGui ( ) {
        int yCount = this.y + 1;
        int id = 0;

        if (GuiComponent.getByName ( "Hud Theme" ).getActiveMode ( ).equalsIgnoreCase ( "Compact" ))
            yCount = this.y - 4;

        int fadeY = this.y;

        if (old != current) {
            if (old > current)
                fadeAnimation = 10F;
            else
                fadeAnimation = -10F;

            old = current;
        }

        if (fadeAnimation > 0F)
            fadeAnimation -= 0.30F;

        if (fadeAnimation < 0F)
            fadeAnimation += 0.30F;

        GL11.glPushMatrix ( );
        GL11.glScalef ( 1.0f , 1.0f , 1.0f );
        Gui.drawRect ( this.x - 1 , fadeY , this.x + this.getMaxModuleWidth ( ) + 1 ,
                fadeY + this.getSortedModuleList ( ).size ( ) * 10 , Candy.RGBtoHEX ( 0 , 0 , 0 , 150 ) );
        for ( Module modules : this.getSortedModuleList ( ) ) {

            if (current == id) {
                if (fadeAnimation > 0F) {
                    Gui.drawRect ( this.x - 1 , ( int ) ( fadeY + fadeAnimation ) ,
                            this.x + this.getMaxModuleWidth ( ) + 1 , ( int ) ( fadeY + 10 + fadeAnimation ) ,
                            Candy.RGBtoHEX ( 0 , 110 , 128 , 255 ) );
                } else {
                    Gui.drawRect ( this.x - 1 , ( int ) ( fadeY + fadeAnimation ) ,
                            this.x + this.getMaxModuleWidth ( ) + 1 , ( int ) ( fadeY + 10 + fadeAnimation ) ,
                            Candy.RGBtoHEX ( 0 , 110 , 128 , 255 ) );
                }
            }
            fadeY += 10;
            id++;
        }

        for ( Module modules : this.getSortedModuleList ( ) ) {

            if (!GuiComponent.getByName ( "Hud Theme" ).getActiveMode ( ).equalsIgnoreCase ( "Compact" ))
                mc.ingameGUI.drawCenteredString ( Minecraft.fontRendererObj , modules.getName ( ) ,
                        this.x + this.getMaxModuleWidth ( ) / 2 , yCount , -1 );
            else
                Candy.getVerdanaFont ( ).drawCenteredString ( modules.getName ( ) , this.x + this.getMaxModuleWidth ( ) / 2 , yCount + 1 , -1 );
            if (GuiComponent.getByName ( "Hud Theme" ).getActiveMode ( ).equalsIgnoreCase ( "Compact" ))
                yCount += 10;
            else
                yCount += 10;
        }
        GL11.glPopMatrix ( );
    }

}
