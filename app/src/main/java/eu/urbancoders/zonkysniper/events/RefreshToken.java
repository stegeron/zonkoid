package eu.urbancoders.zonkysniper.events;

import eu.urbancoders.zonkysniper.dataobjects.AuthToken;

/**
 * Obnoveni tokenu
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 14.05.2016
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
