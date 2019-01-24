package eu.urbancoders.zonkysniper.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Obsluzne a pomocne metody kolem JSON de/serializace
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 04.04.2017
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
