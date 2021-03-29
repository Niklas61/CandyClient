package de.staticcode.candy.gui.ui;

import de.staticcode.candy.Candy;
import de.staticcode.candy.gui.ClickGUI;
import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GuiFrame {

    private final Category category;
    private boolean dragging = false;
    private int xPos, yPos;

    private final int width;
    private final int height;
    public int dragX, dragY;
    public boolean show;

    private boolean isHovered;
    private boolean isExtend;

    private final Minecraft mc = Minecraft.getMinecraft ( );
    private final List< GuiButton > buttonList = new ArrayList<> ( );

    private float fadeDown;

    private final int maxHeight;

    public List< Module > getSortedList ( ) {
        List< Module > moduleList = new ArrayList<> ( );
        for ( Module modules : Module.getModuleList ( ) )
            moduleList.add ( modules );

        moduleList.sort ( new Comparator< Module > ( ) {
            @Override
            public int compare ( Module o1 , Module o2 ) {
                return Minecraft.fontRendererObj.getStringWidth ( o2.getName ( ) )
                        - Minecraft.fontRendererObj.getStringWidth ( o1.getName ( ) );
            }
        } );
        return moduleList;
    }

    public GuiFrame ( Category category , int xPos , int yPos ) {
        this.category = category;
        this.xPos = xPos;
        this.yPos = yPos;

        this.width = this.getCategoryWidth ( );
        this.height = 10;

        int yCount = 15;

        this.fadeDown = this.yPos - 30;

        for ( Module modules : this.getSortedList ( ) ) {
            if (modules.getCategory ( ) == this.category && modules.show) {
                this.buttonList.add ( new GuiButton ( this , modules , yCount , this.getMaxModuleWidth ( ) - 5 , 10 ) );
                yCount += 15;
            }
        }
        this.maxHeight = yCount;
    }

    public Category getCategory ( ) {
        return category;
    }

    public int xPos ( ) {
        return xPos;
    }

    public int yPos ( ) {
        return yPos;
    }

    public int getWidth ( ) {
        return width;
    }

    public int getHeight ( ) {
        return height;
    }

    public boolean isDragging ( ) {
        return dragging;
    }

    public int getCategoryWidth ( ) {
        return Minecraft.fontRendererObj.getStringWidth ( this.category.name ( ) );
    }

    public boolean isExtend ( ) {
        return isExtend;
    }

    private int getMaxModuleWidth ( ) {
        int maxWidth = 0;

        for ( Module modules : Module.getModuleList ( ) ) {
            if (modules.getCategory ( ) == this.category) {
                if (Minecraft.fontRendererObj.getStringWidth ( modules.getName ( ) ) > maxWidth)
                    maxWidth = Minecraft.fontRendererObj.getStringWidth ( modules.getName ( ) ) + 10;
            }
        }

        return maxWidth;
    }

    public void setxPos ( int xPos ) {
        this.xPos = xPos;
    }

    public void setyPos ( int yPos ) {
        this.yPos = yPos;
    }

    public void setDragging ( boolean dragging ) {
        this.dragging = dragging;
    }

    public void onRender ( int mouseX , int mouseY ) {

        if (!this.show)
            return;

        if (this.dragging) {
            this.xPos = mouseX - this.dragX;
            this.yPos = mouseY - this.dragY;
            this.fadeDown = this.fadeDown + this.maxHeight;
        }

        int rectWidth = this.xPos + this.getMaxModuleWidth ( ) + 3;

        if (this.getMaxModuleWidth ( ) == 0) {
            rectWidth = this.xPos + this.width + 3;
        }

        this.isHovered = ClickGUI.isHovered ( mouseX , mouseY , this.xPos , this.yPos ,
                this.getMaxModuleWidth ( ) != 0 ? this.getMaxModuleWidth ( ) + 10 : this.width + 6 , this.height / 2 + 3 );

        Gui.drawRect ( this.xPos - 13 , this.yPos - this.height / 2 - 5 , this.xPos - 10 ,
                this.yPos + this.height ,
                this.dragging ? ClickGUI.RGBtoHEX ( 0 , 0 , 0 , 150 ) : ClickGUI.RGBtoHEX ( 0 , 128 , 170 , 255 ) );

        Gui.drawRect ( rectWidth + 10 , this.yPos - this.height / 2 - 5 , rectWidth + 13 ,
                this.yPos + this.height ,
                this.dragging ? ClickGUI.RGBtoHEX ( 0 , 0 , 0 , 150 ) : ClickGUI.RGBtoHEX ( 0 , 128 , 170 , 255 ) );

        Gui.drawRect ( this.xPos - 10 , this.yPos - this.height / 2 - 5 , rectWidth + 10 ,
                this.yPos + this.height ,
                this.dragging ? ClickGUI.RGBtoHEX ( 0 , 128 , 170 , 255 ) : ClickGUI.RGBtoHEX ( 0 , 0 , 0 , 150 ) );

        String name = this.category.name ( );
        name = name.substring ( 0 , 1 ).toUpperCase ( ) + name.substring ( 1 ).toLowerCase ( );
        int stringWidth = this.xPos + this.getMaxModuleWidth ( ) / 2 + 4;
        if (this.getMaxModuleWidth ( ) == 0) {
            stringWidth = this.xPos + this.width / 2 + 4;
        }

        GlStateManager.pushMatrix ( );
        GlStateManager.scale ( 1.5f , 1.5f , 1.5f );
        mc.currentScreen.drawCenteredString ( Minecraft.fontRendererObj , name , ( int ) ( stringWidth * 0.6666666f ) ,
                ( int ) ( this.yPos * 0.666667f - 3 ) , ClickGUI.RGBtoHEX ( 255 , 255 , 255 , 255 ) );
        GlStateManager.popMatrix ( );

        if (this.isExtend) {

            if (this.fadeDown < this.yPos + this.maxHeight) {
                this.fadeDown += 7f;
            }

            for ( GuiButton button : this.buttonList ) {
                if (this.fadeDown >= button.yPos ( )) {
                    button.onRender ( mouseX , mouseY );
                }
            }
        }
    }

    public void onClick ( int mouseX , int mouseY , int mouseButton ) {

        if (!this.show)
            return;

        if (this.isHovered) {
            if (mouseButton == 0) {
                this.dragX = mouseX - this.xPos;
                this.dragY = mouseY - this.yPos;
                this.dragging = true;
            } else if (mouseButton == 1) {
                this.isExtend = !this.isExtend;

                if (!this.isExtend) {
                    this.fadeDown = this.yPos - 30;
                }
            }
        }

        if (this.isExtend) {
            for ( GuiButton button : this.buttonList ) {
                if (this.fadeDown >= button.yPos ( )) {
                    button.onClick ( mouseX , mouseY , mouseButton );
                }
            }
        }
    }

    public void mouseReleased ( int mouseX , int mouseY ) {

        if (!this.show)
            return;

        if (this.dragging) {
            this.dragging = false;
            this.saveDragging ( this.xPos , this.yPos );
        }

        if (this.isExtend) {
            for ( GuiButton button : this.buttonList ) {
                if (this.fadeDown >= button.yPos ( )) {
                    button.onMouseReleased ( );
                }
            }
        }
    }

    private void saveDragging ( int x , int y ) {
        Candy.getGuiPos ( ).setString ( category.name ( ) , x + ":" + y );
        Candy.getGuiPos ( ).save ( );
    }

    public void setExtend ( boolean isExtend ) {
        this.isExtend = isExtend;
    }

    public void setFadeDown ( float fadeDown ) {
        this.fadeDown = fadeDown;
    }

}
