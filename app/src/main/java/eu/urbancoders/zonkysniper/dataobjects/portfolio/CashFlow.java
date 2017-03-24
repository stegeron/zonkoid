package eu.urbancoders.zonkysniper.dataobjects.portfolio;

import java.util.Date;

/**
 * Data pro graf splaceni
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 23.03.2017
 */

public class CashFlow {

    /**
     * "cashFlow":[
     *      {
     *          "month":"2016-07-01T00:00:00.000+02:00",
     *          "instalmentAmount":336.62,
     *          "principalPaid":265.95,
     *          "interestPaid":100.33
     *      },
     *      {
     *          "month":"2016-08-01T00:00:00.000+02:00",
     *          "instalmentAmount":336.62,
     *          "principalPaid":240.27,
     *          "interestPaid":96.62
     *      },
     *      ....
     *      {
     *          "month":"2017-06-01T00:00:00.000+02:00",
     *          "instalmentAmount":889.11,
     *          "principalPaid":null,
     *          "interestPaid":null
     *      }
     * ],
     */

    Date month;
    Double instalmentAmount;
    Double principalPaid;
    Double interestPaid;

    public Date getMonth() {
        return month;
    }

    public void setMonth(Date month) {
        this.month = month;
    }

    public Double getInstalmentAmount() {
        return instalmentAmount;
    }

    public void setInstalmentAmount(Double instalmentAmount) {
        this.instalmentAmount = instalmentAmount;
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
}
