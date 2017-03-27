package eu.urbancoders.zonkysniper.dataobjects;

import java.io.Serializable;
import java.util.Date;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 20.05.2016
 */
public class MyInvestment implements Serializable {

//    "myInvestment":
//    {
//        "id":2133,
//        "firstAmount":200.00
//        "amount":400.00,
//        "timeCreated":"zulu date",
//        "investorId":12343,
//        "loanId":32132,
//        "investorNickname":"kath",
//        "status":"ACTIVE"
//        "captcha_response":"dsadoiajeoijdueewbfewfeiu"
//    }

    Integer id;
    Double firstAmount;
    Double amount;
    Date timeCreated;
    Integer investorId;
    Integer loanId;
    String investorNickname;
    String status;
    String captcha_response;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getFirstAmount() {
        return firstAmount;
    }

    public void setFirstAmount(Double firstAmount) {
        this.firstAmount = firstAmount;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    public Integer getInvestorId() {
        return investorId;
    }

    public void setInvestorId(Integer investorId) {
        this.investorId = investorId;
    }

    public Integer getLoanId() {
        return loanId;
    }

    public void setLoanId(Integer loanId) {
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

    public String getCaptcha_response() {
        return captcha_response;
    }

    public void setCaptcha_response(String captcha_response) {
        this.captcha_response = captcha_response;
    }
}
