package kr.asv.android.sqlite;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2016-04-29.
 */
public abstract class SQLiteHandler {
    //객체들
    protected Context context;
    protected SQLiteDatabase db;
    protected OpenHelper openHelper;

    //db config
    protected String dbName;
    protected int dbVersion;
    protected String dbHashFileName;
    //기타
    protected boolean debug = false;

    /**
     * 생성자
     */
    public SQLiteHandler()
    {
    }

    /**
     * 초기화
     * @param context
     * @param dbName
     * @param dbVersion
     * @param debug
     */
    public void initialize(Context context,String dbName,int dbVersion,boolean debug)
    {
        this.debug = debug;
        this.context = context;
        this.dbName = dbName;
        this.dbVersion = dbVersion;
        debug("SQLiteHandler.initialize");
        this.dbHashFileName = new StringBuffer(this.dbName).append(".hash").toString();


        //중복체크
        if(!isDuplicatedDatabase()) {
            //기존 디비와 assets 의 디비가 서로 다를 경우에 copy 실행.
            debug("[db duplicate check] false");
            //데이터베이스 복사
            copyDatabase();
            //데이터베이스 복사 후 해쉬 저장
            copyDatabaseHashFile();
        } else {
            debug("[db duplicate check] true");
        }

        this.openHelper = new OpenHelper(context,dbName,null,dbVersion);

        try{
            this.db = this.openHelper.getWritableDatabase();
        } catch(Exception e){
            debug("openHelper getWritableDatabase Exception : " + e.toString());
        }
    }

    /**
     * open 컨트롤
     * @return OpenHelper
     */
    public OpenHelper getOpenHelper()
    {
        return this.openHelper;
    }

    /**
     * 디버깅
     * @param log
     */
    public void debug(String log)
    {
        if(debug) {
            Log.e("SHH-DEBUG", log);
        }
    }
    public SQLiteDatabase db()
    {
        return getDb();
    }
    public SQLiteDatabase getDb()
    {
        return this.db;
    }

    /**
     * 데이터베이스 생성 과정에서 취할 액션
     */
    public abstract void onCreateDatabase();

    /**
     * 데이터베이스 업그레이드 과정에서 취할 액션
     */
    public abstract void onUpgradeDatabase(int oldVersion,int newVersion);

    /**
     * 다운그레이드의 경우
     * @param oldVersion
     * @param newVersion
     */
    public abstract void onDowngradeDatabase(int oldVersion,int newVersion);
    /**
     * asset 에 있는 디비파일을 앱 내부로 복사.
     * 기존에 있는 경우에는 삭제하고 복사.
     */
    public void copyDatabase(){
        DatabaseCopy copy = new DatabaseCopy();
        copy.copyDatabase(context,dbName);
    }

    public void copyDatabaseHashFile(){
        //asset 의 HashCode 를 읽어온다.
        String assetHashCode = "";
        try{
            assetHashCode = readFromAssets(context,"db/"+dbHashFileName);
        } catch (Exception e){
            assetHashCode = "";
        }
        try {
            writeInternalFile(context, dbHashFileName, assetHashCode);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    /**
     * copy 하기전에 중복 체크.
     * @return true 중복 false 중복아님
     */
    public boolean isDuplicatedDatabase()
    {
        //저장된 파일이 없는 경우는 false 를 리턴한다.
        if(!fileExists(context,dbHashFileName)){
            debug("hashFile not found");
            return false;
        }

        //저장된 파일의 해쉬코드를 읽어온다.
        String appHashCode = readInternalFile(context,dbHashFileName);

        //asset 의 HashCode 를 읽어온다.
        String assetHashCode = "";
        try{
            assetHashCode = readFromAssets(context,"db/"+dbHashFileName);
        } catch (Exception e){

        }
        debug("[appHashCode]"+appHashCode);
        debug("[assetHashCode]"+assetHashCode);

        //둘을 비교한다.
        if(appHashCode.equals(assetHashCode)){
            //중복
            return true;
        } else {
            return false;
        }

    }

    public boolean fileExists(Context context, String filename) {
        File file = context.getFileStreamPath(filename);
        if(file == null || !file.exists()) {
            return false;
        }
        return true;
    }

    /**
     * asset 에서 String 을 읽어오는 메서드.
     * @param context
     * @param assetName
     * @return
     * @throws IOException
     */
    public String readFromAssets(Context context,String assetName) throws IOException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(assetName)));
        // do reading, usually loop until end of file reading
        StringBuilder sb = new StringBuilder();
        String mLine = reader.readLine();
        while (mLine != null) {
            sb.append(mLine); // process line
            mLine = reader.readLine();
        }
        reader.close();
        return sb.toString();
    }


    /**
     * Internal 저장소에 텍스트 파일을 저장하는 메서드
     * @param context
     * @param filename
     * @param value
     * @throws IOException
     */
    public void writeInternalFile(Context context, String filename, String value) throws IOException
    {
        FileOutputStream outputStream;
        outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
        outputStream.write(value.getBytes());
        outputStream.close();
    }

    /**
     * Internal 저장소에서 텍스트 파일을 읽어오는 메서드
     * @param context
     * @param filename
     * @return
     */
    public String readInternalFile(Context context, String filename)
    {
        try {
            FileInputStream fis = context.openFileInput(filename);
            byte[] data = new byte[fis.available()];
            while (fis.read(data) != -1) {
                ;
            }
            fis.close();
            return new String(data);
        } catch(Exception e) {
            debug("loadFileHashCode Exception:" + e.toString());
            return "";
        }
    }

    /**
     * Asset 의 파일을 Internal 저장소로 복사하는 메서드.
     * @param context
     * @param filePath
     * @param assetName
     * @throws Exception
     */
    public void copyFileFromAsset(Context context,String filePath,String assetName) throws Exception {
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;

        File file = new File(filePath);
        // 파일이 이미 있는 경우 파일을 삭제한다.
        if (file.exists()) {
            file.delete();
        }
        //assets 에 있는 DB 파일을 읽어온다.
        AssetManager assetManager = context.getAssets();
        InputStream is = assetManager.open(assetName);
        BufferedInputStream bis = new BufferedInputStream(is);

        //파일을 새로 생성
        file.createNewFile();

        //파일 Copy 동작
        fos = new FileOutputStream(file);
        bos = new BufferedOutputStream(fos);
        int read = -1;
        byte[] buffer = new byte[1024];
        while ((read = bis.read(buffer, 0, 1024)) != -1) {
            bos.write(buffer, 0, read);
        }
        bos.flush();
        bos.close();
        fos.close();
        bis.close();
        is.close();
    }


    public class OpenHelper extends SQLiteOpenHelper {
        /**
         * 생성자
         * @param context
         * @param name
         * @param factory
         * @param version
         */
        public OpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
            super(context, name, factory, version);
        }
        /**
         * DB가 처음 만들어 질 때 호출.
         */
        @Override
        public void onCreate(SQLiteDatabase db){
            onCreateDatabase();
        }
        /**
         * DB를 업그레이드 할 때 호출.
         * SQLiteOpenHelper 에서는 abstract 로 선언되어있는 메서드.
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            onUpgradeDatabase(oldVersion,newVersion);
        }
        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onDowngradeDatabase(oldVersion,newVersion);
        }
    }

    /**
     * 데이터베이스 파일 복사를 위한 클래스
     */
    public class DatabaseCopy{
        public void copyDatabase(Context context,String dbName) {
            try {
                debug("[CopyDatabase] Start copying databases -> " + dbName);

                //경로 지정
                String package_name = context.getPackageName();
                String folderPath = new StringBuffer("/data/data/").append(package_name).append("/databases").toString();
                String filePath = new StringBuffer("/data/data/").append(package_name).append("/databases/").append(dbName).toString();

                File folder = new File(folderPath);

                // data/data/databases 폴더가 없을 경우 폴더를 생성해준다.
                if (!folder.exists()) {
                    debug("[CopyDatabase] Create Database Folder");
                    folder.mkdirs();
                }
                copyFileFromAsset(context, filePath, "db/" + dbName);
                debug("[CopyDatabase] Complete CopyDatabase");
            } catch (Exception e){
                debug("[CopyDatabase] Failed - copyDatabase Exception");
            } finally {
                debug("[CopyDatabase] Closes CopyDatabase Method");
            }
        }
     }
}
