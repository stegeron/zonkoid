package eu.urbancoders.zonkysniper.dataobjects;

import java.util.ArrayList;
import java.util.List;

/**
 * Stav pujcky, napr. "ACTIVE","SIGNED","COVERED","PAID","PAID_OFF","STOPPED"
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Datum: 26.11.2017
 */

public enum LoanStatus {

    ACTIVE,
    SIGNED,
    COVERED,
    PAID,
    SOLD,
    PAID_OFF,
    STOPPED;

    public static List<String> names() {
        List<String> names = new ArrayList<>();
        for(LoanStatus item : LoanStatus.values()) {
            names.add("\"" + item.name() + "\"");
        }
        return names;
    }
}
