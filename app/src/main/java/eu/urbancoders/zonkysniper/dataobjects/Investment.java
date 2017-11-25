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

    /**
     * {
     * "id":1500015,
     * "loanId":138372,
     * "loanName":"Rekonstrukce bytu",
     * "investmentDate":null,
     * "amount":200.00,
     * "firstAmount":200.00,
     * "purchasePrice":200.00,
     * "interestRate":0.154900,
     * "nickname":"zonky171477",
     * "firstName":null,
     * "surname":null,
     * "rating":"C",
     * "paid":null,
     * "toPay":null,
     * "nextPaymentDate":"2017-12-25T00:00:00.000+01:00",
     * "paymentStatus":"OK",
     * "legalDpd":0,
     * "amountDue":null,
     * "loanTermInMonth":26,
     * "paidInterest":0.00,
     * "dueInterest":0.00,
     * "paidPrincipal":0.00,
     * "duePrincipal":0.00,
     * "remainingPrincipal":200.00,
     * "paidPenalty":0.00,
     * "smpSoldFor":null,
     * "expectedInterest":36.73,
     * "currentTerm":26,
     * "canBeOffered":false,
     * "onSmp":false,
     * "smpRelated":null,
     * "remainingMonths":26,
     * "status":"ACTIVE",
     * "timeCreated":null,
     * "activeTo":null,
     * "smpFee":null,
     * "additionalAmount":0.00
     * }
     */



    int investorId;
    String investorNickname;

    int id;
    int loanId;
    String loanName;
    Date investmentDate;
    double amount;
    double firstAmount;
    double purchasePrice;
    double interestRate;
    String nickname;
    String firstName;
    String surname;
    String rating;
//    Object paid;
//    Object toPay;
    Date nextPaymentDate;
    String paymentStatus;
    int legalDpd;
    double amountDue;
    int loanTermInMonth;
    double paidInterest;
    double dueInterest;
    double paidPrincipal;
    double duePrincipal;
    double remainingPrincipal;
    double paidPenalty;
    double smpSoldFor;
    double expectedInterest;
    int currentTerm;
    boolean canBeOffered;
    boolean onSmp;
//    Object smpRelated;
    int remainingMonths;
    String status;
    Date timeCreated;
    Date activeTo;
    double smpFee;
    double additionalAmount;

    boolean zonkoidInvested; // special pro zonkoida, na zonky api se nedostane
    boolean isMyInvestment;  // special pro zonkoida, na zonky api se nedostane

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

    public String getLoanName() {
        return loanName;
    }

    public void setLoanName(String loanName) {
        this.loanName = loanName;
    }

    public Date getInvestmentDate() {
        return investmentDate;
    }

    public void setInvestmentDate(Date investmentDate) {
        this.investmentDate = investmentDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getFirstAmount() {
        return firstAmount;
    }

    public void setFirstAmount(double firstAmount) {
        this.firstAmount = firstAmount;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public Date getNextPaymentDate() {
        return nextPaymentDate;
    }

    public void setNextPaymentDate(Date nextPaymentDate) {
        this.nextPaymentDate = nextPaymentDate;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public int getLegalDpd() {
        return legalDpd;
    }

    public void setLegalDpd(int legalDpd) {
        this.legalDpd = legalDpd;
    }

    public double getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(double amountDue) {
        this.amountDue = amountDue;
    }

    public int getLoanTermInMonth() {
        return loanTermInMonth;
    }

    public void setLoanTermInMonth(int loanTermInMonth) {
        this.loanTermInMonth = loanTermInMonth;
    }

    public double getPaidInterest() {
        return paidInterest;
    }

    public void setPaidInterest(double paidInterest) {
        this.paidInterest = paidInterest;
    }

    public double getDueInterest() {
        return dueInterest;
    }

    public void setDueInterest(double dueInterest) {
        this.dueInterest = dueInterest;
    }

    public double getPaidPrincipal() {
        return paidPrincipal;
    }

    public void setPaidPrincipal(double paidPrincipal) {
        this.paidPrincipal = paidPrincipal;
    }

    public double getDuePrincipal() {
        return duePrincipal;
    }

    public void setDuePrincipal(double duePrincipal) {
        this.duePrincipal = duePrincipal;
    }

    public double getRemainingPrincipal() {
        return remainingPrincipal;
    }

    public void setRemainingPrincipal(double remainingPrincipal) {
        this.remainingPrincipal = remainingPrincipal;
    }

    public double getPaidPenalty() {
        return paidPenalty;
    }

    public void setPaidPenalty(double paidPenalty) {
        this.paidPenalty = paidPenalty;
    }

    public double getSmpSoldFor() {
        return smpSoldFor;
    }

    public void setSmpSoldFor(double smpSoldFor) {
        this.smpSoldFor = smpSoldFor;
    }

    public double getExpectedInterest() {
        return expectedInterest;
    }

    public void setExpectedInterest(double expectedInterest) {
        this.expectedInterest = expectedInterest;
    }

    public int getCurrentTerm() {
        return currentTerm;
    }

    public void setCurrentTerm(int currentTerm) {
        this.currentTerm = currentTerm;
    }

    public boolean isCanBeOffered() {
        return canBeOffered;
    }

    public void setCanBeOffered(boolean canBeOffered) {
        this.canBeOffered = canBeOffered;
    }

    public boolean isOnSmp() {
        return onSmp;
    }

    public void setOnSmp(boolean onSmp) {
        this.onSmp = onSmp;
    }

    public int getRemainingMonths() {
        return remainingMonths;
    }

    public void setRemainingMonths(int remainingMonths) {
        this.remainingMonths = remainingMonths;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    public Date getActiveTo() {
        return activeTo;
    }

    public void setActiveTo(Date activeTo) {
        this.activeTo = activeTo;
    }

    public double getSmpFee() {
        return smpFee;
    }

    public void setSmpFee(double smpFee) {
        this.smpFee = smpFee;
    }

    public double getAdditionalAmount() {
        return additionalAmount;
    }

    public void setAdditionalAmount(double additionalAmount) {
        this.additionalAmount = additionalAmount;
    }

    public boolean isZonkoidInvested() {
        return zonkoidInvested;
    }

    public void setZonkoidInvested(boolean zonkoidInvested) {
        this.zonkoidInvested = zonkoidInvested;
    }

    public boolean isMyInvestment() {
        return isMyInvestment;
    }

    public void setMyInvestment(boolean myInvestment) {
        isMyInvestment = myInvestment;
    }
}
