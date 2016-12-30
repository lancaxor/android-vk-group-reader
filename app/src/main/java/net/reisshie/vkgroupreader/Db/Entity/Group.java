package net.reisshie.vkgroupreader.Db.Entity;

import android.content.Context;
import android.database.Cursor;

import net.reisshie.vkgroupreader.tools.Pager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexey on 21.12.2016.
 */

public class Group extends Base {

    public final String COLUMN_ID = "id";
    public final String COLUMN_ENABLED = "enabled";
    public final String COLUMN_TITLE = "title";
    public final String COLUMN_VK_ID = "vk_id";

    protected String[] columns = {this.COLUMN_ENABLED,this.COLUMN_ID,this.COLUMN_TITLE,this.COLUMN_VK_ID};

    @Override
    public void install() {
        String sql = "CREATE TABLE IF NOT EXISTS `" + this.getTableName() + "` (" +
                "  `" + this.COLUMN_ID + "` INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  `" + this.COLUMN_TITLE + "` VARCHAR(256) NOT NULL DEFAULT '<unknown group>'," +
                "  `" + this.COLUMN_ENABLED + "` INTEGER NOT NULL DEFAULT 1," +
                "  `" + this.COLUMN_VK_ID + "` VARCHAR(45) NOT NULL" +
                ")";
        this.db.executeSql(sql, false);
    }

    public boolean isEnabled() {
        return (this.getData(this.COLUMN_ENABLED, "0") == "1");
    }

    public void setEnabled(boolean enabled) {
        this.setData(this.COLUMN_ENABLED, (enabled ? "1" : "0"));
    }

    public Long getId() {
        String data = this.getData(this.COLUMN_ID, "0");
        return (data == null ? null : Long.valueOf(data));
    }

    public void setId(Long id) {
        this.setEntityId(id);
    }

    public String getTitle() {
        return this.getData(this.COLUMN_TITLE, null);
    }

    public void setTitle(String title) {
        this.data.put(this.COLUMN_TITLE, title);
    }

    public void setVkId(Long vkId) {
        this.setData(this.COLUMN_VK_ID, vkId.toString());
    }

    public String getVkId() {
        return this.getData(this.COLUMN_VK_ID);
    }

    public Group(Context context) {
        super(context);
        this.TABLE_NAME = "group";
        this.KEY_COLUMN = this.COLUMN_ID;
    }

    public Group(Context context, Long id) {
        this(context);
        this.setEntityId(id);
        this.load();
    }

    public List<Group> getEnabledGroups(Pager pager) {
        List<Group> result = new ArrayList<Group>();
        String[] columns = this.columns;
        String[] columnsData = {"1"};   // enabled
        Cursor cursor = this.db.getData(this.getTableName(), columns, this.COLUMN_ENABLED + " = ?", columnsData, null, null, this.COLUMN_ID, pager);
        do {
            Group group = new Group(this.context);
            int indexId = cursor.getColumnIndex(this.COLUMN_ID);
            int indexVkId = cursor.getColumnIndex(this.COLUMN_VK_ID);
            int indexTitle = cursor.getColumnIndex(this.COLUMN_TITLE);
            int indexEnabled = cursor.getColumnIndex(this.COLUMN_ENABLED);

            Long id = cursor.getLong(indexId);
            Long vkId = cursor.getLong(indexVkId);
            Boolean enabled = (cursor.getInt(indexEnabled) == 1);
            String title = cursor.getString(indexTitle);

            group.setId(id);
            group.setVkId(vkId);
            group.setEnabled(enabled);
            group.setTitle(title);
            result.add(group);

        } while(cursor.moveToNext());
        cursor.close();
        return result;
    }

    public List<Group> getGroups() {
        List<Group> result = new ArrayList<Group>();
        String[] columns = this.columns;
        String[] columnsData = {"1"};   // enabled
        Cursor cursor = this.db.getData(this.getTableName(), columns, this.COLUMN_ENABLED + " = ?", columnsData, null, null, this.COLUMN_ID, null);
        if(cursor == null || !cursor.moveToFirst()) {
            return result;
        }
        do {
            Group group = new Group(this.context);
            int indexId = cursor.getColumnIndex(this.COLUMN_ID);
            int indexVkId = cursor.getColumnIndex(this.COLUMN_VK_ID);
            int indexTitle = cursor.getColumnIndex(this.COLUMN_TITLE);
            int indexEnabled = cursor.getColumnIndex(this.COLUMN_ENABLED);

            Long id = cursor.getLong(indexId);
            Long vkId = cursor.getLong(indexVkId);
            Boolean enabled = (cursor.getInt(indexEnabled) == 1);
            String title = cursor.getString(indexTitle);

            group.setId(id);
            group.setVkId(vkId);
            group.setEnabled(enabled);
            group.setTitle(title);
            result.add(group);

        } while(cursor.moveToNext());
        cursor.close();
        return result;
    }
}
