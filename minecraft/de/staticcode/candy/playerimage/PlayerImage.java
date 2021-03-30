package de.staticcode.candy.playerimage;

import de.staticcode.candy.utils.RotationUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;

public class PlayerImage {

    protected static Minecraft mc = Minecraft.getMinecraft ( );

    public static void onRender ( ) {
        if (mc.theWorld != null) {

            EntityLivingBase entityLivingBase = Minecraft.thePlayer;
            final int posX = ScaledResolution.getScaledWidth ( ) - 25;
            final int posY = ScaledResolution.getScaledHeight ( ) - 50;
            final int scale = 30;
            final int mouseX = ( int ) RotationUtils.server_yaw;
            final int mouseY = ( int ) RotationUtils.server_pitch;
            drawEntityOnScreen ( posX , posY , scale , mouseX , mouseY , entityLivingBase );

        }

    }

    protected static void drawEntityOnScreen ( int posX , int posY , int scale , float mouseX , float mouseY ,
                                               EntityLivingBase ent ) {
        GlStateManager.enableColorMaterial ( );
        GlStateManager.pushMatrix ( );
        GlStateManager.enableDepth ( );
        GlStateManager.resetColor ( );
        GlStateManager.enableLighting ( );
        GlStateManager.enableNormalize ( );
        GlStateManager.translate ( ( float ) posX , ( float ) posY , 50.0F );
        GlStateManager.scale ( ( float ) ( -scale ) , ( float ) scale , ( float ) scale );
        GlStateManager.rotate ( 180.0F , 0.0F , 0.0F , 1.0F );
        float f = ent.renderYawOffset;
        float f1 = ent.rotationYaw;
        float f2 = ent.rotationPitch;
        float f3 = ent.prevRotationYawHead;
        float f4 = ent.rotationYawHead;
        GlStateManager.rotate ( 135.0F , 0.0F , 1.0F , 0.0F );
        RenderHelper.enableStandardItemLighting ( );
        GlStateManager.rotate ( -135.0F , 0.0F , 1.0F , 0.0F );
        GlStateManager.rotate ( -( ( float ) Math.atan ( mouseY / 40.0F ) ) * 20.0F , 1.0F , 0.0F , 0.0F );
        ent.renderYawOffset = ( float ) Math.atan ( mouseX / 40.0F ) * 20.0F;
        ent.rotationYaw = ( float ) Math.atan ( mouseX / 40.0F ) * 40.0F;
        ent.rotationPitch = -( ( float ) Math.atan ( mouseY / 40.0F ) ) * 20.0F;
        ent.rotationYawHead = ent.rotationYaw;
        ent.prevRotationYawHead = ent.rotationYaw;
        GlStateManager.translate ( 0.0F , 0.0F , 0.0F );
        RenderManager rendermanager = Minecraft.getMinecraft ( ).getRenderManager ( );
        rendermanager.setPlayerViewY ( 180.0F );
        rendermanager.setRenderShadow ( false );
        rendermanager.renderEntityWithPosYaw ( ent , 0.0D , 0.0D , 0.0D , 0.0F , 1.0F );
        rendermanager.setRenderShadow ( true );
        ent.renderYawOffset = f;
        ent.rotationYaw = f1;
        ent.rotationPitch = f2;
        ent.prevRotationYawHead = f3;
        ent.rotationYawHead = f4;
        GlStateManager.popMatrix ( );
        RenderHelper.disableStandardItemLighting ( );
        GlStateManager.disableRescaleNormal ( );
        GlStateManager.setActiveTexture ( OpenGlHelper.lightmapTexUnit );
        GlStateManager.disableTexture2D ( );
        GlStateManager.setActiveTexture ( OpenGlHelper.defaultTexUnit );
    }

}
