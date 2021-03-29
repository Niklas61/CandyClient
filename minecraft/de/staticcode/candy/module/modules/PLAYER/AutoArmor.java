package de.staticcode.candy.module.modules.PLAYER;

import de.staticcode.candy.gui.components.GuiComponent;
import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import de.staticcode.candy.module.modules.COMBAT.Killaura;
import de.staticcode.candy.utils.Timings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0DPacketCloseWindow;

import java.util.*;

public class AutoArmor extends Module {
    private final int[] itemHelmet = new int[]{ 298 , 302 , 306 , 310 , 314 };
    private final int[] itemChestplate = new int[]{ 299 , 303 , 307 , 311 , 315 };
    private final int[] itemLeggings = new int[]{ 300 , 304 , 308 , 312 , 316 };
    private final int[] itemBoots = new int[]{ 301 , 305 , 309 , 313 , 317 };
    private final GuiComponent sDelayWear = new GuiComponent ( "Delay wear Armor" , this , 1000D , 1.0D , 1D );
    private final GuiComponent sDelayDrop = new GuiComponent ( "Delay drop Armor" , this , 1000D , 1D , 1D );
    private final GuiComponent sDelayContains = new GuiComponent ( "Delay contains Armor" , this , 2000D , 1D , 1D );
    private final GuiComponent bOpenInv = new GuiComponent ( "Open Inv" , this , false );
    private final GuiComponent bDontMove = new GuiComponent ( "Dont Move" , this , false );
    private final GuiComponent bWalkBypass = new GuiComponent ( "OpenInv Bypass" , this , false );
    private final Map armorContains = new HashMap ( );
    private final Timings wearTimings = new Timings ( );
    private final Timings dropTimings = new Timings ( );
    private final Timings waitTimings = new Timings ( );
    public static boolean openedInventory;

    public AutoArmor ( ) {
        super ( "AutoArmor" , Category.PLAYER );
    }

    public void onUpdate ( ) {
        this.displayName = "AutoArmor §3(" + this.sDelayWear.getCurrent ( ) + ")";
        super.onUpdate ( );
        if (Killaura.underAttack != null) {
            Minecraft.thePlayer.sendQueue.addToSendQueue ( new C0DPacketCloseWindow ( Minecraft.thePlayer.inventoryContainer.windowId ) );
            openedInventory = false;
        } else if (!this.bDontMove.isToggled ( ) || Minecraft.thePlayer.moveForward == 0.0F && Minecraft.thePlayer.moveStrafing == 0.0F) {
            boolean wearArmor = this.bOpenInv.isToggled ( ) && this.mc.currentScreen != null && this.mc.currentScreen instanceof GuiInventory || !this.bOpenInv.isToggled ( ) || this.bWalkBypass.isToggled ( );
            boolean finished = true;
            Iterator var4 = this.getArmors ( ).iterator ( );

            while ( true ) {
                String armorNames;
                int currentSlot;
                int bestArmorSlot;
                do {
                    if (!var4.hasNext ( )) {
                        if (finished) {
                            Minecraft.thePlayer.sendQueue.addToSendQueue ( new C0DPacketCloseWindow ( Minecraft.thePlayer.inventoryContainer.windowId ) );
                            openedInventory = false;
                        }

                        return;
                    }

                    armorNames = ( String ) var4.next ( );
                    finished = false;
                    currentSlot = this.getSlotByName ( armorNames );
                    bestArmorSlot = this.getBestInInventory ( armorNames );
                    boolean shouldAdd = true;
                    if (bestArmorSlot != -1) {
                        shouldAdd = this.getValence ( Minecraft.thePlayer.inventoryContainer.getSlot ( bestArmorSlot ).getStack ( ) ) > this.getValence ( Minecraft.thePlayer.inventoryContainer.getSlot ( currentSlot ).getStack ( ) );
                    }

                    if (shouldAdd && bestArmorSlot != -1 && !this.armorContains.containsKey ( Minecraft.thePlayer.inventoryContainer.getSlot ( bestArmorSlot ).getStack ( ) )) {
                        this.armorContains.put ( Minecraft.thePlayer.inventoryContainer.getSlot ( bestArmorSlot ).getStack ( ) , Long.valueOf ( System.currentTimeMillis ( ) ) );
                    }
                } while ( !wearArmor );

                if (bestArmorSlot != -1 && Minecraft.thePlayer.inventoryContainer.getSlot ( currentSlot ).getHasStack ( ) && this.getValence ( Minecraft.thePlayer.inventoryContainer.getSlot ( bestArmorSlot ).getStack ( ) ) < this.getValence ( Minecraft.thePlayer.inventoryContainer.getSlot ( currentSlot ).getStack ( ) )) {
                    bestArmorSlot = -1;
                }

                if (this.wearTimings.hasReached ( ( long ) this.sDelayWear.getCurrent ( ) ) && bestArmorSlot != -1) {
                    if (this.bWalkBypass.isToggled ( ) && this.mc.currentScreen == null && !openedInventory) {
                        Minecraft.thePlayer.sendQueue.addToSendQueue ( new C0BPacketEntityAction ( Minecraft.thePlayer , C0BPacketEntityAction.Action.OPEN_INVENTORY ) );
                        openedInventory = true;
                    }

                    if (this.armorContains.containsKey ( Minecraft.thePlayer.inventoryContainer.getSlot ( bestArmorSlot ).getStack ( ) ) && System.currentTimeMillis ( ) - ( ( Long ) this.armorContains.get ( Minecraft.thePlayer.inventoryContainer.getSlot ( bestArmorSlot ).getStack ( ) ) ).longValue ( ) >= ( long ) this.sDelayContains.getCurrent ( )) {
                        this.putOnItem ( currentSlot , bestArmorSlot );
                        this.armorContains.remove ( Minecraft.thePlayer.inventoryContainer.getSlot ( bestArmorSlot ).getStack ( ) );
                        this.wearTimings.resetTimings ( );
                    }
                }

                Iterator var9 = this.findArmor ( armorNames ).iterator ( );

                while ( var9.hasNext ( ) ) {
                    int anotherArmors = ( ( Integer ) var9.next ( ) ).intValue ( );
                    boolean isOldBetter = false;
                    if (currentSlot != -1) {
                        isOldBetter = this.getValence ( Minecraft.thePlayer.inventoryContainer.getSlot ( currentSlot ).getStack ( ) ) >= this.getValence ( Minecraft.thePlayer.inventoryContainer.getSlot ( anotherArmors ).getStack ( ) );
                    }

                    if (isOldBetter) {
                        finished = false;
                        if (this.dropTimings.hasReached ( ( long ) this.sDelayDrop.getCurrent ( ) )) {
                            this.dropOldArmor ( anotherArmors );
                            this.dropTimings.resetTimings ( );
                        }
                    }
                }
            }
        } else {
            if (openedInventory) {
                Minecraft.thePlayer.sendQueue.addToSendQueue ( new C0DPacketCloseWindow ( Minecraft.thePlayer.inventoryContainer.windowId ) );
                openedInventory = false;
            }

        }
    }

    private void putOnItem ( int armorSlot , int slot ) {
        if (armorSlot != -1 && Minecraft.thePlayer.inventoryContainer.getSlot ( armorSlot ).getStack ( ) != null) {
            this.dropOldArmor ( armorSlot );
        }

        this.inventoryAction ( slot );
    }

    private void dropOldArmor ( int slot ) {
        Minecraft.thePlayer.inventoryContainer.slotClick ( slot , 0 , 4 , Minecraft.thePlayer );
        this.mc.playerController.windowClick ( Minecraft.thePlayer.inventoryContainer.windowId , slot , 1 , 4 , Minecraft.thePlayer );
    }

    private void inventoryAction ( int click ) {
        this.mc.playerController.windowClick ( Minecraft.thePlayer.inventoryContainer.windowId , click , 1 , 1 , Minecraft.thePlayer );
    }

    private List getArmors ( ) {
        return Arrays.asList ( "helmet" , "leggings" , "chestplate" , "boots" );
    }

    private int[] getIdsByName ( String armorName ) {
        switch ( armorName.hashCode ( ) ) {
            case -1220934547:
                if (armorName.equals ( "helmet" )) {
                    return this.itemHelmet;
                }
                break;
            case 93922241:
                if (armorName.equals ( "boots" )) {
                    return this.itemBoots;
                }
                break;
            case 1069952181:
                if (armorName.equals ( "chestplate" )) {
                    return this.itemChestplate;
                }
                break;
            case 1735676010:
                if (armorName.equals ( "leggings" )) {
                    return this.itemLeggings;
                }
        }

        return new int[ 0 ];
    }

    private List findArmor ( String armorName ) {
        int[] itemIds = this.getIdsByName ( armorName );
        ArrayList availableSlots = new ArrayList ( );

        for ( int slots = 9; slots < Minecraft.thePlayer.inventoryContainer.getInventory ( ).size ( ); ++slots ) {
            ItemStack itemStack = Minecraft.thePlayer.inventoryContainer.getSlot ( slots ).getStack ( );
            if (itemStack != null) {
                int itemId = Item.getIdFromItem ( itemStack.getItem ( ) );
                int[] var10 = itemIds;
                int var9 = itemIds.length;

                for ( int var8 = 0; var8 < var9; ++var8 ) {
                    int ids = var10[ var8 ];
                    if (itemId == ids) {
                        availableSlots.add ( Integer.valueOf ( slots ) );
                    }
                }
            }
        }

        return availableSlots;
    }

    private int getBestInInventory ( String armorName ) {
        int slot = -1;
        Iterator var4 = this.findArmor ( armorName ).iterator ( );

        while ( var4.hasNext ( ) ) {
            int slots = ( ( Integer ) var4.next ( ) ).intValue ( );
            if (slot == -1) {
                slot = slots;
            }

            if (Minecraft.thePlayer.inventoryContainer.getSlot ( slots ) != null && Minecraft.thePlayer.inventoryContainer.getSlot ( slots ).getStack ( ).getItem ( ) instanceof ItemArmor && this.getValence ( Minecraft.thePlayer.inventoryContainer.getSlot ( slots ).getStack ( ) ) > this.getValence ( Minecraft.thePlayer.inventoryContainer.getSlot ( slot ).getStack ( ) )) {
                slot = slots;
            }
        }

        return slot;
    }

    private int getSlotByName ( String armorName ) {
        byte id = -1;
        switch ( armorName.hashCode ( ) ) {
            case -1220934547:
                if (armorName.equals ( "helmet" )) {
                    id = 5;
                }
                break;
            case 93922241:
                if (armorName.equals ( "boots" )) {
                    id = 8;
                }
                break;
            case 1069952181:
                if (armorName.equals ( "chestplate" )) {
                    id = 6;
                }
                break;
            case 1735676010:
                if (armorName.equals ( "leggings" )) {
                    id = 7;
                }
        }

        return id;
    }

    private int getValence ( ItemStack itemStack ) {
        int valence = 0;
        if (itemStack == null) {
            return 0;
        } else {
            if (itemStack.getItem ( ) instanceof ItemArmor) {
                valence += ( ( ItemArmor ) itemStack.getItem ( ) ).damageReduceAmount;
            }

            if (itemStack != null && itemStack.hasTagCompound ( )) {
                valence += ( int ) itemStack.getEnchantmentTagList ( ).getCompoundTagAt ( 0 ).getDouble ( "lvl" );
                valence += ( int ) itemStack.getEnchantmentTagList ( ).getCompoundTagAt ( 1 ).getDouble ( "lvl" );
                valence += ( int ) itemStack.getEnchantmentTagList ( ).getCompoundTagAt ( 2 ).getDouble ( "lvl" );
                valence += ( int ) itemStack.getEnchantmentTagList ( ).getCompoundTagAt ( 3 ).getDouble ( "lvl" );
                valence += ( int ) itemStack.getEnchantmentTagList ( ).getCompoundTagAt ( 4 ).getDouble ( "lvl" );
                valence += ( int ) itemStack.getEnchantmentTagList ( ).getCompoundTagAt ( 5 ).getDouble ( "lvl" );
                valence += ( int ) itemStack.getEnchantmentTagList ( ).getCompoundTagAt ( 6 ).getDouble ( "lvl" );
                valence += ( int ) itemStack.getEnchantmentTagList ( ).getCompoundTagAt ( 7 ).getDouble ( "lvl" );
                valence += ( int ) itemStack.getEnchantmentTagList ( ).getCompoundTagAt ( 8 ).getDouble ( "lvl" );
                valence += ( int ) itemStack.getEnchantmentTagList ( ).getCompoundTagAt ( 9 ).getDouble ( "lvl" );
                valence += ( int ) itemStack.getEnchantmentTagList ( ).getCompoundTagAt ( 34 ).getDouble ( "lvl" );
            }

            valence += itemStack.getMaxDamage ( ) - itemStack.getItemDamage ( );
            return valence;
        }
    }
}
