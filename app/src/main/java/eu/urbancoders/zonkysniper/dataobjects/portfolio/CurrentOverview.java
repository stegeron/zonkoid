package eu.urbancoders.zonkysniper.dataobjects.portfolio;

import java.io.Serializable;

/**
 * Aktuální stav portfólia
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 17.03.2017
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

public class CurrentOverview implements Serializable {

    /**
     *      "totalInvestment":31200,          investováno
     *      "principalPaid":3861,             vráceno
     *      "interestPaid":1496,              zaplacený úrok
     *      "investmentCount":111,            aktivních investic
     *      "principalLeft":27339,            ve hre
     *      "principalLeftToPay":27313,
     *      "principalLeftDue":26,            po splatnosti
     *      "interestPlanned":8039,           očekávaný úrok
     *      "interestLeft":6543,              ve hre
     *      "interestLeftToPay":6532,
     *      "interestLeftDue":11              úrok po splatnosti
     */

    Integer investmentCount;

    /**
     * Castky
     */
    Double totalInvestment;
    Double principalPaid;
    Double principalLeftDue;
    Double principalLeft;

    /**
     * Uroky
     */
    Double interestPlanned;
    Double interestPaid;
    Double interestLeftDue;
    Double interestLeft;


    public Double getInterestLeft() {
        return interestLeft;
    }

    public void setInterestLeft(Double interestLeft) {
        this.interestLeft = interestLeft;
    }

    public Double getPrincipalLeft() {
        return principalLeft;
    }

    public void setPrincipalLeft(Double principalLeft) {
        this.principalLeft = principalLeft;
    }

    public Integer getInvestmentCount() {
        return investmentCount;
    }

    public void setInvestmentCount(Integer investmentCount) {
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

    public Double getPrincipalLeftDue() {
        return principalLeftDue;
    }

    public void setPrincipalLeftDue(Double principalLeftDue) {
        this.principalLeftDue = principalLeftDue;
    }

    public Double getInterestPlanned() {
        return interestPlanned;
    }

    public void setInterestPlanned(Double interestPlanned) {
        this.interestPlanned = interestPlanned;
    }

    public Double getInterestPaid() {
        return interestPaid;
    }

    public void setInterestPaid(Double interestPaid) {
        this.interestPaid = interestPaid;
    }

    public Double getInterestLeftDue() {
        return interestLeftDue;
    }

    public void setInterestLeftDue(Double interestLeftDue) {
        this.interestLeftDue = interestLeftDue;
    }
}
