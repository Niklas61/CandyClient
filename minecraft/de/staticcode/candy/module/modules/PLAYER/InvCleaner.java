package de.staticcode.candy.module.modules.PLAYER;

import de.staticcode.candy.gui.components.GuiComponent;
import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;

import java.util.Iterator;

public class InvCleaner extends Module {
    public GuiComponent opendelay = new GuiComponent ( "Open Inv Delay" , this , 20D , 1D , 4D );
    public GuiComponent delay = new GuiComponent ( "Delay (ms)" , this , 500D , 50D , 50D );
    public GuiComponent openInv = new GuiComponent ( "Open Inv" , this , true );
    public GuiComponent keepTools = new GuiComponent ( "Keep Tools" , this , false );
    public GuiComponent preferSwords = new GuiComponent ( "Prefer Swords" , this , false );
    public GuiComponent sort = new GuiComponent ( "Sort" , this , false );
    public boolean working = false;
    public int odcound = 0;
    public static InvCleaner instance;

    public InvCleaner ( ) {
        super ( "InvCleaner" , Category.PLAYER );
        instance = this;
    }

    public void onDisable ( ) {
        this.working = false;
        super.onDisable ( );
    }

    public void onUpdate ( ) {
        super.onUpdate ( );
        this.displayName = "InvCleaner §3(" + this.delay.getCurrent ( ) + ")";
        if (this.mc.theWorld != null) {
            if (this.openInv.isToggled ( ) && !( this.mc.currentScreen instanceof GuiInventory )) {
                this.odcound = 0;
            } else if (!this.working) {
                if (this.openInv.isToggled ( ) && ( double ) this.odcound < this.opendelay.getCurrent ( )) {
                    ++this.odcound;
                } else {
                    this.working = true;
                    ( new Thread ( new Runnable ( ) {
                        public void run ( ) {
                            try {
                                for ( int ex = 9; ex < Minecraft.thePlayer.inventoryContainer.getInventory ( ).size ( ); ++ex ) {
                                    int bestSwordSlot = InvCleaner.this.getBestSwordSlot ( );
                                    boolean isBowInSlot2 = Minecraft.thePlayer.inventoryContainer.getInventory ( ).get ( 37 ) != null && Minecraft.thePlayer.inventoryContainer.getInventory ( ).get ( 37 ).getItem ( ) == Items.bow;
                                    boolean isRodInSlot3 = Minecraft.thePlayer.inventoryContainer.getInventory ( ).get ( 38 ) != null && Minecraft.thePlayer.inventoryContainer.getInventory ( ).get ( 38 ).getItem ( ) == Items.fishing_rod;
                                    boolean isFoodInSlot4 = Minecraft.thePlayer.inventoryContainer.getInventory ( ).get ( 39 ) != null && Minecraft.thePlayer.inventoryContainer.getInventory ( ).get ( 39 ).getItem ( ) instanceof ItemFood;
                                    boolean isBlockInSlot5 = Minecraft.thePlayer.inventoryContainer.getInventory ( ).get ( 40 ) != null && Minecraft.thePlayer.inventoryContainer.getInventory ( ).get ( 40 ).getItem ( ) instanceof ItemBlock;
                                    boolean isTntInSlot9 = Minecraft.thePlayer.inventoryContainer.getInventory ( ).get ( 44 ) != null && Minecraft.thePlayer.inventoryContainer.getInventory ( ).get ( 44 ).getItem ( ) instanceof ItemBlock && ( ( ItemBlock ) Minecraft.thePlayer.inventoryContainer.getInventory ( ).get ( 44 ).getItem ( ) ).getBlock ( ) == Blocks.tnt;
                                    if (ex == bestSwordSlot) {
                                        if (InvCleaner.this.sort.isToggled ( ) && ex != 36) {
                                            InvCleaner.this.swapItem ( ex , 0 , 2 );

                                            try {
                                                Thread.sleep ( ( long ) InvCleaner.this.delay.getCurrent ( ) );
                                            } catch ( Exception var27 ) {
                                            }
                                        }
                                    } else {
                                        ItemStack item = Minecraft.thePlayer.inventoryContainer.getInventory ( ).get ( ex );
                                        if (item != null) {
                                            if (InvCleaner.this.sort.isToggled ( )) {
                                                if (!isBowInSlot2 && item.getItem ( ) == Items.bow) {
                                                    InvCleaner.this.swapItem ( ex , 1 , 2 );

                                                    try {
                                                        Thread.sleep ( ( long ) InvCleaner.this.delay.getCurrent ( ) );
                                                    } catch ( Exception var26 ) {
                                                    }
                                                    continue;
                                                }

                                                if (!isRodInSlot3 && item.getItem ( ) == Items.fishing_rod) {
                                                    InvCleaner.this.swapItem ( ex , 2 , 2 );

                                                    try {
                                                        Thread.sleep ( ( long ) InvCleaner.this.delay.getCurrent ( ) );
                                                    } catch ( Exception var25 ) {
                                                    }
                                                    continue;
                                                }

                                                if (!isFoodInSlot4 && item.getItem ( ) instanceof ItemFood) {
                                                    InvCleaner.this.swapItem ( ex , 3 , 2 );

                                                    try {
                                                        Thread.sleep ( ( long ) InvCleaner.this.delay.getCurrent ( ) );
                                                    } catch ( Exception var24 ) {
                                                    }
                                                    continue;
                                                }

                                                if (!isBlockInSlot5 && item.getItem ( ) instanceof ItemBlock && ( ( ItemBlock ) item.getItem ( ) ).getBlock ( ) != Blocks.tnt) {
                                                    InvCleaner.this.swapItem ( ex , 4 , 2 );

                                                    try {
                                                        Thread.sleep ( ( long ) InvCleaner.this.delay.getCurrent ( ) );
                                                    } catch ( Exception var23 ) {
                                                    }
                                                    continue;
                                                }

                                                if (item.getItem ( ) instanceof ItemBlock) {
                                                    ItemBlock bl = ( ItemBlock ) item.getItem ( );
                                                    if (!isTntInSlot9 && bl.getBlock ( ) == Blocks.tnt) {
                                                        InvCleaner.this.swapItem ( ex , 8 , 2 );

                                                        try {
                                                            Thread.sleep ( ( long ) InvCleaner.this.delay.getCurrent ( ) );
                                                        } catch ( Exception var22 ) {
                                                        }
                                                        continue;
                                                    }
                                                }
                                            }

                                            if (item.getItem ( ) != Items.arrow && !( item.getItem ( ) instanceof ItemFood ) && !( item.getItem ( ) instanceof ItemPotion ) && !( item.getItem ( ) instanceof ItemBlock ) && item.getItem ( ) != Items.nether_star && item.getItem ( ) != Items.ender_eye && !( item.getItem ( ) == Items.ender_pearl | item.getItem ( ) == Items.experience_bottle | item.getItem ( ) == Items.snowball | item.getItem ( ) == Items.egg ) && item.getItem ( ) != Items.iron_ingot && item.getItem ( ) != Items.diamond && item.getItem ( ) != Items.stick) {
                                                if (!InvCleaner.this.sort.isToggled ( )) {
                                                    if (item.getItem ( ) == Items.bow || item.getItem ( ) == Items.fishing_rod) {
                                                        continue;
                                                    }
                                                } else if (ex == 37 && item.getItem ( ) == Items.bow || ex == 38 && item.getItem ( ) == Items.fishing_rod) {
                                                    continue;
                                                }

                                                if (!InvCleaner.this.keepTools.isToggled ( ) || !( item.getItem ( ) instanceof ItemTool )) {
                                                    try {
                                                        Thread.sleep ( ( long ) InvCleaner.this.delay.getCurrent ( ) );
                                                    } catch ( Exception var21 ) {
                                                    }

                                                    if (InvCleaner.this.openInv.isToggled ( ) && !( InvCleaner.this.mc.currentScreen instanceof GuiInventory )) {
                                                        InvCleaner.this.working = false;
                                                        return;
                                                    }

                                                    if (!( Minecraft.thePlayer.inventoryContainer.getSlot ( ex ).getStack ( ).getItem ( ) instanceof ItemArmor )) {
                                                        InvCleaner.this.dropSlot ( ex );
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } catch ( Exception var28 ) {
                                var28.printStackTrace ( );
                            } finally {
                                InvCleaner.this.working = false;
                            }

                        }
                    } ) ).start ( );
                }
            }
        }
    }

    public void dropSlot ( int slot ) {
        Minecraft.thePlayer.inventoryContainer.slotClick ( slot , 0 , 4 , Minecraft.thePlayer );
        this.mc.playerController.windowClick ( Minecraft.thePlayer.inventoryContainer.windowId , slot , 1 , 4 , Minecraft.thePlayer );
    }

    public float getSwordWertigkeit ( ItemStack i ) {
        if (i == null) {
            return -1.0F;
        } else {
            int damage = 0;
            Iterator b = i.getAttributeModifiers ( ).values ( ).iterator ( );

            while ( b.hasNext ( ) ) {
                AttributeModifier list = ( AttributeModifier ) b.next ( );
                if (list.getOperation ( ) == 0) {
                    damage = ( int ) ( ( double ) damage + list.getAmount ( ) );
                }
            }

            NBTTagList var9 = i.getEnchantmentTagList ( );
            int var10 = 0;
            if (var9 != null) {
                for ( ; !var9.get ( var10 ).toString ( ).equalsIgnoreCase ( "END" ); ++var10 ) {
                    NBTBase base = var9.get ( var10 );
                    String ench = base.toString ( );

                    try {
                        int strengh;
                        if (ench.contains ( "id:16s" )) {
                            strengh = Integer.valueOf ( ench.split ( "," )[ 0 ].replace ( "s" , "" ).substring ( 5 ) ).intValue ( );
                            damage = ( int ) ( ( double ) damage + ( double ) strengh * 1.25D );
                        } else if (ench.contains ( "id:20s" )) {
                            strengh = Integer.valueOf ( ench.split ( "," )[ 0 ].replace ( "s" , "" ).substring ( 5 ) ).intValue ( );
                            damage += strengh / 2;
                        }
                    } catch ( Exception var8 ) {
                    }
                }
            }

            if (this.preferSwords.isToggled ( ) && i.getItem ( ) instanceof ItemSword) {
                ++damage;
            }

            return ( float ) damage;
        }
    }

    public int getBestSwordSlot ( ) {
        int bestSlot = -1;
        float wertigkeit = -1.0F;

        for ( int i = Minecraft.thePlayer.inventoryContainer.getInventory ( ).size ( ) - 1; i > -1; --i ) {
            ItemStack item = Minecraft.thePlayer.inventoryContainer.getInventory ( ).get ( i );
            if (item != null && ( item.getItem ( ) instanceof ItemTool || item.getItem ( ) instanceof ItemSword && item.getItem ( ) != Items.bow ) && this.getSwordWertigkeit ( item ) > wertigkeit) {
                bestSlot = i;
                wertigkeit = this.getSwordWertigkeit ( item );
            }
        }

        return bestSlot;
    }

    public void swapItem ( int from , int to , int clickType ) {
        this.mc.playerController.windowClick ( Minecraft.thePlayer.inventoryContainer.windowId , from , to , clickType , Minecraft.thePlayer );
    }
}
