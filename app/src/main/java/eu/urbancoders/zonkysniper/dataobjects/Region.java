package eu.urbancoders.zonkysniper.dataobjects;

/**
 * Kraje CR
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Copyright 2019
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
public enum Region {
    
    region_1("Hlavní město Praha", 1),
    region_2("Středočeský kraj", 2),
    region_3("Jihočeský kraj", 3),
    region_4("Plzeňský kraj", 4),
    region_5("Karlovarský kraj", 5),
    region_6("Ústecký kraj", 6),
    region_7("Liberecký kraj", 7),
    region_8("Královéhradecký kraj", 8),
    region_9("Pardubický kraj", 9),
    region_10("Kraj Vysočina", 10),
    region_11("Jihomoravský kraj", 11),
    region_12("Olomoucký kraj", 12),
    region_13("Moravskoslezský kraj", 13),
    region_14("Zlínský kraj", 14);

    String name;
    int regionKey;

    Region(String name, int regionKey) {
        this.name = name;
        this.regionKey = regionKey;
    }
}
