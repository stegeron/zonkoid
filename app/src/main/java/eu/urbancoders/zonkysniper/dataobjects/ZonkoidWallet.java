package eu.urbancoders.zonkysniper.dataobjects;

import java.util.Date;
import java.util.List;

/**
 * Zonkoid peněženka
 *
 * Created by osteger on 01.06.2017.
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
public class ZonkoidWallet extends Wallet {

    List<WalletTransaction> walletTransactions;
    Date lastPaymentDate;
    String ourBankAccount;

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

    public Date getLastPaymentDate() {
        return lastPaymentDate;
    }

    public void setLastPaymentDate(Date lastPaymentDate) {
        this.lastPaymentDate = lastPaymentDate;
    }
}