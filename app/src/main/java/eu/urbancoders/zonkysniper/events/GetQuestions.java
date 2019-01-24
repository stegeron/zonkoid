package eu.urbancoders.zonkysniper.events;

import eu.urbancoders.zonkysniper.dataobjects.Question;

import java.util.List;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 19.09.2016
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
