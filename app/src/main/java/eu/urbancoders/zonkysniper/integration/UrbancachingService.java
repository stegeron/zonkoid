package eu.urbancoders.zonkysniper.integration;

import eu.urbancoders.zonkysniper.dataobjects.Investment;
import eu.urbancoders.zonkysniper.dataobjects.MyInvestment;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;

/**
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 04.07.2016
 */
public interface UrbancachingService {

    /**
     *
     * @param username
     * @param description
     * @param logs
     * @param timestamp ve formatu yyyy-mm-dd hh:mm:ss
     * @return
     */
    @POST("/zonkycommander/rest/message/bugreport")
    @Headers({
            "Accept: text/plain"
    })
    @FormUrlEncoded
    Call<String> sendBugreport(
            @Field("username") String username,
            @Field("description") String description,
            @Field("logs") String logs,
            @Field("timestamp") String timestamp
    );


    /**
     * BETATESTOVANI - ZADOST O BETA UCET
     */

    /**
     * Overeni betatestera
     * @deprecated predelat na metodu zapisujici statistiky
     */
    @Deprecated
    @GET("/zonkycommander/rest/admin/betatesters/{username}")
    Call<String> isBetatester(
            @Path("username") String username
            );

    /**
     * Zadost o registraci betatestera
     * @deprecated
     */
    @Deprecated
    @POST("/zonkycommander/rest/admin/betatesters")
    @Headers({
            "Accept: text/plain, */*"
    })
    @FormUrlEncoded
    Call<String> requestBetaRegistration(
            @Field("username") String username
    );

    /**
     * Logovani zainvestovane pujcky
     * @param username
     * @param myInvestment
     * @return
     */
    @Headers({
            "Accept: text/plain, */*",
            "User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Zonkoid/48.0.2564.97 Safari/537.36",
    })
    @POST("/zonkycommander/rest/investments")
    Call<Void> logInvestment(
            @Header("username") String username,
            @Body MyInvestment myInvestment
    );

    /**
     * Vrati seznam investic pres Zonkoida do dane pujcky
     * @param loanId
     * @return
     */
    @Headers({
            "Accept: application/json, */*",
            "User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Zonkoid/48.0.2564.97 Safari/537.36",
    })
    @GET("/zonkycommander/rest/investments/loans/{loanId}")
    Call<List<Investment>> getInvestmentsByZonkoid(
            @Path("loanId") int loanId
    );

}
