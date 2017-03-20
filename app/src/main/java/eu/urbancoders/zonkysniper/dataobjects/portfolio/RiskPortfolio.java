package eu.urbancoders.zonkysniper.dataobjects.portfolio;

import eu.urbancoders.zonkysniper.dataobjects.Rating;

/**
 * Počet aktivních investic per riziková kategorie
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 17.03.2017
 */

public class RiskPortfolio {

    /**
     * "riskPortfolio":[
     *      {
     *          "unpaid":323,
     *          "paid":77,
     *          "due":0,
     *          "rating":"AAAAA",
     *          "totalAmount":400
     *      },
     *      {
     *          "unpaid":1295,
     *          "paid":105,
     *          "due":0,
     *          "rating":"AAAA",
     *          "totalAmount":1400
     *      },
     *      ......
     *      {
     *          "unpaid":2511,
     *          "paid":289,
     *          "due":2,
     *          "rating":"D",
     *          "totalAmount":2800
     *      }
     * ]
     * }
     */

    Double unpaid;
    Double paid;
    Double due;
    Rating rating;
    Double totalAmount;


    public Double getUnpaid() {
        return unpaid;
    }

    public void setUnpaid(Double unpaid) {
        this.unpaid = unpaid;
    }

    public Double getPaid() {
        return paid;
    }

    public void setPaid(Double paid) {
        this.paid = paid;
    }

    public Double getDue() {
        return due;
    }

    public void setDue(Double due) {
        this.due = due;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
