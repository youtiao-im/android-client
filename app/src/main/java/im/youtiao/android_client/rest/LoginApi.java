package im.youtiao.android_client.rest;


import im.youtiao.android_client.rest.responses.TokenResponse;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

public interface LoginApi {
    //OAuth
    @POST("/oauth/token")
    TokenResponse getToken(@Query("grant_type") String grantType, @Query("username") String username, @Query("password") String password);
}
