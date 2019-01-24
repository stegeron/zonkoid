package eu.urbancoders.zonkysniper.events;

import eu.urbancoders.zonkysniper.dataobjects.Restrictions;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Datum: 07.03.2018
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

public class GetInvestorRestrictions extends AbstractEvent {


    public static class Request {

    }

    public static class Response {
        Restrictions restrictions;

        public Response(Restrictions restrictions) {
            this.restrictions = restrictions;
        }

        public Restrictions getRestrictions() {
            return restrictions;
        }
    }

    public static class Failure {

    }
}
