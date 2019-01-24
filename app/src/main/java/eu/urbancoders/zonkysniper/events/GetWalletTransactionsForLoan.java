package eu.urbancoders.zonkysniper.events;

import java.util.List;

import eu.urbancoders.zonkysniper.dataobjects.WalletTransaction;

/**
 * Ziskani seznamu plateb z jedne pujcky
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Datum: 08.03.2018
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

public class GetWalletTransactionsForLoan {

    public static class Request {
        String loanId;

        public Request(String loanId) {
            this.loanId = loanId;
        }

        public String getLoanId() {
            return loanId;
        }
    }

    public static class Response {
        List<WalletTransaction> transactions;

        public Response(List<WalletTransaction> transactions) {
            this.transactions = transactions;
        }

        public List<WalletTransaction> getTransactions() {
            return transactions;
        }
    }

    public static class Failure {

    }
}
