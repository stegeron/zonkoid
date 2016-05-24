package eu.urbancoders.zonky;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cz.steger.urbancaching.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Spolecny predek pro vsechny resources
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 12/25/14
 */
public class AbstractResource {

    public static final Logger log = LoggerFactory.getLogger(AbstractResource.class);

    private static GsonBuilder jsonBuilder = new GsonBuilder();

    static {
        jsonBuilder.setPrettyPrinting();
        jsonBuilder.setDateFormat(Constants.Formats.DATE_FORMAT_ISO8601);
        jsonBuilder.disableHtmlEscaping();
    }

    public String toJson(Object o) {
        synchronized (jsonBuilder) {
            Gson gson = jsonBuilder.create();
            return gson.toJson(o);
        }
    }

}
