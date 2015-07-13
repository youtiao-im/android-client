package im.youtiao.android_client.rest;


import im.youtiao.android_client.model.Token;
import im.youtiao.android_client.model.User;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

public interface LoginApi {
    //OAuth
    @POST("/api/v1/users.sign_up")
    Observable<User> signUpUser(@Query("email") String email, @Query("name") String name, @Query("password") String password);

    @POST("/oauth/token")
    Observable<Token> getTokenSync(@Query("grant_type") String grantType, @Query("username") String username, @Query("password") String password);
}
