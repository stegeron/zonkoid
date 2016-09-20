package eu.urbancoders.zonkysniper.events;

import eu.urbancoders.zonkysniper.dataobjects.Question;

import java.util.List;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 19.09.2016
 */
public class GetQuestions {

    public static class Request {
        int loanId;
        int numberOfQuestions;

        public Request(int loanId, int numberOfQuestions) {
            this.loanId = loanId;
            this.numberOfQuestions = numberOfQuestions;
        }

        public int getLoanId() {
            return loanId;
        }

        public int getNumberOfQuestions() {
            return numberOfQuestions;
        }
    }

    public static class Response {
        List<Question> questions;

        public Response(List<Question> questions) {
            this.questions = questions;
        }

        public List<Question> getQuestions() {
            return questions;
        }
    }

    public static class Failure {
        String description;

        public Failure(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
