package eu.urbancoders.zonkysniper.integration;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 04.07.2016
 */
public interface UrbancachingService {

    /**
     * BETATESTOVANI - ZADOST O BETA UCET
     */

    /**
     * Overeni betatestera
     */
    @GET("/zonkycommander/rest/admin/betatesters/{username}")
    Call<String> isBetatester(
            @Path("username") String username
            );

    /**
     * Zadost o registraci betatestera
     */
    @POST("/zonkycommander/rest/admin/betatesters")
    @Headers({
            "Accept: text/plain, */*"
    })
    @FormUrlEncoded
    Call<String> requestBetaRegistration(
            @Field("username") String username
    );

}
