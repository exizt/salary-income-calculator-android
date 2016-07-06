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
 * Created by Administrator on 2016-04-10.
 */
public class SQLiteAdapter {
    private Connection connection = null;
    private Statement statement = null;
    private PreparedStatement prepareStatement = null;
    private String dbFileName = "sample.db";

    public SQLiteAdapter(){
        initConnection();
    }
    public SQLiteAdapter(String dbFileName)
    {
        setDbFileName(dbFileName);
        initConnection();
    }
    private void initConnection()
    {
        try
        {
            //여기서 커넥션을 생성해준다.
            connection = DriverManager.getConnection(getDSN());
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
        }
        catch(SQLException e)
        {
            System.err.println(e.getMessage());
        }
    }
    public void setDbFileName(String dbFileName)
    {
        this.dbFileName = dbFileName;
    }
    public Connection getConnection()
    {
        if(connection == null) return null;
        return connection;
    }

    /**
     * 쿼리를 하나 실행시킨다. update, delete 등의 구문.
     * @param query
     */
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
            if(statement!=null) try{ statement.close(); } catch (Exception e) {}
        }
    }
    /**
     * prepared 를 사용할 경우
     * @param query
     * @return
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
    	} catch(SQLException e) {
    		
    	}
    }
    /**
     * 여러 쿼리를 실행시킨다. String 배열을 인자로 사용한다.
     * @param queries
     */
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
            if(statement!=null) try{ statement.close(); } catch (Exception e) {}
        }
    }

    /**
     * 여러 쿼리를 실행시킨다. String 배열을 인자로 사용한다.
     * @param queries
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
            if(statement!=null) try{ statement.close(); } catch (Exception e) {}
        }
    }
    /**
     * DSN 정보를 만들기 위함.
     * 예시) jdbc:splite:sample.db
     * @return
     */
    private String getDSN()
    {
        return new StringBuilder().append("jdbc:sqlite:").append(this.dbFileName).toString();
    }
    /**
     * 소멸자 대용으로 주로 쓴다던 finalize
     * @throws Throwable
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


}
