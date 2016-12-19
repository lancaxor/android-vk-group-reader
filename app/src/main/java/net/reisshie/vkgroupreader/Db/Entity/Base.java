package net.reisshie.vkgroupreader.Db.Entity;

import android.content.Context;

import net.reisshie.vkgroupreader.Db.DbManager;

/**
 * Created by Alexey on 19.12.2016.
 */

public class Base {
    protected String TABLE_NAME = "";
    protected DbManager db;

    public Base(Context context) {
        this.db = new DbManager(context);
    }


    /**
     * @return this
     */
    public Base save(Base object) {

        return this;
    }
}
