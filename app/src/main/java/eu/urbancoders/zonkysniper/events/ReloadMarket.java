package eu.urbancoders.zonkysniper.events;

import eu.urbancoders.zonkysniper.dataobjects.Loan;

import java.util.List;

/**
 * Udalost pro nacteni trziste
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 14.05.2016
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
public class ReloadMarket extends AbstractEvent {

    public static class Request {
        boolean showCovered;
        int pageNumber;
        int numOfRows;

        public Request(boolean showCovered, int pageNumber, int numOfRows) {
            this.showCovered = showCovered;
            this.pageNumber = pageNumber;
            this.numOfRows = numOfRows;
        }

        public boolean isShowCovered() {
            return showCovered;
        }

        public int getPageNumber() {
            return pageNumber;
        }

        public int getNumOfRows() {
            return numOfRows;
        }
    }

    public static class Response {
        public List<Loan> market;

        public Response(List<Loan> market) {
            this.market = market;
        }

        public List<Loan> getMarket() {
            return market;
        }
    }

    public static class Failure {
        public String errorCode;

        public Failure(String errorCode) {
            this.errorCode = errorCode;
        }
    }
}
