package eu.urbancoders.zonkysniper.dataobjects;

import java.io.Serializable;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 23.08.2016
 */
public class Photo implements Serializable {
    String name;
    String url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
