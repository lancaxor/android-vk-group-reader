package net.reisshie.vkgroupreader.Db.Entity;

import android.content.Context;

/**
 * Created by Alexey on 19.12.2016.
 */

public class Post extends Base {
    private final String COLUMN_ID = "id";
    private final String COLUMN_TEXT = "text";
    private final String COLUMN_TIMESTAMP = "timestamp";

    private String text;
    private int id;
    private String timestamp;

    public Post() {
        super(null);
    }

    public Post(Context context) {
        super(context);
        this.TABLE_NAME = "post";
    }

    public String getText() {
        return text;
    }

    public int getId() {
        return id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
