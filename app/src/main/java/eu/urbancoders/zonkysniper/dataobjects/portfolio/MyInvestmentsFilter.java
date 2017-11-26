package eu.urbancoders.zonkysniper.dataobjects.portfolio;

import java.util.List;

/**
 * Filtrovani seznamu mych investic
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Datum: 25.11.2017
 */

public class MyInvestmentsFilter {

    /**
     * all je ["ACTIVE","SIGNED","COVERED","PAID","PAID_OFF","STOPPED"]
     */
    List<String> statuses;
    Boolean unpaidLastInstallment;  // true, pokud se maji zobrazit problematicke pujcky

//    String status; // stav investice jako takove
//    String statusSecondary; // stav investice na sekundaru
//
//    boolean canBeOfferedOnSecondary;


    public List<String> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<String> statuses) {
        this.statuses = statuses;
    }

    public Boolean getUnpaidLastInstallment() {
        return unpaidLastInstallment;
    }

    public void setUnpaidLastInstallment(Boolean unpaidLastInstallment) {
        this.unpaidLastInstallment = unpaidLastInstallment;
    }
}
