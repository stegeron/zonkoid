package eu.urbancoders.zonkysniper.dataobjects;

/**
 * Pro vytvoreni JSON requestu na reset hesla
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 13.09.2016
 */
public class PasswordResetter {

    String email;
    String captcha_response;

    public PasswordResetter(String email, String captcha_response) {
        this.email = email;
        this.captcha_response = captcha_response;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCaptcha_response() {
        return captcha_response;
    }

    public void setCaptcha_response(String captcha_response) {
        this.captcha_response = captcha_response;
    }
}
