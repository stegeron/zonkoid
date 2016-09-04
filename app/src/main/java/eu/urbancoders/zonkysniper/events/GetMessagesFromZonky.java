package eu.urbancoders.zonkysniper.events;

import eu.urbancoders.zonkysniper.dataobjects.Message;

import java.util.List;

/**
 * Zpravy od zonky
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 04.09.2016
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
