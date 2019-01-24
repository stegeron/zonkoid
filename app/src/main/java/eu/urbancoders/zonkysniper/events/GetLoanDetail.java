package eu.urbancoders.zonkysniper.events;

import eu.urbancoders.zonkysniper.dataobjects.Loan;

/**
 * Nacteni detailu pujcky, vraci Loan
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 24.08.2016
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
public class GetLoanDetail {

    public static class Request {
        int loanId;

        public Request(int loanId) {
            this.loanId = loanId;
        }

        public int getLoanId() {
            return loanId;
        }
    }

    public static class Response {
        Loan loan;

        public Response(Loan loan) {
            this.loan = loan;
        }

        public Loan getLoan() {
            return loan;
        }
    }

    public static class Failure {

    }

}
