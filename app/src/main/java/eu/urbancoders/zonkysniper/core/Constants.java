package eu.urbancoders.zonkysniper.core;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;

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
    public static final String KEY_ADMOB = "ca-app-pub-7981249418859222~6742773541";

    public static final DecimalFormat FORMAT_NUMBER_NO_DECIMALS = new DecimalFormat("#,###,###");
    public static final DecimalFormat FORMAT_NUMBER_WITH_DECIMALS = new DecimalFormat("#,###,###.##");

    public static final DateFormat DATE_DD_MM_YYYY = new SimpleDateFormat("dd.MM.yyyy");
    public static final DateFormat DATE_DD_MM_YYYY_HH_MM = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    public static final DateFormat DATE_DD_MM_YYYY_HH_MM_SS = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    public static final DateFormat DATE_YYYY_MM_DD_HH_MM_SS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final DateFormat DATE_MM_YY = new SimpleDateFormat("MM/yy");
    public static final DateFormat DATE_YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");

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
    public static final int AMOUNT_TO_INVEST_MAX = 20000;
    public static final int AMOUNT_TO_INVEST_STEP = 200;


    /**
     * Shared preferences names
     */
    public static final String SHARED_PREF_SHOW_COVERED = "showCovered";
    public static final String SHARED_PREF_SHOW_INSURED_ONLY = "showInsured";
    public static final String SHARED_PREF_SHOW_RESERVED = "showReserved";
    public static final String SHARED_PREF_MUTE_NOTIFICATIONS = "mute_notif";
    public static final String SHARED_PREF_ZONKOID_NOTIF_SOUND = "zonkoid_notif_sound";
    public static final String SHARED_PREF_ZONKOID_NOTIF_VIBRATE = "zonkoid_notif_vibrate";
    public static final String SHARED_PREF_ROBOZONKY_NOTIF_VIBRATE = "robozonky_notif_vibrate";
    public static final String SHARED_PREF_NOTIF_INSURED_ONLY = "zonkoid_notif_insured_only";
    public static final String SHARED_PREF_AUTOINVEST_INSURED_ONLY = "zonkoid_autoinvest_insured_only";
    public static final String SHARED_PREF_AUTOINVEST_MAX_AMOUNT = "zonkoid_autoinvest_max_amount";
    public static final String SHARED_PREF_AUTOINVEST_INCOME_TYPES = "zonkoid_autoinvest_income_types";
    public static final String SHARED_PREF_AUTOINVEST_REGIONS = "zonkoid_autoinvest_regions";
    public static final String SHARED_PREF_PRESET_AMOUNT = "presetAmountToInvest";
    public static final String SHARED_PREF_PRESET_AUTOINVEST_AMOUNT = "presetAmountToAutoInvest";
    public static final String SHARED_PREF_INVESTOR_STATUS = "investorStatusInZonkoid";  // ulozeny stav investora po poslednim checkpointu nebo logInvestmentu
    public static final String SHARED_PREF_COACHMARK_VERSION_READ = "coachMarkVersionRead";  // zapsat verzi, jejiz coachmark uzivatel precetl

    public enum ClientApps {
        ROBOZONKY,
        ZONKOID,
        ZONKIOS
    }

    public static final String NOTIF_ROBOZONKY_USERCODE = "notif_robozonky_userCode";

    /**
     * parametry pujcek
     */
    public static final int REPAYMENTS_MONTHS_FROM = 0;
    public static final int REPAYMENTS_MONTHS_TO = 84;

    /**
     * @deprecated
     */
    public static final int CAPTCHA_REQUIRED_TIME = 2;  // v minutach, doba, po kterou je vyzadovana captcha

    public static final String FILTER_MYINVESTMENTS_STATUSES_NAME = "filter_myinvestments_statuses";
    public static final String FILTER_MYINVESTMENTS_UNPAID_LAST_INSTALLMENT_NAME = "unpaidLastInstallment";
    public static final String FILTER_MYINVESTMENTS_SET = "filter_myinvestments_set";   // priznak, zda je nastaveny filtr mych investic, kvuli vysviceni fab
    public static final String FILTER_MARKETPLACE_RATINGS = "filter_marketplace_ratings_";   // filtr podle ratingu - prefix
    public static final String FILTER_MARKETPLACE_TERMINMONTHS_FROM = "filter_marketplace_terminmonths_from";   // filtr podle splatnosti od
    public static final String FILTER_MARKETPLACE_TERMINMONTHS_TO = "filter_marketplace_terminmonths_to";   // filtr podle splatnosti do
    public static final String FILTER_MYINVESTMENTS_STATUS_EQ_NAME = "status__eq";

    /**
     * Billing
     */
    public static final String SUBSCRIPTION_AD_REMOVE = "ad_remove_yearly";
    public static final int    SUBSCRIPTION_AD_REMOVE_BIT = 1;      // odstraneni reklamy voucherem
    public static final int    SUBSCRIPTION_AUTOINEST_PRO_BIT = 2;  // profi autoinvest voucherem
    public static final String VOUCHER_ID = "voucher";
}
