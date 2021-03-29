package de.staticcode.candy.key;

import java.util.Base64;

public class KeyAPI {

    int extend;
    int ceaser;
    int base;

    public KeyAPI ( int ceaser , int base , int extend ) {
        this.extend = extend;
        this.ceaser = ceaser;
        this.base = base;
    }

    public String encode ( String s ) {
        String k = s;
        k = this.doEXTEND ( k );
        k = this.doB ( k );
        k = this.doSWITCH ( k );
        k = this.doC ( k );
        k = this.doR ( k );

        return k;
    }

    private String doB ( String input ) {
        String build = input;
        for ( int i = 0; i < this.base; i++ ) {
            build = new String ( Base64.getEncoder ( ).encode ( build.getBytes ( ) ) );
        }
        return build;
    }

    public String doC ( String input ) {
        char[] charArray = input.toCharArray ( );
        char[] cryptArray = new char[ charArray.length ];
        for ( int i = 0; i < charArray.length; i++ ) {
            int v = ( charArray[ i ] + this.ceaser ) % 128;
            cryptArray[ i ] = ( char ) ( v );
        }
        return new String ( cryptArray );
    }

    public String doEXTEND ( String input ) {
        String build = "";

        int count = 0;
        for ( int i = 0; i < input.length ( ); i++ ) {
            count++;
            if (count > extend) {
                build += input.charAt ( i );
                count = 0;
            }
            build += input.charAt ( i );
        }
        return build;
    }

    public String doR ( String input ) {
        String build = "";

        for ( int i = input.length ( ) - 1; i >= 0; i-- ) {
            build += input.charAt ( i );
        }
        return build;
    }

    public String doSWITCH ( String input ) {
        String build = "";

        for ( int i = 1; i < input.length ( ); i += 2 ) {
            build += input.charAt ( i );
            build += input.charAt ( i - 1 );
        }

        if (build.length ( ) < input.length ( ))
            build += input.charAt ( input.length ( ) - 1 );

        return build;
    }
}