package eu.urbancoders.zonkysniper.events;

import eu.urbancoders.zonkysniper.dataobjects.MyInvestment;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 22.05.2016
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
