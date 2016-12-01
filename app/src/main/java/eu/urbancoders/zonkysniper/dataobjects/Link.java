package eu.urbancoders.zonkysniper.dataobjects;

/**
 * Link pouzity ve zpravach ze Zonky (Message)
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 01.12.2016
 */

public class Link {
    int id;
    String type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
