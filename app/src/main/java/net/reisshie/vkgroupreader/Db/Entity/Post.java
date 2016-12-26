package net.reisshie.vkgroupreader.Db.Entity;

import android.content.Context;
import android.database.Cursor;

import net.reisshie.vkgroupreader.tools.Pager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Alexey on 19.12.2016.
 */

public class Post extends Base {
    public final String COLUMN_ID = "id";
    public final String COLUMN_TEXT = "text";
    public final String COLUMN_TIMESTAMP = "timestamp";
    public final String COLUMN_GROUP_ID = "group_id";
    public final String COLUMN_VK_ID = "vk_id";
    public final String COLUMN_IS_VIEWED = "is_viewed";

    private Group group;

    public Post(Context context) {
        super(context);
        this.TABLE_NAME = "post";
        this.KEY_COLUMN = this.COLUMN_ID;
    }


    public Post(Context context, Long id) {
        this(context);
        this.setEntityId(id);
        this.load();
    }

    @Override
    public void install() {
        String sql = "CREATE TABLE IF NOT EXISTS `" + this.getTableName() + "` (" +
                "  `" + this.COLUMN_ID + "` INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  `" + this.COLUMN_GROUP_ID + "` INTEGER," +
                "  `" + this.COLUMN_TEXT + "` TEXT NOT NULL DEFAULT '<no text>'," +
                "  `" + this.COLUMN_IS_VIEWED + "` INTEGER NOT NULL DEFAULT 1," +
                "  `" + this.COLUMN_VK_ID + "` VARCHAR(45) NOT NULL," +
                "  `" + this.COLUMN_TIMESTAMP + "` BIGINT NOT NULL" +
                ")";
        this.db.executeSql(sql, false);
    }

    public Post loadByVkId() {
        this.load(this.COLUMN_VK_ID);
        return this;
    }

    // region setters

    public Post setId(Long id) {
        this.setEntityId(id);
        return this;
    }

    public Post setText(String text) {
        this.setData(this.COLUMN_TEXT, text);
        return this;
    }

    public Post setTimestamp(Long timestamp) {
        this.setData(this.COLUMN_TIMESTAMP, timestamp.toString());
        return this;
    }

    public Post setVkId(Long vkId) {
        this.setData(this.COLUMN_VK_ID, vkId.toString());
        return this;
    }

    public Post setGroupId(Long groupId) {
        this.setData(this.COLUMN_GROUP_ID, groupId.toString());
        return this;
    }

    public Post setGroup(Group group) {
        this.group = group;
        this.setData(this.COLUMN_GROUP_ID, group.getId().toString());
        return this;
    }

    public Post setIsViewed(boolean isViewed) {
        this.setData(this.COLUMN_IS_VIEWED, (isViewed ? "1" : "0"));
        return this;
    }
    // endregion setters

    // region getters
    public Long getId() {
        return Long.parseLong(this.getData(this.COLUMN_ID, "0"));
    }

    public String getText() {
        return this.getData(this.COLUMN_TEXT, "");
    }

    public Long getTimestamp() {
        return Long.parseLong(this.getData(this.COLUMN_TIMESTAMP, "0"));
    }

    public Date getDate() {
        Long timestamp = this.getTimestamp();
        if(timestamp == null) {
            return null;
        }
        Date time = new Date(timestamp * 1000L);
        return time;
    }

    public Long getVkId() {
        return Long.parseLong(this.getData(this.COLUMN_VK_ID, "0"));
    }

    public Long getGroupId() {
        return Long.parseLong(this.getData(this.COLUMN_GROUP_ID, "0"));
    }

    public Group getGroup() {
        if(this.group == null) {
            Long groupId = this.getGroupId();
            this.group = new Group(this.context, groupId);
            group.load();
        }
        return this.group;
    }

    public boolean isViewed() {
        return (Integer.valueOf(this.getData(this.COLUMN_IS_VIEWED, "0")) == 1);
    }

    public List<Post> getNewPosts(Pager pager) {
        List<Post> result = new ArrayList<Post>();
        String[] columns = this.columns;
        String[] columnsData = {"0"};   // viewed
        Cursor cursor = this.db.getData(this.getTableName(), columns, this.COLUMN_IS_VIEWED + " = ?", columnsData, null, null, this.COLUMN_TIMESTAMP + " DESC", pager);
        if(cursor == null || !cursor.moveToFirst()) {
            cursor.close();
            return result;
        }
        do {
            Post post = new Post(this.context);
            int indexId = cursor.getColumnIndex(this.COLUMN_ID);
            int indexGroupId = cursor.getColumnIndex(this.COLUMN_GROUP_ID);
            int indexVkId = cursor.getColumnIndex(this.COLUMN_VK_ID);
            int indexViewed = cursor.getColumnIndex(this.COLUMN_IS_VIEWED);
            int indexText = cursor.getColumnIndex(this.COLUMN_TEXT);
            int indexTimestamp = cursor.getColumnIndex(this.COLUMN_TIMESTAMP);

            Long id = cursor.getLong(indexId);
            Long groupId = cursor.getLong(indexGroupId);
            Long vkId = cursor.getLong(indexVkId);
            boolean viewed = (cursor.getInt(indexViewed) == 1);
            String text = cursor.getString(indexText);
            Long timestamp = cursor.getLong(indexTimestamp);

            post.setId(id);
            post.setVkId(vkId);
            post.setIsViewed(viewed);
            post.setText(text);
            post.setTimestamp(timestamp);

            Group group = new Group(this.context);
            group.setEntityId(groupId);
            group.load();
            post.setGroup(group);

            result.add(post);

        } while(cursor.moveToNext());
        cursor.close();
        return result;
    }
    // endregion getters

    public void removeAll() {
        this.db.removeRow(this.getTableName(), null, null);
    }

}
