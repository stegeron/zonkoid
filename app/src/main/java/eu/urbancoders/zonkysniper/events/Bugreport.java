package eu.urbancoders.zonkysniper.events;

/**
 * Odeslani bugreportu
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 08.09.2016
 */
public class Bugreport {

    public static class Request {

        String username, description, logs, timestamp;

        public Request(String username, String description, String logs, String timestamp) {
            this.username = username;
            this.description = description;
            this.logs = logs;
            this.timestamp = timestamp;
        }

        public String getUsername() {
            return username;
        }

        public String getDescription() {
            return description;
        }

        public String getLogs() {
            return logs;
        }

        public String getTimestamp() {
            return timestamp;
        }
    }

    public static class Response {
        // no response needed
    }

    public static class Failure {
        // no failure report needed
    }

}
