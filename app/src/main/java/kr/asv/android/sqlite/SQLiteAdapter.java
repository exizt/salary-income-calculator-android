package kr.asv.android.sqlite;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016-04-28.
 */
@SuppressWarnings("DefaultFileTemplate")
public class SQLiteAdapter {
    private SQLiteDatabase db;
    private SQLiteOpenHelper helper;

    public SQLiteAdapter(SQLiteOpenHelper helper)
    {
        this.helper = helper;
        this.db = this.helper.getWritableDatabase();
    }
    public SQLiteDatabase db()
    {
        return this.db;
    }
}
