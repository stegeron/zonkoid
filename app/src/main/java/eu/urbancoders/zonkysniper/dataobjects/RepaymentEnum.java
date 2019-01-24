package eu.urbancoders.zonkysniper.dataobjects;

import java.io.Serializable;

/**
 * Splatnosti
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 16.06.2016
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
public enum RepaymentEnum implements Serializable {

    R12(0, 12),
    R24(13, 24),
    R36(25, 36),
    R48(37, 48),
    R60(49, 60),
    R72(61, 72),
    R84(73, 84);

    public int monthsTo, monthsFrom;

    RepaymentEnum(int monthsFrom, int monthsTo) {
        this.monthsFrom = monthsFrom;
        this.monthsTo = monthsTo;
    }
}
