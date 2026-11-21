package ClasesDAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static final String URL = "jdbc:mysql://172.16.1.64:3306/nombre_bdd";
    //private static final String URL = "jdbc:mysql://127.0.0.1:3306//silent_hill";
    private static final String USUARIO =  "bdd1"; //"root";
    private static final String PASSWORD =  "bdd1"; //"nueva_pass";

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL,USUARIO,PASSWORD);
    }
}