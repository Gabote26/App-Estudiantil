package calificaciones;

import java.sql.*;
import db.ConexionMysql;
import modelos.Calificacion;

public class CalificacionesBase {
	private final ConexionMysql conexionDB = new ConexionMysql();

	public Calificacion getCalificaciones(String numControl) {
		String sql = "SELECT * FROM calificaciones WHERE num_control = ?";
		try (Connection cn = conexionDB.conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {
			ps.setString(1, numControl);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return new Calificacion(rs.getString("num_control"), rs.getDouble("calf_lengua"),
						rs.getDouble("calf_humanidades"), rs.getDouble("calf_matematicas"),
						rs.getDouble("calf_sociales"), rs.getDouble("calf_ciencias"));
			}
		} catch (SQLException e) {
			System.err.println("Error al obtener calificaciones: " + e.getMessage());
		}
		return null;
	}

	public boolean insertarCalificaciones(Calificacion c) {
		String sql = "INSERT INTO calificaciones (num_control, calf_lengua, calf_humanidades, calf_matematicas, calf_sociales, calf_ciencias) VALUES (?, ?, ?, ?, ?, ?)";
		try (Connection cn = conexionDB.conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {
			ps.setString(1, c.getNumControl());
			ps.setDouble(2, c.getCalfLengua());
			ps.setDouble(3, c.getCalfHumanidades());
			ps.setDouble(4, c.getCalfMatematicas());
			ps.setDouble(5, c.getCalfSociales());
			ps.setDouble(6, c.getCalfCiencias());
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error al insertar calificaciones: " + e.getMessage());
			return false;
		}
	}

	public boolean actualizarCalificaciones(Calificacion c) {
		String sql = "UPDATE calificaciones SET calf_lengua=?, calf_humanidades=?, calf_matematicas=?, calf_sociales=?, calf_ciencias=? WHERE num_control=?";
		try (Connection cn = conexionDB.conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {
			ps.setDouble(1, c.getCalfLengua());
			ps.setDouble(2, c.getCalfHumanidades());
			ps.setDouble(3, c.getCalfMatematicas());
			ps.setDouble(4, c.getCalfSociales());
			ps.setDouble(5, c.getCalfCiencias());
			ps.setString(6, c.getNumControl());
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error al actualizar calificaciones: " + e.getMessage());
			return false;
		}
	}
}
