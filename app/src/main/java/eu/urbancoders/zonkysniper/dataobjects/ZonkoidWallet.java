package eu.urbancoders.zonkysniper.dataobjects;

import java.util.Date;
import java.util.List;

/**
 * Zonkoid peněženka
 *
 * Created by osteger on 01.06.2017.
 */
public class ZonkoidWallet extends Wallet {

    private List<WalletTransaction> walletTransactions;
    private double pricePerInvestment;
    private Date lastPaymentDate;
    private double minPaymentPrice;
    private String ourBankAccount;

    public String getOurBankAccount() {
        return ourBankAccount;
    }

    public void setOurBankAccount(String ourBankAccount) {
        this.ourBankAccount = ourBankAccount;
    }

    public List<WalletTransaction> getWalletTransactions() {
        return walletTransactions;
    }

    public void setWalletTransactions(List<WalletTransaction> walletTransactions) {
        this.walletTransactions = walletTransactions;
    }

    public double getPricePerInvestment() {
        return pricePerInvestment;
    }

    public void setPricePerInvestment(double pricePerInvestment) {
        this.pricePerInvestment = pricePerInvestment;
    }

    public Date getLastPaymentDate() {
        return lastPaymentDate;
    }

    public void setLastPaymentDate(Date lastPaymentDate) {
        this.lastPaymentDate = lastPaymentDate;
    }

    public void setMinPaymentPrice(double minPaymentPrice) {
        this.minPaymentPrice = minPaymentPrice;
    }

    public double getMinPaymentPrice() {
        return minPaymentPrice;
    }
}