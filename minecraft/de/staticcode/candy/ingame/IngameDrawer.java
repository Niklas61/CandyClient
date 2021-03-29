package de.staticcode.candy.ingame;

import de.staticcode.candy.Candy;
import de.staticcode.candy.gui.components.GuiComponent;
import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.modules.COMBAT.Killaura;
import de.staticcode.candy.playerimage.PlayerImage;
import de.staticcode.candy.save.LoadConfig;
import de.staticcode.candy.tabgui.TabGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class IngameDrawer extends GuiIngame {

    public IngameDrawer ( Minecraft mcIn ) {
        super ( mcIn );
    }

    public static TabGui tabGui = new TabGui ( 6 , 25 );
    public static boolean setupedModules = false;

    public static float fadeUP;
    public static boolean shouldFade;
    public static Module fadeMod;

    static ResourceLocation imageLocation = new ResourceLocation ( "textures/gui/Image.png" );

    public static void onModuleDisable ( Module mod ) {
        IngameDrawer.fadeUP = 8.0F;
        IngameDrawer.shouldFade = true;
        IngameDrawer.fadeMod = mod;
    }

    public void drawScreen ( ) {

        PlayerImage.onRender ( );

        if (!setupedModules) {
            new LoadConfig ( );
        }

        if (fadeUP > 1F) {
            fadeUP -= 0.10F;

        } else if (fadeUP <= 1.0F && shouldFade) {
            shouldFade = false;
            fadeMod.settedY = false;
            fadeMod = null;
        }

        if (GuiComponent.getByName ( "Hud Theme" ).getActiveMode ( ).equalsIgnoreCase ( "Simple" )) {

            tabGui = new TabGui ( 6 , 25 );
            GL11.glPushMatrix ( );
            GL11.glScalef ( 1.8f , 1.8f , 1.8f );
            drawRect ( 2 , 3 , Minecraft.fontRendererObj.getStringWidth ( Candy.NAME ) + 11 , 15 ,
                    Candy.RGBtoHEX ( 0 , 0 , 0 , 150 ) );
            Minecraft.fontRendererObj.drawStringWithShadow ( Candy.NAME , 7 , 5 , Candy.RGBtoHEX ( 255 , 255 , 255 , 255 ) );
            GL11.glScalef ( 0.4f , 0.4f , 0.4f );
            Minecraft.fontRendererObj.drawString ( Candy.VERSION ,
                    96 - Minecraft.fontRendererObj.getStringWidth ( Candy.VERSION ) , 9 , Candy.RGBtoHEX ( 0 , 200 , 255 , 255 ) );
            GL11.glPopMatrix ( );
            this.drawModules ( );
        } else if (GuiComponent.getByName ( "Hud Theme" ).getActiveMode ( ).equalsIgnoreCase ( "Image" )) {

            tabGui = new TabGui ( 0 , 48 );
            tabGui.drawGui ( );

            if (GuiComponent.getByName ( "Image Background" ).isToggled ( ))
                drawRect ( -1 , -1 , 51 , 48 , Candy.RGBtoHEX ( 0 , 0 , 0 , 120 ) );

            GL11.glPushMatrix ( );
            GL11.glEnable ( GL11.GL_BLEND );
            if (GuiComponent.getByName ( "Rotate Image" ).isToggled ( )) {
                GlStateManager.rotate ( ( float ) Math.sin ( System.currentTimeMillis ( ) / 500d ) * 90f , 0f , 360f , 0f );
                GlStateManager.translate ( 0f , 0f , ( float ) Math.sin ( System.currentTimeMillis ( ) / 500d ) * 39f );
            }
            GL11.glColor3f ( 1F , 1F , 1F );
            Minecraft.getMinecraft ( ).getTextureManager ( ).bindTexture ( imageLocation );
            drawModalRectWithCustomSizedTexture ( -6 , 1 , 1 , 1 , 57 , 49 , 65 , 50 );
            GL11.glDisable ( GL11.GL_BLEND );
            GL11.glPopMatrix ( );
            this.drawModules ( );
        } else if (GuiComponent.getByName ( "Hud Theme" ).getActiveMode ( ).equalsIgnoreCase ( "New" )) {
            tabGui = new TabGui ( 1 , 9 );
            GL11.glPushMatrix ( );
            GL11.glScalef ( 0.8F , 0.8F , 0.8F );
            Minecraft.fontRendererObj.drawStringWithShadow ( Candy.NAME + " " + Candy.VERSION , 1 , 2 , -1 );
            GL11.glPopMatrix ( );
            this.drawModules ( );
            tabGui.drawGui ( );
        } else if (GuiComponent.getByName ( "Hud Theme" ).getActiveMode ( ).equalsIgnoreCase ( "Compact" )) {
            this.drawModules ( );

            tabGui = new TabGui ( 1 , 13 );
            tabGui.drawGui ( );
        }

        if (Killaura.underAttack != null && Killaura.underAttack instanceof EntityPlayer && GuiComponent.getByName ( "TargetInfo" ).isToggled ( )) {
            GlStateManager.pushMatrix ( );
            GlStateManager.disableBlend ( );

            EntityPlayer entityplayer = ( EntityPlayer ) Killaura.underAttack;

            if (entityplayer.hurtTimeNoCam > 0)
                GlStateManager.color ( 0.9F , 0.3F , 0.3F );
            else
                GlStateManager.color ( 1F , 1F , 1F );

            int l2 = 8;
            int i3 = 8;
            int x = ScaledResolution.getScaledWidth ( ) / 2;
            int y = ( ScaledResolution.getScaledHeight ( ) / 2 ) + 6;

            String hurtTime = "HurtTime: " + entityplayer.hurtTimeNoCam;
            int nameWidth = Candy.getVerdanaFont ( ).getStringWidth ( entityplayer.getName ( ) );
            int hurtTimeWidth = Candy.getVerdanaFont ( ).getStringWidth ( hurtTime );

            if (nameWidth < hurtTimeWidth)
                nameWidth = hurtTimeWidth;

            this.drawGradientRect ( x , y , x + nameWidth + 27 , y + 25 , Candy.RGBtoHEX ( 0 , 130 , 170 , 255 ) , Candy.RGBtoHEX ( 128 , 128 , 128 , 255 ) );
            Minecraft.getMinecraft ( ).getTextureManager ( ).bindTexture ( new NetworkPlayerInfo ( entityplayer.getGameProfile ( ) ).getLocationSkin ( ) );
            Gui.drawScaledCustomSizeModalRect ( x , y , 8.0F , ( float ) l2 , 8 , i3 , 25 , 25 , 64.0F , 64.0F );
            Candy.getVerdanaFont ( ).drawStringWithShadow ( entityplayer.getName ( ) , x + 25 , y , -1 );
            drawRect ( x + 25 , y + 14 , x + nameWidth + 26 , y + 15 , Candy.RGBtoHEX ( 0 , 0 , 0 , 255 ) );
            Candy.getVerdanaFont ( ).drawStringWithShadow ( hurtTime , x + 25 , y + 13 , -1 );
            GlStateManager.popMatrix ( );
        }

    }

    static ArrayList< Module > sortedModules = new ArrayList<> ( );

    public List< Module > getSortedModuleList ( ) {
        if (sortedModules.isEmpty ( )) {
            List< Module > modules = new ArrayList<> ( );
            for ( Module mods : Module.getModuleList ( ) ) {
                if (mods.isToggled ( ) || mods.shouldFadeOut || ( shouldFade && mods == fadeMod ))
                    modules.add ( mods );
            }
            modules.sort ( new Comparator< Module > ( ) {

                @Override
                public int compare ( Module o1 , Module o2 ) {
                    return ( Minecraft.fontRendererObj.getStringWidth ( o2.displayName )
                            - Minecraft.fontRendererObj.getStringWidth ( o1.displayName ) );
                }
            } );
            return modules;
        } else {
            return sortedModules;
        }
    }

    public int getModuleId ( Module mod ) {
        int id = 0;
        for ( Module modules : this.getSortedModuleList ( ) ) {
            if (modules.isToggled ( ) || modules.shouldFadeOut) {
                id++;

                if (modules == mod)
                    break;
            }
        }
        return id;
    }

    public void drawModules ( ) {
        int yCount = 0;
        int moduleID = 0;
        int oldWidth = 0;
        GL11.glPushMatrix ( );
        if (Module.getByName ( "ModuleScale" ).isToggled ( )) {
            GL11.glScaled ( GuiComponent.getByName ( "Scale" ).getCurrent ( ) , GuiComponent.getByName ( "Scale" ).getCurrent ( ) ,
                    GuiComponent.getByName ( "Scale" ).getCurrent ( ) );
        }

        for ( Module modules : this.getSortedModuleList ( ) ) {
            modules.moduleID = moduleID;
            modules.yCount = yCount;
            moduleID++;

            if (modules.isToggled ( ) || modules.shouldFadeOut || IngameDrawer.shouldFade) {

                if (!modules.shouldFadeOut) {
                    if (modules.slideIn < Minecraft.fontRendererObj.getStringWidth ( modules.displayName )) {
                        modules.slideIn++;
                        if (modules.isToggled ( ))
                            fadeUP = 0F;

                    } else if (modules.slideIn > Minecraft.fontRendererObj.getStringWidth ( modules.displayName )) {
                        modules.slideIn--;
                    }
                }

                if (modules.shouldFadeOut) {
                    if (modules.slideIn > 0) {
                        modules.slideIn--;
                    } else {
                        onModuleDisable ( modules );
                        modules.shouldFadeOut = false;
                    }
                }

                int width = ScaledResolution.getScaledWidth ( );
                if (Module.getByName ( "ModuleScale" ).isToggled ( )) {
                    width /= GuiComponent.getByName ( "Scale" ).getCurrent ( );
                }

                boolean draw = true;

                if (shouldFade && fadeMod != null && fadeMod == modules)
                    draw = false;

                if (draw) {
                    if (GuiComponent.getByName ( "Rect2" ).isToggled ( )
                            && !GuiComponent.getByName ( "Surrounded" ).isToggled ( ))
                        drawRect ( width , yCount , width - 3 , yCount + 8 , modules.getRandomColor ( ) );


                    if (GuiComponent.getByName ( "Surrounded" ).isToggled ( )) {

                        drawRect ( width - modules.slideIn - 1 , yCount , width , yCount + 8 ,
                                Candy.RGBtoHEX ( 0 , 0 , 0 , 150 ) );

                        if (modules.slideIn >= Minecraft.fontRendererObj.getStringWidth ( modules.displayName ))
                            drawRect ( width - modules.slideIn - 3 , yCount ,
                                    width - modules.slideIn - 2 , yCount + 8 ,
                                    Candy.RGBtoHEX ( 255 , 255 , 255 , 255 ) );

                        if (oldWidth > modules.slideIn
                                && modules.slideIn >= Minecraft.fontRendererObj.getStringWidth ( modules.displayName ))
                            drawRect ( width - oldWidth - 3 , yCount , width - modules.slideIn - 2 ,
                                    yCount + 1 , Candy.RGBtoHEX ( 255 , 255 , 255 , 255 ) );

                        if (moduleID == this.getSortedModuleList ( ).size ( )) {
                            drawRect ( width - modules.slideIn - 3 , yCount + 8 , width , yCount + 9 ,
                                    Candy.RGBtoHEX ( 255 , 255 , 255 , 255 ) );
                        }
                    } else {
                        if (!GuiComponent.getByName ( "Rect2" ).isToggled ( )) {
                            drawRect ( width - modules.slideIn , yCount , width , yCount + 8 ,
                                    Candy.RGBtoHEX ( 0 , 0 , 0 , 150 ) );
                        } else {
                            drawRect ( width - modules.slideIn - 3 , yCount , width , yCount + 8 ,
                                    Candy.RGBtoHEX ( 0 , 0 , 0 , 150 ) );
                        }
                    }
                    if (GuiComponent.getByName ( "Rect2" ).isToggled ( )
                            && !GuiComponent.getByName ( "Surrounded" ).isToggled ( )) {

                        Minecraft.fontRendererObj.drawStringWithShadow ( modules.displayName ,
                                width - modules.slideIn - 3 , yCount , modules.getRandomColor ( ) );
                    } else {

                        Minecraft.fontRendererObj.drawStringWithShadow ( modules.displayName ,
                                width - modules.slideIn , yCount , modules.getRandomColor ( ) );
                    }

                }

                if (shouldFade && modules == fadeMod && moduleID > fadeMod.moduleID) {
                    yCount += fadeUP;
                } else {
                    yCount += 8;
                }

                oldWidth = modules.slideIn;
            }
        }
        GL11.glPopMatrix ( );
    }

}
