import java.sql.DriverManager;
import java.sql.Statement;

public class Conn {
    java.sql.Connection connection;
    Statement statement;


    public Conn(){
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_management_system", "root","hardik310805");

            statement = connection.createStatement();

        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
