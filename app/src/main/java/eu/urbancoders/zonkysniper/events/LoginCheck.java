package eu.urbancoders.zonkysniper.events;

import eu.urbancoders.zonkysniper.dataobjects.Investor;

/**
 * Provede identifikaci uzivatele pri startu, zaloguje na server a vyhledove mozna ziska nejake informace
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 04.07.2016
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
