package eu.urbancoders.zonkysniper.events;

/**
 * Na zaklade teto udalosti je potreba zobrazit vyzvu k aktualizaci
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 14.05.2016
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
public class UpdateMandatoryWarning {

    public static class Request {
        String version;

        public Request(String version) {
            this.version = version;
        }

        public String getVersion() {
            return version;
        }
    }

    public static class Response {
        // no response needed
    }

    public static class Failure {

    }
}
