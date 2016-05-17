package eu.urbancoders.zonkysniper.events;

import eu.urbancoders.zonkysniper.dataobjects.Wallet;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 18.05.2016
 */
public class GetWallet extends AbstractEvent {

    public static class Request {

    }

    public static class Response {
        Wallet wallet;

        public Response(Wallet wallet) {
            this.wallet = wallet;
        }

        public Wallet getWallet() {
            return wallet;
        }

        public void setWallet(Wallet wallet) {
            this.wallet = wallet;
        }
    }

    public static class Failure {

    }
}
