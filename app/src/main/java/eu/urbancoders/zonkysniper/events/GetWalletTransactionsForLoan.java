package eu.urbancoders.zonkysniper.events;

import java.util.List;

import eu.urbancoders.zonkysniper.dataobjects.WalletTransaction;

/**
 * Ziskani seznamu plateb z jedne pujcky
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Datum: 08.03.2018
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
