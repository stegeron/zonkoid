package eu.urbancoders.zonkysniper.events;

import eu.urbancoders.zonkysniper.dataobjects.portfolio.Portfolio;

/**
 * Nacteni portfolia investora ze Zonky
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 17.03.2017
 */

public class GetPortfolio {

    public static class Request {
        // no params
    }

    public static class Response {
        Portfolio portfolio;

        public Response(Portfolio portfolio) {
            this.portfolio = portfolio;
        }

        public Portfolio getPortfolio() {
            return portfolio;
        }
    }

    public static class Failure {

    }
}
