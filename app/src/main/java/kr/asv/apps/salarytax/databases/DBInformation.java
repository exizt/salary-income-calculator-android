package kr.asv.apps.salarytax.databases;

import android.content.Context;

import kr.asv.android.sqlite.SQLiteHandler;

/**
 * DBInformation
 */
public class DBInformation extends SQLiteHandler {
    protected String dbName = "salarytax_information.db";
    protected int dbVersion = 1;
    protected boolean debug = false;

    /**
     * Construction
     * @param context
     */
    public DBInformation(Context context)
    {
        debug("[DBInformation] Construct");
        super.initialize(context,dbName,dbVersion,debug);
    }

    /**
     * onCreateDatabase
     */
    public void onCreateDatabase()
    {
        debug("[DBInformation] onCreateDatabase");
        //copyDatabase();
    }

    /**
     * onUpgradeDatabase
     * @param oldVersion
     * @param newVersion
     */
    public void onUpgradeDatabase(int oldVersion,int newVersion){
        debug("[DBInformation] onUpgradeDatabase");
        //copyDatabase();
    }

    /**
     * onDowngradeDatabase
     * @param oldVersion
     * @param newVersion
     */
    public void onDowngradeDatabase(int oldVersion,int newVersion){
        debug("[DBInformation] onDowngradeDatabase");
        //copyDatabase();
    }
}
