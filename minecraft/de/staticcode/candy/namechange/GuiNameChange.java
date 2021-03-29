package de.staticcode.candy.namechange;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.Session;

import java.io.IOException;

public class GuiNameChange extends GuiScreen {

    private GuiTextField inputField;
    private String statusMessage;

    @Override
    public void initGui ( ) {
        this.buttonList.clear ( );
        this.buttonList.add ( new GuiButton ( 1 , this.width / 2 - 100 , ScaledResolution.getScaledHeight ( ) / 2 + 36 + 12 ,
                200 , 20 , "Change Name" ) );

        this.inputField = new GuiTextField ( 2 , Minecraft.fontRendererObj , 0 , 60 , ScaledResolution.getScaledWidth ( ) , 20 );
        this.inputField.setText ( mc.getSession ( ).getUsername ( ) );

        super.initGui ( );
    }

    @Override
    public void drawScreen ( int mouseX , int mouseY , float partialTicks ) {
        this.drawDefaultBackground ( );

        this.drawString ( Minecraft.fontRendererObj , this.statusMessage , 3 , 20 , -1 );

        this.inputField.drawTextBox ( );
        super.drawScreen ( mouseX , mouseY , partialTicks );
    }

    @Override
    protected void actionPerformed ( GuiButton button ) throws IOException {
        if (button.id == 1) {
            if (!this.inputField.getText ( ).equalsIgnoreCase ( mc.getSession ( ).getUsername ( ) )) {
                this.statusMessage = "The name must be EqualsIgnoreCase to the old name!";
                return;
            }
            mc.session = new Session ( this.inputField.getText ( ) , mc.getSession ( ).getPlayerID ( ) ,
                    mc.getSession ( ).getToken ( ) , "mojang" );
            this.statusMessage = "Name changed to: " + this.inputField.getText ( );
        }
        super.actionPerformed ( button );
    }

    @Override
    protected void keyTyped ( char typedChar , int keyCode ) throws IOException {
        this.inputField.textboxKeyTyped ( typedChar , keyCode );
        super.keyTyped ( typedChar , keyCode );
    }

    @Override
    protected void mouseClicked ( int mouseX , int mouseY , int mouseButton ) throws IOException {
        this.inputField.mouseClicked ( mouseX , mouseY , mouseButton );
        super.mouseClicked ( mouseX , mouseY , mouseButton );
    }
}
