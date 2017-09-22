package eu.urbancoders.zonkysniper.dataobjects;

import java.io.Serializable;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 16.05.2016
 */
public enum Rating implements Serializable {

    AAAAA("A**", "#596abe", 3.99d, 0.2d, 0.49d),
    AAAA("A*", "#599ebe", 4.99d, 0.5d, 0.59d),
    AAA("A++", "#3bbc70", 5.99d, 1d, 0.79d),
    AA("A+", "#67cd75", 8.49d, 2.5d, 1.69d),
    A("A", "#9acd67", 10.99d, 3d, 2.59d),
    B("B", "#cebe5a", 13.49d, 3.5d, 3.59d),
    C("C", "#d7954b", 15.49d, 4d, 4.59d),
    D("D", "#e75637", 19.99d, 5d, 7.1d);

    String desc;
    String color;
    double interestRate;
    double feeRate;
    double riskCost;

    Rating(String desc, String color, double interestRate, double feeRate, double riskCost) {
        this.desc = desc;
        this.color = color;
        this.interestRate = interestRate;
        this.feeRate = feeRate;
        this.riskCost = riskCost;
    }

    public static String getDesc(String rating) {
        return Rating.valueOf(rating).desc;
    }

    public static String getColor(String rating) {
        return Rating.valueOf(rating).color;
    }

    public static double getInterestRate(String rating) {
        return Rating.valueOf(rating).interestRate;
    }

    public static double getFeeRate(String rating) {
        return Rating.valueOf(rating).feeRate;
    }
    public static double getRiskCost(String rating) {
        return Rating.valueOf(rating).riskCost;
    }

    public String getDesc() {
        return desc;
    }

    public String getColor() {
        return color;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public double getFeeRate() {
        return feeRate;
    }

    public double getRiskCost() {return riskCost;}
}
