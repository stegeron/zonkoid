package eu.urbancoders.zonkysniper.events;

import eu.urbancoders.zonkysniper.dataobjects.PasswordResetter;

/**
 * Zaslat nove heslo
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 13.09.2016
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
