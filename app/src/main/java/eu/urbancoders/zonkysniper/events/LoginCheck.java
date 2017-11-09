package eu.urbancoders.zonkysniper.events;

import eu.urbancoders.zonkysniper.dataobjects.Investor;

/**
 * Provede identifikaci uzivatele pri startu, zaloguje na server a vyhledove mozna ziska nejake informace
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 04.07.2016
 *
 */
public class LoginCheck {

    public static class Request {
        Investor investor;

        public Request(Investor investor) {
            this.investor = investor;
        }

        public Investor getInvestor() {
            return investor;
        }
    }

    public static class Response {
        Investor investor;

        public Response(Investor investor) {
            this.investor = investor;
        }

        public Investor getInvestor() {
            return investor;
        }
    }

    public static class Failure {

    }


}
