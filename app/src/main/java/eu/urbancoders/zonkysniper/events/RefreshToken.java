package eu.urbancoders.zonkysniper.events;

import eu.urbancoders.zonkysniper.dataobjects.AuthToken;

/**
 * Obnoveni tokenu
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 14.05.2016
 */
public class RefreshToken extends AbstractEvent {

    public static class Request {
        AuthToken tokenInvalid;

        public Request(AuthToken tokenInvalid) {
            this.tokenInvalid = tokenInvalid;
        }

        public AuthToken getTokenInvalid() {
            return tokenInvalid;
        }
    }

    public static class Response {
        public AuthToken tokenNew;

        public Response(AuthToken token) {
            this.tokenNew = token;
        }
    }

    public static class Failure {
        public String errorCode;

        public Failure(String errorCode) {
            this.errorCode = errorCode;
        }
    }
}
