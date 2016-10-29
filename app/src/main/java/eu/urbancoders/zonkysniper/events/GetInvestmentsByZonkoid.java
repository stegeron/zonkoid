package eu.urbancoders.zonkysniper.events;

import eu.urbancoders.zonkysniper.dataobjects.Investment;

import java.util.List;

/**
 * Vraci investice pres Zonkoid, tabulka investments na zonkycommanderu
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 29.10.2016
 */

public class GetInvestmentsByZonkoid {

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
        List<Investment> investments;

        public Response(List<Investment> investments) {
            this.investments = investments;
        }

        public List<Investment> getInvestments() {
            return investments;
        }
    }

    public static class Failure {

    }

}
