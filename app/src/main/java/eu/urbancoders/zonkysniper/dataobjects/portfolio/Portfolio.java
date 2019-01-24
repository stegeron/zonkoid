package eu.urbancoders.zonkysniper.dataobjects.portfolio;

import java.io.Serializable;
import java.util.List;

/**
 * Prehledovky v portfoliu
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

public class Portfolio implements Serializable {

    /**
     * {
     * "currentProfitability":0.1039,
     * "expectedProfitability":0.0704,
     * "currentOverview":{                                 ok
     *      "totalInvestment":31200,
     *      "principalPaid":3861,
     *      "interestPaid":1496,
     *      "investmentCount":111,
     *      "principalLeft":27339,
     *      "principalLeftToPay":27313,
     *      "principalLeftDue":26,
     *      "interestPlanned":8039,
     *      "interestLeft":6543,
     *      "interestLeftToPay":6532,
     *      "interestLeftDue":11
     * },
     * "overallOverview":{
     *      "totalInvestment":32800,
     *      "principalPaid":5461,
     *      "interestPaid":1555,
     *      "investmentCount":117,
     *      "feesAmount":145,
     *      "netIncome":1555,
     *      "principalLost":0
     * },
     * "overallPortfolio":{
     *      "unpaid":0,
     *      "paid":0,
     *      "due":0
     * },
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
     * "riskPortfolio":[                                         ok
     *      {
     *          "unpaid":323,
     *          "paid":77,
     *          "due":0,
     *          "rating":"AAAAA",
     *          "totalAmount":400
     *      },
     *      {
     *          "unpaid":1295,
     *          "paid":105,
     *          "due":0,
     *          "rating":"AAAA",
     *          "totalAmount":1400
     *      },
     *      ......
     *      {
     *          "unpaid":2511,
     *          "paid":289,
     *          "due":2,
     *          "rating":"D",
     *          "totalAmount":2800
     *      }
     * ]
     * }
     */

    Double currentProfitability;
    Double expectedProfitability;

    CurrentOverview currentOverview;
    OverallOverview overallOverview;
    List<RiskPortfolio> riskPortfolio;

    List<CashFlow> cashFlow;

    public List<CashFlow> getCashFlow() {
        return cashFlow;
    }

    public void setCashFlow(List<CashFlow> cashFlow) {
        this.cashFlow = cashFlow;
    }

    public Double getCurrentProfitability() {
        return currentProfitability;
    }

    public void setCurrentProfitability(Double currentProfitability) {
        this.currentProfitability = currentProfitability;
    }

    public Double getExpectedProfitability() {
        return expectedProfitability;
    }

    public void setExpectedProfitability(Double expectedProfitability) {
        this.expectedProfitability = expectedProfitability;
    }

    public OverallOverview getOverallOverview() {
        return overallOverview;
    }

    public void setOverallOverview(OverallOverview overallOverview) {
        this.overallOverview = overallOverview;
    }

    public List<RiskPortfolio> getRiskPortfolio() {
        return riskPortfolio;
    }

    public void setRiskPortfolio(List<RiskPortfolio> riskPortfolio) {
        this.riskPortfolio = riskPortfolio;
    }

    public CurrentOverview getCurrentOverview() {
        return currentOverview;
    }

    public void setCurrentOverview(CurrentOverview currentOverview) {
        this.currentOverview = currentOverview;
    }
}
