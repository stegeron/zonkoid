package eu.urbancoders.zonkysniper.events;

import eu.urbancoders.zonkysniper.dataobjects.Investor;

/**
 * Nastaveni stavu investora treba po odsouhlaseni poplatku
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Datum: 01.08.2017
 */

public class SetUserStatus {

    public static class Request {
        int investorId;
        Investor.Status status;

        public Request(int investorId, Investor.Status status) {
            this.investorId = investorId;
            this.status = status;
        }

        public int getInvestorId() {
            return investorId;
        }

        public Investor.Status getStatus() {
            return status;
        }
    }

    public static class Response {

    }

    public static class Failure {

    }

}
