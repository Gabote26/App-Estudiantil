package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionMysql {
    private static final String URL = 
    		"jdbc:mysql://students-data-mysql-studentsdatamysql.c.aivencloud.com:11529/students_data_mysql"
            + "?sslMode=REQUIRED"
            + "&serverTimezone=UTC"
            + "&connectTimeout=8000"
            + "&socketTimeout=8000";

    private static final String USER = "avnadmin";
    private static final String PASSWORD = "AVNS_X8ZLHz18HP2o0JB2eGv";

    public Connection conectar() {
        Connection cn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            cn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Conectado correctamente a la base de datos");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ No se encontró el driver JDBC: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("❌ Error al conectar con la base de datos: " + e.getMessage());
        }
        return cn;
    }
}
