package eu.urbancoders.zonkysniper.dataobjects;

import java.util.Date;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 20.05.2016
 */
public class MyInvestment {

//    "myInvestment":
//    {
//        "id":2133,
//        "amount":200.00,
//        "timeCreated":"zulu date",
//        "investorId":12343,
//        "loanId":32132,
//        "investorNickname":"kath",
//        "status":"ACTIVE"
//    }

    int id;
    double amount;
    Date timeCreated;
    int investorId;
    int loanId;
    String investorNickname;
    String status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getLoanId() {
        return loanId;
    }

    public void setLoanId(int loanId) {
        this.loanId = loanId;
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
