package eu.urbancoders.zonkysniper.events;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 04.07.2016
 *
 * @deprecated zrusit, protoze neni potreba
 */
@Deprecated
public class BetatesterCheck {

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
        boolean isBetatester;

        public Response(boolean isBetatester) {
            this.isBetatester = isBetatester;
        }

        public boolean isBetatester() {
            return isBetatester;
        }
    }

    public static class Failure {

    }


}
