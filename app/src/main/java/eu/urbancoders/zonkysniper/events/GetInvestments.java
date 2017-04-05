package eu.urbancoders.zonkysniper.events;

import eu.urbancoders.zonkysniper.dataobjects.Investment;

import java.util.List;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 26.08.2016
 */
public class GetInvestments {

    public static class Request {

        int loanId;
        int numberOfItems;
        int numberOfPage;

        public Request(int loanId, int numberOfItems, int numberOfPage) {
            this.loanId = loanId;
            this.numberOfItems = numberOfItems;
            this.numberOfPage = numberOfPage;
        }

        public int getLoanId() {
            return loanId;
        }

        public int getNumberOfItems() {
            return numberOfItems;
        }

        public int getNumberOfPage() {
            return numberOfPage;
        }
    }

    public static class Response {
        List<Investment> investments;
        int totalNumOfInvestors;

        public Response(List<Investment> investments, int totalNumOfInvestors) {
            this.investments = investments;
            this.totalNumOfInvestors = totalNumOfInvestors;
        }

        public int getTotalNumOfInvestors() {
            return totalNumOfInvestors;
        }

        public List<Investment> getInvestments() {
            return investments;
        }
    }

    public static class Failure {
        // not implemented
    }
}
