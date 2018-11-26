package eu.urbancoders.zonkysniper.dataobjects;

import android.util.Log;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

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
