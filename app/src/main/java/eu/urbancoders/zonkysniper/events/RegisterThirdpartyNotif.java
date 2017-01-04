package eu.urbancoders.zonkysniper.events;

import eu.urbancoders.zonkysniper.core.Constants;

/**
 * Slouzi pro registraci aplikace treti strany, ziskani kodu a nasledne umozneni prijimani notifikaci, napr. RoboZonky
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 25.12.2016
 */

public class RegisterThirdpartyNotif {

    public static class Request {
        String username;
        Constants.ClientApps clientApp;

        public Request(String username, Constants.ClientApps clientApp) {
            this.username = username;
            this.clientApp = clientApp;
        }

        public String getUsername() {
            return username;
        }

        public Constants.ClientApps getClientApp() {
            return clientApp;
        }
    }

    public static class Response {
        Integer userCode;

        public Response(Integer userCode) {
            this.userCode = userCode;
        }

        public Integer getUserCode() {
            return userCode;
        }
    }

    public static class Failure {

    }
}
