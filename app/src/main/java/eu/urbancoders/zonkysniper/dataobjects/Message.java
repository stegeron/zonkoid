package eu.urbancoders.zonkysniper.dataobjects;

import java.io.Serializable;
import java.util.Date;

/**
 * Zprava od Zonky
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 31.08.2016
 */
public class Message implements Serializable {
    /**
     * {
     * "date":"2016-08-22T23:48:50.720+02:00",
     * "text":"Opožděná splátka od zonky38223 právě přistála ve vaší peněžence. ",
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
     * "visited":true
     * }
     *
     *
     *
     *
     */

    Date date;
    String text;
    Link link;
    boolean visited;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }
}
