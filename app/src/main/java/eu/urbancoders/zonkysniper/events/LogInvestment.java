package eu.urbancoders.zonkysniper.events;

import eu.urbancoders.zonkysniper.dataobjects.MyInvestment;

/**
 * Zaloguje investici na zonkycommander
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 26.10.2016
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
