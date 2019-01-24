package eu.urbancoders.zonkysniper.events;

/**
 * Ulozi na ZonkyCommander server novy nebo zmeneny token uzivatele
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 28.12.2016
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
