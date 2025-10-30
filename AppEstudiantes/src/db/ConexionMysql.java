package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionMysql {

	private static final String URL = "jdbc:mysql://localhost:3306/students_data_mysql" + "?useSSL=false"
			+ "&allowPublicKeyRetrieval=true" + "&serverTimezone=UTC";
	private static final String USER = "root";
	private static final String PASSWORD = "";

	public Connection conectar() {
		Connection cn = null;
		try {
			// Cargar driver de MySQL
			Class.forName("com.mysql.cj.jdbc.Driver");

			// Se intenta establecer la conexión
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
