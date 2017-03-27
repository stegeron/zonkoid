package eu.urbancoders.zonkysniper.dataobjects;

import java.io.Serializable;
import java.util.Map;

/**
 * Link pouzity ve zpravach ze Zonky (Message)
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 01.12.2016
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
