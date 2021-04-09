package de.staticcode.candy.module.modules.RENDER;

import de.staticcode.candy.gui.components.GuiComponent;
import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;

public class ESP extends Module {

    private final GuiComponent guiComponent = new GuiComponent ( "ESP Mode" , this , Arrays.asList ( "Shine" , "Outline" , "Box" ) , "Shine" );

    public ESP ( ) {
        super ( "ESP" , Keyboard.KEY_P , Category.RENDER );
    }

    @Override
    public void onUpdate ( ) {
        this.displayName = "ESP §7" + this.guiComponent.getActiveMode ( );
        super.onUpdate ( );
    }

    @Override
    public void onRender ( ) {
        if (this.guiComponent.getActiveMode ( ).equals ( "Box" )) {

            for ( Entity entities : mc.theWorld.loadedEntityList ) {
                if (entities instanceof EntityLivingBase) {

                    if (entities.equals ( mc.thePlayer ))
                        continue;

                    if (entities.isDead)
                        continue;

                    if (entities.isInvisible ( ))
                        continue;

                    if (( ( EntityLivingBase ) entities ).deathTime > 0)
                        continue;

                    final double x = ( entities.lastTickPosX + ( entities.posX - entities.lastTickPosX ) * ( double ) mc.timer.renderPartialTicks ) - mc.getRenderManager ( ).renderPosX;
                    final double y = ( entities.lastTickPosY + ( entities.posY - entities.lastTickPosY ) * ( double ) mc.timer.renderPartialTicks ) - mc.getRenderManager ( ).renderPosY;
                    final double z = ( entities.lastTickPosZ + ( entities.posZ - entities.lastTickPosZ ) * ( double ) mc.timer.renderPartialTicks ) - mc.getRenderManager ( ).renderPosZ;
                    final float width = entities.width;
                    final float height = entities.height;

                    GL11.glPushMatrix ( );
                    GL11.glEnable ( GL11.GL_BLEND );

                    if (( ( EntityLivingBase ) entities ).hurtTimeNoCam == 0)
                        GL11.glColor4f ( 0.0F , 0.2F , 0.7F , 0.35F );
                    else
                        GL11.glColor4f ( 1F , 0.2F , 0.3F , 0.35F );

                    GL11.glBlendFunc ( 770 , 771 );
                    GL11.glDisable ( GL11.GL_TEXTURE_2D );
                    GL11.glEnable ( GL11.GL_LINE_SMOOTH );
                    GL11.glDisable ( GL11.GL_DEPTH_TEST );
                    RenderGlobal.func_181561_a ( new AxisAlignedBB ( x - width , y , z - width , x + width , y + height + 0.15F , z + width ) );
                    GL11.glDisable ( GL11.GL_LINE_SMOOTH );
                    GL11.glEnable ( GL11.GL_TEXTURE_2D );
                    GL11.glEnable ( GL11.GL_DEPTH_TEST );
                    GL11.glPopMatrix ( );

                }
            }
        }
    }
}
