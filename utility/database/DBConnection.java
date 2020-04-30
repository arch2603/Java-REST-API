package utility.database;

import utility.LogCases;
import utility.Logs;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.FileHandler;

public class DBConnection {
    private Connection con;
    private Properties props;
    File file;
    String filename = "D:\\Liam\\Documents\\JavaProjects\\logs\\log.txt";
    Logs logger = new Logs();
    FileHandler fh = null;

    public DBConnection(){
        try{
            file = new File(filename);
            fh = new FileHandler(file.getAbsolutePath(), true);
            logger.setup(fh);

            String url = "jdbc:mysql://localhost:3306/inventory_mgmnt_test";
            String user = "root";
            String password = "password";
            String usessl = "false";
            String autorecon = "true";

            props = new Properties();
            props.setProperty("user", user);
            props.setProperty("password", password);
            props.setProperty("useSSL", usessl);
            props.setProperty("autoReconnect", autorecon);

            con = DriverManager.getConnection(url, props);
            logger.logMessages(LogCases.LOG_MESSAGE, "Database connected successfully");

        }catch(SQLException sq){
            logger.logMessages(LogCases.LOG_MESSAGE_EXCEPTION, sq.getMessage());
        } catch (IOException e) {
            logger.logMessages(LogCases.LOG_MESSAGE_EXCEPTION, e.getMessage());
        }
    }
    public Connection getCon(){
        return this.con;
    }

}
