package eu.urbancoders.zonkysniper.events;

import eu.urbancoders.zonkysniper.dataobjects.Loan;

/**
 * Nacteni detailu pujcky, vraci Loan
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 24.08.2016
 */
public class GetLoanDetail {

    public static class Request {
        int loanId;

        public Request(int loanId) {
            this.loanId = loanId;
        }

        public int getLoanId() {
            return loanId;
        }
    }

    public static class Response {
        Loan loan;

        public Response(Loan loan) {
            this.loan = loan;
        }

        public Loan getLoan() {
            return loan;
        }
    }

    public static class Failure {

    }

}
