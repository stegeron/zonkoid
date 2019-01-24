package eu.urbancoders.zonkysniper.dataobjects;

import java.io.Serializable;

/**
 * Handler pro chyby ze Zonky API<br/>
 * {"error":"invalid_token","error_description":"Access token expired: 8900e639-1c6d-4c0a-88d3-4251ee24fdcd"}
 *
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 16.05.2016
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
public class ZonkyAPIError implements Serializable {

    String error;
    String error_description;
    String uuid;

    public ZonkyAPIError() {
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getError_description() {
        return error_description;
    }

    public void setError_description(String error_description) {
        this.error_description = error_description;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
