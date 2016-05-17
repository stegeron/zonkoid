package eu.urbancoders.zonkysniper.integration;

import eu.urbancoders.zonkysniper.dataobjects.AuthToken;
import eu.urbancoders.zonkysniper.dataobjects.Loan;
import eu.urbancoders.zonkysniper.dataobjects.Wallet;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

/**
 * Definice REST API, ktere nabizi Zonky
 *
 * Author: Ondrej Steger (ondrej@steger.cz)
 * Date: 14.05.2016
 */
public interface ZonkyService {

    @Headers({
            "Accept: application/json, text/plain, */*",
            "Authorization: Basic d2ViOndlYg==",
            "Referer: https://app.zonky.cz/",
            "User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.97 Safari/537.36",
    })
    @FormUrlEncoded
    @POST("/oauth/token")
    Call<AuthToken> getAuthToken(
            @Field("username") String username,
            @Field("password") String password,
            @Field("grant_type") String grant_type /** password*/,
            @Field("scope") String scope /** SCOPE_APP_WEB */
    );

    @Headers({
            "Accept: application/json, text/plain, */*",
            "Referer: https://app.zonky.cz/",
            "User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.97 Safari/537.36",
    })
    @GET("/loans/marketplace?remainingInvestment__gt=0")
    Call<List<Loan>> getNewLoansOnMarket(
            @Header("Authorization") String token
    );


    @Headers({
            "Accept: application/json, text/plain, */*",
            "Referer: https://app.zonky.cz/",
            "User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.97 Safari/537.36",
    })
    @GET("/users/me/wallet")
    Call<Wallet> getWallet(
            @Header("Authorization") String token
    );

}
