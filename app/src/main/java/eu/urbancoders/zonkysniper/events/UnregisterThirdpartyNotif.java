package eu.urbancoders.zonkysniper.events;

import eu.urbancoders.zonkysniper.core.Constants;

/**
 * Slouzi pro odregistraci aplikace treti strany, tim padem znemozneni prijimani notifikaci, napr. od RoboZonky
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 25.12.2016
 */

public class UnregisterThirdpartyNotif {

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

    }

    public static class Failure {

    }
}
