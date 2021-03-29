package de.staticcode.candy.module.modules.RENDER;

import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import de.staticcode.candy.utils.RenderUtils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;

public class BedESP extends Module {

    public BedESP ( ) {
        super ( "BedESP" , Category.RENDER );
    }

    @Override
    public void onDisable ( ) {
        RenderUtils.renderList.clear ( );
        super.onDisable ( );
    }

    @Override
    public void onEnable ( ) {
        new Thread ( new Runnable ( ) {

            @Override
            public void run ( ) {
                for ( double x = -200d; x <= 200d; x++ ) {
                    for ( double y = 100d; y >= -100d; y-- ) {
                        for ( double z = -200d; z <= 200d; z++ ) {
                            BlockPos pos = new BlockPos ( Minecraft.thePlayer.posX + x , Minecraft.thePlayer.posY + y , Minecraft.thePlayer.posZ + z );

                            if (mc.theWorld.getBlockState ( pos ) != null
                                    && mc.theWorld.getBlockState ( pos ).getBlock ( ) == Block.getBlockById ( 26 )) {

                                if (!RenderUtils.renderList.containsKey ( pos )) {
                                    RenderUtils.renderList.put ( pos , 26 );
                                }
                            }
                        }
                    }
                }
            }
        } ).start ( );

        super.onEnable ( );
    }

    @Override
    public void onUpdate ( ) {
        RenderUtils.shouldRender = true;
        super.onUpdate ( );
    }

}
