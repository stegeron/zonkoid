package eu.urbancoders.zonkysniper.dataobjects;

import java.util.List;

/**
 * Zonkoid peněženka
 *
 * Created by osteger on 01.06.2017.
 */
public class ZonkoidWallet extends Wallet {

    List<WalletTransaction> walletTransactions;

    public List<WalletTransaction> getWalletTransactions() {
        return walletTransactions;
    }

    public void setWalletTransactions(List<WalletTransaction> walletTransactions) {
        this.walletTransactions = walletTransactions;
    }
}
