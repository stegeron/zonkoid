package eu.urbancoders.zonkysniper.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Obsluzne a pomocne metody kolem JSON de/serializace
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 04.04.2017
 */

public class JsonBuilderParser {

    private static final GsonBuilder jsonBuilder = new GsonBuilder();

    static {
        jsonBuilder.setPrettyPrinting();
        jsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        jsonBuilder.disableHtmlEscaping();
    }

    public String toJson(Object o) {
        synchronized (jsonBuilder) {
            Gson gson = jsonBuilder.create();
            return gson.toJson(o);
        }
    }

    public <T> T fromJson(String json, Class<T> clazz) {
        synchronized (jsonBuilder) {
            Gson gson = jsonBuilder.create();
            return gson.fromJson(json, clazz);
        }
    }
}
