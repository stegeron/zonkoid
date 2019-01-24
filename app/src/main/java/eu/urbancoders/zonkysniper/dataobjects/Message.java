package eu.urbancoders.zonkysniper.dataobjects;

import java.io.Serializable;
import java.util.Date;

/**
 * Zprava od Zonky
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 31.08.2016
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
