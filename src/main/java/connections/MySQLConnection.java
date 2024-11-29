package connections;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;


public class MySQLConnection {
    private static MySQLConnection instance;
    private Connection sqlConnection;

    private MySQLConnection(){
        try{
            Properties config = new Properties();
            config.load(new FileInputStream("src/main/resources/config.properties"));
            // Connect MySQL
            String sqlURL = config.getProperty("sql.url");
            String sqlUsername = config.getProperty("sql.username");
            String sqlPassword = config.getProperty("sql.password");
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                this.sqlConnection = DriverManager.getConnection(sqlURL, sqlUsername, sqlPassword);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
            //throw new SQLException("Cannot load config",e);
        }
    }

    public static MySQLConnection getInstance(){
        if (instance == null) {
            instance = new MySQLConnection();
        } else {
            try {
                if (instance.getSQLConnection().isClosed()) {
                    instance = new MySQLConnection();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    public Connection getSQLConnection(){
        return sqlConnection;
    }

    // Close connection
    public void closeSQLConn(Connection connection) {
        if (connection!= null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}