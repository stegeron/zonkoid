package eu.urbancoders.zonkysniper.events;

/**
 * Ulozi na ZonkyCommander server novy nebo zmeneny token uzivatele
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 28.12.2016
 */

public class FcmTokenRegistration {

    public static class Request {
        String username, token;

        public Request(String username, String token) {
            this.username = username;
            this.token = token;
        }

        public String getUsername() {
            return username;
        }

        public String getToken() {
            return token;
        }
    }

    public static class Response {

    }

    public static class Failure {

    }
}
