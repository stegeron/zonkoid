package eu.urbancoders.zonkysniper.dataobjects;

import java.util.ArrayList;
import java.util.List;

/**
 * Stav investice, napr. "ACTIVE","SIGNED","COVERED","PAID","PAID_OFF","STOPPED"
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Datum: 26.11.2017
 */

public enum InvestmentStatus {

    ACTIVE,
    SIGNED,
    COVERED,
    PAID,
    PAID_OFF,
    STOPPED;

    public static List<String> names() {
        List<String> names = new ArrayList<>();
        for(InvestmentStatus item : InvestmentStatus.values()) {
            names.add("\"" + item.name() + "\"");
        }
        return names;
    }
}
