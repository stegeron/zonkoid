package eu.urbancoders.zonkysniper.integration;

import eu.urbancoders.zonkysniper.BuildConfig;
import eu.urbancoders.zonkysniper.core.Constants;
import eu.urbancoders.zonkysniper.dataobjects.Investment;
import eu.urbancoders.zonkysniper.dataobjects.Investor;
import eu.urbancoders.zonkysniper.dataobjects.MyInvestment;
import eu.urbancoders.zonkysniper.dataobjects.WalletTransaction;
import eu.urbancoders.zonkysniper.dataobjects.ZonkoidWallet;
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
            "Accept: text/plain",
            "User-Agent: Zonkoid/" + BuildConfig.VERSION_NAME + "/" + BuildConfig.VERSION_CODE+" ",
    })
    @FormUrlEncoded
    Call<String> sendBugreport(
            @Field("username") String username,
            @Field("description") String description,
            @Field("logs") String logs,
            @Field("timestamp") String timestamp,
            @Field("clientApp") Constants.ClientApps clientApp
            );

    /**
     * Zaloguje a zkontroluje uzivatele, ktery otevrel zonkoida
     * @param investor
     * @return
     */
    @Headers({
            "Accept: application/json",
            "User-Agent: Zonkoid/" + BuildConfig.VERSION_NAME + "/" + BuildConfig.VERSION_CODE+" ",
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
            "User-Agent: Zonkoid/" + BuildConfig.VERSION_NAME + "/" + BuildConfig.VERSION_CODE+" ",
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
            "User-Agent: Zonkoid/" + BuildConfig.VERSION_NAME + "/" + BuildConfig.VERSION_CODE+" ",
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
            "User-Agent: Zonkoid/" + BuildConfig.VERSION_NAME + "/" + BuildConfig.VERSION_CODE+" ",
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
            "User-Agent: Zonkoid/" + BuildConfig.VERSION_NAME + "/" + BuildConfig.VERSION_CODE+" ",
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
            "User-Agent: Zonkoid/" + BuildConfig.VERSION_NAME + "/" + BuildConfig.VERSION_CODE+" ",
    })
    @POST("/zonkycommander/rest/users/fcm-registrations/")
    @FormUrlEncoded
    Call<Void> registerUserToFcm(
            @Field("username") String username,
            @Field("fcmToken") String fcmToken
    );

    /**
     * Vrati data pro Zonkoid wallet
     *
     * @param investorId
     * @return
     */
    @Headers({
            "Accept: application/json, */*",
            "User-Agent: Zonkoid/" + BuildConfig.VERSION_NAME + "/" + BuildConfig.VERSION_CODE + " ",
    })
    @GET("/zonkycommander/rest/users/{investorId}/wallet")
    Call<ZonkoidWallet> getZonkoidWallet(
            @Path("investorId") int investorId
    );

    /**
     * Zauctuje platbu provedenou pres Google IAP a snizi dluh za investice pres Zonkoida
     * @param investorId id investora
     * @param clientApp ZONKOID
     * @param purchase platba vcetne tokenu a SKU
     * @return
     */
    @Headers({
            "Accept: text/plain, */*",
            "User-Agent: Zonkoid/" + BuildConfig.VERSION_NAME + "/" + BuildConfig.VERSION_CODE+" ",
    })
    @POST("/zonkycommander/rest/accountant/purchase/")
    Call<String> bookPurchase(
            @Header("investorId") int investorId,
            @Header("clientApp") Constants.ClientApps clientApp,
            @Body WalletTransaction purchase
    );
}
