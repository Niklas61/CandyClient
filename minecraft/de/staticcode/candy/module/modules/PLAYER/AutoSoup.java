package de.staticcode.candy.module.modules.PLAYER;

import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSoup;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;

public class AutoSoup extends Module {

    public AutoSoup ( ) {
        super ( "AutoSoup" , Category.PLAYER );
    }

    private int lastSlot = 0;
    private boolean isHealing = false;
    private float delay = 0.0f;

    @Override
    public void onUpdate ( ) {

        delay += 0.50f;
        if (!this.containsInvSoups ( ) && getRedMushrooms ( ) > 0 && getBrownMushrooms ( ) > 0 && getBowls ( ) > 0) {
            this.recraftSoups ( );
            return;
        }
        lastSlot = Minecraft.thePlayer.inventory.currentItem;

        this.isHealing = false;

        if (!isHealing) {

            if (isHotbarEmpty ( )) {
                refillSoups ( );
                return;
            }

            if (shouldEat ( )) {

                int slot = getSoup ( );
                System.out.println ( slot );
                Minecraft.thePlayer.sendQueue.addToSendQueue ( new C09PacketHeldItemChange ( slot ) );
                isHealing = true;

                Minecraft.thePlayer.sendQueue
                        .addToSendQueue ( new C08PacketPlayerBlockPlacement ( Minecraft.thePlayer.inventory.getStackInSlot ( slot ) ) );

                new Thread ( new Runnable ( ) {

                    @Override
                    public void run ( ) {
                        try {

                            if (Minecraft.thePlayer.inventoryContainer.getSlot ( 3 ).getStack ( ) != null
                                    || Minecraft.thePlayer.inventoryContainer.getSlot ( 4 ).getStack ( ) != null
                                    || Minecraft.thePlayer.inventoryContainer.getSlot ( 2 ) != null)
                                moveItemDown ( );

                            Thread.sleep ( 200 );
                            dropSoup ( ( 36 + slot ) );

                            Thread.sleep ( 50 );
                            Minecraft.thePlayer.sendQueue.addToSendQueue ( new C09PacketHeldItemChange ( lastSlot ) );
                            isHealing = false;
                        } catch ( InterruptedException e ) {
                        }
                    }
                } ).start ( );

            }
        }

        super.onUpdate ( );
    }

    public void dropSoup ( int slot ) {
        Minecraft.thePlayer.inventoryContainer.slotClick ( slot , 0 , 4 , Minecraft.thePlayer );
        mc.playerController.windowClick ( Minecraft.thePlayer.inventoryContainer.windowId , slot , 1 , 4 , Minecraft.thePlayer );
    }

    public boolean shouldEat ( ) {
        return Minecraft.thePlayer.getHealth ( ) <= 10f;
    }

    public boolean isHotbarEmpty ( ) {

        for ( int slots = 0; slots < 9; slots++ ) {
            if (Minecraft.thePlayer.inventoryContainer.getSlot ( ( 36 + slots ) ).getStack ( ) != null
                    && Minecraft.thePlayer.inventoryContainer.getSlot ( ( 36 + slots ) ).getStack ( ).getItem ( ) instanceof ItemSoup) {
                return false;

            }
        }
        return true;
    }

    public boolean containsBowls ( ) {

        for ( int slots = 0; slots < 9; slots++ ) {
            if (Minecraft.thePlayer.inventoryContainer.getSlot ( ( 36 + slots ) ).getStack ( ) != null
                    && Minecraft.thePlayer.inventoryContainer.getSlot ( ( 36 + slots ) ).getStack ( ).getItem ( ) == Items.bowl) {
                return true;

            }
        }
        return false;
    }

    public int getBowl ( ) {
        int freeSlot = 0;
        for ( int slots = 0; slots < 9; slots++ ) {
            if (Minecraft.thePlayer.inventoryContainer.getSlot ( ( 36 + slots ) ).getStack ( ) != null
                    && Minecraft.thePlayer.inventoryContainer.getSlot ( ( 36 + slots ) ).getStack ( ).getItem ( ) == Items.bowl) {
                freeSlot = slots;
                break;
            }
        }
        return freeSlot;
    }

    public void refillSoups ( ) {

        new Thread ( new Runnable ( ) {

            @Override
            public void run ( ) {
                isHealing = true;

                while ( containsBowls ( ) ) {
                    int slot = getBowl ( );
                    try {
                        Thread.sleep ( 10L );
                        dropSoup ( 36 + slot );
                    } catch ( InterruptedException e ) {
                    }
                }

                for ( int slots = 8; slots < 36; slots++ ) {

                    if (Minecraft.thePlayer.inventoryContainer.getSlot ( slots ).getStack ( ) != null
                            && Minecraft.thePlayer.inventoryContainer.getSlot ( slots ).getStack ( )
                            .getItem ( ) instanceof ItemSoup) {

                        int empty = getEmptySpace ( );

                        if (Minecraft.thePlayer.inventoryContainer.getSlot ( 36 + empty ).getStack ( ) != null) {
                            break;
                        }
                        try {
                            Thread.sleep ( 10L );

                            mc.playerController.windowClick ( Minecraft.thePlayer.inventoryContainer.windowId , slots , empty , 2 ,
                                    Minecraft.thePlayer );
                        } catch ( InterruptedException e ) {
                        }
                    }
                }
                isHealing = false;

            }
        } ).start ( );

    }

    public void recraftSoups ( ) {
        isHealing = true;

        new Thread ( new Runnable ( ) {

            @Override
            public void run ( ) {
                try {
                    if (getFreeSpaceAmount ( ) > 3 && getRedMushrooms ( ) > 0 && getBrownMushrooms ( ) > 0
                            && getBowls ( ) > 0) {

                        moveItemsUp ( );

                        while ( getFreeSpaceAmount ( ) > 3 && isToggled ( ) ) {
                            Thread.sleep ( 20L );
                            craft ( );
                        }

                        Thread.sleep ( 10L );
                        moveItemDown ( );

                        isHealing = false;
                    }
                } catch ( Exception e ) {
                    e.printStackTrace ( );
                }

            }
        } ).start ( );

    }

    public void craft ( ) {
        this.moveRecraft ( 0 , this.getFreeInvSpace ( ) );
    }

    public void moveItemsUp ( ) throws InterruptedException {
        this.moveRecraft ( this.getBowls ( ) , 4 );
        this.moveRecraft ( this.getRedMushrooms ( ) , 3 );
        this.moveRecraft ( this.getBrownMushrooms ( ) , 2 );
    }

    public void moveRecraft ( int from , int to ) {
        mc.playerController.windowClick ( Minecraft.thePlayer.inventoryContainer.windowId , from , 0 , 0 , Minecraft.thePlayer );
        mc.playerController.windowClick ( Minecraft.thePlayer.inventoryContainer.windowId , to , 0 , 0 , Minecraft.thePlayer );
    }

    public void moveItemDown ( ) throws InterruptedException {
        this.moveRecraft ( 4 , this.getFreeInvSpace ( ) );
        Thread.sleep ( 50L );
        this.moveRecraft ( 3 , this.getFreeInvSpace ( ) );
        Thread.sleep ( 50L );
        this.moveRecraft ( 2 , this.getFreeInvSpace ( ) );
    }

    public void swapItem ( int inv , int slot ) {
        mc.playerController.windowClick ( Minecraft.thePlayer.inventoryContainer.windowId , inv , slot , 0 , Minecraft.thePlayer );
    }

    public int getBrownMushrooms ( ) {

        for ( int slots = 8; slots < 45; slots++ ) {

            if (Minecraft.thePlayer.inventoryContainer.getSlot ( slots ).getStack ( ) != null && Item
                    .getIdFromItem ( ( Minecraft.thePlayer.inventoryContainer.getSlot ( slots ).getStack ( ).getItem ( ) ) ) == 39) {
                return slots;
            }
        }
        return 0;
    }

    public int getRedMushrooms ( ) {

        for ( int slots = 0; slots < 45; slots++ ) {

            if (Minecraft.thePlayer.inventoryContainer.getSlot ( slots ).getStack ( ) != null && Item
                    .getIdFromItem ( ( Minecraft.thePlayer.inventoryContainer.getSlot ( slots ).getStack ( ).getItem ( ) ) ) == 40) {
                return slots;
            }
        }
        return 0;
    }

    public int getBowls ( ) {

        for ( int slots = 8; slots < 45; slots++ ) {

            if (Minecraft.thePlayer.inventoryContainer.getSlot ( slots ).getStack ( ) != null
                    && Minecraft.thePlayer.inventoryContainer.getSlot ( slots ).getStack ( ).getItem ( ) == Item.getItemById ( 281 )) {
                return slots;
            }
        }
        return 0;
    }

    public int getSoup ( ) {
        int freeSlot = 0;
        for ( int slots = 0; slots < 9; slots++ ) {
            if (Minecraft.thePlayer.inventoryContainer.getSlot ( ( 36 + slots ) ).getStack ( ) != null
                    && Minecraft.thePlayer.inventoryContainer.getSlot ( ( 36 + slots ) ).getStack ( ).getItem ( ) instanceof ItemSoup) {
                freeSlot = slots;
                break;
            }
        }
        return freeSlot;
    }

    public boolean containsInvSoups ( ) {
        for ( int slots = 0; slots < 36; slots++ ) {
            if (Minecraft.thePlayer.inventoryContainer.getSlot ( slots ).getStack ( ) != null
                    && Minecraft.thePlayer.inventoryContainer.getSlot ( slots ).getStack ( ).getItem ( ) instanceof ItemSoup) {
                return true;
            }
        }
        return false;
    }

    public int getFreeSpaceAmount ( ) {
        int amount = 0;
        for ( int slots = 9; slots < 36; slots++ ) {
            if (Minecraft.thePlayer.inventoryContainer.getSlot ( slots ).getStack ( ) == null) {
                amount++;
            }
        }

        return amount;
    }

    public int getFreeInvSpace ( ) {
        for ( int slots = 9; slots < 45; slots++ ) {
            if (Minecraft.thePlayer.inventoryContainer.getSlot ( slots ).getStack ( ) == null) {
                return slots;
            }
        }

        return 0;
    }

    public int getSlots ( ) {
        for ( int slots = 9; slots < 36; slots++ ) {
            if (Minecraft.thePlayer.inventoryContainer.getSlot ( slots ).getStack ( ) == null) {
                return slots;
            }
        }
        return 0;
    }

    public int getEmptySpace ( ) {
        int freeSlot = 0;
        for ( int slots = ( Minecraft.thePlayer.inventoryContainer.getSlot ( ( 37 ) ).getStack ( ) != null ? 1
                : 0 ); slots < 9; slots++ ) {
            if (Minecraft.thePlayer.inventoryContainer.getSlot ( ( 36 + slots ) ).getStack ( ) == null) {
                freeSlot = slots;
                break;
            }
        }
        return freeSlot;
    }

}