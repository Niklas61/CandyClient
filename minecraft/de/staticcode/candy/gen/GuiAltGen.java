package de.staticcode.candy.gen;

import com.mojang.authlib.Agent;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.util.Session;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class GuiAltGen extends GuiScreen {

    private GuiTextField inputField;
    private final String altListURL = "https://pastebin.com/raw/1a04gz51";
    private String statusMessage;

    private final List< String > alts = this.getAlts ( );

    @Override
    public void initGui ( ) {
        this.buttonList.clear ( );
        this.buttonList.add ( new GuiButton ( 1 , this.width / 2 - 100 , ScaledResolution.getScaledHeight ( ) / 2 + 36 + 12 ,
                200 , 20 , "Generate" ) );
        this.buttonList.add ( new GuiButton ( 2 , this.width / 2 - 100 , ScaledResolution.getScaledHeight ( ) / 2 + 10 + 12 ,
                200 , 20 , "Copy" ) );
        this.buttonList.add ( new GuiButton ( 4 , this.width / 2 - 100 , ScaledResolution.getScaledHeight ( ) / 2 + 60 + 12 ,
                200 , 20 , "Login and Play" ) );

        this.inputField = new GuiTextField ( 3 , Minecraft.fontRendererObj , 0 , ScaledResolution.getScaledHeight ( ) / 2 - 36 + 12 ,
                ScaledResolution.getScaledWidth ( ) , 20 );
        super.initGui ( );
    }

    @Override
    public void drawScreen ( int mouseX , int mouseY , float partialTicks ) {
        this.drawDefaultBackground ( );
        this.inputField.drawTextBox ( );
        GL11.glPushMatrix ( );
        GL11.glScalef ( 0.8f , 0.8f , 0.8f );
        this.drawString ( Minecraft.fontRendererObj , "Alts by �2PatCatHD" , 3 , 3 , -1 );
        GL11.glPopMatrix ( );

        this.drawString ( Minecraft.fontRendererObj , this.statusMessage , 3 , 10 , -1 );
        super.drawScreen ( mouseX , mouseY , partialTicks );
    }

    @Override
    protected void actionPerformed ( GuiButton button ) throws IOException {
        if (button.id == 2) {
            Toolkit defaultToolkit = Toolkit.getDefaultToolkit ( );
            Clipboard systemClipboard = defaultToolkit.getSystemClipboard ( );
            systemClipboard.setContents ( new StringSelection ( this.inputField.getText ( ) ) , null );
        }
        if (button.id == 1) {
            this.inputField.setText ( this.alts.get ( new Random ( ).nextInt ( this.alts.size ( ) ) ) );
        }

        if (button.id == 4) {
            Toolkit defaultToolkit = Toolkit.getDefaultToolkit ( );
            Clipboard systemClipboard = defaultToolkit.getSystemClipboard ( );
            systemClipboard.setContents ( new StringSelection ( this.inputField.getText ( ) ) , null );

            if (this.tryToLogin ( )) {
                mc.displayGuiScreen ( new GuiMultiplayer ( this ) );
            } else {
                this.statusMessage = "Login failed.";
            }
        }
        super.actionPerformed ( button );
    }

    private boolean tryToLogin ( ) {
        String[] args = getClipboardString ( ).contains ( ":" ) ? getClipboardString ( ).split ( ":" )
                : getClipboardString ( ).contains ( ";" ) ? getClipboardString ( ).split ( ";" )
                : getClipboardString ( ).contains ( "," ) ? getClipboardString ( ).split ( "," )
                : getClipboardString ( ).split ( " " );

        if (args.length < 2)
            return false;

        YggdrasilUserAuthentication a = ( YggdrasilUserAuthentication ) new YggdrasilAuthenticationService ( Proxy.NO_PROXY ,
                "" ).createUserAuthentication ( Agent.MINECRAFT );
        a.setUsername ( args[ 0 ].trim ( ) );
        a.setPassword ( args[ 1 ].trim ( ) );

        try {
            a.logIn ( );
            mc.session = new Session ( a.getSelectedProfile ( ).getName ( ) , a.getSelectedProfile ( ).getId ( ).toString ( ) ,
                    a.getAuthenticatedToken ( ) , "mojang" );
            return true;
        } catch ( Exception e ) {
            return false;
        }

    }

    private List< String > getAlts ( ) {
        List< String > list = new ArrayList<> ( );

        try {
            URLConnection hc = new URL ( this.altListURL ).openConnection ( );
            hc.setRequestProperty ( "User-Agent" ,
                    "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2" );
            Scanner scanner = new Scanner ( hc.getInputStream ( ) );
            while ( scanner.hasNextLine ( ) ) {
                list.add ( scanner.nextLine ( ) );
            }

        } catch ( Exception e ) {
        }

        return list;
    }

}
