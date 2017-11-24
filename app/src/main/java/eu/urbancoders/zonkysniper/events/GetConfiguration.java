package eu.urbancoders.zonkysniper.events;

import java.util.List;

import eu.urbancoders.zonkysniper.dataobjects.ConfigurationItem;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Datum: 21.11.2017
 */

public class GetConfiguration {

    public static class Request {
        List<String> keys;

        public Request(List<String> keys) {
            this.keys = keys;
        }

        public List<String> getKeys() {
            return keys;
        }
    }

    public static class Response {
        List<ConfigurationItem> items;

        public Response(List<ConfigurationItem> items) {
            this.items = items;
        }

        public List<ConfigurationItem> getItems() {
            return items;
        }
    }

    public static class Failure {

    }
}
