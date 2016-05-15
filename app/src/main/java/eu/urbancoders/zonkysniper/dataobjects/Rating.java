package eu.urbancoders.zonkysniper.dataobjects;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 16.05.2016
 */
public enum Rating {

    AAAAA,
    AAAA,
    AAA,
    AA,
    A,
    B,
    C,
    D;

    String desc;

    public String getType() {
        return this.name();
    }
}
