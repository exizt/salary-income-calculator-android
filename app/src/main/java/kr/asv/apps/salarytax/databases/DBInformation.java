package kr.asv.apps.salarytax.databases;

import android.content.Context;
import android.util.Log;

import kr.asv.android.sqlite.SQLiteHandler;

/**
 * DBInformation
 * DBInformation 테이블 을 처리하기 위한 클래스
 */
public class DBInformation extends SQLiteHandler {
    protected String dbName = "salarytax_information.db";
    protected int dbVersion = 1;
    private boolean isDebug = false;

    /**
     * Construction
     * @param context
     */
    public DBInformation(Context context)
    {
        debug("[Constructor] >> ");
        super.initialize(context,dbName,dbVersion,false);
        if(super.isPassingCopyProcess){
            debug("[Constructor] passing db copy process");
        }
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
     * @param oldVersion
     * @param newVersion
     */
    public void onUpgradeDatabase(int oldVersion,int newVersion){
        debug("[onUpgradeDatabase] >>");
        //copyDatabase();
    }

    /**
     * onDowngradeDatabase
     * @param oldVersion
     * @param newVersion
     */
    public void onDowngradeDatabase(int oldVersion,int newVersion){
        debug("[onDowngradeDatabase] >>");
        //copyDatabase();
    }

    /**
     * 디버깅
     * @param log
     */
    private void debug(String log)
    {
        if(isDebug) {
            Log.e("[EXIZT-DEBUG]", new StringBuilder("[DBInformation]").append(log).toString());
        }
    }
}
