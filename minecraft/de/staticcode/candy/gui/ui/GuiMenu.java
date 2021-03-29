package de.staticcode.candy.gui.ui;

import de.staticcode.candy.Candy;
import de.staticcode.candy.gui.ClickGUI;
import de.staticcode.candy.module.category.Category;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;

import java.util.*;

public class GuiMenu {

    private final int xPos;
    private final int yPos;

    private boolean isExtend;
    private float fadeUp;

    private final Map< Category, Integer > catePositions = new HashMap<> ( );

    private final Minecraft mc = Minecraft.getMinecraft ( );

    public GuiMenu ( int x , int y ) {
        this.xPos = x;
        this.yPos = y;

        int yCount = this.yPos - 25;
        this.fadeUp = this.yPos - 25;

        for ( Category categorys : this.getSortedCategoryList ( ) ) {
            this.catePositions.put ( categorys , yCount );

            yCount -= 25;
        }
    }

    public List< Category > getSortedCategoryList ( ) {
        List< Category > list = new ArrayList<> ( );
        for ( Category categorys : Category.values ( ) ) {
            if (categorys == Category.COMMANDS)
                continue;
            list.add ( categorys );
        }

        list.sort ( new Comparator< Category > ( ) {

            @Override
            public int compare ( Category o1 , Category o2 ) {
                return Minecraft.fontRendererObj.getStringWidth ( o1.name ( ) ) - Minecraft.fontRendererObj.getStringWidth ( o2.name ( ) );
            }
        } );
        return list;
    }

    public int getyPos ( ) {
        return yPos;
    }

    public int getxPos ( ) {
        return xPos;
    }

    private int getMaxCategoryWidth ( ) {
        return Minecraft.fontRendererObj.getStringWidth ( this.getSortedCategoryList ( ).get ( 0 ).name ( ) ) + 30;
    }

    public void onRender ( int mouseX , int mouseY ) {

        boolean hovered = ClickGUI.isHovered ( mouseX , mouseY , this.xPos , this.yPos , this.getMaxCategoryWidth ( ) + 37 , 10 );

        GL11.glPushMatrix ( );
        GL11.glScalef ( 1.5f , 1.5f , 1.5f );

        if (hovered) {
            Gui.drawRect ( this.xPos * 0.66f - 10 , this.yPos * 0.66f ,
                    this.xPos * 0.66f + this.getMaxCategoryWidth ( ) + 5 , this.yPos * 0.66f + 15F ,
                    Candy.RGBtoHEX ( 64 , 64 , 64 , 150 ) );
        }

        Gui.drawRect ( this.xPos * 0.66f - 10 , this.yPos * 0.66f - 1 ,
                this.xPos * 0.66f + this.getMaxCategoryWidth ( ) + 5 , this.yPos * 0.66f + 16F ,
                Candy.RGBtoHEX ( 0 , 0 , 0 , 200 ) );

        Minecraft.fontRendererObj.drawString ( "Frame Menus" , ( int ) ( this.xPos * 0.66f ) - 1 , ( int ) ( this.yPos * 0.66f ) + 4 , -1 );

        if (this.isExtend) {

            if (this.fadeUp < this.yPos + this.getSortedCategoryList ( ).size ( ) * 25F) {
                this.fadeUp--;
            }

            for ( Map.Entry< Category, Integer > toDraw : this.catePositions.entrySet ( ) ) {

                hovered = ClickGUI.isHovered ( mouseX , mouseY , this.xPos , toDraw.getValue ( ) ,
                        this.getMaxCategoryWidth ( ) + 37 , 16 );
                GuiFrame frame = null;
                for ( GuiFrame frames : ClickGUI.frameList ) {
                    if (frames.getCategory ( ) == toDraw.getKey ( ))
                        frame = frames;
                }

                if (this.fadeUp <= toDraw.getValue ( )) {

                    if (hovered) {
                        Gui.drawRect ( ( float ) this.xPos * 0.66f - 5 , ( float ) toDraw.getValue ( ) * 0.66f ,
                                ( float ) this.xPos * 0.66f + this.getMaxCategoryWidth ( ) + 5 ,
                                ( float ) toDraw.getValue ( ) * 0.66f + 16F , Candy.RGBtoHEX ( 64 , 64 , 64 , 250 ) );

                    }

                    Gui.drawRect ( ( float ) this.xPos * 0.66f - 5 , ( float ) toDraw.getValue ( ) * 0.66f - 1 ,
                            ( float ) this.xPos * 0.66f + this.getMaxCategoryWidth ( ) + 5 ,
                            ( float ) toDraw.getValue ( ) * 0.66f + 16F + 1 , Candy.RGBtoHEX ( 0 , 0 , 0 , 200 ) );

                    mc.ingameGUI.drawCenteredString ( Minecraft.fontRendererObj ,
                            ( frame.show ? "§7" : "§f" ) + toDraw.getKey ( ).name ( ).substring ( 0 , 1 ).toUpperCase ( )
                                    + toDraw.getKey ( ).name ( ).substring ( 1 ).toUpperCase ( ) ,
                            ( int ) ( this.xPos * 0.66f + this.getMaxCategoryWidth ( ) / 2 ) ,
                            ( int ) ( toDraw.getValue ( ) * 0.66f + 4f ) , -1 );
                }

            }
        }

        GL11.glPopMatrix ( );

    }

    public void onClick ( int mouseX , int mouseY , int mouseButton ) {

        if (ClickGUI.isHovered ( mouseX , mouseY , this.xPos , this.yPos , this.getMaxCategoryWidth ( ) + 37 , 15 )) {
            this.isExtend = !this.isExtend;

            if (!this.isExtend) {
                this.fadeUp = this.yPos - 25;
            }
            return;
        }

        if (!this.isExtend)
            return;

        for ( Map.Entry< Category, Integer > toCheck : this.catePositions.entrySet ( ) ) {
            if (ClickGUI.isHovered ( mouseX , mouseY , this.xPos , toCheck.getValue ( ) , this.getMaxCategoryWidth ( ) + 37 ,
                    16 )) {
                if (this.fadeUp <= toCheck.getValue ( )) {

                    for ( GuiFrame frame : ClickGUI.frameList ) {
                        if (frame.getCategory ( ) == toCheck.getKey ( )) {
                            frame.show = !frame.show;
                            Minecraft.thePlayer.playSound ( "random.wood_click" , 0.5f , 1f );

                            frame.setExtend ( true );
                            frame.setFadeDown ( frame.yPos ( ) - 30 );

                            Candy.getGuiPos ( ).setBoolean ( toCheck.getKey ( ).name ( ) + ".Show" , frame.show );
                            Candy.getGuiPos ( ).save ( );
                        }
                    }
                }
            }
        }
    }

}
