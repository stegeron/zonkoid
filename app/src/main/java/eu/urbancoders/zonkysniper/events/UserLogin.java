package eu.urbancoders.zonkysniper.events;

import eu.urbancoders.zonkysniper.dataobjects.AuthToken;

/**
 * Prihlaseni uzivatele
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 14.05.2016
 */
public class UserLogin extends AbstractEvent {

    public static class Request {
        public String userName;
        public String password;

        public Request(String userName, String password) {
            this.userName = userName;
            this.password = password;
        }
    }

    public static class Response {
        public AuthToken token;

        public Response(AuthToken token) {
            this.token = token;
        }
    }

    public static class Failure {
        public String errorCode;

        public Failure(String errorCode) {
            this.errorCode = errorCode;
        }
    }
}
