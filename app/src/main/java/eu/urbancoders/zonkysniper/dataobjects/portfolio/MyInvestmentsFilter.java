package eu.urbancoders.zonkysniper.dataobjects.portfolio;

import java.util.List;

/**
 * Filtrovani seznamu mych investic
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Datum: 25.11.2017
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

public class MyInvestmentsFilter {

    /**
     * all je ["ACTIVE","SIGNED","COVERED","PAID","PAID_OFF","STOPPED"]
     */
    List<String> statuses;
    Boolean unpaidLastInstallment;  // true, pokud se maji zobrazit problematicke pujcky
    private String stausEq;    // ACTIVE, SOLD

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

    public void setStausEq(String stausEq) {
        this.stausEq = stausEq;
    }

    public String getStausEq() {
        return stausEq;
    }
}
