package eu.urbancoders.zonkysniper.events;

import eu.urbancoders.zonkysniper.dataobjects.Investor;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 18.05.2016
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
public class GetInvestor extends AbstractEvent {

    public static class Request {

    }

    public static class Response {
        Investor investor;

        public Response(Investor investor) {
            this.investor = investor;
        }

        public Investor getInvestor() {
            return investor;
        }

        public void setInvestor(Investor investor) {
            this.investor = investor;
        }
    }

    public static class Failure {

    }
}
