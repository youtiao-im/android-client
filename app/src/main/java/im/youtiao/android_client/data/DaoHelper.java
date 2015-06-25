package im.youtiao.android_client.data;

import im.youtiao.android_client.dao.Bulletin;
import im.youtiao.android_client.dao.BulletinDao;
import im.youtiao.android_client.dao.DaoSession;

public class DaoHelper {

    public static Bulletin insertOrUpdate(DaoSession daoSession, Bulletin entity) {
        BulletinDao bulletinDao = daoSession.getBulletinDao();
        Bulletin bulletin = bulletinDao.queryBuilder().where(BulletinDao.Properties.ServerId.eq(entity.getServerId())).unique();
        if (bulletin == null) {
            long rowId = bulletinDao.insert(entity);
            entity.setId(rowId);
            return entity;
        } else {
            bulletin.setServerId(entity.getServerId());
            bulletin.setJson(entity.getJson());
            bulletinDao.update(bulletin);
            return bulletin;
        }
    }
    /*
    public static User insertOrUpdate(DaoSession daoSession, User entity) {
//        UserDao userDao = daoSession.getUserDao();
//        User user = userDao.queryBuilder().where(UserDao.Properties.ServerId.eq(entity.getServerId())).unique();
//        if (user == null) {
//            user = new User(null, entity.getServerId(), entity.getEmail(), entity.getCreatedAt(), entity.getUpdatedAt());
//            long userId = userDao.insert(user);
//            user.setId(userId);
//        } else {
//            user.setEmail(entity.getEmail());
//            user.setCreatedAt(entity.getCreatedAt());
//            user.setCreatedAt(entity.getUpdatedAt());
//            userDao.update(user);
//        }
//        return user;
        return null;
    }

    public static Channel insertOrUpdate(DaoSession daoSession, Channel entity) {
//        ChannelDao channelDao = daoSession.getChannelDao();
//        Channel channel = channelDao.queryBuilder().where(ChannelDao.Properties.ServerId.eq(entity.getServerId())).unique();
//        if (channel == null) {
//            channel = new Channel(null, entity.getServerId(), entity.getName(), entity.getRole(), entity.getUsersCount(), entity.getCreatedAt(), entity.getUpdatedAt(), entity.getUserId());
//            long userId = channelDao.insert(channel);
//            channel.setId(userId);
//        } else {
//            channel.setName(entity.getName());
//            channel.setUsersCount(entity.getUsersCount());
//            channel.setRole(entity.getRole());
//            channel.setUserId(entity.getUserId());
//            channel.setCreatedAt(entity.getCreatedAt());
//            channel.setCreatedAt(entity.getUpdatedAt());
//            channelDao.update(channel);
//        }
        return null;
    }

    public static Feed insertOrUpdate(DaoSession daoSession, Feed entity) {
//        FeedDao feedDao = daoSession.getFeedDao();
//        Feed feed = feedDao.queryBuilder().where(FeedDao.Properties.ServerId.eq(entity.getServerId())).unique();
//        if (feed == null) {
//            feed = new Feed(null, entity.getServerId(), entity.getText(), entity.getSymbol(),entity.getIsStarred(), entity.getCreatedAt(), entity.getChannelId(), entity.getCreatedBy());
//            long feedId = feedDao.insert(feed);
//            feed.setId(feedId);
//        } else {
//            feed.setText(entity.getText());
//            feed.setSymbol(entity.getSymbol());
//            feed.setIsStarred(entity.getIsStarred());
//            feed.setCreatedAt(entity.getCreatedAt());
//            feed.setChannelId(entity.getChannelId());
//            feed.setCreatedBy(entity.getCreatedBy());
//            feedDao.update(feed);
//        }
        return null;
    }

    public static Comment insertOrUpdate(DaoSession daoSession, Comment entity) {
//        CommentDao commentDao = daoSession.getCommentDao();
//        Comment comment = commentDao.queryBuilder().where(CommentDao.Properties.ServerId.eq(entity.getServerId())).unique();
//        if (comment == null) {
//            comment = new Comment(null, entity.getServerId(), entity.getText(), entity.getUpdatedAt(), entity.getCreatedBy(), entity.getFeedId(), entity.getCreatedAt());
//            long commentId = commentDao.insert(comment);
//            comment.setId(commentId);
//        } else {
//            comment.setText(entity.getText());
//            comment.setCreatedAt(entity.getCreatedAt());
//            comment.setUpdatedAt(entity.getUpdatedAt());
//            comment.setCreatedBy(entity.getCreatedBy());
//            comment.setCreatedBy(entity.getCreatedBy());
//            comment.setFeedId(entity.getFeedId());
//            commentDao.update(comment);
//        }
        return null;
    }
    */
}
