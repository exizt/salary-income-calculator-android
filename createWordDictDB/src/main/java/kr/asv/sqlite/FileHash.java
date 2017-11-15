package kr.asv.sqlite;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 파일에서 해쉬 코드 를 추출하거나 해쉬 파일을 생성하는 클래스
 * Created by EXIZT on 2017-11-13.
 */

public class FileHash {
    @SuppressWarnings("FieldCanBeLocal")
    private boolean isDebug = false;
    private String debugTag = "[EXIZT-DEBUG][FileHash]";

    @SuppressWarnings("unused")
    public FileHash(){

    }

    @SuppressWarnings("unused")
    public FileHash(boolean debug){
        isDebug = debug;
    }

    /**
     *
     * @param databaseFilePath database file
     * @param hashFilePath hash file
     */
    @SuppressWarnings("unused")
    public void writeHashFile(String databaseFilePath,String hashFilePath)
    {
        //데이터베이스 파일의 해쉬값 생성
        File file = new File(databaseFilePath);
        String hash = getHashcode(file);
        debug("-----------------",true);
        debug("Hash Code : "+hash,true);
        debug("-----------------",true);

        /*
         * 해쉬값을 별도 파일에 저장
         */
        try
        {
            FileWriter fw = new FileWriter(hashFilePath); // 절대주소 경로 가능
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(hash);
            bw.newLine(); //줄바꿈
            bw.close();
        }
        catch (IOException e)
        {
            debug(e.toString());
        }
    }

    /**
     * byte 배열로부터 SHA-256를 이용해 해시된 값을 얻는 메서드
     *
     * NoSuchAlgorithmException :  getInstance메서드의 인자 전달한 암호화 암고리즘이 존재하지 않을 때 발생
     * @return message의 해시된 값으로 16진수 표현의 String으로 반환된다.
     */
    @SuppressWarnings({"unused", "WeakerAccess"})
    public String getHashcode(File file) {
        FileInputStream fis = null;
        try {
            // SHA-256 를 사용하기 위해 MessageDigest 클래스로부터 인스턴스를 얻는다.
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            fis = new FileInputStream(file);
            byte[] dataBytes = new byte[1024];
            int nread;
            while ((nread = fis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nread);
            }

            // 해싱된 byte 배열을 digest메서드의 반환값을 통해 얻는다.
            byte[] hashbytes = md.digest();
            // 해싱할 byte배열을 넘겨준다.
            // SHA-256의 경우 메시지로 전달할 수 있는 최대 bit 수는 2^64-1개 이다.

            // 보기 좋게 16진수로 만드는 작업
            StringBuilder sb = new StringBuilder();
            for (byte hashbyte : hashbytes) {
                // %02x 부분은 0 ~ f 값 까지는 한자리 수이므로 두자리 수로 보정하는 역할을 한다.
                sb.append(String.format("%02x", hashbyte & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | IOException e) {
            debug(e.toString());
        } finally {
            try {
                if(fis != null){
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
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
    @SuppressWarnings({"unused", "SameParameterValue"})
    private void debug(String msg,boolean debug)
    {
        //noinspection ConstantConditions
        if(isDebug || debug) {
            System.out.println(debugTag + msg);
        }
    }

    @SuppressWarnings("unused")
    public void setDebugTag(String debugTag) {
        this.debugTag = debugTag;
    }
}
