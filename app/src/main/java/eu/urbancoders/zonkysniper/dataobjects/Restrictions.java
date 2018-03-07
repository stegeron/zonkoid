package eu.urbancoders.zonkysniper.dataobjects;

import java.io.Serializable;
import java.util.Date;

/**
 * Restrikce investora, pouziva se pro zjisteni max. castky pro investovani
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Datum: 07.03.2018
 */

public class Restrictions implements Serializable {

/**    {
//        "requestDate": null,
//            "withdrawalDate": null,
//            "cannotInvest": false,
//            "cannotAccessSmp": false,
//            "maximumInvestmentAmount": 20000
//    }
 **/

    Date requestDate;
    Date withdrawalDate;
    boolean cannotInvest;
    boolean cannotAccessSmp;
    int maximumInvestmentAmount;

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public Date getWithdrawalDate() {
        return withdrawalDate;
    }

    public void setWithdrawalDate(Date withdrawalDate) {
        this.withdrawalDate = withdrawalDate;
    }

    public boolean isCannotInvest() {
        return cannotInvest;
    }

    public void setCannotInvest(boolean cannotInvest) {
        this.cannotInvest = cannotInvest;
    }

    public boolean isCannotAccessSmp() {
        return cannotAccessSmp;
    }

    public void setCannotAccessSmp(boolean cannotAccessSmp) {
        this.cannotAccessSmp = cannotAccessSmp;
    }

    public int getMaximumInvestmentAmount() {
        return maximumInvestmentAmount;
    }

    public void setMaximumInvestmentAmount(int maximumInvestmentAmount) {
        this.maximumInvestmentAmount = maximumInvestmentAmount;
    }
}
