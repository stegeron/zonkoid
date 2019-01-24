package eu.urbancoders.zonkysniper.events;

/**
 * Zadost o registraci k betatestu
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 07.07.2016
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
 *
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
