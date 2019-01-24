package eu.urbancoders.zonkysniper.dataobjects;

/**
 * Pro vytvoreni JSON requestu na reset hesla
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 13.09.2016
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
