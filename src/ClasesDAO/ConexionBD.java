package ClasesDAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static final String URL = "jdbc:mysql://127.0.0.1:3306/silent_hill_mdp";
    private static final String USUARIO = "root";
    private static final String PASSWORD = "nueva_pass";
    /*
    private static final String URL = "jdbc:mysql://172.16.1.64:3306/silent_hill_mdp";
    private static final String USUARIO =  "bdd1";
    private static final String PASSWORD =  "bdd1";
*/
    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL,USUARIO,PASSWORD);
    }
}