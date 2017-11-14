package kr.asv.apps.salarycalculator;

import kr.asv.sqlite.FileHash;
import kr.asv.sqlite.SQLiteAdapter;

/**
 * 용어 사전 database 를 생성하는 클래스
 * Created by EXIZT on 2017-11-13.
 */

class MainController {
    @SuppressWarnings("FieldCanBeLocal")
    private final String DB_PATH = "..\\app\\src\\main\\assets\\db\\salarytax_information.db";
    @SuppressWarnings("FieldCanBeLocal")
    private final String DB_HASH_PATH = "..\\app\\src\\main\\assets\\db\\salarytax_information.db.hash";
    @SuppressWarnings("FieldCanBeLocal")
    private final boolean isDebug = false;
    @SuppressWarnings("FieldCanBeLocal")
    private final String DEBUG_TAG = "[EXIZT-DEBUG]";

    /**
     * 생성자
     */
    MainController(){
        debug("컨트롤러 생성",true);

        // 테이블 데이터 생성
        SQLiteAdapter database = new SQLiteAdapter(DB_PATH);
        WordDictionaryTable wordDictionaryTable = new WordDictionaryTable(database);
        wordDictionaryTable.createDatabase();

        //데이터베이스 파일의 해쉬값 생성
        FileHash fileHash = new FileHash();
        fileHash.writeHashFile(DB_PATH, DB_HASH_PATH);

        // 완료
        debug("Database File has been created.",true);
    }

    /**
     * 디버깅 메서드
     * @param msg messages
     */
    @SuppressWarnings("unused")
    private void debug(String msg)
    {
        debug(msg,false);
    }

    /**
     * 디버깅 메서드.
     * 강제적으로 출력하고 싶을 때에 사용한다.
     * debug("messages",true)
     * @param msg messages
     */
    @SuppressWarnings("unused")
    private void debug(String msg,boolean debug)
    {
        //noinspection ConstantConditions
        if(isDebug || debug) {
            System.out.println(DEBUG_TAG + msg);
        }
    }
}
