package eu.urbancoders.zonkysniper.events;

import eu.urbancoders.zonkysniper.dataobjects.MyInvestment;

/**
 * Zaloguje investici na zonkycommander
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 26.10.2016
 */

public class LogInvestment {

    public static class Request {
        MyInvestment myInvestment;
        String username;

        public Request(MyInvestment myInvestment, String username) {
            this.myInvestment = myInvestment;
            this.username = username;
        }

        public MyInvestment getMyInvestment() {
            return myInvestment;
        }

        public String getUsername() {
            return username;
        }
    }

    public static class Response {

    }

    public static class Failure {

    }

}
