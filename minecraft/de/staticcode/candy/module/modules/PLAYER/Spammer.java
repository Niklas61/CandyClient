package de.staticcode.candy.module.modules.PLAYER;

import de.staticcode.candy.Candy;
import de.staticcode.candy.gui.components.GuiComponent;
import de.staticcode.candy.module.Module;
import de.staticcode.candy.module.category.Category;
import de.staticcode.candy.utils.Timings;
import net.minecraft.client.Minecraft;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Spammer extends Module {

    private final Timings timings = new Timings ( );

    private final GuiComponent timingsSlider = new GuiComponent ( "MS" , this , 50000d , 1d , 500d );

    private String SPAM_MESSAGE = "Client: " + Candy.NAME + " - Version: " + Candy.VERSION
            + " | Developed by StaticCode & LiquidDev.";

    public Spammer ( ) {
        super ( "Spammer" , Category.PLAYER );

        if (Candy.getModuleConfigs ( ).containsValue ( "Spammer.Message" )) {
            this.SPAM_MESSAGE = Candy.getModuleConfigs ( ).getString ( "Spammer.Message" );
        }
    }

    @Override
    public void onCommand ( String[] args ) {
        if (args.length < 2) {
            Candy.sendChat ( ".Spammer <Message>" );
            return;
        }

        StringBuilder b = new StringBuilder ( );
        for ( int i = 2; i < args.length; i++ ) {
            if (i == args.length) {
                b.append ( args[ i ] );
            } else {
                b.append ( args[ i ] + " " );
            }
        }
        this.SPAM_MESSAGE = b.toString ( );
        Candy.getModuleConfigs ( ).setString ( "Spammer.Message" , b.toString ( ) );
        Candy.getModuleConfigs ( ).save ( );
        Candy.sendChat ( "Spammer Message was set to: " + b.toString ( ) );
        super.onCommand ( args );
    }

    @Override
    public void onUpdate ( ) {
        if (this.timings.hasReached ( ( long ) this.timingsSlider.getCurrent ( ) )) {
            this.sendMessage ( );
            this.timings.resetTimings ( );
        }
        super.onUpdate ( );
    }

    private void sendMessage ( ) {
        List< String > array = Arrays.asList ( "GommeHD" , "Rewinside" , "ArazuhlHD" , "SturmwaffelLP" , "ZanderLP" ,
                "Drachenlord" , "Haze" , "HazeMerch" );
        Minecraft.thePlayer.sendChatMessage ( "[" + Math.round ( Math.random ( ) * 1000d ) / 1000d + "] " + this.SPAM_MESSAGE + " ("
                + RandomStringUtils.randomAlphanumeric ( 10 ) + ")" + array.get ( new Random ( ).nextInt ( array.size ( ) ) ) );
    }

}
