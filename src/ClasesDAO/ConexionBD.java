package ClasesDAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static final String URL = "jdbc:mysql://172.16.1.64:3306/nombre_tabla";
    private static final String USUARIO = "bdd1";
    private static final String PASSWORD = "bdd1";

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL,USUARIO,PASSWORD);
    }
}