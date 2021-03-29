package de.staticcode.candy.module.modules.PLAYER;

import de.staticcode.candy.gui.components.GuiComponent;
import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.item.ItemStack;

import java.util.Arrays;

/**
 * Created by nikla on 09.06.2017.
 */
public class ChestStealer extends Module {

    private float cooldown = 0f;

    private final GuiComponent chestStealerMode = new GuiComponent ( "ChestStealer Mode" , this , Arrays.asList ( "Normal Stealer" , "AAC Stealer" ) ,
            "Normal Stealer" );

    public ChestStealer ( ) {
        super ( "ChestStealer" , Category.PLAYER );
    }


    @Override
    public void onUpdate ( ) {
        if (this.isToggled ( )) {
            if (mc.currentScreen != null && mc.currentScreen instanceof GuiChest) {
                if (this.chestStealerMode.getActiveMode ( ).equalsIgnoreCase ( "Normal Stealer" )) {
                    this.updateNormal ( );
                } else {
                    this.updateFAST ( );
                }
            }
        }
        super.onUpdate ( );
    }

    public void updateNormal ( ) {
        GuiChest chest = ( GuiChest ) mc.currentScreen;

        cooldown += 0.10f;

        if (this.cooldown < 0.30f)
            return;

        if (chest.inventorySlots == null)
            return;

        boolean grabbed = false;

        for ( int i = 0; i < chest.inventorySlots.inventorySlots.size ( ) - 36; i++ ) {
            ItemStack stack = chest.inventorySlots.getSlot ( i ).getStack ( );
            if (stack != null) {
                mc.playerController.windowClick ( chest.inventorySlots.windowId , i , 1 , 2 , Minecraft.thePlayer );
                this.cooldown = 0.0f;
                grabbed = true;
                break;
            }
        }

        if (!grabbed)
            Minecraft.thePlayer.closeScreen ( );
    }

    public void updateFAST ( ) {

        GuiChest chest = ( GuiChest ) mc.currentScreen;

        if (chest.inventorySlots == null)
            return;

        boolean grabbed = false;

        for ( int i = 0; i < chest.inventorySlots.inventorySlots.size ( ) - 36; i++ ) {
            ItemStack stack = chest.inventorySlots.getSlot ( i ).getStack ( );
            if (stack != null) {
                mc.playerController.windowClick ( chest.inventorySlots.windowId , i , 1 , 2 , Minecraft.thePlayer );
            }
        }

        Minecraft.thePlayer.closeScreen ( );
    }
}
