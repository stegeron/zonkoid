package eu.urbancoders.zonkysniper.events;

/**
 * Zadost o registraci k betatestu
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 07.07.2016
 * @deprecated zrusit, protoze neni potreba
 */
@Deprecated
public class BetatesterRegister {

    public static class Request {
        String username;

        public Request(String username) {
            this.username = username;
        }

        public String getUsername() {
            return username;
        }
    }

    public static class Response {

    }

    public static class Failure {

    }

}
