package im.youtiao.android_client.rest;

import java.util.List;

import im.youtiao.android_client.model.Bulletin;
import im.youtiao.android_client.model.Comment;
import im.youtiao.android_client.model.Group;
import im.youtiao.android_client.model.Membership;
import im.youtiao.android_client.model.Stamp;
import im.youtiao.android_client.model.User;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

public interface RemoteApi {
    final String PREFIX = "/v1";

    @GET(PREFIX + "/user.info")
    Observable<User> getAuthenticatedUser();

    @POST(PREFIX + "/user.update")
    Observable<User> updateUser(@Query("name") String name, @Query("avatar_id") String avatarId);

    @POST(PREFIX + "/users.sign_up")
    Observable<User> signUpUser(@Query("email") String email, @Query("name") String name, @Query("password") String password);

    @POST(PREFIX + "/user.change_password")
    Observable<User> changePassword(@Query("password") String password, @Query("new_password") String newPassword);

    @GET(PREFIX + "/groups.list")
    Observable<List<Group>> listGroups();

    @POST(PREFIX + "/groups.create")
    Observable<Group> createGroup(@Query("name") String name, @Query("code") String code);

    @POST(PREFIX + "/groups.join")
    Observable<Group> joinGroup(@Query("code") String code);

    @POST(PREFIX + "/groups.leave")
    Observable<Group> leaveGroup(@Query("id") String groupId);

    @POST(PREFIX + "/groups.update")
    Observable<Group> updateGroup(@Query("id") String groupId, @Query("name") String name, @Query("code") String code);

    @GET(PREFIX + "/memberships.list")
    Observable<List<Membership>> listGroupMemberships(@Query("group_id") String groupId);

    @GET(PREFIX + "/bulletins.list")
    Observable<List<Bulletin>> listBulletins(@Query("before_id") String beforeId, @Query("limit") Integer limit);

    @GET(PREFIX + "/bulletins.list")
    Observable<List<Bulletin>> listGroupBulletins(@Query("group_id") String groupId, @Query("before_id") String beforeId, @Query("limit") Integer limit);

    @POST(PREFIX + "/bulletins.create")
    Observable<Bulletin> createBulletin(@Query("group_id") String groupId, @Query("text") String text);

    @POST(PREFIX + "/bulletins.stamp")
    Observable<Bulletin> markBulletin(@Query("id") String bulletinId, @Query("symbol") String symbol);

    @GET(PREFIX + "/stamps.list")
    Observable<List<Stamp>> listStamps(@Query("bulletin_id") String bulletinId, @Query("before_id") String beforeId, @Query("limit") Integer limit);

    @GET(PREFIX + "/comments.list")
    Observable<List<Comment>> listComments(@Query("bulletin_id") String bulletinId, @Query("before_id") String beforeId, @Query("limit") Integer limit);

    @POST(PREFIX + "/comments.create")
    Observable<Comment> createComment(@Query("bulletin_id") String bulletinId, @Query("text") String text);
}
