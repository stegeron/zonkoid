package eu.urbancoders.zonkysniper.events;

import eu.urbancoders.zonkysniper.dataobjects.portfolio.Portfolio;

/**
 * Nacteni portfolia investora ze Zonky
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 17.03.2017
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

public class GetPortfolio {

    public static class Request {
        // no params
    }

    public static class Response {
        Portfolio portfolio;

        public Response(Portfolio portfolio) {
            this.portfolio = portfolio;
        }

        public Portfolio getPortfolio() {
            return portfolio;
        }
    }

    public static class Failure {

    }
}
