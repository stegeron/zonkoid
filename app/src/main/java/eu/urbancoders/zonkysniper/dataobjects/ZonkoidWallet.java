package eu.urbancoders.zonkysniper.dataobjects;

import java.util.Date;
import java.util.List;

/**
 * Zonkoid peněženka
 *
 * Created by osteger on 01.06.2017.
 */
public class ZonkoidWallet extends Wallet {

    List<WalletTransaction> walletTransactions;
    double pricePerInvestment;
    Date lastPaymentDate;

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
}
