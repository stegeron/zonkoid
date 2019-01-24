package eu.urbancoders.zonkysniper.dataobjects;

import java.io.Serializable;

/**
 * Konfiguracni polozka obsahuje typicky hodnoty z databaze
 *
 * Created by osteger on 08.09.2017.
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
