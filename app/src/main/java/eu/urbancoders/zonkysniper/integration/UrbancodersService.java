package eu.urbancoders.zonkysniper.integration;

import eu.urbancoders.zonkysniper.core.Constants;
import eu.urbancoders.zonkysniper.dataobjects.Investment;
import eu.urbancoders.zonkysniper.dataobjects.Investor;
import eu.urbancoders.zonkysniper.dataobjects.MyInvestment;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
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
public interface UrbancodersService {

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
     * Zaloguje a zkontroluje uzivatele, ktery otevrel zonkoida
     * @param investor
     * @return
     */
    @Headers({
            "Accept: application/json",
            "User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Zonkoid/48.0.2564.97 Safari/537.36",
    })
    @POST("/zonkycommander/rest/users/checkpoint")
    Call<Investor> loginCheck(
            @Body Investor investor
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
    Call<ResponseBody> logInvestment(
            @Header("username") String username,
            @Header("clientApp") Constants.ClientApps clientApp,
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

    /**
     * Registrace notifikaci od aplikace treti strany (napr. RoboZonky)
     * @param username
     * @param clientApp
     * @return
     */
    @Headers({
            "Accept: text/plain, */*",
            "User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Zonkoid/48.0.2564.97 Safari/537.36",
    })
    @POST("/zonkycommander/rest/users/thirdparties/")
    @FormUrlEncoded
    Call<String> registerUserAndThirdParty(
            @Field("username") String username,
            @Field("clientApp") String clientApp
    );

    /**
     * Odegistrace notifikaci od aplikace treti strany (napr. RoboZonky)
     *
     * @param username
     * @param clientApp
     * @return
     */
    @Headers({
            "Accept: text/plain, */*",
            "User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Zonkoid/48.0.2564.97 Safari/537.36",
    })
    @DELETE("/zonkycommander/rest/users/thirdparties/{clientApp}/users/{username}")
    Call<String> unregisterUserAndThirdParty(
            @Path("username") String username,
            @Path("clientApp") String clientApp
    );

    /**
     * Registruje nebo preregistruje uzivatelsky FCM token (volano po startu Zonkoida, pokud se token zmenil nebo je novy)
     * @param username
     * @param fcmToken
     * @return
     */
    @Headers({
            "Accept: text/plain, */*",
            "User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Zonkoid/48.0.2564.97 Safari/537.36",
    })
    @POST("/zonkycommander/rest/users/fcm-registrations/")
    @FormUrlEncoded
    Call<Void> registerUserToFcm(
            @Field("username") String username,
            @Field("fcmToken") String fcmToken
    );
}
