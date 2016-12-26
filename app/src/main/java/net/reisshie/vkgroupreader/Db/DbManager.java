package net.reisshie.vkgroupreader.Db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Pair;

import net.reisshie.vkgroupreader.tools.Pager;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by Alexey on 19.12.2016.
 */

public class DbManager extends SQLiteOpenHelper{

    private static final String DB_NAME = "reisshieGroupReader";
    private static final int DB_VERSION = 1;    // database version
    private static String DB_PATH = "";         // path to database
    private SQLiteDatabase db;

    public DbManager(Context context) {

        super(context, DbManager.DB_NAME, null, DbManager.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
//        if(!this.isDatabaseExists()) {
//            this.getReadableDatabase();
//            this.close();
//        }
    }

    /**
     * Check if database file exists in filesystem
     * @return boolean
     */
    private boolean isDatabaseExists() {
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    @Override
    public synchronized void close() {
        if(this.db != null) {
            this.db.close();
        }
        super.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    public boolean isTableExists(String tableName, boolean openDb) {
        if(openDb) {
            if(this.db == null || !this.db.isOpen()) {
                this.db = getReadableDatabase();
            }

            if(!this.db.isReadOnly()) {
                this.db.close();
                this.db = getReadableDatabase();
            }
        }

        Cursor cursor = this.db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+tableName+"'", null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

    public Cursor getData(String tableName, String[] columns, String columnFilter, String[] columnFilterData,
                             String groupBy, String having, String orderBy, Pager pager) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(tableName, columns, columnFilter, columnFilterData, groupBy, having, orderBy, (pager == null ? null : String.valueOf(pager.getLimit())));
        if(cursor != null && cursor.moveToFirst()) {
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }

    public Cursor getRawData(String sql, String[] selectionArgs) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        db.close();
        return cursor;
    }

    public DbManager insertRow(String tableName, Map<String, String> params) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        for (Map.Entry<String, String> field: params.entrySet()) {
            values.put(field.getKey(), (field.getValue() == null ? null : field.getValue().toString()));
        }
        db.insert(tableName, null, values);
        db.close();
        return this;
    }

    public DbManager updateRow(String tableName, Map<String, String> params, String where, String[] whereArgs) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        for (Map.Entry<String, String> field: params.entrySet()) {
            values.put(field.getKey(), (field.getValue() == null ? null : field.getValue().toString()));
        }
        db.update(tableName, values, where, whereArgs);
        db.close();
        return this;
    }

    public DbManager executeSql(String sql) {
        return this.executeSql(sql, true);
    }

    public DbManager executeSql(String sql, boolean readonly) {
        SQLiteDatabase db = (readonly ? this.getReadableDatabase() : this.getWritableDatabase());
        db.execSQL(sql);
        db.close();
        return this;
    }

    public DbManager removeRow(String tableName, String where, String[] whereArgs) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tableName, where, whereArgs);
        db.close();
        return this;
    }
}
