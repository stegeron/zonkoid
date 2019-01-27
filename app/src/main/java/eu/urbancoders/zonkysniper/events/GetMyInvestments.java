package eu.urbancoders.zonkysniper.events;

import java.util.List;

import eu.urbancoders.zonkysniper.dataobjects.Investment;
import eu.urbancoders.zonkysniper.dataobjects.portfolio.MyInvestmentsFilter;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Datum: 25.11.2017
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
        int totalCount;

        public Response(List<Investment> investments, int totalCount) {
            this.investments = investments;
            this.totalCount = totalCount;
        }

        public List<Investment> getInvestments() {
            return investments;
        }

        public int getTotalCount() {
            return totalCount;
        }
    }

    public static class Failure {

    }
}
