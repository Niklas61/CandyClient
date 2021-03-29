package de.staticcode.candy.utils;

import net.minecraft.util.BlockPos;

import java.util.HashMap;
import java.util.Map;

public class RenderUtils {

    public static boolean shouldRender;
    public static Map< BlockPos, Integer > renderList = new HashMap<> ( );

    public static void clearAllWithID ( int blockId ) {
        Map< BlockPos, Integer > map = new HashMap<> ( );

        for ( Map.Entry< BlockPos, Integer > blocks : renderList.entrySet ( ) ) {
            if (blocks.getValue ( ) != blockId)
                map.put ( blocks.getKey ( ) , blocks.getValue ( ) );
        }


        renderList = map;
    }
}
