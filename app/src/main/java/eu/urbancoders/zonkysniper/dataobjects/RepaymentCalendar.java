package eu.urbancoders.zonkysniper.dataobjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Vypocitany splatkovy kalendar pro zobrazeni parametru pujcky pro investora
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Datum: 21.09.2017
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

public class RepaymentCalendar implements Serializable {

    private List<RepaymentCalendarItem> calendarItems;
    private int months;
    private double amount;

    public RepaymentCalendar(int months, double amount) {
        this.months = months;
        this.amount = amount;
        calendarItems = new ArrayList<>(months);
    }

    public void addItem(RepaymentCalendarItem item) {
        calendarItems.add(item);
    }

    public List<RepaymentCalendarItem> getCalendarItems() {
        return calendarItems;
    }

    /**
     * Suma poplatků (bez daně)
     * @return
     */
    public double sumFees() {
        double sumOfFees = 0;

        for (RepaymentCalendarItem row : calendarItems) {
            sumOfFees += row.getFee();
        }

        return sumOfFees;
    }

    /**
     * Průměrná měsíční splátka
     * @return
     */
    public double avgMothlyRepayments() {
        return getCalendarItems().get(0).getMonthlyRepayment();
    }

    /**
     * Hrubý výnos
     * @return
     */
    public double sumGrossIncome() {
        double sumOfGrossIncome = 0;

        for(RepaymentCalendarItem row : calendarItems) {
            sumOfGrossIncome += row.getInterest();
        }

        return sumOfGrossIncome;
    }

    /**
     * Čistý výnos
     * @return
     */
    public double getNetIncome() {
        return sumGrossIncome() - sumFees() - getTax();
    }

    /**
     * Vypočítaná daň, t.č. 15%
     * @return
     */
    public double getTax() {
        return (sumGrossIncome() - sumFees())*(1-0.85);
    }
}
