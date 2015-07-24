package im.youtiao.android_client.rest;


import im.youtiao.android_client.model.RevokeToken;
import im.youtiao.android_client.model.Token;
import retrofit.http.Body;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

public interface OAuthApi {
    @POST("/oauth/token")
    Observable<Token> getTokenSync(@Query("grant_type") String grantType, @Query("username") String username, @Query("password") String password);

    @POST("/oauth/revoke")
    Observable<Token> revokeToken(@Body RevokeToken token);

//    @POST("/oauth/revoke")
//    Observable<Token> revokeToken();
}
