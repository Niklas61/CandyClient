package de.staticcode.candy.save;

public class Value {
    String valString;
    StringToValue val;

    public Value(String valueString) {
        this.valString = valueString;
    }

    public StringToValue getValue() throws Exception {
        if (this.val != null) {
            return this.val;
        } else {
            this.val = new StringToValue(this.valString);
            return this.val;
        }
    }
}
