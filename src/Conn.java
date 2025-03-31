import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Conn {
    private Connection connection;
    private Statement statement;

    public Conn() {
        try {
            // Load properties from file
            Properties props = new Properties();
            FileInputStream fis = new FileInputStream("C:\\Users\\DELL\\Desktop\\hospitaljava\\Hospital-Management-System\\db_config.properties");
            props.load(fis);

            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");

            // Establish Connection
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();

            fis.close();
        } catch (IOException e) {
            System.out.println("Error loading database configuration.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Database connection failed.");
            e.printStackTrace();
        }

    }

    public Statement getStatement() {
        return statement;
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        try {
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        } catch (SQLException e) {
            System.out.println("Error closing the connection.");
            e.printStackTrace();
        }
    }
}