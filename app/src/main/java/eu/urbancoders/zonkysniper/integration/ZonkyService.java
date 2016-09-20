package eu.urbancoders.zonkysniper.integration;

import eu.urbancoders.zonkysniper.dataobjects.AuthToken;
import eu.urbancoders.zonkysniper.dataobjects.Investment;
import eu.urbancoders.zonkysniper.dataobjects.Loan;
import eu.urbancoders.zonkysniper.dataobjects.Message;
import eu.urbancoders.zonkysniper.dataobjects.MyInvestment;
import eu.urbancoders.zonkysniper.dataobjects.PasswordResetter;
import eu.urbancoders.zonkysniper.dataobjects.Question;
import eu.urbancoders.zonkysniper.dataobjects.Wallet;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

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
            "User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Zonkoid/48.0.2564.97 Safari/537.36",
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
            "Authorization: Basic d2ViOndlYg==",
            "Referer: https://app.zonky.cz/",
            "User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Zonkoid/48.0.2564.97 Safari/537.36",
    })
    @FormUrlEncoded
    @POST("/oauth/token")
    Call<AuthToken> refreshAuthToken(
            @Field("refresh_token") String refreshToken,
            @Field("grant_type") String grant_type /** password*/,
            @Field("scope") String scope /** SCOPE_APP_WEB */
    );

    /**
     * Vraci covered i uncovered! Je potreba odfiltrovat.
     * @param numberOfItems
     * @param orderBy
     * @return
     */
    @Headers({
            "Accept: application/json, text/plain, */*",
            "Referer: https://app.zonky.cz/",
            "User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Zonkoid/48.0.2564.97 Safari/537.36",
    })
    @GET("/loans/marketplace?remainingInvestment__gt=0")
    Call<List<Loan>> getNewLoansOnMarket(
            @Header("X-Size") int numberOfItems,
            @Header("X-Order") String orderBy
//            @Header("Authorization") String token
    );


    @Headers({
            "Accept: application/json, text/plain, */*",
            "Referer: https://app.zonky.cz/",
            "User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Zonkoid/48.0.2564.97 Safari/537.36",
    })
    @GET("/users/me/wallet")
    Call<Wallet> getWallet(
            @Header("Authorization") String token
    );

    @Headers({
            "Accept: application/json, text/plain, */*",
            "Referer: https://app.zonky.cz/",
            "User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Zonkoid/48.0.2564.97 Safari/537.36",
    })
    @GET("/users/me/logout")
    Call<String> logout(
            @Header("Authorization") String token
    );


    /**
     * POST /users/me/investment HTTP/1.1http://urbancaching.eu/zonkycommander/rest/admin/betatesters/
     Host: api.zonky.cz
     Connection: keep-alive
     Content-Length: 29
     Accept: application/json, text/plain,

    Origin:https://app.zonky.cz
    User-Agent:Mozilla/5.0(
    Windows NT
    6.1;WOW64)AppleWebKit/537.36(KHTML,
    like Gecko
    )Chrome/47.0.2526.106Safari/537.36
    Authorization:Bearer 695761fd-be81-4685-9e73-adf87efffdc9
    Content-Type:application/json;charset=UTF-8
    Referer:https://app.zonky.cz/
    Accept-Encoding:gzip,deflate
    Accept-Language:cs-CZ,cs;q=0.8


    Body je

    {
        "loanId":28994, "amount":200
    }
     */

    @Headers({
            "Accept: application/json, text/plain, */*",
            "Referer: https://app.zonky.cz/",
            "User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Zonkoid/48.0.2564.97 Safari/537.36",
    })
    @POST("/marketplace/investment")
    Call<String> invest(
            @Header("Authorization") String token,
            @Body MyInvestment myInvestment
    );

    /**
     * Detail pujcky
     * @param loanId
     * @return
     */
    @Headers({
            "Accept: application/json, text/plain, */*",
            "Referer: https://app.zonky.cz/",
            "User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Zonkoid/48.0.2564.97 Safari/537.36",
    })
    @GET("/loans/{loanId}")
    Call<Loan> getLoanDetail(
            @Path("loanId") int loanId
    );

    /**
     * Seznam investoru a investic do pujcky
     * @param loanId
     * @return
     */
    @Headers({
            "Accept: application/json, text/plain, */*",
            "Referer: https://app.zonky.cz/",
            "User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Zonkoid/48.0.2564.97 Safari/537.36",
    })
    @GET("/loans/{loanId}/investments")
    Call<List<Investment>> getInvestments(
            @Header("Authorization") String token,
            @Path("loanId") int loanId
    );

    /**
     * Zobrazi seznam zprav od Zonky
     * @return
     */
    @Headers({
            "Accept: application/json, text/plain, */*",
            "Referer: https://app.zonky.cz/",
            "User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Zonkoid/48.0.2564.97 Safari/537.36",
    })
    @GET("/users/me/notifications")
    Call<List<Message>> getMessages(
            @Header("Authorization") String token,
            @Header("X-Size") int numberOfMessages
    );

    /**
     * Reset hesla
     * @param passwordResetter
     * @return
     */
    @Headers({
            "Accept: application/json, text/plain, */*",
            "Referer: https://app.zonky.cz/",
            "User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Zonkoid/48.0.2564.97 Safari/537.36",
            "Host: api.zonky.cz",
            "Origin: https://app.zonky.cz"
    })
    @PUT("/users/reset-password")
    Call<Void> passwordReset(
            @Body PasswordResetter passwordResetter
    );

    /**
     * Zobrazi seznam dotazu u pujcky
     *
     * @return
     */
    @Headers({
            "Accept: application/json, text/plain, */*",
            "Referer: https://app.zonky.cz/",
            "User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Zonkoid/48.0.2564.97 Safari/537.36",
            "X-Order:-timeCreated"
    })
    @GET("/loans/{loanId}/questions")
    Call<List<Question>> getQuestions(
            @Header("Authorization") String token,
            @Header("X-Size") int numberOfQuestions,
            @Path("loanId") int loanId
    );
}
