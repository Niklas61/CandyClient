package de.staticcode.candy.gui.components;

import de.staticcode.candy.Candy;
import de.staticcode.candy.gui.ui.GuiButton;
import de.staticcode.candy.module.Module;

import java.util.ArrayList;
import java.util.List;

public class GuiComponent {

    public enum ComponentType {
        BUTTON, SLIDER, MODE
    }

    static List< GuiComponent > components = new ArrayList<> ( );

    public static List< GuiComponent > getComponents ( ) {
        return components;
    }

    // Mains
    private GuiButton guiButton;
    private final String name;
    private final Module module;
    private final ComponentType componentType;
    private boolean render = true;
    private boolean isHovered;

    // Button
    private boolean toggled;

    // Slider
    private double max;
    private double min;
    private double current;

    // Mode
    private List< String > modes;
    private String activeMode;
    private boolean isExtend = false;
    private boolean isModeHovered = false;

    public static GuiComponent getByName ( String name ) {
        for ( GuiComponent guiComponent : getComponents ( ) ) {
            if (guiComponent.getName ( ).equals ( name ))
                return guiComponent;
        }
        return null;
    }

    public GuiComponent ( String name , Module module , boolean toggled ) {
        this.name = name;
        this.module = module;
        this.toggled = toggled;
        this.componentType = ComponentType.BUTTON;
        components.add ( this );
    }

    public GuiComponent ( String name , Module module , double max , double min , double current ) {
        this.name = name;
        this.module = module;
        this.max = max;
        this.min = min;
        this.current = current;
        this.componentType = ComponentType.SLIDER;
        components.add ( this );
    }

    public GuiComponent ( String name , Module module , List< String > modes , String active ) {
        this.name = name;
        this.module = module;
        this.modes = modes;
        this.activeMode = active;
        this.componentType = ComponentType.MODE;
        components.add ( this );

        if (!Candy.getGuiModes ( ).containsValue ( this.getName ( ) )) {
            Candy.getGuiModes ( ).setString ( this.getName ( ) , this.getActiveMode ( ) );
            Candy.getGuiModes ( ).save ( );
        }
    }

    // Mains
    public String getName ( ) {
        return name;
    }

    public Module getModule ( ) {
        return module;
    }

    public ComponentType getComponentType ( ) {
        return componentType;
    }

    public boolean isRender ( ) {
        return render;
    }

    public boolean isHovered ( ) {
        return isHovered;
    }

    public void setRender ( boolean render ) {
        this.render = render;
    }

    public void setGuiButton ( GuiButton guiButton ) {
        this.guiButton = guiButton;
    }

    public void setHovered ( boolean isHovered ) {
        this.isHovered = isHovered;
    }

    // Button
    public boolean isToggled ( ) {
        return toggled;
    }

    public void setToggled ( boolean toggled ) {
        this.toggled = toggled;
        Candy.getGuiButtons ( ).setBoolean ( this.getName ( ) , this.isToggled ( ) );
        Candy.getGuiButtons ( ).save ( );
    }

    public void setConfigToggled ( boolean toggled ) {
        this.toggled = toggled;
    }

    // Slider
    public double getMax ( ) {
        return max;
    }

    public double getMin ( ) {
        return min;
    }

    public double getCurrent ( ) {
        return current;
    }

    public void setMax ( double max ) {
        this.max = max;
    }

    public void setMin ( double min ) {
        this.min = min;
    }

    public void setCurrent ( double current ) {
        this.current = current;
        Candy.getGuiSliders ( ).setDouble ( this.getName ( ) , this.getCurrent ( ) );
        Candy.getGuiSliders ( ).save ( );
    }

    public void setConfigCurrent ( double current ) {
        this.current = current;
    }

    // Mode
    public List< String > getModes ( ) {
        return modes;
    }

    public String getActiveMode ( ) {
        return activeMode;
    }

    public boolean isExtend ( ) {
        return isExtend;
    }

    public boolean isModeHovered ( ) {
        return isModeHovered;
    }

    public void setExtend ( boolean isExtend ) {
        this.isExtend = isExtend;
    }

    public void setActiveMode ( String activeMode ) {
        this.activeMode = activeMode;
        for ( Module modules : Module.getModuleList ( ) ) {
            if (modules == this.module)
                modules.onModeChange ( this.activeMode );
        }
        Candy.getGuiModes ( ).setString ( this.getName ( ) , this.getActiveMode ( ) );
        Candy.getGuiModes ( ).save ( );
    }

    public void setConfigActiveMode ( String activeMode ) {
        this.activeMode = activeMode;
    }

    public void setModeHovered ( boolean isModeHovered ) {
        this.isModeHovered = isModeHovered;
    }

}
