package eu.urbancoders.zonkysniper.dataobjects;

/**
 * Zonky:
 "access_token" : "f5ac25cb-06e5-4e43-97d2-e1b6c7b3258e",
 "token_type" : "bearer",
 "refresh_token" : "3860824c-463f-4aa1-95e3-2ee71c85330a",
 "expires_in" : 300,
 "scope" : "SCOPE_APP_WEB"
 *
 * Google:
 *
 * "access_token" : "ya29.GltyBGFerTLHIN_1WkAYHJ......08DcxrTkWoDIrEmsSM0b7fL3ZLid2hnT8i",
 * "token_type" : "Bearer",
 * "expires_in" : 3600
 *
 * Pouzito pro Zonky i pro Google!
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 14.05.2016
 */
public class AuthToken {

    String access_token;
    String token_type;
    String refresh_token;
    long expires_in;
    long expires_at; // dopocitany cas, kdy token vyprsi
    String scope;

    public long getExpires_at() {
        return expires_at;
    }

    public void setExpires_at(long expires_at) {
        this.expires_at = expires_at;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public long getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(long expires_in) {
        this.expires_in = expires_in;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}

