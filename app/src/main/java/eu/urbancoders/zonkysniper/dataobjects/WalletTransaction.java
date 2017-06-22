package eu.urbancoders.zonkysniper.dataobjects;

import java.io.Serializable;
import java.util.Date;

/**
 * Prehled transakci v penezence - deposity, splatky od dluzniku, investice apod.
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 19.04.2017
 */

public class WalletTransaction implements Serializable {


    /**
     * {
     * "amount":3.82,
     * "category":"PAYMENT",
     * "transactionDate":"2017-04-18T00:00:00.000+02:00",
     * "customMessage":null,
     * "orientation":"IN",
     * "loanId":54371,
     * "nickName":"zonky69831",
     * "loanName":"Refinancování půjček"
     * },
     * {
     * "amount":3000.00,
     * "category":"DEPOSIT",
     * "transactionDate":"2017-04-18T00:00:00.000+02:00",
     * "customMessage":null,
     * "orientation":"IN",
     * "loanId":null,
     * "nickName":null,
     * "loanName":null
     * },
     * {
     * "amount":200.00,
     * "category":"INVESTMENT",
     * "transactionDate":"2017-04-18T00:00:00.000+02:00",
     * "customMessage":null,
     * "orientation":"OUT",
     * "loanId":82449,
     * "nickName":"Tatulda",
     * "loanName":"Kouzelná zahrada pro děti"
     * },
     * {
     * "amount":22.13,
     * "category":"INVESTMENT_FEE",
     * "transactionDate":"2017-04-03T00:00:00.000+02:00",
     * "customMessage":null,
     * "orientation":"OUT",
     * "loanId":null,
     * "nickName":null,
     * "loanName":null
     * }
     * <p>
     * TODO výběr peněz na bankovní účet
     */

    double amount;
    WalletTransactionCategory category;
    Date transactionDate;
    String customMessage;
    String orientation;  // OUT | IN
    int loanId;
    String nickName;
    String loanName;
    String purchaseToken;   // token z Google Play
    String purchaseSKU;     // product ID

    public String getPurchaseSKU() {
        return purchaseSKU;
    }

    public void setPurchaseSKU(String purchaseSKU) {
        this.purchaseSKU = purchaseSKU;
    }

    public String getPurchaseToken() {
        return purchaseToken;
    }

    public void setPurchaseToken(String purchaseToken) {
        this.purchaseToken = purchaseToken;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public WalletTransactionCategory getCategory() {
        return category;
    }

    public void setCategory(WalletTransactionCategory category) {
        this.category = category;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getCustomMessage() {
        return customMessage;
    }

    public void setCustomMessage(String customMessage) {
        this.customMessage = customMessage;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public int getLoanId() {
        return loanId;
    }

    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getLoanName() {
        return loanName;
    }

    public void setLoanName(String loanName) {
        this.loanName = loanName;
    }
}
