package eu.urbancoders.zonkysniper.events;

import eu.urbancoders.zonkysniper.dataobjects.MyInvestment;

/**
 * Dodatecne investovani
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 22.05.2016
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
public class InvestAdditional extends AbstractEvent {

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
