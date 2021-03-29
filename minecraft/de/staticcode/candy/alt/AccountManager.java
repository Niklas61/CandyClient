package de.staticcode.candy.alt;

import com.mojang.authlib.Agent;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import de.staticcode.candy.Candy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.util.Session;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;
import java.net.Proxy;

public class AccountManager extends GuiScreen {

    private final Minecraft mc = Minecraft.getMinecraft ( );
    public GuiTextField character;
    public GuiTextField pwd;

    public GuiScreen eventscreen;

    public AccountManager ( GuiScreen event ) {
        eventscreen = event;
    }

    public void initGui ( ) {
        Keyboard.enableRepeatEvents ( true );

        this.buttonList.add ( new GuiButton ( 1 , width / 2 - 100 , this.height / 4 + 85 , "Login" ) );

        this.buttonList.add ( new GuiButton ( 3 , width / 2 - 100 , this.height / 4 + 105 , "Clipboard Login" ) );

        character = new GuiTextField ( 3 , fontRendererObj , width / 2 - 100 , 76 , 200 , 20 );
        pwd = new GuiTextField ( 4 , fontRendererObj , width / 2 - 100 , 116 , 200 , 20 );
        this.pwd.setCensored ( true );

        character.setMaxStringLength ( 50 );
        pwd.setMaxStringLength ( 50 );

        if (mc.session.getUsername ( ) != null)
            if (mc.session.getSessionType ( ) == Session.Type.MOJANG) {
                status = "Logged in with: " + mc.session.getUsername ( );
            } else {
                status = "Logged in with cracked-name: " + mc.session.getUsername ( );
            }
    }

    public void onGuiClosed ( ) {
        Keyboard.enableRepeatEvents ( false );
    }

    public void updateScreen ( ) {
        character.updateCursorCounter ( );
        pwd.updateCursorCounter ( );
    }

    public void mouseClicked ( int x , int y , int m ) {
        character.mouseClicked ( x , y , m );
        pwd.mouseClicked ( x , y , m );

        try {
            super.mouseClicked ( x , y , m );
        } catch ( Exception e ) {
            e.printStackTrace ( );
        }
    }

    protected void keyTyped ( char c , int i ) {
        character.textboxKeyTyped ( c , i );
        pwd.textboxKeyTyped ( c , i );

        if (c == '\t') {
            if (character.isFocused ( )) {
                character.setFocused ( false );
                pwd.setFocused ( true );
            } else {
                character.setFocused ( true );
                pwd.setFocused ( false );
            }
        }

        if (c == '\r') {
            try {
                actionPerformed ( buttonList.get ( 0 ) );
            } catch ( Exception e ) {
                e.printStackTrace ( );
            }
        }

    }

    public String status = "-|-";

    public void drawScreen ( int mouseX , int mouseY , float partialTicks ) {
        drawDefaultBackground ( );
        character.drawTextBox ( );
        pwd.drawTextBox ( );

        if (Keyboard.isKeyDown ( Keyboard.KEY_ESCAPE ))
            mc.displayGuiScreen ( new GuiMainMenu ( ) );

        this.drawString ( Minecraft.fontRendererObj , "E-Mail / Username" , this.width / 2 - 100 , 76 - 11 ,
                Color.WHITE.getRGB ( ) );
        this.drawString ( Minecraft.fontRendererObj , "Password" , this.width / 2 - 100 , 116 - 11 ,
                Color.WHITE.getRGB ( ) );

        this.drawString ( Minecraft.fontRendererObj , "Status: " + status , 2 , 2 , Color.WHITE.getRGB ( ) );

        super.drawScreen ( mouseX , mouseY , partialTicks );
    }

    protected void actionPerformed ( GuiButton button ) throws IOException {
        if (button.id == 3) {
            // getFreeAlt();

            if (getClipboardString ( ) == null || getClipboardString ( ).length ( ) < 10) {
                this.status = "§cYour clipboard is empty!";
                return;
            }

            String[] args = getClipboardString ( ).contains ( ":" ) ? getClipboardString ( ).split ( ":" )
                    : getClipboardString ( ).contains ( ";" ) ? getClipboardString ( ).split ( ";" )
                    : getClipboardString ( ).contains ( "," ) ? getClipboardString ( ).split ( "," )
                    : getClipboardString ( ).split ( " " );

            if (args.length != 2) {
                this.status = "§cInvalid clipboard entry!";
                return;
            }

            System.out.println ( "Logging in..." );
            YggdrasilUserAuthentication a = ( YggdrasilUserAuthentication ) new YggdrasilAuthenticationService (
                    Proxy.NO_PROXY , "" ).createUserAuthentication ( Agent.MINECRAFT );
            a.setUsername ( args[ 0 ].trim ( ) );
            a.setPassword ( args[ 1 ].trim ( ) );

            try {
                a.logIn ( );
                mc.session = new Session ( a.getSelectedProfile ( ).getName ( ) , a.getSelectedProfile ( ).getId ( ).toString ( ) ,
                        a.getAuthenticatedToken ( ) , "mojang" );
                Candy.getIrcHandler ( ).setAccount ( mc.session.getUsername ( ) );
                status = "§aSuccesss: Logged in with: " + mc.session.getUsername ( );
                mc.displayGuiScreen ( new GuiMultiplayer ( new GuiMainMenu ( ) ) );
            } catch ( Exception e ) {
                System.out.println ( "Error!" );
                status = "§cError! Wrong Password or Username!";
            }

        } else {
            if (pwd.getText ( ).trim ( ).isEmpty ( )) {
                if (!character.getText ( ).trim ( ).isEmpty ( )) {
                    mc.session = new Session ( character.getText ( ).trim ( ) , "-" , "-" , "Legacy" );
                    status = "Succesfully logged in with cracked account: " + character.getText ( ).trim ( );
                    Candy.getIrcHandler ( ).setAccount ( mc.session.getUsername ( ) );
                } else {
                    if (!character.getText ( ).trim ( ).isEmpty ( )) {
                        System.out.println ( "Logging in..." );
                        YggdrasilUserAuthentication a = ( YggdrasilUserAuthentication ) new YggdrasilAuthenticationService (
                                Proxy.NO_PROXY , "" ).createUserAuthentication ( Agent.MINECRAFT );
                        a.setUsername ( character.getText ( ).trim ( ) );
                        a.setPassword ( pwd.getText ( ).trim ( ) );
                        try {
                            a.logIn ( );
                            mc.session = new Session ( a.getSelectedProfile ( ).getName ( ) ,
                                    a.getSelectedProfile ( ).getId ( ).toString ( ) , a.getAuthenticatedToken ( ) , "mojang" );
                            Candy.getIrcHandler ( ).setAccount ( mc.session.getUsername ( ) );
                            status = "§aSuccesss: Logged in with: " + mc.session.getUsername ( );
                            mc.displayGuiScreen ( new GuiMultiplayer ( new GuiMainMenu ( ) ) );
                        } catch ( Exception e ) {
                            System.out.println ( "Error!" );
                            status = "§cError! Wrong Password or Username!";
                        }
                    }
                }
            }
        }

    }
}