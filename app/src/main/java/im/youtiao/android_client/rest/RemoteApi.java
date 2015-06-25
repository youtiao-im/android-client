package im.youtiao.android_client.rest;

import java.util.List;

import im.youtiao.android_client.model.Bulletin;
import im.youtiao.android_client.model.Comment;
import im.youtiao.android_client.model.Group;
import im.youtiao.android_client.model.Membership;
import im.youtiao.android_client.model.User;
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

    @GET(PREFIX + "/user")
    Observable<User> getAuthenticatedUser();

    @GET(PREFIX + "/groups")
    Observable<List<Group>> listGroups();

    @GET(PREFIX + "/groups/{group_id}")
    Observable<Group> getGroup(@Path("group_id") String groupId);

    @POST(PREFIX + "/groups")
    Observable<Group> createGroup(@Query("name") String name);

    @POST(PREFIX + "/groups/{group_id}/join")
    Observable<Group> joinGroup(@Path("group_id") String groupId);

    @GET(PREFIX + "/groups/{group_id}/memberships")
    Observable<List<Membership>> listGroupMemberships(@Path("group_id") String groupId, @Query("after_id") String beforeId, @Query("limit") Integer limit);

    @GET(PREFIX + "/bulletins")
    Observable<List<Bulletin>> listBulletins(@Query("before_id") String beforeId, @Query("limit") Integer limit);

    @GET(PREFIX + "/groups/{group_id}/bulletins")
    Observable<List<Bulletin>> listGroupBulletins(@Path("group_id") String groupId, @Query("before_id") String beforeId, @Query("limit") Integer limit);

    @GET(PREFIX + "/bulletins/{bulletin_id}")
    Observable<Bulletin> getBulletin(@Query("bulletin_id") String bulletinId);

    @POST(PREFIX + "/groups/{group_id}/bulletins")
    Observable<Bulletin> createBulletin(@Path("group_id") String groupId, @Query("text") String text);

    @POST(PREFIX + "/bulletins/{bulletin_id}/stamp")
    Observable<Bulletin> markBulletin(@Path("bulletin_id") String bulletinId, @Query("symbol") String symbol);

    @GET(PREFIX + "/bulletins/{bulletin_id}/comments")
    Observable<List<Comment>> listComments(@Path("bulletin_id") String bulletinId, @Query("after_id") String beforeId, @Query("limit") Integer limit);

    @GET(PREFIX + "/comments/{comment_id}")
    Observable<Comment> getComment(@Path("comment_id") String comentId);

    @POST(PREFIX + "/bulletins/{bulletin_id}/comments")
    Observable<Comment> createComment(@Path("bulletin_id") String bulletinId, @Query("text") String text);
}
