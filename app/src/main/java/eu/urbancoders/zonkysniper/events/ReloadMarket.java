package eu.urbancoders.zonkysniper.events;

import eu.urbancoders.zonkysniper.dataobjects.Loan;

import java.util.List;

/**
 * Udalost pro nacteni trziste
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 14.05.2016
 */
public class ReloadMarket extends AbstractEvent {

    public static class Request {
        boolean onlyUncovered;

        public Request(boolean onlyUncovered) {
            this.onlyUncovered = onlyUncovered;
        }

        public Request() {
        }
    }

    public static class Response {
        public List<Loan> market;

        public Response(List<Loan> market) {
            this.market = market;
        }

        public List<Loan> getMarket() {
            return market;
        }
    }

    public static class Failure {
        public String errorCode;

        public Failure(String errorCode) {
            this.errorCode = errorCode;
        }
    }
}
