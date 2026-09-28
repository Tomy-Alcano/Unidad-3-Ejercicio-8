import java.sql.*;

public class Conexion {

    private static final String URL = "jdbc:mysql://localhost:3306/programacion3";
    private static final String USER = "root";
    private static final String PASS = "";

    public static Connection conectar()
        throws SQLException{
        return DriverManager.getConnection(URL,USER,PASS);
        }
    }

