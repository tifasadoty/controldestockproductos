
package cl.control.controldestockproductos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static final String URL = "jdbc:mysql://localhost:3306/controldestock";
    private static final String USER = "root";
    private static final String PASSWORD = "Tu contraseña";

    public static Connection getConnection() throws SQLException {
        System.out.println("se ha conectado a la base de dato");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

   
