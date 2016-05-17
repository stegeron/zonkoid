package eu.urbancoders.zonkysniper;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 17.05.2016
 */
public class Constants {

    public static final DecimalFormat FORMAT_NUMBER_NO_DECIMALS = new DecimalFormat("#,###,###");

    static {
        DecimalFormatSymbols formatSymbols = FORMAT_NUMBER_NO_DECIMALS.getDecimalFormatSymbols();
        formatSymbols.setGroupingSeparator(' ');
        FORMAT_NUMBER_NO_DECIMALS.setDecimalFormatSymbols(formatSymbols);
    }

}
