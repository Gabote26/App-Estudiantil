package faltas;

import java.sql.*;
import db.ConexionMysql;
import modelos.Faltas;

public class FaltasBase {
	private final ConexionMysql conexionDB = new ConexionMysql();

	public Faltas getFaltas(String numControl) {
		String sql = "SELECT * FROM faltas WHERE num_control = ?";
		try (Connection cn = conexionDB.conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {
			ps.setString(1, numControl);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return new Faltas(rs.getString("num_control"), rs.getInt("faltas_lengua"),
						rs.getInt("faltas_humanidades"), rs.getInt("faltas_matematicas"),
						rs.getInt("faltas_sociales"), rs.getInt("faltas_ciencias"));
			}
		} catch (SQLException e) {
			System.err.println("Error al obtener las faltas: " + e.getMessage());
		}
		return null;
	}

	public boolean insertarFaltas(Faltas c) {
		String sql = "INSERT INTO faltas (num_control, faltas_lengua, faltas_humanidades, faltas_matematicas, faltas_sociales, faltas_ciencias) VALUES (?, ?, ?, ?, ?, ?)";
		try (Connection cn = conexionDB.conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {
			ps.setString(1, c.getNumControl());
			ps.setDouble(2, c.getFaltasLengua());
			ps.setDouble(3, c.getFaltasHumanidades());
			ps.setDouble(4, c.getFaltasMatematicas());
			ps.setDouble(5, c.getFaltasSociales());
			ps.setDouble(6, c.getFaltasCiencias());
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error al insertar las faltas: " + e.getMessage());
			return false;
		}
	}

	public boolean actualizarFaltas(Faltas c) {
		String sql = "UPDATE faltas SET faltas_lengua=?, faltas_humanidades=?, faltas_matematicas=?, faltas_sociales=?, faltas_ciencias=? WHERE num_control=?";
		try (Connection cn = conexionDB.conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {
			ps.setDouble(1, c.getFaltasLengua());
			ps.setDouble(2, c.getFaltasHumanidades());
			ps.setDouble(3, c.getFaltasMatematicas());
			ps.setDouble(4, c.getFaltasSociales());
			ps.setDouble(5, c.getFaltasCiencias());
			ps.setString(6, c.getNumControl());
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			System.err.println("Error al actualizar las faltas: " + e.getMessage());
			return false;
		}
	}
}
