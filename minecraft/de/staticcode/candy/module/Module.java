package de.staticcode.candy.module;

import de.staticcode.candy.Candy;
import de.staticcode.candy.module.category.Category;
import de.staticcode.candy.module.modules.COMBAT.Killaura;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Module {

    static List< Module > moduleList = new ArrayList<> ( );

    private String name;
    private int key;
    private boolean toggled;
    private Category category;

    public String displayName;

    private final int randomColor;
    public int slideIn;

    public boolean shouldFadeOut;
    public boolean settedY;

    public int moduleID;
    public int yCount;

    public boolean show = true;

    public Minecraft mc = Minecraft.getMinecraft ( );

    public static List< Module > getModuleList ( ) {
        return moduleList;
    }

    public static Module getByName ( String name ) {
        for ( Module modules : getModuleList ( ) ) {
            if (modules.getName ( ).equalsIgnoreCase ( name )) {
                return modules;
            }
        }
        return null;
    }

    public static void updateAllModules ( ) {
        for ( Module modules : getModuleList ( ) ) {
            if (modules.isToggled ( ))
                modules.onUpdate ( );

            if (modules instanceof Killaura) {
                if (Killaura.isTurnOffRotation)
                    Killaura.getKillaura ( ).updateTurnoffRotation ( );
            }
        }
    }

    public static void toggleOnKeyPress ( int key ) {
        for ( Module modules : getModuleList ( ) ) {
            if (modules.getKey ( ) == key)
                modules.setToggled ( !modules.isToggled ( ) );
        }
    }

    public Module ( String name , int key , Category category ) {
        this.name = name;
        this.key = key;
        this.category = category;
        this.randomColor = Candy.RGBtoHEX ( new Random ( ).nextInt ( 200 ) + 50 , new Random ( ).nextInt ( 200 ) + 50 ,
                new Random ( ).nextInt ( 200 ) + 50 , 255 );
        this.displayName = name;
        moduleList.add ( this );
    }

    public Module ( String name , Category category ) {
        this.name = name;
        this.key = 0;
        this.category = category;
        this.randomColor = Candy.RGBtoHEX ( new Random ( ).nextInt ( 200 ) + 50 , new Random ( ).nextInt ( 200 ) + 50 ,
                new Random ( ).nextInt ( 200 ) + 50 , 255 );
        this.displayName = name;
        moduleList.add ( this );
    }

    public void onUpdate ( ) {
    }

    public void onEnable ( ) {
        if (!this.shouldFadeOut) {
            this.slideIn = 0;
        }
        this.shouldFadeOut = false;
    }

    public void onCommand ( String[] args ) {

    }

    public void onDisable ( ) {
        this.shouldFadeOut = true;
    }

    public void setKey ( int key ) {
        this.key = key;
        Candy.getModuleKeybinds ( ).setInteger ( this.name , key );
        Candy.getModuleKeybinds ( ).save ( );
    }

    public void setName ( String name ) {
        this.name = name;
    }

    public void setCategory ( Category category ) {
        this.category = category;
    }

    public void setToggled ( boolean toggled ) {
        this.toggled = toggled;

        if (this.isToggled ( ))
            this.onEnable ( );
        else
            this.onDisable ( );

        Candy.getActiveModules ( ).setBoolean ( this.name , this.toggled );
        Candy.getActiveModules ( ).save ( );
    }

    public String getName ( ) {
        return name;
    }

    public int getKey ( ) {
        return key;
    }

    public Category getCategory ( ) {
        return category;
    }

    public boolean isToggled ( ) {
        return toggled;
    }

    public int getRandomColor ( ) {
        return randomColor;
    }

    public void onModeChange ( String mode ) {
    }

}
