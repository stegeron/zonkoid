package eu.urbancoders.zonkysniper.events;

import eu.urbancoders.zonkysniper.dataobjects.Restrictions;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Datum: 07.03.2018
 */

public class GetInvestorRestrictions extends AbstractEvent {


    public static class Request {

    }

    public static class Response {
        Restrictions restrictions;

        public Response(Restrictions restrictions) {
            this.restrictions = restrictions;
        }

        public Restrictions getRestrictions() {
            return restrictions;
        }
    }

    public static class Failure {

    }
}
