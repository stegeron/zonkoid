package eu.urbancoders.zonkysniper.events;

import eu.urbancoders.zonkysniper.dataobjects.PasswordResetter;

/**
 * Zaslat nove heslo
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 13.09.2016
 */
public class PasswordReset {

    public static class Request {
        PasswordResetter passwordResetter;

        public Request(PasswordResetter passwordResetter) {
            this.passwordResetter = passwordResetter;
        }

        public PasswordResetter getPasswordResetter() {
            return passwordResetter;
        }
    }

    public static class Response {
        // no response
    }

    public static class Failure {
        int code;

        public Failure(int code) {
            // 400 - chybna captcha
        }

        public int getCode() {
            return code;
        }
    }

}
