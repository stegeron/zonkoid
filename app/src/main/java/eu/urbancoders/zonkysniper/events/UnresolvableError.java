package eu.urbancoders.zonkysniper.events;

import eu.urbancoders.zonkysniper.integration.ZonkyAPIError;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 16.05.2016
 */
public class UnresolvableError extends AbstractEvent{

    public static class Request {
        ZonkyAPIError error;

        public Request(ZonkyAPIError error) {
            this.error = error;
        }

        public ZonkyAPIError getError() {
            return error;
        }
    }

    public static class Response {

    }

    public static class Failure {

    }

}
