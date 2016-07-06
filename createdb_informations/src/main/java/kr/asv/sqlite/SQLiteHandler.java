package kr.asv.sqlite;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2016-04-11.
 */
public class SQLiteHandler {
    SQLiteAdapter database;
    private String dbPath = "";
    private String dbHashPath = "";

    public SQLiteHandler(String dbName) {
        this.dbPath = ".\\app\\src\\main\\assets\\db\\"+dbName;
        this.dbHashPath = ".\\app\\src\\main\\assets\\db\\"+dbName+".hash";
        this.database = new SQLiteAdapter(dbPath);
    }
    public SQLiteHandler(String dbPath,String dbHashPath) {
        this(dbPath);
        this.dbHashPath = dbHashPath;
    }
    public SQLiteAdapter getDatabase()
    {
        return this.database;
    }

    /**
     * byte 배열로부터 SHA-256를 이용해 해시된 값을 얻는 메서드
     * @return message의 해시된 값으로 16진수 표현의 String으로 반환된다.
     * @throws NoSuchAlgorithmException
     *             getInstance메서드의 인자 전달한 암호화 암고리즘이 존재하지 않을 때 발생
     */
    public String getHashcode(File file) {
        FileInputStream fis = null;
        try {
            // SHA를 사용하기 위해 MessageDigest 클래스로부터 인스턴스를 얻는다.
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            //MessageDigest md = MessageDigest.getInstance("SHA-1");
            fis = new FileInputStream(file);
            byte[] dataBytes = new byte[1024];
            int nread = 0;
            while ((nread = fis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nread);
            }
            ;
            // 해싱된 byte 배열을 digest메서드의 반환값을 통해 얻는다.
            byte[] hashbytes = md.digest();
            // 해싱할 byte배열을 넘겨준다.
            // SHA-256의 경우 메시지로 전달할 수 있는 최대 bit 수는 2^64-1개 이다.

            // 보기 좋게 16진수로 만드는 작업
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < hashbytes.length; i++) {
                // %02x 부분은 0 ~ f 값 까지는 한자리 수이므로 두자리 수로 보정하는 역할을 한다.
                sb.append(String.format("%02x", hashbytes[i] & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return "";
        } catch (FileNotFoundException e) {
            return "";
        } catch (IOException e) {
            return "";
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void writeHashFile()
    {
        writeHashFile(dbPath,dbHashPath);
    }
    /**
     * 해쉬값을 별도 파일에 저장
     * @param databasePath
     * @param hashWritePath
     */
    public void writeHashFile(String databasePath,String hashWritePath)
    {
        //데이터베이스 파일의 해쉬값 생성
        String hash = getHashcode(new File(databasePath));
        System.out.println("-----------------");
        System.out.println(hash);
        System.out.println("-----------------");


        try
        {
            FileWriter fw = new FileWriter(hashWritePath); // 절대주소 경로 가능
            BufferedWriter bw = new BufferedWriter(fw);
            String str = hash;
            bw.write(str);
            bw.newLine(); //줄바꿈
            bw.close();
        }
        catch (IOException e)
        {
            System.err.println(e); // 에러가 있다면 메시지 출력
        }
    }
}
