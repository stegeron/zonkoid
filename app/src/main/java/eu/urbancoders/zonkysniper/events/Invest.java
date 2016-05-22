package eu.urbancoders.zonkysniper.events;

import eu.urbancoders.zonkysniper.dataobjects.MyInvestment;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 22.05.2016
 */
public class Invest extends AbstractEvent {

    public static class Request {
        MyInvestment investment;

        public Request(MyInvestment investment) {
            this.investment = investment;
        }

        public MyInvestment getInvestment() {
            return investment;
        }
    }

    public static class Response {
        // no response, only 200
    }

    public static class Failure {
        String error;
        String desc;

        public Failure(String error, String desc) {
            this.error = error;
            this.desc = desc;
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
