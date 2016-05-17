package eu.urbancoders.zonkysniper.integration;

/**
 * Handler pro chyby ze Zonky API<br/>
 * {"error":"invalid_token","error_description":"Access token expired: 8900e639-1c6d-4c0a-88d3-4251ee24fdcd"}
 *
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 16.05.2016
 */
public class ZonkyAPIError {

    String error;
    String error_description;

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
}
