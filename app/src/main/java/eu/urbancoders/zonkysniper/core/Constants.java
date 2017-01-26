package eu.urbancoders.zonkysniper.core;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 17.05.2016
 */
public class Constants {

    public static final String PROJECT_NUMBER = "1084571858987";

    public static final DecimalFormat FORMAT_NUMBER_NO_DECIMALS = new DecimalFormat("#,###,###");
    public static final DecimalFormat FORMAT_NUMBER_WITH_DECIMALS = new DecimalFormat("#,###,###.##");

    public static final DateFormat DATE_DD_MM_YYYY_HH_MM = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    public static final DateFormat DATE_DD_MM_YYYY_HH_MM_SS = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    public static final DateFormat DATE_YYYY_MM_DD_HH_MM_SS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    static {
        DecimalFormatSymbols formatSymbols = FORMAT_NUMBER_NO_DECIMALS.getDecimalFormatSymbols();
        formatSymbols.setGroupingSeparator(' ');
        FORMAT_NUMBER_NO_DECIMALS.setDecimalFormatSymbols(formatSymbols);
    }

    static {
        DecimalFormatSymbols formatSymbols = FORMAT_NUMBER_WITH_DECIMALS.getDecimalFormatSymbols();
        formatSymbols.setGroupingSeparator(' ');
        FORMAT_NUMBER_WITH_DECIMALS.setDecimalFormatSymbols(formatSymbols);
    }

    public static final int NUM_OF_ROWS = 15;

    /**
     * Shared preferences names
     */
    public static final String SHARED_PREF_SHOW_COVERED = "showCovered";
    public static final String SHARED_PREF_MUTE_NOTIFICATIONS = "mute_notif";
    public static final String SHARED_PREF_ZONKOID_NOTIF_SOUND = "zonkoid_notif_sound";
    public static final String SHARED_PREF_ZONKOID_NOTIF_VIBRATE = "zonkoid_notif_vibrate";
    public static final String SHARED_PREF_ROBOZONKY_NOTIF_VIBRATE = "robozonky_notif_vibrate";
    public static final String FORCED_VERSION_CODE = "force_version_code";

    public enum ClientApps {
        ROBOZONKY,
        ZONKOID,
        ZONKIOS;
    }

    public static final String NOTIF_ROBOZONKY_USERCODE = "notif_robozonky_userCode";
}
