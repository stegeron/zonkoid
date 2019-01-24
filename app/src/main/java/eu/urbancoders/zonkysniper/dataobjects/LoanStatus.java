package eu.urbancoders.zonkysniper.dataobjects;

import java.util.ArrayList;
import java.util.List;

/**
 * Stav pujcky, napr. "ACTIVE","SIGNED","COVERED","PAID","PAID_OFF","STOPPED"
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

public enum LoanStatus {

    ACTIVE,
    SIGNED,
    COVERED,
    PAID,
    SOLD,
    PAID_OFF,
    STOPPED;

    public static List<String> names() {
        List<String> names = new ArrayList<>();
        for(LoanStatus item : LoanStatus.values()) {
            names.add("\"" + item.name() + "\"");
        }
        return names;
    }
}
