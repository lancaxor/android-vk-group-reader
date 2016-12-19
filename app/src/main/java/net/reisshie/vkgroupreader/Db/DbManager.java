package net.reisshie.vkgroupreader.Db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Pair;

import java.util.List;
import java.util.Map;

/**
 * Created by Alexey on 19.12.2016.
 */

public class DbManager extends SQLiteOpenHelper{

    private static final String DB_NAME = "reisshieGroupReader";
    private static final int DB_VERSION = 1;

    public DbManager(Context context) {
        super(context, DbManager.DB_NAME, null, DbManager.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    public DbManager executeSql(String sql, List<Map<String, Object>> params) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        return this;
    }

    public DbManager insertRow(String tableName, List<Map<String, Object>> params) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        for(Map<String, Object> row: params) {
            for (Map.Entry<String, Object> field: row.entrySet()) {
                values.put(field.getKey(), field.getValue().toString());
            }
        }
        db.insert(tableName, null, values);
        db.close();
        return this;
    }
}
