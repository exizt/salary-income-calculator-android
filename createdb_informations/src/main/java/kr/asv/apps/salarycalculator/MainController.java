package kr.asv.salary;

import kr.asv.sqlite.FileHash;
import kr.asv.sqlite.SQLiteAdapter;

/**
 * Created by Adminn on 2017-11-13.
 */

public class MainController {
    private final String dbPath = "..\\app\\src\\main\\assets\\db\\salarytax_information.db";
    private final String dbHashPath = "..\\app\\src\\main\\assets\\db\\salarytax_information.db.hash";

    @SuppressWarnings("FieldCanBeLocal")
    private final boolean isDebug = true;

    public MainController(){
        debug("데이터베이스 작성 시작");

        //SQLiteHandler dbHandler = new SQLiteHandler("salarytax_information.db");
        //TableDictionary tableDictionary = new TableDictionary(dbHandler.getDatabase());

        TableDictionary tableDictionary = new TableDictionary(new SQLiteAdapter(dbPath));
        tableDictionary.createWithInsert();

        FileHash fileHash = new FileHash();
        fileHash.writeHashFile(dbPath,dbHashPath);

        //tableDictionary.test();
        //System.out.println(tableDictionary.dataList.toString());

    }

    /**
     * 디버깅 메서드
     * @param msg messages
     */
    private void debug(String msg)
    {
        //noinspection ConstantConditions
        if(isDebug) {
            System.out.println("[EXIZT-MODULE-DEBUG] " + msg);
        }
    }
}
