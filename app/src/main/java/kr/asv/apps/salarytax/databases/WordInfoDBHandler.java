package kr.asv.apps.salarytax.databases;

import android.content.Context;

import kr.asv.android.sqlite.SQLiteHandler;

/**
 * WordInfoDBHandler
 * WordInfoDBHandler 테이블 을 처리하기 위한 클래스
 */
public class WordInfoDBHandler extends SQLiteHandler {
    private final String dbName = "salarytax_information.db";
    private final int dbVersion = 1;
    private final boolean isDebug = false;

    /**
     * Construction
     * @param context Context
     */
    public WordInfoDBHandler(Context context)
    {
        debug("[Constructor] >> ");
        super.initialize(context,dbName,dbVersion,isDebug);
    }

    /**
     * onCreateDatabase
     */
    public void onCreateDatabase()
    {
        debug("[onCreateDatabase] >> ");
        //copyDatabase();
    }

    /**
     * onUpgradeDatabase
     * @param oldVersion int
     * @param newVersion int
     */
    public void onUpgradeDatabase(int oldVersion,int newVersion){
        debug("[onUpgradeDatabase] >>");
        //copyDatabase();
    }

    /**
     * onDowngradeDatabase
     * @param oldVersion int
     * @param newVersion int
     */
    public void onDowngradeDatabase(int oldVersion,int newVersion){
        debug("[onDowngradeDatabase] >>");
        //copyDatabase();
    }

    @SuppressWarnings("unused")
    public String getDbName(){
        return dbName;
    }

    public int getDbVersion(){
        return dbVersion;
    }

    @SuppressWarnings("unused")
    public boolean isDebug(){
        return isDebug;
    }
}
