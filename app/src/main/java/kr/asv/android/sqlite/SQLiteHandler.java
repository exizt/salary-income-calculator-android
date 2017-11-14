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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * Created by Administrator on 2016-04-29.
 * 기존의 DB 파일과 해쉬 체크를 하는 로직이 담겨져 있음. 주의할 것.
 *
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

    // db 파일 카피 프로세스 를 진행했는지 여부
    protected boolean isPassingCopyProcess = false;

    //기타
    private boolean isDebug;

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
        this.isDebug = debug;
        this.context = context;
        this.dbName = dbName;
        this.dbVersion = dbVersion;
        this.dbHashFileName = new StringBuffer(this.dbName).append(".hash").toString();
        debug("[initialize] dbHashFileName > ",dbHashFileName);

        // Assets 내에 데이터베이스 파일이 존재하는지 체크를 해봐야 한다. 없다면, 이 로직은 실패이고,
        // 앱 전체가 동작해서는 안 된다.
        try{
            boolean a = Arrays.asList(context.getAssets().list("")).contains(new StringBuffer("db/").append(dbName).toString());
        } catch (Exception e){
            debug("[initialize] not found db file in assets");
        }


        //동일한지 체크
        if(!isEqualDatabaseFile()) {
            //기존 디비와 assets 의 디비가 서로 다를 경우에 copy 실행.
            debug("[initialize] not equal db files. start copy processing");
            //데이터베이스 복사
            copyDatabase();
            //데이터베이스 복사 후 해쉬 저장
            copyDatabaseHashFile();
        } else {
            debug("[initialize] pass db copy processing. ");
            isPassingCopyProcess = true;
        }

        this.openHelper = new OpenHelper(context,dbName,null,dbVersion);

        try{
            this.db = this.openHelper.getWritableDatabase();
        } catch(Exception e){
            debug("[initialize] openHelper getWritableDatabase > Exception : ", e.toString());
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
     * copy 하기전에 동일한 파일인지 확인
     * @return true 동일 / false 동일하지않음
     */
    public boolean isEqualDatabaseFile()
    {
        debug("[isEqualDatabaseFile] >> ");
        //저장된 파일이 없는 경우는 false 를 리턴한다.
        if(!fileExists(context,dbHashFileName)){
            debug("[isEqualDatabaseFile] > hashFile not found");
            return false;
        }
        //debug("isDuplicatedDatabase > fileExists true");

        //저장된 파일의 해쉬코드를 읽어온다.
        String appHashCode = readInternalFile(context,dbHashFileName);
        debug("[isEqualDatabaseFile] > internal DB Hash Code > ",appHashCode);

        //asset 의 HashCode 를 읽어온다.
        String assetHashCode = "";
        try{
            assetHashCode = readFromAssets(context,new StringBuffer("db/").append(dbHashFileName).toString());
            debug("[isEqualDatabaseFile] > Asset DB Hash Code > ",assetHashCode);
        } catch (Exception e){
            debug("[isEqualDatabaseFile] > Asset DB Hash File Not found or Not Read");

            // assets 에서 못 읽어올 경우, 일단 false 로 가정한다. (db 파일이 다를 것으로 가정)
            return false;
        }

        // 값이 동일한지 확인해본다.
        if(appHashCode.equals(assetHashCode)){
            return true;
        } else {
            return false;
        }
    }

    /**
     * asset 에 있는 디비파일을 앱 내부로 복사.
     * 기존에 있는 경우에는 삭제하고 복사.
     *
     */
    public void copyDatabase(){
        createDBFileFromAssets(context,dbName);
    }

    /**
     * file copy 를 안 쓰고 직접 적는 방식을 사용하는 이유는, 경우에 따라서 txt 파일 등은
     * 복사 과정에서 압축해서 복사할 수가 있다고 한다. 그래서 그냥 내용을 읽어서 write 하는 방식으로
     * 하기로 하였다. (이게 더 빠르니까)
     */
    public void copyDatabaseHashFile(){
        //asset 의 HashCode 를 읽어온다.
        String assetHashCode = "";
        try{
            assetHashCode = readFromAssets(context,new StringBuffer("db/").append(dbHashFileName).toString());
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
     * 파일 유무 체크 <br>
     * 접근 권한이나 이상이 있을 시에는 false 리턴. 파일이 없다고 가정한다.
     * @param context
     * @param filename
     * @return
     */
    public boolean fileExists(Context context, String filename) {
        try{
            File file = context.getFileStreamPath(filename);
            if(file == null || !file.exists()) {
                return false;
            }
            return true;
        } catch (Exception e){
            return false;
        }
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
        debug("[readInternalFile] >> ");
        try {
            FileInputStream fis = context.openFileInput(filename);
            byte[] data = new byte[fis.available()];
            while (fis.read(data) != -1) {
                ;
            }
            fis.close();
            return new String(data);
        } catch(Exception e) {
            debug("[readInternalFile] loadFileHashCode Exception:", e.toString());
            return "";
        }
    }

    /**
     * 동작하는 기기의 내부에 /data/data/{package}/databases 폴더를 생성한다. <br>
     * assets 의 *.db 파일을 이 안으로 복사(생성) 해준다.
     * @param context
     * @param dbName
     */
    public void createDBFileFromAssets(Context context,String dbName) {
        try {
            debug("[createDBFileFromAssets] >> DB Name > ",dbName);
            //String package_name = context.getPackageName();

            // DB 파일의 폴더 (내부 저장소)
            //String folderPath = new StringBuffer("/data/data/").append(package_name).append("/databases").toString();
            String folderPath = context.getDatabasePath(dbName).getParent();

            // data/data/databases 폴더가 없을 경우 폴더를 생성해준다.
            File folder = new File(folderPath);
            if (!folder.exists()) {
                debug("[createDBFileFromAssets] Create Database Folder");
                folder.mkdirs();
            } else {
                debug("[createDBFileFromAssets] Already exists Database Folder. passing create folder");
            }

            // DB 파일의 경로 (내부 저장소)
            //String filePath = new StringBuffer(folderPath).append("/").append(dbName).toString();
            String filePath = context.getDatabasePath(dbName).getPath();
            String assetName = new StringBuffer("db/").append(dbName).toString();
            debug("[createDBFileFromAssets] copyAssetFileToLocalStorage before");
            copyAssetFileToLocalStorage(context, filePath, assetName);

            debug("[createDBFileFromAssets] Complete");
        } catch (IOException e){
            debug("[createDBFileFromAssets] Exception > IOException",e.toString());
        } catch (Exception e){
            debug("[createDBFileFromAssets] Exception > Exception",e.toString());
        } finally {
            //debug("[createDBFileFromAssets] Closes CopyDatabase Method");
        }
    }

    /**
     * Asset 의 파일을 Internal 저장소로 복사하는 메서드.<br>
     * '이미 존재하고 있는 경우'에는 지우고 replace 한다.
     * 성능 개선 버전. (작업일 : 2017/11/08)
     * @param context Assets 의 Context
     * @param toFilePath 내부({storage?}/data/data/{package?})에 넣게 될 파일 경로
     * @param assetFileName 복사하고싶은 Assets 의 이름
     * @throws Exception
     */
    public void copyAssetFileToLocalStorage(Context context,String toFilePath,String assetFileName) throws Exception {
        BufferedInputStream bis  = null;
        BufferedOutputStream bos = null;
        InputStream ins = null;
        try{
            File file = new File(toFilePath);
            // 파일이 이미 있는 경우 파일을 삭제한다.
            if (file.exists()) {
                file.delete();
            }
            //파일을 새로 생성
            file.createNewFile();

            //AssetFileDescriptor assetFileDescriptor = context.getAssets().openFd(assetFileName);
            //FileDescriptor fileDescriptor = assetFileDescriptor.getFileDescriptor();
            ins = context.getAssets().open(assetFileName);
            bis = new BufferedInputStream(ins);

            //파일 Copy 동작
            bos = new BufferedOutputStream(new FileOutputStream(file));

            int read = -1;
            byte[] readBuffer = new byte[1024];
            while ((read = bis.read(readBuffer, 0, 1024)) != -1) {
                bos.write(readBuffer, 0, read);
            }

            bis.close();
            bos.close();
            ins.close();
        } catch (Exception e){
            throw e;
        } finally {
            if(bis != null) bis.close();
            if(bos != null) bos.close();
            if(ins != null) ins.close();
        }
    }

    /**
     * Deprecated <br>
     * Asset 의 파일을 Internal 저장소로 복사하는 메서드.<br>
     * '이미 존재하고 있는 경우'에는 지우고 replace 한다.
     * @param context Assets 의 Context
     * @param toFilePath 내부({storage?}/data/data/{package?})에 넣게 될 파일 경로
     * @param fromAssetName 복사하고싶은 Assets 의 이름
     * @throws Exception
     */
    public void createOrReplaceFileFromAsset(Context context,String toFilePath,String fromAssetName) throws Exception {
        AssetManager assetManager = context.getAssets();

        File file = new File(toFilePath);
        // 파일이 이미 있는 경우 파일을 삭제한다.
        if (file.exists()) {
            file.delete();
        }
        //파일을 새로 생성
        file.createNewFile();

        //assets 에 있는 DB 파일을 읽어온다.
        InputStream is = assetManager.open(fromAssetName);
        BufferedInputStream bis = new BufferedInputStream(is);

        FileOutputStream fos = null;
        BufferedOutputStream bos = null;

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

    /**
     * 디버깅
     * @param log
     */
    private void debug(String log)
    {
        if(isDebug) {
            Log.e("[EXIZT-DEBUG]", new StringBuilder("[SQLiteHandler]").append(log).toString());
        }
    }
    private void debug(String log, String log2)
    {
        debug(new StringBuffer(log).append(log2).toString());
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

}
