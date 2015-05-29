package im.youtiao.android_client.rest;

import java.util.List;

import im.youtiao.android_client.rest.responses.ChannelResponse;
import im.youtiao.android_client.rest.responses.ChannelUserMembershipResponse;
import im.youtiao.android_client.rest.responses.CommentResponse;
import im.youtiao.android_client.rest.responses.FeedResponse;
import im.youtiao.android_client.rest.responses.MarkResponse;
import im.youtiao.android_client.rest.responses.StarResponse;
import im.youtiao.android_client.rest.responses.TokenResponse;
import im.youtiao.android_client.rest.responses.UserChannelMembershipResponse;
import im.youtiao.android_client.rest.responses.UserFeedMarkResponse;
import im.youtiao.android_client.rest.responses.UserFeedStarResponse;
import im.youtiao.android_client.rest.responses.UserResponse;
import retrofit.http.GET;
import retrofit.http.PATCH;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface RemoteApi {
    final String PREFIX = "/api/v1";

    //OAuth
    @POST("/oauth/token")
    Observable<TokenResponse> getToken(@Query("grant_type") String grantType, @Query("username") String username, @Query("password") String password);

    // users
    @GET(PREFIX + "/user")
    Observable<UserResponse> getAuthenticatedUser();

    @GET(PREFIX + "/user/memberships")
    Observable<List<UserChannelMembershipResponse>> getUserChannelMemberships();

    @GET(PREFIX + "/user/memberships/channels/{channel_id}")
    Observable<UserChannelMembershipResponse> getUserChannelMembership(@Query("channel_id") String channelId);

    @PUT(PREFIX + "/user/memberships/channels/{channel_id}")
    Observable<UserChannelMembershipResponse> createUserChannelMembership(@Query("channel_id") String channelId);

    @GET(PREFIX + "/user/marks")
    Observable<List<UserFeedMarkResponse>> getUserFeedMarks();

    @GET(PREFIX + "/user/marks/feeds/{feed_id}")
    Observable<UserFeedMarkResponse> getUserFeedMark(@Path("feed_id") String feedId);

    @PUT(PREFIX + "/user/marks/feeds/{feed_id}")
    Observable<UserFeedMarkResponse> createUserFeedMark(@Path("feed_id") String feedId, @Query("symbol") String symbol);

    @PATCH(PREFIX + "/user/marks/feeds/{feed_id}")
    Observable<UserFeedMarkResponse> updateUserFeedMark(@Path("feed_id") String feedId, @Query("symbol") String symbol);

    @GET(PREFIX + "/user/stars")
    Observable<List<UserFeedStarResponse>> getUserFeedStars();

    @GET(PREFIX + "/user/stars/feeds/{feed_id}")
    Observable<UserFeedStarResponse> getUserFeedStar(@Path("feed_id") String feedId);

    @PUT(PREFIX + "/user/stars/feeds/{feed_id}")
    Observable<UserFeedStarResponse> createUserFeedStar(@Path("feed_id") String feedId, @Query("symbol") String symbol);

    // channels
    @GET(PREFIX + "/channels/{channel_id}")
    Observable<ChannelResponse> getChannel(@Path("channel_id") String channelId);

    @POST(PREFIX + "/channels")
    Observable<ChannelResponse> createChannel(@Query("name") String name);

    @GET(PREFIX + "/channels/{channel_id}/memberships")
    Observable<List<ChannelUserMembershipResponse>> getChannelUserMemberships(@Path("channel_id") String channelId);

    @GET(PREFIX + "/channels/{channel_id}/memberships/users/{user_id}")
    Observable<ChannelUserMembershipResponse> getChannelUserMemberships(@Path("channel_id") String channelId, @Path("user_id") String userId);

    // feeds
    @GET(PREFIX + "/channels/{channel_id}/feeds")
    Observable<List<FeedResponse>> getChannelFeeds(@Path("channel_id") String channelId);

    @POST(PREFIX + "/channels/{channel_id}/feeds")
    Observable<FeedResponse> createChannelFeed(@Path("channel_id") String channelId, @Query("text") String text);

    @GET(PREFIX + "/feeds/{feed_id}")
    Observable<FeedResponse> getFeed(@Path("feed_id") String feedId);

    @GET(PREFIX + "/feeds/{feed_id}/marks")
    Observable<List<MarkResponse>> getFeedMarks(@Path("feed_id") String feedId);

    @GET(PREFIX + "/feeds/{feed_id}/marks/users/{user_id}")
    Observable<MarkResponse> getFeedMarkByUser(@Path("feed_id") String feedId, @Path("user_id") String userId);

    @GET(PREFIX + "/feeds/{feed_id}/stars")
    Observable<List<StarResponse>> getFeedStars(@Path("feed_id") String feedId);

    @GET(PREFIX + "/feeds/{feed_id}/stars/users/{user_id}")
    Observable<StarResponse> getFeedStarByUser(@Path("feed_id") String feedId, @Path("user_id") String userId);

    @GET(PREFIX + "/feeds/{feed_id}/comments")
    Observable<List<CommentResponse>> getFeedComments(@Path("feed_id") String feedId);

    @GET(PREFIX + "/comments/{comment_id}")
    Observable<CommentResponse> getComment(@Path("comment_id") String commentId);

    @POST(PREFIX + "/feeds/{feed_id}/comments")
    Observable<CommentResponse> createFeedComment(@Path("feed_id") String feedId, @Query("text") String text);


}
