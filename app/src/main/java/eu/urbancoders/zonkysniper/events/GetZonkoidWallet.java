package eu.urbancoders.zonkysniper.events;

import eu.urbancoders.zonkysniper.dataobjects.ZonkoidWallet;

/**
 * Volani data pro Zonkoid Wallet
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 21.06.2017
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

public class GetZonkoidWallet extends AbstractEvent {

    public static class Request {
        int investorId;

        public Request(int investorId) {
            this.investorId = investorId;
        }

        public int getInvestorId() {
            return investorId;
        }
    }

    public static class Response {
        ZonkoidWallet zonkoidWallet;

        public Response(ZonkoidWallet zonkoidWallet) {
            this.zonkoidWallet = zonkoidWallet;
        }

        public ZonkoidWallet getZonkoidWallet() {
            return zonkoidWallet;
        }
    }

    public static class Failure {

    }

}
