package com.kelsos.mbrc;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

public final class Generator {

  public static final String NAME = "name";

  private Generator() { }

  public static void main(String[] args) throws Exception {
    Schema schema = new Schema(11, "im.youtiao.android_client.greendao");

    Entity user = schema.addEntity("User");
    user.addIdProperty();
    user.addStringProperty("serverId").unique().notNull().index();
    user.addStringProperty("email").unique().notNull().index();
    user.addDateProperty("createdAt");
    user.addDateProperty("updatedAt");

    Entity channel = schema.addEntity("Channel");
    channel.addIdProperty();
    channel.addStringProperty("serverId").unique().notNull().index();
    channel.addStringProperty("name").unique().notNull().index();
    channel.addStringProperty("role");
    channel.addIntProperty("usersCount");
    channel.addDateProperty("createdAt");
    channel.addDateProperty("updatedAt");
    Property channelCreatedBy =  channel.addLongProperty("userId").getProperty();
    channel.addToOne(user, channelCreatedBy);

    Entity feed = schema.addEntity("Feed");
    feed.addIdProperty();
    feed.addStringProperty("serverId").unique().notNull().index();
    feed.addStringProperty("text");
    feed.addStringProperty("symbol");
    feed.addBooleanProperty("isStarred");
    Property feedCreatedDate = feed.addDateProperty("createdAt").getProperty();
    Property channelId = feed.addLongProperty("channelId").notNull().getProperty();
    feed.addToOne(channel, channelId);
    Property feedCreatedBy = feed.addLongProperty("createdBy").notNull().getProperty();
    feed.addToOne(user, feedCreatedBy);
    ToMany channelToFeeds = channel.addToMany(feed, channelId);
    channelToFeeds.setName("feeds");
    channelToFeeds.orderDesc(feedCreatedDate);

    Entity comment = schema.addEntity("Comment");
    comment.addIdProperty();
    comment.addStringProperty("serverId").unique().notNull().index();
    comment.addStringProperty("text");
    comment.addDateProperty("updatedAt");
    Property commentCreatedBy =  comment.addLongProperty("createdBy").notNull().getProperty();
    comment.addToOne(user, commentCreatedBy);
    Property feedId =  comment.addLongProperty("feedId").notNull().getProperty();
    comment.addToOne(feed, feedId);
    Property commentsCreatedDate = comment.addDateProperty("createdAt").getProperty();
    ToMany feedToComments = feed.addToMany(comment, feedId);
    feedToComments.setName("comments");
    feedToComments.orderDesc(commentsCreatedDate);

    final String outDir = "app/src-gen/";
    new DaoGenerator().generateAll(schema, outDir);
    new HelperGenerator().generateAll(schema, outDir);
  }
}
