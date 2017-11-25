package eu.urbancoders.zonkysniper.events;

import java.util.List;

import eu.urbancoders.zonkysniper.dataobjects.Investment;
import eu.urbancoders.zonkysniper.dataobjects.portfolio.MyInvestmentsFilter;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Datum: 25.11.2017
 */

public class GetMyInvestments {

    public static class Request {
        MyInvestmentsFilter filter;
        int numberOfRows;
        int pageNumber;

        public Request(MyInvestmentsFilter filter, int numberOfRows, int pageNumber) {
            this.filter = filter;
            this.numberOfRows = numberOfRows;
            this.pageNumber = pageNumber;
        }

        public MyInvestmentsFilter getFilter() {
            return filter;
        }

        public int getNumberOfRows() {
            return numberOfRows;
        }

        public int getPageNumber() {
            return pageNumber;
        }
    }

    public static class Response {
        List<Investment> investments;

        public Response(List<Investment> investments) {
            this.investments = investments;
        }

        public List<Investment> getInvestments() {
            return investments;
        }
    }

    public static class Failure {

    }
}
