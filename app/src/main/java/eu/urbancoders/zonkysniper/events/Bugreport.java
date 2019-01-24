package eu.urbancoders.zonkysniper.events;

/**
 * Odeslani bugreportu
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 08.09.2016
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
