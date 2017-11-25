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

    String status; // stav investice jako takove
    String statusSecondary; // stav investice na sekundaru

    boolean canBeOfferedOnSecondary;

    public List<String> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<String> statuses) {
        this.statuses = statuses;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusSecondary() {
        return statusSecondary;
    }

    public void setStatusSecondary(String statusSecondary) {
        this.statusSecondary = statusSecondary;
    }

    public boolean isCanBeOfferedOnSecondary() {
        return canBeOfferedOnSecondary;
    }

    public void setCanBeOfferedOnSecondary(boolean canBeOfferedOnSecondary) {
        this.canBeOfferedOnSecondary = canBeOfferedOnSecondary;
    }
}
