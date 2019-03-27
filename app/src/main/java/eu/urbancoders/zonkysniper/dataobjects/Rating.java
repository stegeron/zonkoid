package eu.urbancoders.zonkysniper.dataobjects;

import java.io.Serializable;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 16.05.2016
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
public enum Rating implements Serializable {

    AAAAAA("A***", "#8b59be", 2.99d, 0.2d, 0.49d),
    AAAAA("A**", "#8b59be", 3.99d, 0.2d, 0.49d),
    AAAA("A*", "#596abe", 4.99d, 0.5d, 0.59d),
    AAA("A++", "#599ebe", 5.99d, 1d, 0.79d),
    AAE("AA+", "#59bea8", 6.99d, 1.5d, 1.11d),
    AA("A+", "#67cd75", 8.49d, 2.5d, 1.69d),
    AE("AE", "#9acd67", 9.49d, 2.5d, 2.21d),
    A("A", "#cebe5a", 10.99d, 3d, 2.59d),
    B("B", "#d7954b", 13.49d, 3.5d, 3.59d),
    C("C", "#e75637", 15.49d, 4d, 4.59d),
    D("D", "#d12f2f", 19.99d, 5d, 7.1d);

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
