package eu.urbancoders.zonkysniper.dataobjects;

import java.io.Serializable;

/**
 * Jeden radek ve splatkovem kalendari
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

public class RepaymentCalendarItem implements Serializable {

    public int month;
    public double oldBalance, newBalance;
    public double monthlyRepayment; // konstantni kazdy mesic
    public double interest;
    public double principle;
    public double fee;
    public double revenue;

    public RepaymentCalendarItem(int month, double oldBalance, double newBalance, double monthlyRepayment, double interest, double principle, double fee, double revenue) {
        this.month = month;
        this.oldBalance = oldBalance;
        this.newBalance = newBalance;
        this.monthlyRepayment = monthlyRepayment;
        this.interest = interest;
        this.principle = principle;
        this.fee = fee;
        this.revenue = revenue;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public double getOldBalance() {
        return oldBalance;
    }

    public void setOldBalance(double oldBalance) {
        this.oldBalance = oldBalance;
    }

    public double getNewBalance() {
        return newBalance;
    }

    public void setNewBalance(double newBalance) {
        this.newBalance = newBalance;
    }

    public double getMonthlyRepayment() {
        return monthlyRepayment;
    }

    public void setMonthlyRepayment(double monthlyRepayment) {
        this.monthlyRepayment = monthlyRepayment;
    }

    public double getInterest() {
        return interest;
    }

    public void setInterest(double interest) {
        this.interest = interest;
    }

    public double getPrinciple() {
        return principle;
    }

    public void setPrinciple(double principle) {
        this.principle = principle;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }
}
