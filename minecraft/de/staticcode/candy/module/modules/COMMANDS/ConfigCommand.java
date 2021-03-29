package de.staticcode.candy.module.modules.COMMANDS;

import de.staticcode.candy.Candy;
import de.staticcode.candy.config.Config;
import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;

public class ConfigCommand extends Module {

    public ConfigCommand ( ) {
        super ( "Config" , Category.COMMANDS );
    }

    @Override
    public void onCommand ( String[] args ) {
        if (args.length == 1) {
            Candy.sendChat ( ".config save|load <Name>" );
            return;
        }
        String option = args[ 1 ];
        String configName = args[ 2 ];

        if (configName.equalsIgnoreCase ( "" ) || configName.equalsIgnoreCase ( " " )) {
            Candy.sendChat ( ".config save:load <Name>" );
            return;
        }

        if (option.equalsIgnoreCase ( "save" )) {
            new Config ( configName ).saveConfig ( );
            Candy.sendChat ( "Config was saved with name: " + configName );
            return;
        } else if (option.equalsIgnoreCase ( "load" )) {
            if (new Config ( configName ).loadConfig ( )) {
                Candy.sendChat ( "Config " + configName + " was loaded!" );
            } else {
                Candy.sendChat ( "Config " + configName + " dosnt exist!" );
            }
            return;
        }
    }

}
