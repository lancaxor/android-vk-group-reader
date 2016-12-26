package net.reisshie.vkgroupreader.Db.Entity;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import net.reisshie.vkgroupreader.Db.DbManager;
import net.reisshie.vkgroupreader.tools.Pager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alexey on 19.12.2016.
 */

/**
 * Base class for all DB models.
 * All data should be converted to String before setting data,
 * and convert back after getting data from the parent class.
 */
public class Base {
    protected String TABLE_NAME = "";
    protected String TABLE_PREFIX = "vgr_";
    protected DbManager db;
    protected String[] columns;
    protected Map<String, String> data;
    protected String KEY_COLUMN = "id";
    protected Context context;

    public Base(Context context) {
        this.db = new DbManager(context);
        this.context = context;
        this.data = new HashMap<String, String>();
    }

    public void install() {
        this.install(null);
    }

    public String getTableName() {
        return this.TABLE_PREFIX + this.TABLE_NAME;
    }

    public String getTableName(String tableName) {
        return this.TABLE_PREFIX + tableName;
    }

    public void install(String sql) {
        if(sql == null) {
            return;
        }
        db.executeSql(sql, false);
    }

    public Base setData(String key, String value) {
        if(this.data == null) {
            this.data = new HashMap<String, String>();
        }
        this.data.put(key, value);
        return this;
    }

    public String getData(String key) {
        if(this.data == null) {
            this.data = new HashMap<String, String>();
        }
        return this.getData(key, null);
    }

    public String getData(String key, String defaultValue) {

        if(this.data == null) {
            this.data = new HashMap<String, String>();
        }
        if(this.data.containsKey(key)) {
            return this.data.get(key);
        }

        return defaultValue;
    }

    public void setEntityId(Long id) {
        this.setData(this.KEY_COLUMN, (id == null ? null : id.toString()));
    }

    public Long getEntityId() {
        if(this.KEY_COLUMN == null) {
            return 0L; // (long) 0
        }
        String result = this.getData(this.KEY_COLUMN, "0");
        return (result == null ? null : Long.valueOf(result));
    }

    /**
     * @return this
     */
    public Base save(Base object) {

        if(this.data.containsKey(this.KEY_COLUMN) && this.data.get(this.KEY_COLUMN) != null) {
            String[] args = { this.data.get(this.KEY_COLUMN) };
            this.db.updateRow(this.getTableName(),this.data, KEY_COLUMN + "=?", args);
        } else {
            if(this.data.containsKey(this.KEY_COLUMN)) {    // key might be equals to null
                this.data.remove(this.KEY_COLUMN);
            }

            //insert
            this.db.insertRow(this.getTableName(), this.data);
        }
        return this;
    }

    public Base save() {
        return this.save(this);
    }

    public Base load() throws SQLException{
        if(this.KEY_COLUMN == null) {
            throw new SQLException("Key column value was not specified!");
        }

        return this.load(this.KEY_COLUMN);
    }

    public Base load(String fieldName) throws SQLException {
        int defaultPageSize = 1000;
        Pager pager = new Pager();
        pager.setPageSize(defaultPageSize);
        pager.setCurrentPage(0);
        return this.load(fieldName, pager);
    }

    public Base load(String fieldName, Pager pager) throws SQLException {

        if(!this.data.containsKey(fieldName)) {
            throw new SQLException("Value of column column '" + fieldName + "' was not specified!");
        }

        String[] columnsFilter = {this.getData(fieldName)};

        // columnFilter: "column1 = ? AND column2 = ?
        // columnFilterData: ["column1Value", "column2Value"]
        Cursor cursor = this.db.getData(this.getTableName(), this.columns, fieldName + "=?", columnsFilter, null, null, null, pager);
        if(cursor != null && cursor.moveToFirst()) {
            String[] columnNames = cursor.getColumnNames();
            for (String columnName : columnNames) {
                int id = cursor.getColumnIndex(columnName);
                String value = cursor.getString(id);
                this.data.put(columnName, value);
            }
        } else {
            this.setEntityId(null);
        }
        return this;
    }

    public Base remove(String entityId) {
        String[] whereArgs = {entityId};
        this.db.removeRow(this.getTableName(), this.KEY_COLUMN + "=?", whereArgs);
        return this;
    }
}
