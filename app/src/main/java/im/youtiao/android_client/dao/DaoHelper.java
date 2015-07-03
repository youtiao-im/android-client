package im.youtiao.android_client.dao;

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

    public static Bulletin getOldestBulletin(DaoSession daoSession) {
        BulletinDao bulletinDao = daoSession.getBulletinDao();
        Bulletin bulletin = bulletinDao.queryBuilder().orderAsc(BulletinDao.Properties.CreatedAt).limit(1).unique();
        return bulletin;
    }

    public static Group insertOrUpdate(DaoSession daoSession, Group entity) {
        GroupDao groupDao = daoSession.getGroupDao();
        Group group = groupDao.queryBuilder().where(GroupDao.Properties.ServerId.eq(entity.getServerId())).unique();
        if (group == null) {
            long rowId = groupDao.insert(entity);
            entity.setId(rowId);
            return entity;
        } else {
            group.setServerId(entity.getServerId());
            group.setJson(entity.getJson());
            groupDao.update(group);
            return group;
        }
    }
}
