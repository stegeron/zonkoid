package eu.urbancoders.zonkysniper.dataobjects;

/**
 * Splatnosti
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 16.06.2016
 */
public enum RepaymentEnum {

    R12(0, 12),
    R24(13, 24),
    R36(25, 36),
    R48(37, 48),
    R60(49, 60),
    R72(61, 72),
    R84(73, 84);

    public int monthsTo, monthsFrom;

    RepaymentEnum(int monthsFrom, int monthsTo) {
        this.monthsFrom = monthsFrom;
        this.monthsTo = monthsTo;
    }
}
