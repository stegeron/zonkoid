package eu.urbancoders.zonkysniper.dataobjects;

import java.util.Date;

/**
 * Zprava od Zonky
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 31.08.2016
 */
public class Message {
    /**
     * {
     * "date":"2016-08-22T23:48:50.720+02:00",
     * "text":"Opožděná splátka od zonky38223 právě přistála ve vaší peněžence. ",
     * "link":{
     *      "id":28288,
     *      "type":null
     *      },
     * "visited":true
     * }
     */

    Date date;
    String text;
    // link zatim nepotrebuju, nevim, na co je
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
}
