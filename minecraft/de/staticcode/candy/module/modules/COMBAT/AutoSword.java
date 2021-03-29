package de.staticcode.candy.module.modules.COMBAT;

import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

public class AutoSword extends Module {


    public AutoSword ( ) {
        super ( "AutoSword" , Category.COMBAT );
    }

    public static int findItem ( ) {
        for ( int i = 0; i < 9; i++ ) {
            ItemStack itemStack = Minecraft.thePlayer.inventory.getStackInSlot ( i );
            if (itemStack == null)
                continue;

            if (itemStack.getItem ( ) instanceof ItemSword || itemStack.getItem ( ) instanceof ItemAxe)
                return i;
        }
        return -1;
    }
}
