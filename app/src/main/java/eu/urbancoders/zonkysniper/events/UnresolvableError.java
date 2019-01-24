package eu.urbancoders.zonkysniper.events;

import eu.urbancoders.zonkysniper.dataobjects.ZonkyAPIError;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 16.05.2016
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
public class UnresolvableError extends AbstractEvent{

    public static class Request {
        ZonkyAPIError error;

        public Request(ZonkyAPIError error) {
            this.error = error;
        }

        public ZonkyAPIError getError() {
            return error;
        }
    }

    public static class Response {

    }

    public static class Failure {

    }

}
