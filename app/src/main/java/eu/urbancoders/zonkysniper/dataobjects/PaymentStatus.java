package eu.urbancoders.zonkysniper.dataobjects;

/**
 * Stav splatek za investice
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Datum: 26.11.2017
 */

public enum PaymentStatus {

    OK("#4fbf5b"),
    DUE("#f2d000"),
    SOLD("#81e3fb"),                       // prodano na sekundaru
    PAID("#2d919c"),
    PAID_OFF("#979797"),            // zesplatnena
    CANCELED("#979797"),
    COVERED("#E872E7"),
    NOT_COVERED("#b372e8"),
    WRITTEN_OFF("#ac6a00");


    String backgroundColor;

    PaymentStatus(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }
}
