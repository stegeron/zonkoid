package eu.urbancoders.zonkysniper.dataobjects;

import android.util.Log;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

/**
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
public enum IncomeType {

    EMPLOYMENT("Zaměstnanec", null),
    ENTREPRENEUR("Podnikatel", null),
    SELF_EMPLOYMENT("OSVČ", null),
    PENSION("Důchod", null),
    MATERNITY_LEAVE("Rodičovská dovolená", null),
    STUDENT("Student", null),
    UNEMPLOYED("Bez zaměstnání", null),
    LIBERAL_PROFESSION("Svobodné povolání", null),
    OTHERS_MAIN("Jiný příjem", null),
    NEXT_WORK("Brigády", OTHERS_MAIN),
    RENT("Pronájem", OTHERS_MAIN),
    ART_ACTION("Umělecká činnost", OTHERS_MAIN),
    ALIMONY("Alimenty", OTHERS_MAIN),
    NEXT_PENSION("Vdovský důchod", OTHERS_MAIN),
    NEXT_OTHERS("Jiný", OTHERS_MAIN);

    String descCZ;
    IncomeType parent;

    IncomeType(String descCZ, @Nullable IncomeType parent) {
        this.descCZ = descCZ;
        this.parent = parent;
    }

    public static Set<IncomeType> getParentIncomeTypes() {
        Set<IncomeType> result = new HashSet<IncomeType>();

        for (IncomeType incomeType : values()) {
            if(incomeType.getParent() == null) {
                result.add(incomeType);
            }
        }

        return result;
    }

    public static IncomeType parse(String incomeType) {

        IncomeType incomeTypeEnum = null;

        try {
            incomeTypeEnum = IncomeType.valueOf(incomeType);
        } catch (IllegalArgumentException iae) {
            Log.e("IncomeTypeEnum", "IncomeType not in enum, maybe a new one?: "+incomeType);
        }

        return incomeTypeEnum;
    }

    public IncomeType getParent() {
        return parent;
    }

    public String getDescCZ() {
        return descCZ;
    }
}
