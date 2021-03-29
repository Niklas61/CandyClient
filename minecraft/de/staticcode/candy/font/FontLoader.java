package de.staticcode.candy.font;

import net.minecraft.client.Minecraft;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;

public class FontLoader {
    private SlickFont slickFont = null;

    private File file = null;
    public static boolean fontEffect = false;


    public FontLoader ( String fontName , int size ) {
        this.loadFont ( fontName , size );
    }

    private void loadFont ( String fontName , int size ) {
        try {
            this.loadFolder ( );
            this.file = new File ( Minecraft.getMinecraft ( ).mcDataDir + "/Candy/Fonts/" + fontName + ".ttf" );
            if (!this.file.exists ( )) {
                this.patchFont ( );
            }
            this.slickFont = new SlickFont ( this.file.getAbsolutePath ( ) , size , false );
        } catch ( Exception e ) {
            e.printStackTrace ( );
        }
    }

    private void loadFolder ( ) {
        ( new File ( Minecraft.getMinecraft ( ).mcDataDir + "/Candy/Fonts/x" ) ).getParentFile ( ).mkdirs ( );
    }

    private void patchFont ( ) {
        try {
            URL url = new URL ( "http://candy.bplaced.net/" + this.file.getName ( ) );
            URLConnection urlConnection = url.openConnection ( );
            BufferedInputStream bufferedInputStream = new BufferedInputStream ( urlConnection.getInputStream ( ) );
            FileOutputStream fileOutputStream = new FileOutputStream ( this.file );
            byte[] byteCount = new byte[ 1024 ];
            int byteContent;
            while ( ( byteContent = bufferedInputStream.read ( byteCount , 0 , 1024 ) ) != -1 ) {
                fileOutputStream.write ( byteCount , 0 , byteContent );
            }

        } catch ( Exception e ) {
            e.printStackTrace ( );
        }
    }

    public void drawString ( String text , int x , int y , int hex ) {
        this.slickFont.drawString ( text , x , y , hex );
    }

    public void drawCenteredString ( String text , int x , int y , int hex ) {
        this.slickFont.drawCenteredString ( text , x , y , hex );
    }

    public void drawStringWithShadow ( String text , int x , int y , int hex ) {
        this.slickFont.drawShadowString ( text , x , y , hex );
    }

    public int getStringWidth ( String text ) {
        return this.slickFont.getStringWidth ( text );
    }

    public SlickFont getSlickFont ( ) {
        return slickFont;
    }

}
