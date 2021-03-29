package de.staticcode.candy.tabgui;

import de.staticcode.candy.Candy;
import de.staticcode.candy.gui.components.GuiComponent;
import de.staticcode.candy.module.category.Category;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TabGui {

    private final int x;
    private final int y;

    private final Minecraft mc = Minecraft.getMinecraft ( );

    public static int current = 0;
    public static Category extend = null;

    private static int old = -1;
    private static float fadeAnimation;

    public TabGui ( int x , int y ) {
        this.x = x;
        this.y = y;
    }

    public int getMaxCategoryWidth ( ) {
        int maxWidth = 0;
        for ( Category category : Category.values ( ) ) {
            if (GuiComponent.getByName ( "Hud Theme" ).getActiveMode ( ).equalsIgnoreCase ( "Compact" )) {
                if (Candy.getVerdanaFont ( ).getStringWidth ( category.name ( ) ) > maxWidth)
                    maxWidth = Candy.getVerdanaFont ( ).getStringWidth ( category.name ( ) );
            } else {
                if (Minecraft.fontRendererObj.getStringWidth ( category.name ( ) ) > maxWidth)
                    maxWidth = Minecraft.fontRendererObj.getStringWidth ( category.name ( ) );
            }
        }
        return maxWidth;
    }

    public List< Category > getSortedCategoryList ( ) {
        List< Category > categorys = new ArrayList<> ( );

        for ( Category cates : Category.values ( ) ) {
            if (cates == Category.COMMANDS)
                continue;

            categorys.add ( cates );
        }
        categorys.sort ( new Comparator< Category > ( ) {

            @Override
            public int compare ( Category o1 , Category o2 ) {
                if (GuiComponent.getByName ( "Hud Theme" ).getActiveMode ( ).equalsIgnoreCase ( "Compact" ))
                    return Candy.getVerdanaFont ( ).getStringWidth ( o2.name ( ) ) - Candy.getVerdanaFont ( ).getStringWidth ( o1.name ( ) );
                else
                    return Minecraft.fontRendererObj.getStringWidth ( o2.name ( ) ) - Minecraft.fontRendererObj.getStringWidth ( o1.name ( ) );
            }
        } );

        return categorys;
    }

    public void drawGui ( ) {
        int yCount = this.y;
        int id = 0;

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

        if (extend != null) {
            new ModuleTab ( this.x + this.getMaxCategoryWidth ( ) + 15 , this.y , extend ).drawGui ( );
        }

        Gui.drawRect ( this.x , yCount , this.x + this.getMaxCategoryWidth ( ) + 3 , yCount + this.getSortedCategoryList ( ).size ( ) * 10 ,
                Candy.RGBtoHEX ( 0 , 0 , 0 , 150 ) );

        for ( Category category : this.getSortedCategoryList ( ) ) {

            if (current == id) {
                if (fadeAnimation > 0F) {
                    Gui.drawRect ( this.x , ( int ) ( fadeY + fadeAnimation ) ,
                            this.x + this.getMaxCategoryWidth ( ) + 3 , ( int ) ( fadeY + 10 + fadeAnimation ) ,
                            Candy.RGBtoHEX ( 0 , 110 , 128 , 255 ) );
                } else {
                    Gui.drawRect ( this.x , ( int ) ( fadeY + fadeAnimation ) ,
                            this.x + this.getMaxCategoryWidth ( ) + 3 , ( int ) ( fadeY + 10 + fadeAnimation ) ,
                            Candy.RGBtoHEX ( 0 , 110 , 128 , 255 ) );
                }
            }
            id++;
            fadeY += 10;
        }


        for ( Category category : this.getSortedCategoryList ( ) ) {
            String name = category.name ( ).substring ( 0 , 1 ).toUpperCase ( );
            name += category.name ( ).substring ( 1 ).toLowerCase ( );

            if (GuiComponent.getByName ( "Hud Theme" ).getActiveMode ( ).equalsIgnoreCase ( "Compact" ))
                Candy.getVerdanaFont ( ).drawStringWithShadow ( name , this.x + 1 , yCount - 2 , -1 );
            else
                Minecraft.fontRendererObj.drawStringWithShadow ( name , this.x + 1 ,
                        yCount + 1 , -1 );


            yCount += 10;

        }


    }

}
