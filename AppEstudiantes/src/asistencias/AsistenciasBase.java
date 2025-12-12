package asistencias;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import db.ConexionMysql;
import modelos.Asistencia;

public class AsistenciasBase {
    private final ConexionMysql conexionDB = new ConexionMysql();
    
    // Registrar la asistencia mediante una consulta SQL
    public boolean registrarAsistencia(Asistencia asistencia) {
        String sql = """
            INSERT INTO asistencias (num_control, materia, fecha, estado)
            VALUES (?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE estado = VALUES(estado)
            """;
        
        try (Connection cn = conexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setLong(1, asistencia.getNumControl());
            ps.setString(2, asistencia.getMateria());
            ps.setDate(3, Date.valueOf(asistencia.getFecha()));
            ps.setString(4, asistencia.getEstado());
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al registrar asistencia: " + e.getMessage());
            return false;
        }
    }
    
    // Registrar varias asistencias a la vez
    public boolean registrarAsistenciasLote(List<Asistencia> asistencias) {
        String sql = """
            INSERT INTO asistencias (num_control, materia, fecha, estado)
            VALUES (?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE estado = VALUES(estado)
            """;
        
        Connection cn = null;
        try {
            cn = conexionDB.conectar();
            cn.setAutoCommit(false);
            
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                for (Asistencia a : asistencias) {
                    ps.setLong(1, a.getNumControl());
                    ps.setString(2, a.getMateria());
                    ps.setDate(3, Date.valueOf(a.getFecha()));
                    ps.setString(4, a.getEstado());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            
            cn.commit();
            System.out.println("✅ " + asistencias.size() + " asistencias registradas");
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error al registrar asistencias: " + e.getMessage());
            if (cn != null) {
                try {
                    cn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error en rollback: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            if (cn != null) {
                try {
                    cn.setAutoCommit(true);
                    cn.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar conexión: " + e.getMessage());
                }
            }
        }
    }
    
    // Se obtiene la lista de asistencias de un grupo completo
    public List<AsistenciaConAlumno> obtenerAsistenciasGrupo(int grupoId, String materia, LocalDate fecha) {
        List<AsistenciaConAlumno> lista = new ArrayList<>();
        String sql = """
            SELECT 
                u.id,
                u.no_control,
                u.nombre,
                u.apellido,
                COALESCE(a.estado, '') as estado
            FROM usuarios u
            LEFT JOIN asistencias a ON u.no_control = a.num_control 
                AND a.materia = ? 
                AND a.fecha = ?
            WHERE u.grupo_id = ? AND u.role = 'ESTUDIANTE'
            ORDER BY u.apellido, u.nombre
            """;
        
        try (Connection cn = conexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setString(1, materia);
            ps.setDate(2, Date.valueOf(fecha));
            ps.setInt(3, grupoId);
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                AsistenciaConAlumno item = new AsistenciaConAlumno(
                    rs.getInt("id"),
                    rs.getLong("no_control"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("estado")
                );
                lista.add(item);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener asistencias del grupo: " + e.getMessage());
        }
        
        return lista;
    }
    
    // Se hace un contador con una consulta SQL al numero de faltas que tiene el alumno
    public int contarFaltas(long numControl, String materia) {
        String sql = """
            SELECT COUNT(*) as total
            FROM asistencias
            WHERE num_control = ? AND materia = ? AND estado = 'F'
            """;
        
        try (Connection cn = conexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setLong(1, numControl);
            ps.setString(2, materia);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
            
        } catch (SQLException e) {
            System.err.println("Error al contar faltas: " + e.getMessage());
        }
        
        return 0;
    }

    // Definir el estado de la asistencia
    public List<ResumenAsistencia> obtenerResumenAlumno(long numControl) {
        List<ResumenAsistencia> resumen = new ArrayList<>();
        String sql = """
            SELECT 
                materia,
                SUM(CASE WHEN estado = 'A' THEN 1 ELSE 0 END) as asistencias,
                SUM(CASE WHEN estado = 'F' THEN 1 ELSE 0 END) as faltas,
                SUM(CASE WHEN estado = 'P' THEN 1 ELSE 0 END) as justificantes,
                COUNT(*) as total
            FROM asistencias
            WHERE num_control = ?
            GROUP BY materia
            """;
        
        try (Connection cn = conexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setLong(1, numControl);
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ResumenAsistencia r = new ResumenAsistencia(
                    rs.getString("materia"),
                    rs.getInt("asistencias"),
                    rs.getInt("faltas"),
                    rs.getInt("justificantes"),
                    rs.getInt("total")
                );
                resumen.add(r);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener resumen: " + e.getMessage());
        }
        
        return resumen;
    }
    
    // Se obtiene el historial de las asistencias(Asistencias totales) del alumno
    public List<Asistencia> obtenerHistorialAsistencias(long numControl, String materia, 
                                                         LocalDate fechaInicio, LocalDate fechaFin) {
        List<Asistencia> lista = new ArrayList<>();
        String sql = """
            SELECT id, num_control, materia, fecha, estado
            FROM asistencias
            WHERE num_control = ? AND materia = ? AND fecha BETWEEN ? AND ?
            ORDER BY fecha DESC
            """;
        
        try (Connection cn = conexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setLong(1, numControl);
            ps.setString(2, materia);
            ps.setDate(3, Date.valueOf(fechaInicio));
            ps.setDate(4, Date.valueOf(fechaFin));
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Asistencia(
                    rs.getInt("id"),
                    rs.getLong("num_control"),
                    rs.getString("materia"),
                    rs.getDate("fecha").toLocalDate(),
                    rs.getString("estado")
                ));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener historial: " + e.getMessage());
        }
        
        return lista;
    }
    
    // Eliminar una asistencia
    public boolean eliminarAsistencia(int id) {
        String sql = "DELETE FROM asistencias WHERE id = ?";
        
        try (Connection cn = conexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar asistencia: " + e.getMessage());
            return false;
        }
    }
    
    // ------------- Clase Auxiliar para métodos base ---------------
    
    public static class AsistenciaConAlumno {
        public int usuarioId;
        public long numControl;
        public String nombre;
        public String apellido;
        public String estado; // "A", "F", "P" o vacío
        
        public AsistenciaConAlumno(int usuarioId, long numControl, String nombre, 
                                   String apellido, String estado) {
            this.usuarioId = usuarioId;
            this.numControl = numControl;
            this.nombre = nombre;
            this.apellido = apellido;
            this.estado = estado;
        }
        
        public String getNombreCompleto() {
            return apellido + ", " + nombre;
        }
    }

    public static class ResumenAsistencia {
        public String materia;
        public int asistencias;
        public int faltas;
        public int justificantes;
        public int total;
        
        public ResumenAsistencia(String materia, int asistencias, int faltas, 
                                 int justificantes, int total) {
            this.materia = materia;
            this.asistencias = asistencias;
            this.faltas = faltas;
            this.justificantes = justificantes;
            this.total = total;
        }
        
        public double getPorcentajeAsistencia() {
            if (total == 0) return 0;
            return ((double)(asistencias + justificantes) / total) * 100;
        }
        
        public String getEstadoAsistencia() {
            double porcentaje = getPorcentajeAsistencia();
            if (porcentaje >= 90) return "Excelente";
            if (porcentaje >= 80) return "Bueno";
            if (porcentaje >= 70) return "Regular";
            return "Crítico";
        }
    }
}