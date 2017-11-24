package eu.urbancoders.zonkysniper.dataobjects;

import java.io.Serializable;

/**
 * Konfiguracni polozka obsahuje typicky hodnoty z databaze
 *
 * Created by osteger on 08.09.2017.
 */
public class ConfigurationItem implements Serializable {

    String key;
    String value;

    public ConfigurationItem(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public ConfigurationItem() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
