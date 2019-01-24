package eu.urbancoders.zonkysniper.dataobjects;

import java.io.Serializable;

/**
 * Link pouzity ve zpravach ze Zonky (Message)
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 01.12.2016
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

public class Link implements Serializable {

    /**
     * "link":{
     *      "type":"LOAN_PREPAYMENT",
     *      "params":{
     *          "loanId":55143
     *          }
     *      },
     * "link":{
     *      "type":"BORROWER_HEAL",
     *      "params":{
     *              "loanId":42431
     *              }
     *        },
     */


    String type;
    LinkParams params;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LinkParams getParams() {
        return params;
    }

    public void setParams(LinkParams params) {
        this.params = params;
    }
}
