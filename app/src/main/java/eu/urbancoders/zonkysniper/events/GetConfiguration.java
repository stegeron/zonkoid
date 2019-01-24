package eu.urbancoders.zonkysniper.events;

import java.util.List;

import eu.urbancoders.zonkysniper.dataobjects.ConfigurationItem;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Datum: 21.11.2017
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

public class GetConfiguration {

    public static class Request {
        List<String> keys;

        public Request(List<String> keys) {
            this.keys = keys;
        }

        public List<String> getKeys() {
            return keys;
        }
    }

    public static class Response {
        List<ConfigurationItem> items;

        public Response(List<ConfigurationItem> items) {
            this.items = items;
        }

        public List<ConfigurationItem> getItems() {
            return items;
        }
    }

    public static class Failure {

    }
}
