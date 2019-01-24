package eu.urbancoders.zonkysniper.events;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 05.06.2016
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
public class TopicSubscription {

    public static class Request {
        boolean subscribe;
        String topicName;

        public Request(String topicName, boolean subscribe) {
            this.subscribe = subscribe;
            this.topicName = topicName;
        }

        public boolean shouldSubscribe() {
            return subscribe;
        }

        public String getTopicName() {
            return topicName;
        }
    }

    public static class Response {

    }

    public static class Failure {

    }
}
