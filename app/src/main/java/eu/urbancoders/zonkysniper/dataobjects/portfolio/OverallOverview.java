package eu.urbancoders.zonkysniper.dataobjects.portfolio;

/**
 * Portfolio celkova cisla
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 23.03.2017
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

public class OverallOverview {

    /**
     * "overallOverview":{
     *      "totalInvestment":32800,       celkem investovano
     *      "principalPaid":5461,          vraceno
     *      "interestPaid":1555,           zaplacený úrok
     *      "investmentCount":117,         celkových investic
     *      "feesAmount":145,              zaplaceno na poplatcích
     *      "netIncome":1555,              vyděláno
     *      "principalLost":0              ztraceno
     * }
     */

    int investmentCount;

    Double totalInvestment;
    Double principalPaid;
    Double interestPaid;
    Double feesAmount;
    Double netIncome;
    Double principalLost;

    public int getInvestmentCount() {
        return investmentCount;
    }

    public void setInvestmentCount(int investmentCount) {
        this.investmentCount = investmentCount;
    }

    public Double getTotalInvestment() {
        return totalInvestment;
    }

    public void setTotalInvestment(Double totalInvestment) {
        this.totalInvestment = totalInvestment;
    }

    public Double getPrincipalPaid() {
        return principalPaid;
    }

    public void setPrincipalPaid(Double principalPaid) {
        this.principalPaid = principalPaid;
    }

    public Double getInterestPaid() {
        return interestPaid;
    }

    public void setInterestPaid(Double interestPaid) {
        this.interestPaid = interestPaid;
    }

    public Double getFeesAmount() {
        return feesAmount;
    }

    public void setFeesAmount(Double feesAmount) {
        this.feesAmount = feesAmount;
    }

    public Double getNetIncome() {
        return netIncome;
    }

    public void setNetIncome(Double netIncome) {
        this.netIncome = netIncome;
    }

    public Double getPrincipalLost() {
        return principalLost;
    }

    public void setPrincipalLost(Double principalLost) {
        this.principalLost = principalLost;
    }
}
