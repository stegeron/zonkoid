package eu.urbancoders.zonkysniper.dataobjects;

import java.io.Serializable;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 16.05.2016
 */
public enum Rating implements Serializable {

    AAAAA("A**", "#596abe"),
    AAAA("A*", "#599ebe"),
    AAA("A++", "#3bbc70"),
    AA("A+", "#67cd75"),
    A("A", "#9acd67"),
    B("B", "#cebe5a"),
    C("C", "#d7954b"),
    D("D", "#e75637");

    String desc;
    String color;

    Rating(String desc, String color) {
        this.desc = desc;
        this.color = color;
    }

    public static String getDesc(String rating) {
        return Rating.valueOf(rating).desc;
    }

    public static String getColor(String rating) {
        return Rating.valueOf(rating).color;
    }

    public String getDesc() {
        return desc;
    }

    public String getColor() {
        return color;
    }
}
