package eu.urbancoders.zonkysniper.core;

import android.widget.TextView;

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
    public static final String FIO_ACCOUNT = "2900949034/2010";

    public static final String LICENCE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAs8CrE3dvfVX6BUcRBBqCvZ" +
            "dWQykDi1znsEbfWkXuymJQYfaUui/ZO726hp6YWINJnsrZsdFvd7RdkeJqeAmXAQm0cg9x3qcw6EK7XMxqqUL" +
            "fmPUygGKapn5lJPSDtdsfU6h1m+8wzi31CXMZqu6ClRl9ueXSCp4RgbiFryICmfAKr9gQ1WJgZJl6TAaYfrmy" +
            "ESyT2f7EQED8tS2bkN74ry1cyq0fGUVu2TFwlNvXccUiorzxw2Bd5B+n1/PUAEGZ9fHaTC6s5LxH4vrWIXaAy" +
            "zoDhIRrd5VAGI+qYbraoR6nF7W37vPBRt5hn2X4ZakCEEZJ9XRmRh8Z/qJMWcBTkQIDAQAB";

    public static final DecimalFormat FORMAT_NUMBER_NO_DECIMALS = new DecimalFormat("#,###,###");
    public static final DecimalFormat FORMAT_NUMBER_WITH_DECIMALS = new DecimalFormat("#,###,###.##");

    public static final DateFormat DATE_DD_MM_YYYY = new SimpleDateFormat("dd.MM.yyyy");
    public static final DateFormat DATE_DD_MM_YYYY_HH_MM = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    public static final DateFormat DATE_DD_MM_YYYY_HH_MM_SS = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    public static final DateFormat DATE_YYYY_MM_DD_HH_MM_SS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final DateFormat DATE_MM_YY = new SimpleDateFormat("MM/yy");
    public static final DateFormat DATE_YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");
//    public static final double AMOUNT_TO_PAY_MIN = 29.00;

    static {
        DecimalFormatSymbols formatSymbols = FORMAT_NUMBER_NO_DECIMALS.getDecimalFormatSymbols();
        formatSymbols.setGroupingSeparator(' ');
        FORMAT_NUMBER_NO_DECIMALS.setDecimalFormatSymbols(formatSymbols);
    }

    static {
        DecimalFormatSymbols formatSymbols = FORMAT_NUMBER_WITH_DECIMALS.getDecimalFormatSymbols();
        formatSymbols.setGroupingSeparator(' ');
        FORMAT_NUMBER_WITH_DECIMALS.setDecimalFormatSymbols(formatSymbols);
        FORMAT_NUMBER_WITH_DECIMALS.setMinimumFractionDigits(2);
    }

    public static final int NUM_OF_ROWS = 15;
    public static final int NUM_OF_ROWS_LONG = 50;

    public static final int AMOUNT_TO_INVEST_MIN = 200;
    public static final int AMOUNT_TO_INVEST_MAX = 5000;
    public static final int AMOUNT_TO_INVEST_STEP = 200;


    /**
     * Shared preferences names
     */
    public static final String SHARED_PREF_SHOW_COVERED = "showCovered";
    public static final String SHARED_PREF_MUTE_NOTIFICATIONS = "mute_notif";
    public static final String SHARED_PREF_ZONKOID_NOTIF_SOUND = "zonkoid_notif_sound";
    public static final String SHARED_PREF_ZONKOID_NOTIF_VIBRATE = "zonkoid_notif_vibrate";
    public static final String SHARED_PREF_ROBOZONKY_NOTIF_VIBRATE = "robozonky_notif_vibrate";
    public static final String SHARED_PREF_PRESET_AMOUNT = "presetAmountToInvest";
    public static final String SHARED_PREF_INVESTOR_STATUS = "investorStatusInZonkoid";  // ulozeny stav investora po poslednim checkpointu nebo logInvestmentu
    public static final String SHARED_PREF_COACHMARK_FEES_AGREEMENT = "coachMarkUserAgreedFees";  // zapsat verzi, jejiz coachmark uzivatel precetl

    public enum ClientApps {
        ROBOZONKY,
        ZONKOID,
        ZONKIOS;
    }

    public static final String NOTIF_ROBOZONKY_USERCODE = "notif_robozonky_userCode";

    public static final int CAPTCHA_REQUIRED_TIME = 2;  // v minutach, doba, po kterou je vyzadovana captcha
}
