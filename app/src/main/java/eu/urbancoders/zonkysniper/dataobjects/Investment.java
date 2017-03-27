package eu.urbancoders.zonkysniper.dataobjects;

import java.io.Serializable;
import java.util.Date;

/**
 * Seznam investic a investoru do pujcky
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 26.08.2016
 */
public class Investment implements Serializable {

    /**
     * {
     * "id":265011,
     * "firstAmount":200.00,
     * "amount":200.00,
     * "timeCreated":"2016-08-22T21:28:27.503+02:00",
     * "investorId":4945,
     * "loanId":43562,
     * "investorNickname":"kuky",
     * "status":"ACTIVE",
     * "additionalAmount":0.00
     * },
     * {
     * "id":270871,
     * "firstAmount":200.00,
     * "amount":600.00,
     * "timeCreated":"2016-08-26T16:40:58.019+02:00",
     * "investorId":19192,
     * "loanId":43562,
     * "investorNickname":"BudouciMilionar",
     * "status":"ACTIVE",
     * "additionalAmount":400.00
     * }
     */

    int id;
    int loanId;
    double firstAmount;
    double additionalAmount;
    double amount;
    Date timeCreated;
    int investorId;
    String investorNickname;
    String status;
    boolean zonkoidInvested; // special pro zonkoida, na zonky api se nedostane

    public boolean getZonkoidInvested() {
        return zonkoidInvested;
    }

    public void setZonkoidInvested(boolean zonkoidInvested) {
        this.zonkoidInvested = zonkoidInvested;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLoanId() {
        return loanId;
    }

    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }

    public double getFirstAmount() {
        return firstAmount;
    }

    public void setFirstAmount(double firstAmount) {
        this.firstAmount = firstAmount;
    }

    public double getAdditionalAmount() {
        return additionalAmount;
    }

    public void setAdditionalAmount(double additionalAmount) {
        this.additionalAmount = additionalAmount;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    public int getInvestorId() {
        return investorId;
    }

    public void setInvestorId(int investorId) {
        this.investorId = investorId;
    }

    public String getInvestorNickname() {
        return investorNickname;
    }

    public void setInvestorNickname(String investorNickname) {
        this.investorNickname = investorNickname;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
