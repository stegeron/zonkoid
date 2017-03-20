package eu.urbancoders.zonkysniper.dataobjects.portfolio;

import java.io.Serializable;

/**
 * Aktuální stav portfólia
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 17.03.2017
 */

public class CurrentOverview implements Serializable {

    /**
     *      "totalInvestment":31200,          investováno
     *      "principalPaid":3861,             vráceno
     *      "interestPaid":1496,              zaplacený úrok
     *      "investmentCount":111,            aktivních investic
     *      "principalLeft":27339,
     *      "principalLeftToPay":27313,
     *      "principalLeftDue":26,            po splatnosti
     *      "interestPlanned":8039,           očekávaný úrok
     *      "interestLeft":6543,
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

    /**
     * Uroky
     */
    Double interestPlanned;
    Double interestPaid;
    Double interestLeftDue;



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
