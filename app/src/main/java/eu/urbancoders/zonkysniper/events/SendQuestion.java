package eu.urbancoders.zonkysniper.events;

import eu.urbancoders.zonkysniper.dataobjects.Question;

/**
 * Posila dotaz / upravu dotazu k pujcce
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 12.11.2016
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

public class SendQuestion {

    public static class Request {
        Question question;
        int loanId;

        public Request(int loanId, Question question) {
            this.question = question;
            this.loanId = loanId;
        }

        public int getLoanId() {
            return loanId;
        }

        public Question getQuestion() {
            return question;
        }
    }

    public static class Response {

    }

    public static class Failure {

    }
}
