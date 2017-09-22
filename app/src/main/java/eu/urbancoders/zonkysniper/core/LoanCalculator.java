package eu.urbancoders.zonkysniper.core;

import eu.urbancoders.zonkysniper.dataobjects.Rating;
import eu.urbancoders.zonkysniper.dataobjects.RepaymentCalendar;
import eu.urbancoders.zonkysniper.dataobjects.RepaymentCalendarItem;

/**
 * Vypocty splatkovych kalendaru, urokovych sazeb apod.
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Datum: 22.09.2017
 */

public class LoanCalculator {

    /**
     * Vypocitat splatkovy kalendar investice
     *
     * @param p - Principal
     * @param iy - Interest rate
     * @param nm - months
     * @param feeRate - procenta poplatku
     */
    public static RepaymentCalendar calculateAmortization(double p, double iy, int nm, double feeRate) {

        RepaymentCalendar cal = new RepaymentCalendar(nm, p);

        double newbal;
        double im = (iy / 12) / 100;
        double mp, ip, pp;
        double fee;
        int i;

        mp = p * im * Math.pow(1 + im, (double) nm) / (Math.pow(1 + im, (double) nm) - 1);
        //print amortization schedule for all months except the last month
        for (i = 1; i < nm; i++) {
            ip = p * im;//interest paid
            pp = mp - ip; //princial paid
            newbal = p - pp; //new balance
            fee = feeRate / 100 / 12 * p;  // poplatek
//            printSch(i, p, mp, ip, pp, newbal, fee);
            cal.addItem(new RepaymentCalendarItem(i, p, newbal, mp, ip, pp, fee));
            p = newbal;  //update old balance
        }
        //last month
        pp = p;
        ip = p * im;
        mp = pp + ip;
        newbal = 0.0;
        fee = 0;
        cal.addItem(new RepaymentCalendarItem(i, p, newbal, mp, ip, pp, fee));

        return cal;
    }

    /**
     * Výpočet čisté úrokové sazby se zohledněním rizikových nákladů
     * @param rating
     */
    public static double calculateNetInterestRate(Rating rating) {
        return rating.getInterestRate() - rating.getRiskCost() - rating.getFeeRate();
    }
}
