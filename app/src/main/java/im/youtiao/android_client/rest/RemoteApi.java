package im.youtiao.android_client.rest;

import java.util.List;

import im.youtiao.android_client.model.Bulletin;
import im.youtiao.android_client.model.Comment;
import im.youtiao.android_client.model.Group;
import im.youtiao.android_client.model.Membership;
import im.youtiao.android_client.model.Stamp;
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

    @GET(PREFIX + "/user.info")
    Observable<User> getAuthenticatedUser();

    @POST(PREFIX + "/user.update")
    Observable<User> updateUser(@Query("name") String name, @Query("avatar_id") String avatarId);

    @GET(PREFIX + "/groups.list")
    Observable<List<Group>> listGroups();

    @POST(PREFIX + "/groups.create")
    Observable<Group> createGroup(@Query("name") String name, @Query("code") String code);

    @POST(PREFIX + "/groups.join")
    Observable<Group> joinGroup(@Query("code") String code);

    @POST(PREFIX + "/groups.update")
    Observable<Group> updateGroup(@Query("id") String groupId, @Query("name") String name, @Query("code") String code);

    @GET(PREFIX + "/memberships.list")
    Observable<List<Membership>> listGroupMemberships(@Query("id") String groupId, @Query("before_id") String beforeId, @Query("limit") Integer limit);

    @GET(PREFIX + "/bulletins.list")
    Observable<List<Bulletin>> listBulletins(@Query("before_id") String beforeId, @Query("limit") Integer limit);

    @GET(PREFIX + "/bulletins.list")
    Observable<List<Bulletin>> listGroupBulletins(@Query("group_id") String groupId, @Query("before_id") String beforeId, @Query("limit") Integer limit);

    @POST(PREFIX + "/bulletins.create")
    Observable<Bulletin> createBulletin(@Query("group_id") String groupId, @Query("text") String text);

    @POST(PREFIX + "/bulletins.stamp")
    Observable<Bulletin> markBulletin(@Query("id") String bulletinId, @Query("symbol") String symbol);

    @GET(PREFIX + "stamps.list")
    Observable<List<Stamp>> listStamps(@Query("bulletin_id") String bulletinId, @Query("before_id") String beforeId, @Query("limit") Integer limit);

    @GET(PREFIX + "/comments.list")
    Observable<List<Comment>> listComments(@Query("bulletin_id") String bulletinId, @Query("before_id") String beforeId, @Query("limit") Integer limit);

    @POST(PREFIX + "/comments.create")
    Observable<Comment> createComment(@Query("bulletin_id") String bulletinId, @Query("text") String text);
}
