package eu.urbancoders.zonkysniper.events;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 05.06.2016
 */
public class TopicSubscription {

    public static class Request {
        boolean subscribe;
        String topicName;

        public Request(String topicName, boolean subscribe) {
            this.subscribe = subscribe;
            this.topicName = topicName;
        }

        public boolean shouldSubscribe() {
            return subscribe;
        }

        public String getTopicName() {
            return topicName;
        }
    }

    public static class Response {

    }

    public static class Failure {

    }
}
