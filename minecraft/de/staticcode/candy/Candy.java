package de.staticcode.candy;

import de.liquiddev.ircclient.client.IrcPlayer;
import de.staticcode.candy.font.FontLoader;
import de.staticcode.candy.irc.IrcHandler;
import de.staticcode.candy.module.modules.COMBAT.*;
import de.staticcode.candy.module.modules.COMMANDS.*;
import de.staticcode.candy.module.modules.EXPLOITS.AntiLaggLobby;
import de.staticcode.candy.module.modules.EXPLOITS.AntiNick;
import de.staticcode.candy.module.modules.EXPLOITS.FakeLagg;
import de.staticcode.candy.module.modules.EXPLOITS.LaggDetector;
import de.staticcode.candy.module.modules.MOVEMENT.*;
import de.staticcode.candy.module.modules.PLAYER.MidClick;
import de.staticcode.candy.module.modules.PLAYER.*;
import de.staticcode.candy.module.modules.RENDER.*;
import de.staticcode.candy.module.modules.WORLD.ChestAura;
import de.staticcode.candy.module.modules.WORLD.Scaffold;
import de.staticcode.candy.module.modules.WORLD.Tower;
import de.staticcode.candy.save.EasySaveGame;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import org.lwjgl.opengl.Display;

public class Candy {

    public static String NAME = "Candy (GitHub-build)";
    public static String VERSION = "b7";

    static Candy radium;

    static EasySaveGame moduleConfigs;
    static EasySaveGame activeModules;
    static EasySaveGame moduleKeybinds;

    static FontLoader verdanaFont;

    static EasySaveGame guiButtons;
    static EasySaveGame guiSliders;
    static EasySaveGame guiModes;
    static EasySaveGame guiPos;
    static EasySaveGame ircConfig;

    static IrcHandler ircHandler;

    public static int RGBtoHEX ( int r , int g , int b , int a ) {
        return ( a << 24 ) + ( r << 16 ) + ( g << 8 ) + b;
    }

    public void setup ( ) {
        radium = new Candy ( );
        verdanaFont = new FontLoader ( "verdana" , 43 );
        ircHandler = new IrcHandler ( );


        this.initConfigs ( );
        this.initModules ( );
        Display.setTitle ( Candy.NAME + " " + Candy.VERSION );
    }

    public void initConfigs ( ) {
        activeModules = new EasySaveGame ( "Active_Modules.yml" );
        moduleKeybinds = new EasySaveGame ( "Module_Keybinds.yml" );
        guiButtons = new EasySaveGame ( "Buttons.esg" );
        guiSliders = new EasySaveGame ( "Sliders.esg" );
        guiModes = new EasySaveGame ( "Modes.esg" );
        guiPos = new EasySaveGame ( "Positions.esg" );
        moduleConfigs = new EasySaveGame ( "Module_Config.esg" );
    }

    public static void sendChat ( Object object ) {
        String msg = String.valueOf ( object );
        if (msg == null)
            return;

        if (msg.startsWith ( "IRC" )) {

            String s = msg.replaceFirst ( "IRC" , "" ).replace ( "�" , ">" );

            if (s.contains ( "c_Server$" )) {
                String trim = s.replace ( "c_Server$" , "" );
                String userName = trim.split ( ":" )[ 1 ];
                String data = trim.split ( ":" )[ 2 ];

                String ircNick = IrcPlayer.getByIngameName ( Minecraft.getMinecraft ( ).session.getUsername ( ) ).getIrcNick ( );
                if (ircNick == null)
                    return;

                if (userName.contains ( ircNick )) {
                    Minecraft.thePlayer.addChatMessage ( new ChatComponentText ( "§3[CandyServer] §f" + data ) );
                    return;
                }
            }

            Minecraft.thePlayer.addChatMessage ( new ChatComponentText ( "§f" + s ) );

        } else
            Minecraft.thePlayer.addChatMessage ( new ChatComponentText ( "§2" + NAME + "§8: §f" + msg ) );
    }

    private void initModules ( ) {
        new ESP ( );
        new Sprint ( );
        new ClickGUI ( );
        new NoHurtcam ( );
        new Teams ( );
        new AutoArmor ( );
        new BowAimbot ( );
        new AntiWater ( );
        new ChestStealer ( );
        new NoVelocity ( );
        new NoFOV ( );
        new Scaffold ( );
        new AutoSoup ( );
        new AntiBots ( );
        new MidClick ( );
        new NoFriends ( );
        new Speed ( );
        new SafeWalk ( );
        new NoSlowdown ( );
        new InvMove ( );
        new InvCleaner ( );
        new Highjump ( );
        new ItemPhysics ( );
        new Killaura ( );
        new Spammer ( );
        new Fullbright ( );
        new NoLadder ( );
        new AnotherHitAnimation ( );
        new FastPlace ( );
        new Tower ( );
        new NoBarrier ( );
        new NoInvisibles ( );
        new DefaultInvBackground ( );
        new ModuleScale ( );
        new MainMenuScale ( );
        new NoChatLines ( );
        new ItemESP ( );
        new NoFall ( );
        new LaggDetector ( );
        new AntiNick ( );
        new IceSpeed ( );
        new AntiCactus ( );
        new WaterSpeed ( );
        new Glide ( );
        new BedESP ( );
        new Jesus ( );
        new NoFire ( );
        new RodAimbot ( );
        new Fly ( );
        new NoRotation ( );
        new SlimeJump ( );
        new Criticals ( );
        new AntiLaggLobby ( );
        new GroundFly ( );
        new Fucker ( );
        new InstantUse ( );
        new FastRegen ( );
        new Teleport ( );
        new AutoFNS ( );
        new Triggerbot ( );
        new Nametags ( );
        new NameProtect ( );
        new ChestESP ( );
        new LongJump ( );
        new Strafe ( );
        new AutoFood ( );
        new AirJump ( );
        new AirJumpFly ( );
        new ChestAura ( );
        new InventoryTweaks ( );
        new AutoSword ( );
        new RedeskyFly ( );
        new FakeLagg ( );
        new AutoPotion ( );
        new SmoothAim ( );

        // Commands
        new ConfigCommand ( );
        new BindCommand ( );
        new DamageCommand ( );
        new FriendCommand ( );
        new ToggleCommand ( );
        new NameCommand ( );
        new IrcCommand ( );
        new GuiScaleCommand ( );

    }

    public static Candy getRadium ( ) {
        return radium;
    }

    public static EasySaveGame getActiveModules ( ) {
        return activeModules;
    }

    public static EasySaveGame getModuleKeybinds ( ) {
        return moduleKeybinds;
    }

    public static EasySaveGame getGuiButtons ( ) {
        return guiButtons;
    }

    public static EasySaveGame getGuiModes ( ) {
        return guiModes;
    }

    public static EasySaveGame getGuiPos ( ) {
        return guiPos;
    }

    public static EasySaveGame getGuiSliders ( ) {
        return guiSliders;
    }

    public static EasySaveGame getIrcConfig ( ) {
        return ircConfig;
    }

    public static EasySaveGame getModuleConfigs ( ) {
        return moduleConfigs;
    }

    public static void setIrcConfig ( EasySaveGame ircConfig ) {
        Candy.ircConfig = ircConfig;
    }

    public static FontLoader getVerdanaFont ( ) {
        return verdanaFont;
    }

    public static void setVerdanaFont ( FontLoader verdanaFont ) {
        Candy.verdanaFont = verdanaFont;
    }

    public static IrcHandler getIrcHandler ( ) {
        return ircHandler;
    }


}
