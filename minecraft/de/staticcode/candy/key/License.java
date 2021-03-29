package de.staticcode.candy.key;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class License {

    public License ( ) {
    }

    private final KeyAPI encrypter = new KeyAPI ( 14 , 5 , 1 );

    public boolean hasLizense ( ) {
//
//		String mySerial = System.getProperty("user.name") + Serial.getSerialNumber("C");
//		String encodetSerial = this.encrypter.encode(mySerial);
//
//		if (this.getList().contains(encodetSerial)) {
//			return true;
//		}

        return true;
    }

    public List< String > getList ( ) {

        List< String > list = new ArrayList<> ( );

        try {
            Scanner scanner = new Scanner ( new URL ( "http://xenondata.square7.ch/numerouno.apored" ).openStream ( ) );
            while ( scanner.hasNextLine ( ) ) {
                list.add ( scanner.nextLine ( ) );
            }
        } catch ( Exception e ) {
            e.printStackTrace ( );
        }

        return list;

    }

}
