package eu.urbancoders.zonkysniper.events;

import eu.urbancoders.zonkysniper.dataobjects.MyInvestment;

/**
 * Autoinvestovani
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 22.05.2016
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
public class InvestAuto extends AbstractEvent {

    public static class Request {
        MyInvestment investment;
        String loanHeader;

        public Request(MyInvestment investment, String loanHeader) {
            this.investment = investment;
            this.loanHeader = loanHeader;
        }

        public MyInvestment getInvestment() {
            return investment;
        }

        public String getLoanHeader() {
            return loanHeader;
        }
    }

    public static class Response {
        MyInvestment investment;
        String loanHeader;

        public Response(MyInvestment investment, String loanHeader) {
            this.investment = investment;
            this.loanHeader = loanHeader;
        }

        public MyInvestment getInvestment() {
            return investment;
        }

        public String getLoanHeader() {
            return loanHeader;
        }
    }

    public static class Failure {
        String error;
        String desc;
        int loanId;

        public Failure(String error, String desc, int loanId) {
            this.error = error;
            this.desc = desc;
            this.loanId = loanId;
        }

        public int getLoanId() {
            return loanId;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }

}
