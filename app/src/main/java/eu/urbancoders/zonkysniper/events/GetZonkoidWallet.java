package eu.urbancoders.zonkysniper.events;

import eu.urbancoders.zonkysniper.dataobjects.ZonkoidWallet;

/**
 * Volani data pro Zonkoid Wallet
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 21.06.2017
 */

public class GetZonkoidWallet extends AbstractEvent {

    public static class Request {
        int investorId;

        public Request(int investorId) {
            this.investorId = investorId;
        }

        public int getInvestorId() {
            return investorId;
        }
    }

    public static class Response {
        ZonkoidWallet zonkoidWallet;

        public Response(ZonkoidWallet zonkoidWallet) {
            this.zonkoidWallet = zonkoidWallet;
        }

        public ZonkoidWallet getZonkoidWallet() {
            return zonkoidWallet;
        }
    }

    public static class Failure {

    }

}
