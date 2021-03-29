package de.staticcode.candy.save;

import de.staticcode.candy.Candy;
import de.staticcode.candy.gui.components.GuiComponent;
import de.staticcode.candy.gui.components.GuiComponent.ComponentType;
import de.staticcode.candy.ingame.IngameDrawer;
import de.staticcode.candy.module.Module;

public class LoadConfig {

    public LoadConfig ( ) {
        this.loadActiveModules ( );
        this.loadKeybinds ( );

        this.loadModes ( );
        this.loadButtons ( );
        this.loadSliders ( );

        IngameDrawer.setupedModules = true;
    }

    public void loadActiveModules ( ) {
        for ( Module modules : Module.getModuleList ( ) ) {
            if (Candy.getActiveModules ( ).containsValue ( modules.getName ( ) )) {
                modules.setToggled ( Candy.getActiveModules ( ).getBoolean ( modules.getName ( ) ) );
            }
        }
    }

    public void loadKeybinds ( ) {
        for ( Module modules : Module.getModuleList ( ) ) {
            if (Candy.getModuleKeybinds ( ).containsValue ( modules.getName ( ) )) {
                System.out.println ( Candy.getModuleKeybinds ( ).getInteger ( modules.getName ( ) ) );
                modules.setKey ( Candy.getModuleKeybinds ( ).getInteger ( modules.getName ( ) ) );
            }
        }
    }

    public void loadButtons ( ) {
        for ( GuiComponent guiComponent : GuiComponent.getComponents ( ) ) {
            if (guiComponent.getComponentType ( ) == ComponentType.BUTTON) {
                if (Candy.getGuiButtons ( ).containsValue ( guiComponent.getName ( ) )) {
                    guiComponent.setToggled ( Candy.getGuiButtons ( ).getBoolean ( guiComponent.getName ( ) ) );
                }
            }
        }
    }

    public void loadSliders ( ) {
        for ( GuiComponent guiComponent : GuiComponent.getComponents ( ) ) {
            if (guiComponent.getComponentType ( ) == ComponentType.SLIDER) {
                if (Candy.getGuiSliders ( ).containsValue ( guiComponent.getName ( ) )) {
                    guiComponent.setCurrent ( Candy.getGuiSliders ( ).getDouble ( guiComponent.getName ( ) ) );
                }
            }
        }
    }

    public void loadModes ( ) {
        for ( GuiComponent guiComponent : GuiComponent.getComponents ( ) ) {
            if (guiComponent.getComponentType ( ) == ComponentType.MODE) {
                if (Candy.getGuiModes ( ).containsValue ( guiComponent.getName ( ) )) {
                    if (guiComponent.getModule ( ).getName ( ).equalsIgnoreCase ( "Killaura" ))
                        continue;
                    guiComponent.setActiveMode ( Candy.getGuiModes ( ).getString ( guiComponent.getName ( ) ) );
                }
            }
        }
    }

}
