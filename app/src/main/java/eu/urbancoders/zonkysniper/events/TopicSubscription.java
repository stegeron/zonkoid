package eu.urbancoders.zonkysniper.events;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 05.06.2016
 */
public class TopicSubscription {

    public static class Request {
        boolean subscribe;

        public Request(boolean subscribe) {
            this.subscribe = subscribe;
        }

        public boolean shouldSubscribe() {
            return subscribe;
        }
    }

    public static class Response {

    }

    public static class Failure {

    }
}
