package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author rocob
 */
public class ProyectoCoco {
    private final String conString = "jdbc:mysql://localhost:3306/proyectococo";
    private final String user = "Diego";
    private final String password = "Diego";
    private Connection con;
    
    public ProyectoCoco() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(conString, user, password);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public final Connection getConnection() {
        return con;
    }
}
