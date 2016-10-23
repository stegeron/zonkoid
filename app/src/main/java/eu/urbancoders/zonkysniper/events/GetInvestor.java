package eu.urbancoders.zonkysniper.events;

import eu.urbancoders.zonkysniper.dataobjects.Investor;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 18.05.2016
 */
public class GetInvestor extends AbstractEvent {

    public static class Request {

    }

    public static class Response {
        Investor investor;

        public Response(Investor investor) {
            this.investor = investor;
        }

        public Investor getInvestor() {
            return investor;
        }

        public void setInvestor(Investor investor) {
            this.investor = investor;
        }
    }

    public static class Failure {

    }
}
