package eu.urbancoders.zonkysniper.dataobjects;

/**
 * Stav splatek za investice
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Datum: 26.11.2017
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

public enum PaymentStatus {

    OK("#4fbf5b"),
    DUE("#f2d000"),
    SOLD("#81e3fb"),                       // prodano na sekundaru
    PAID("#2d919c"),
    PAID_OFF("#979797"),            // zesplatnena
    CANCELED("#979797"),
    COVERED("#E872E7"),
    NOT_COVERED("#b372e8"),
    WRITTEN_OFF("#ac6a00");


    String backgroundColor;

    PaymentStatus(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }
}
