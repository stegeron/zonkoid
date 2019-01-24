package eu.urbancoders.zonkysniper.events;

import eu.urbancoders.zonkysniper.dataobjects.Message;

import java.util.List;

/**
 * Zpravy od zonky
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 04.09.2016
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
public class GetMessagesFromZonky {

    public static class Request {
        int numberOfRows;

        public Request(int numberOfRows) {
            this.numberOfRows = numberOfRows;
        }

        public int getNumberOfRows() {
            return numberOfRows;
        }
    }

    public static class Response {
        List<Message> messages;

        public Response(List<Message> messages) {
            this.messages = messages;
        }

        public List<Message> getMessages() {
            return messages;
        }
    }

    public static class Failure {

    }

}
