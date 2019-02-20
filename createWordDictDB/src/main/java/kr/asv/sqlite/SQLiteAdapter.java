package kr.asv.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * SQLite 를 자바 독립적으로 사용하려 하는데, 클래스가 필요해서 만들게 됨.
 * 어댑터 패턴을 활용해서 만들려고 함.
 * Created by EXIZT on 2016-04-10.
 */
public class SQLiteAdapter {
    private Connection connection = null;
    private Statement statement = null;
    private PreparedStatement prepareStatement = null;
    private String dbFilePath;
    @SuppressWarnings("FieldCanBeLocal")
    private final boolean isDebug = false;
    private String debugTag = "[EXIZT-DEBUG][SQLiteAdapter]";

    /**
     * 생성자
     * @param dbFilePath 파일명
     */
    public SQLiteAdapter(String dbFilePath)
    {
        this.dbFilePath = dbFilePath;
        initConnection();
    }

    /**
     * 커넥션을 생성
     */
    private void initConnection()
    {
        debug("initConnection");

        // class 유무 체크
        try {
            //noinspection SpellCheckingInspection
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            debug("ClassNotFoundException",true);
            e.printStackTrace();
        }

        // 커넥션
        try
        {
            //여기서 커넥션을 생성해준다.
            connection = DriverManager.getConnection("jdbc:sqlite:" + this.dbFilePath);
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
        } catch(SQLException e) {
            debug("initConnection > SQLException");
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }

    public Connection getConnection()
    {
        if(connection == null) return null;
        return connection;
    }
    /**
     * 쿼리 샘플 구문
     */
    @SuppressWarnings("unused")
    public void querySample()
    {
        try {
            //statement 를 생성한다.
            Statement statement = connection.createStatement();

            //이건 설정
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            //쿼리 실행
            statement.executeUpdate("drop table if exists person");
            statement.executeUpdate("create table person (id integer, name string)");
            statement.executeUpdate("insert into person values(1, 'leo')");
            statement.executeUpdate("insert into person values(2, 'yui')");

            //조회 쿼리
            ResultSet rs = statement.executeQuery("select * from person");
            while(rs.next())
            {
                // read the result set
                System.out.println("name = " + rs.getString("name"));
                System.out.println("id = " + rs.getInt("id"));
            }
        } catch(SQLException e)
        {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        }
    }


    /**
     * 쿼리를 하나 실행시킨다. update, delete 등의 구문.
     * @param query string 타입 쿼리구문
     */
    @SuppressWarnings("unused")
    public void execute(String query)
    {
        try {
            //statement 를 생성한다.
            Statement statement = connection.createStatement();

            //쿼리 실행
            statement.executeUpdate(query);
        } catch(SQLException e)
        {
            System.err.println(e.getMessage());
        } finally {
            if(statement!=null) try{ statement.close(); } catch (Exception ignored) {}
        }
    }
    /**
     * prepared 를 사용할 경우
     * @param query string 타입 쿼리구문
     * @return statement
     */
    public PreparedStatement prepare(String query)
    {
        try{

            this.prepareStatement = connection.prepareStatement(query);
            return this.prepareStatement;
        } catch(SQLException e){
            return null;
        }
    }
    public void execute()
    {
        try{
            prepareStatement.execute();
        } catch(SQLException ignored) {

        }
    }
    /**
     * 여러 쿼리를 실행시킨다. String 배열을 인자로 사용한다.
     * @param queries strings 쿼리구문 여러개
     */
    @SuppressWarnings("unused")
    public void execute(String[] queries){
        try {
            //statement 를 생성한다.
            statement = connection.createStatement();

            //쿼리 실행
            for(String query : queries)
            {
                statement.executeUpdate(query);
            }
        } catch(SQLException e)
        {
            System.err.println(e.getMessage());
        } finally {
            if(statement!=null) try{ statement.close(); } catch (Exception ignored) {}
        }
    }

    /**
     * 여러 쿼리를 실행시킨다. String 배열을 인자로 사용한다.
     * @param queries 쿼리들
     */
    public void execute(List<String> queries){
        try {
            //statement 를 생성한다.
            statement = connection.createStatement();

            //쿼리 실행
            for(String query : queries)
            {
                statement.executeUpdate(query);
            }
        } catch(SQLException e)
        {
            System.err.println(e.getMessage());
        } finally {
            if(statement!=null) try{ statement.close(); } catch (Exception ignored) {}
        }
    }

    /**
     * 소멸자 대용으로 주로 쓴다던 finalize
     * @throws Throwable 소멸
     */
    @Override
    public void finalize() throws Throwable {
        System.out.println("finalize call");
        try {
            if(connection != null)
                connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            super.finalize();
        }
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
