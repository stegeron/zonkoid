package eu.urbancoders.zonkysniper.events;

import eu.urbancoders.zonkysniper.dataobjects.Question;

/**
 * Posila dotaz / upravu dotazu k pujcce
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 12.11.2016
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
