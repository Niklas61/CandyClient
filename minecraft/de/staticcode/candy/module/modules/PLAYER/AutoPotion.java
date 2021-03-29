package de.staticcode.candy.module.modules.PLAYER;

import de.staticcode.candy.Candy;
import de.staticcode.candy.gui.components.GuiComponent;
import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import de.staticcode.candy.utils.RotationUtils;
import de.staticcode.candy.utils.Timings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.world.WorldSettings;

public class AutoPotion extends Module {

    protected Timings timings = new Timings ( );
    protected GuiComponent useInventoryContent = new GuiComponent ( "Inventory Content" , this , false );
    protected int oldSlot;

    protected boolean openedInventory;

    public AutoPotion ( ) {
        super ( "AutoPotion" , Category.PLAYER );
    }

    @Override
    public void onDisable ( ) {
        this.closeInv ( );
        this.oldSlot = -1;
        super.onDisable ( );
    }

    @Override
    public void onUpdate ( ) {

        if (mc.currentScreen != null) {
            this.openedInventory = false;
            return;
        }

        if (mc.playerController.getCurrentGameType ( ) == WorldSettings.GameType.CREATIVE)
            return;

        PotionSlot regenSlot = this.getPotion ( "regeneration" );
        PotionSlot speedSlot = this.getPotion ( "moveSpeed" );
        PotionSlot healSlot = this.getPotion ( "heal" );

        if (regenSlot != null && Minecraft.thePlayer.getHealth ( ) <= ( Minecraft.thePlayer.getMaxHealth ( ) / 2F )) {
            this.usePotion ( regenSlot );
        }

        if (speedSlot != null) {
            if (Minecraft.thePlayer.getActivePotionEffect ( Potion.moveSpeed ) == null)
                this.usePotion ( speedSlot );
        }

        if (healSlot != null && Minecraft.thePlayer.getHealth ( ) <= ( Minecraft.thePlayer.getMaxHealth ( ) / 3F )) {
            this.usePotion ( healSlot );
        }

        if (this.oldSlot != -1) {
            if (this.timings.hasReached ( 150L )) {
                Minecraft.thePlayer.inventory.currentItem = this.oldSlot;
                this.oldSlot = -1;
            }
        }

        super.onUpdate ( );
    }

    protected PotionSlot getPotion ( String potionEffectType ) {
        for ( int slotId = 0; slotId < ( this.useInventoryContent.isToggled ( ) ? 36 : 9 ); slotId++ ) {
            ItemStack itemStack = Minecraft.thePlayer.inventory.getStackInSlot ( slotId );

            if (itemStack != null) {

                if (itemStack.getItem ( ) instanceof ItemPotion) {
                    ItemPotion itemPotion = ( ItemPotion ) itemStack.getItem ( );


                    if (ItemPotion.isSplash ( itemStack.getMetadata ( ) )) {
                        Candy.sendChat ( slotId );
                        for ( PotionEffect potionEffect : itemPotion.getEffects ( itemStack ) ) {
                            if (potionEffect.getEffectName ( ).equalsIgnoreCase ( "potion." + potionEffectType ))
                                return new PotionSlot ( slotId , slotId > 8 );
                        }
                    }

                }
            }
        }
        return null;
    }

    protected void usePotion ( PotionSlot potionSlot ) {

        if (this.oldSlot == -1) {
            this.oldSlot = Minecraft.thePlayer.inventory.currentItem;
            this.timings.resetTimings ( );
        }

        if (potionSlot.isInventoryContent ( )) {
            if (!this.isSwapReady ( )) {
                this.openInv ( );

                if (this.foundEmptySlot ( )) {
                    this.moveToInv ( 44 );
                } else {
                    this.dropItem ( 8 );
                }

                this.closeInv ( );

            } else {
                this.openInv ( );
                this.swapItem ( potionSlot.getSlot ( ) , 8 );
                this.closeInv ( );
            }
        }

        Minecraft.thePlayer.inventory.currentItem = potionSlot.getSlot ( );
        C08PacketPlayerBlockPlacement block = new C08PacketPlayerBlockPlacement ( new BlockPos ( Minecraft.thePlayer.posX , Minecraft.thePlayer.posY , Minecraft.thePlayer.posZ ) , 255 ,
                Minecraft.thePlayer.inventory.getStackInSlot ( potionSlot.getSlot ( ) ) , 0.0F , 0.0F , 0.0F );
        RotationUtils.server_pitch = 70F;
        Minecraft.thePlayer.sendQueue.addToSendQueue ( block );
    }

    protected void openInv ( ) {
        if (!( mc.currentScreen instanceof GuiInventory )) {
            Minecraft.thePlayer.sendQueue.addToSendQueue ( new C0BPacketEntityAction ( Minecraft.thePlayer , C0BPacketEntityAction.Action.OPEN_INVENTORY ) );
            this.openedInventory = true;
        }
    }

    protected void closeInv ( ) {
        if (this.openedInventory) {
            Minecraft.thePlayer.sendQueue.addToSendQueue ( new C0BPacketEntityAction ( Minecraft.thePlayer , C0BPacketEntityAction.Action.OPEN_INVENTORY ) );
            this.openedInventory = false;
        }
    }

    protected void moveToInv ( int from ) {
        this.mc.playerController.windowClick ( Minecraft.thePlayer.inventoryContainer.windowId , from , 0 , 1 , Minecraft.thePlayer );
    }

    protected void swapItem ( int from , int to ) {
        this.mc.playerController.windowClick ( Minecraft.thePlayer.inventoryContainer.windowId , from , to , 2 , Minecraft.thePlayer );
    }

    public void dropItem ( int slot ) {
        Minecraft.thePlayer.inventoryContainer.slotClick ( slot , 0 , 4 , Minecraft.thePlayer );
        this.mc.playerController.windowClick ( Minecraft.thePlayer.inventoryContainer.windowId , slot , 1 , 4 , Minecraft.thePlayer );
    }

    protected boolean isSwapReady ( ) {
        return Minecraft.thePlayer.inventory.getStackInSlot ( 8 ) == null;
    }

    protected boolean foundEmptySlot ( ) {
        boolean foundEmpty = false;
        for ( int slot = 9; slot < 36; slot++ ) {
            if (Minecraft.thePlayer.inventory.getStackInSlot ( slot ) == null)
                foundEmpty = true;
        }

        return foundEmpty;
    }

}

class PotionSlot {

    private final int slot;
    private boolean isInventoryContent;

    PotionSlot ( int slot , boolean isInventoryContent ) {
        this.slot = slot;
        this.isInventoryContent = isInventoryContent;
    }

    public int getSlot ( ) {
        return slot;
    }

    public void setInventoryContent ( boolean inventoryContent ) {
        isInventoryContent = inventoryContent;
    }

    public boolean isInventoryContent ( ) {
        return isInventoryContent;
    }
}
