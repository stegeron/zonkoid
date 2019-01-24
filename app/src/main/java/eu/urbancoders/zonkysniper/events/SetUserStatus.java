package eu.urbancoders.zonkysniper.events;

import eu.urbancoders.zonkysniper.dataobjects.Investor;

/**
 * Nastaveni stavu investora treba po odsouhlaseni poplatku
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Datum: 01.08.2017
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

public class SetUserStatus {

    public static class Request {
        int investorId;
        Investor.Status status;

        public Request(int investorId, Investor.Status status) {
            this.investorId = investorId;
            this.status = status;
        }

        public int getInvestorId() {
            return investorId;
        }

        public Investor.Status getStatus() {
            return status;
        }
    }

    public static class Response {

    }

    public static class Failure {

    }

}
