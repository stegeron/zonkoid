package eu.urbancoders.zonkysniper.dataobjects;

import java.io.Serializable;

/**
 * Parametry linku ve Zpravach ze Zonky
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 15.02.2017
 */

public class LinkParams implements Serializable {

    Integer loanId;

    public Integer getLoanId() {
        return loanId;
    }

    public void setLoanId(Integer loanId) {
        this.loanId = loanId;
    }
}
