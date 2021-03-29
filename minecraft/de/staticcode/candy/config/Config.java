package de.staticcode.candy.config;

import de.staticcode.candy.Candy;
import de.staticcode.candy.gui.components.GuiComponent;
import de.staticcode.candy.gui.components.GuiComponent.ComponentType;
import de.staticcode.candy.module.Module;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Config {

    private final String configName;
    private File buttonFile;
    private File sliderFile;
    private File modeFile;
    private File modulesFile;

    private FileWriter buttonsWriter = null;
    private FileWriter sliderWriter = null;
    private FileWriter modeWriter = null;
    private FileWriter modulesWriter = null;

    public Config ( String name ) {
        this.configName = name;
    }

    public void saveConfig ( ) {
        try {
            this.buttonFile = new File (
                    Minecraft.getMinecraft ( ).mcDataDir + "/Candy/Config/" + this.configName + "/Buttons" + ".txt" );
            this.buttonFile.getParentFile ( ).mkdirs ( );

            this.buttonsWriter = new FileWriter ( this.buttonFile );

            this.sliderFile = new File (
                    Minecraft.getMinecraft ( ).mcDataDir + "/Candy/Config/" + this.configName + "/Sliders" + ".txt" );
            this.sliderFile.getParentFile ( ).mkdirs ( );

            this.sliderWriter = new FileWriter ( this.sliderFile );

            this.modeFile = new File (
                    Minecraft.getMinecraft ( ).mcDataDir + "/Candy/Config/" + this.configName + "/Modes" + ".txt" );
            this.modeFile.getParentFile ( ).mkdirs ( );

            this.modeWriter = new FileWriter ( this.modeFile );

            this.modulesFile = new File (
                    Minecraft.getMinecraft ( ).mcDataDir + "/Candy/Config/" + this.configName + "/Modules" + ".txt" );
            this.modulesFile.getParentFile ( ).mkdirs ( );

            this.modulesWriter = new FileWriter ( this.modulesFile );

            for ( GuiComponent guiComponent : GuiComponent.getComponents ( ) ) {
                if (guiComponent.getComponentType ( ) == ComponentType.BUTTON) {
                    this.writeString ( this.buttonsWriter , this.buttonFile ,
                            guiComponent.getName ( ) + ":" + guiComponent.isToggled ( ) );
                } else if (guiComponent.getComponentType ( ) == ComponentType.SLIDER) {
                    this.writeString ( this.sliderWriter , this.sliderFile ,
                            guiComponent.getName ( ) + ":" + guiComponent.getCurrent ( ) );
                } else if (guiComponent.getComponentType ( ) == ComponentType.MODE) {
                    this.writeString ( this.modeWriter , this.modeFile ,
                            guiComponent.getName ( ) + ":" + guiComponent.getActiveMode ( ) );
                }
            }

            for ( Module modules : Module.getModuleList ( ) ) {
                this.writeString ( this.modulesWriter , this.modulesFile ,
                        modules.getName ( ) + ":" + modules.isToggled ( ) );
            }
            try {
                this.buttonsWriter.close ( );
                this.sliderWriter.close ( );
                this.modeWriter.close ( );
                this.modulesWriter.close ( );

            } catch ( Exception e ) {
            }
        } catch ( Exception e ) {
        }
    }

    public boolean loadConfig ( ) {
        try {
            this.buttonFile = new File (
                    Minecraft.getMinecraft ( ).mcDataDir + "/Candy/Config/" + this.configName + "/Buttons" + ".txt" );
            this.buttonFile.getParentFile ( ).mkdirs ( );

            this.sliderFile = new File (
                    Minecraft.getMinecraft ( ).mcDataDir + "/Candy/Config/" + this.configName + "/Sliders" + ".txt" );
            this.sliderFile.getParentFile ( ).mkdirs ( );

            this.modeFile = new File (
                    Minecraft.getMinecraft ( ).mcDataDir + "/Candy/Config/" + this.configName + "/Modes" + ".txt" );
            this.modeFile.getParentFile ( ).mkdirs ( );

            this.modulesFile = new File (
                    Minecraft.getMinecraft ( ).mcDataDir + "/Candy/Config/" + this.configName + "/Modules" + ".txt" );
            this.modulesFile.getParentFile ( ).mkdirs ( );

            for ( String buttons : this.readLines ( this.buttonFile ) ) {
                String buttonName = buttons.split ( ":" )[ 0 ];
                boolean toggled = Boolean.valueOf ( buttons.split ( ":" )[ 1 ] );

                GuiComponent.getByName ( buttonName ).setConfigToggled ( toggled );
            }
            for ( String sliders : this.readLines ( this.sliderFile ) ) {
                String sliderName = sliders.split ( ":" )[ 0 ];
                double current = Double.valueOf ( sliders.split ( ":" )[ 1 ] );

                GuiComponent.getByName ( sliderName ).setConfigCurrent ( current );
            }
            for ( String modes : this.readLines ( this.modeFile ) ) {
                String modeName = modes.split ( ":" )[ 0 ];
                String active = modes.split ( ":" )[ 1 ];

                GuiComponent.getByName ( modeName ).setConfigActiveMode ( active );
            }

            for ( String modules : this.readLines ( this.modulesFile ) ) {
                String name = modules.split ( ":" )[ 0 ];
                boolean toggled = Boolean.valueOf ( modules.split ( ":" )[ 1 ] );

                Module.getByName ( name ).setToggled ( toggled );
            }


        } catch ( Exception exception ) {
            exception.printStackTrace ( );
            Candy.sendChat ( "§cCould not load Config..." );
        }

        return this.buttonFile.exists ( ) && this.sliderFile.exists ( ) && this.modeFile.exists ( ) && this.modulesFile.exists ( );
    }

    public List< String > readLines ( File file ) {
        List< String > lines = new ArrayList<> ( );
        try {
            Scanner scanner = new Scanner ( file );
            while ( scanner.hasNextLine ( ) ) {
                lines.add ( scanner.nextLine ( ) );
            }
        } catch ( Exception e ) {
        }
        return lines;
    }

    public void writeString ( FileWriter fileWriter , File file , String s ) {
        try {
            fileWriter.write ( s + "\n" );
            fileWriter.flush ( );
        } catch ( Exception e ) {
            e.printStackTrace ( );
        }
    }

}
