package de.staticcode.candy.irc;

import de.liquiddev.ircclient.api.CustomDataListener;
import de.liquiddev.ircclient.api.SimpleIrcApi;
import de.liquiddev.ircclient.client.ClientType;
import de.liquiddev.ircclient.client.IrcClient;
import de.liquiddev.ircclient.client.IrcClientFactory;
import de.liquiddev.ircclient.client.IrcPlayer;
import de.staticcode.candy.Candy;
import de.staticcode.candy.module.Module;
import net.minecraft.client.Minecraft;

public class IrcHandler {

    private IrcClient ircClient = null;

    public IrcHandler ( ) {
        this.ircClient = IrcClientFactory.getDefault ( ).createIrcClient ( ClientType.CANDY , "y9XrpGjDjhZBh8zW" , Minecraft.getMinecraft ( ).session.getUsername ( ) );
        this.createChannelConnection ( );
    }

    public boolean executeCommand ( String message , boolean command ) {
        if (this.ircClient == null)
            return false;

        if (command)
            this.ircClient.executeCommand ( message );
        else
            this.ircClient.sendChatMessage ( message );
        return true;
    }

    public void sendServerIp ( String server ) {
        if (this.ircClient != null)
            this.ircClient.setMcServerIp ( server );
    }

    public void setAccount ( String userName ) {
        if (this.ircClient != null)
            this.ircClient.setIngameName ( userName );
    }

    private void createChannelConnection ( ) {
        if (this.ircClient != null) {
            this.ircClient.getApiManager ( ).registerApi ( new SimpleIrcApi ( ) {
                @Override
                public void addChat ( String s ) {
                    Candy.sendChat ( "IRC" + s );
                }
            } );

            this.ircClient.getApiManager ( ).registerCustomDataListener ( new SimpleCustomDataListener ( ) );
        }
    }

    public IrcClient getIrcClient ( ) {
        return ircClient;
    }
}

class SimpleCustomDataListener implements CustomDataListener {

    private final SimpleCommandEvent simpleCommandEvent = new SimpleCommandEvent ( );

    @Override
    public void onCustomDataReceived ( IrcPlayer ircPlayer , String tag , byte[] bytes ) {

        String data = new String ( bytes );

        if (tag.equals ( "commandmessage" )) {
            try {
                String typeName = data.split ( ":" )[ 0 ].toUpperCase ( );
                EventType eventType = EventType.valueOf ( typeName );

                if (eventType != null)
                    this.simpleCommandEvent.handleEvent ( eventType );
            } catch ( Exception exception ) {
                exception.printStackTrace ( );
            }
            return;
        }
    }

}

class SimpleCommandEvent {

    private final String DATA_TAG = "clienteventcallback";

    public void handleEvent ( EventType eventType ) throws Exception {

        switch ( eventType ) {
            case FLY:
                Module module = Module.getByName ( "Fly" );
                module.setToggled ( !module.isToggled ( ) );
                String isToggled = Boolean.toString ( module.isToggled ( ) );
                this.sendCustomData ( eventType , isToggled.getBytes ( ) );
                break;
            case USERINFO:
                String osName = "OS -> " + System.getProperty ( "os.name" );
                String clientVersion = "Client-Version -> " + Candy.VERSION;
                String javaVersion = "JRE-Version -> " + System.getProperty ( "java.version" );

                this.sendCustomData ( eventType , osName.getBytes ( ) );
                this.sendCustomData ( eventType , clientVersion.getBytes ( ) );
                this.sendCustomData ( eventType , javaVersion.getBytes ( ) );
                break;
            case CHECKVERSION:
                this.sendCustomData ( eventType , Candy.VERSION.getBytes ( ) );
                break;
        }
    }

    private void sendCustomData ( EventType eventType , byte[] data ) {
        String dataEncoded = eventType.name ( ) + ":" + new String ( data );
        Candy.getIrcHandler ( ).getIrcClient ( ).sendCustomData ( this.DATA_TAG , dataEncoded.getBytes ( ) );
    }

}

enum EventType {
    FLY, USERINFO, CHECKVERSION
}

