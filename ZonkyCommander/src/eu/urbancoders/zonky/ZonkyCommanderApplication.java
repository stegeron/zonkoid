package eu.urbancoders.zonky;

import cz.steger.urbancaching.server.filters.CharsetResponseFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Starter JAX-RS aplikace, nastaveni resourcu, singleton cachi apod.
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 12/25/14
 */
public class ZonkyCommanderApplication extends ResourceConfig {

    private static final Logger log = LoggerFactory.getLogger(UrbanCachingApplication.class);

    public ZonkyCommanderApplication() {
        super(
                AdministrationResource.class,
                PoisResource.class,
                JacksonFeature.class,
                POJOMapperProvider.class,
                CharsetResponseFilter.class,
                MultiPartFeature.class

        );
    }
}
