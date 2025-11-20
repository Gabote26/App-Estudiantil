package calificaciones;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import db.ConexionMysql;
import modelos.Calificacion;

public class CalificacionesBase {
    private final ConexionMysql conexionDB = new ConexionMysql();

    public List<Calificacion> getCalificacionesEstudiante(long numControl) {
        List<Calificacion> lista = new ArrayList<>();
        String sql = """
            SELECT id, num_control, materia, parcial_1, parcial_2, parcial_3, promedio
            FROM calificaciones
            WHERE num_control = ?
            ORDER BY materia
            """;
        
        try (Connection cn = conexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setLong(1, numControl);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                lista.add(new Calificacion(
                    rs.getInt("id"),
                    rs.getLong("num_control"),
                    rs.getString("materia"),
                    (Double) rs.getObject("parcial_1"),
                    (Double) rs.getObject("parcial_2"),
                    (Double) rs.getObject("parcial_3")
                ));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener calificaciones: " + e.getMessage());
        }
        
        return lista;
    }
    
    public Calificacion getCalificacion(long numControl, String materia) {
        String sql = """
            SELECT id, num_control, materia, parcial_1, parcial_2, parcial_3, promedio
            FROM calificaciones
            WHERE num_control = ? AND materia = ?
            """;
        
        try (Connection cn = conexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setLong(1, numControl);
            ps.setString(2, materia);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return new Calificacion(
                    rs.getInt("id"),
                    rs.getLong("num_control"),
                    rs.getString("materia"),
                    (Double) rs.getObject("parcial_1"),
                    (Double) rs.getObject("parcial_2"),
                    (Double) rs.getObject("parcial_3")
                );
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener calificación: " + e.getMessage());
        }
        
        return null;
    }

    public boolean guardarCalificacion(Calificacion cal) {
        String sql = """
            INSERT INTO calificaciones (num_control, materia, parcial_1, parcial_2, parcial_3)
            VALUES (?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE 
                parcial_1 = VALUES(parcial_1),
                parcial_2 = VALUES(parcial_2),
                parcial_3 = VALUES(parcial_3)
            """;
        
        try (Connection cn = conexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setLong(1, cal.getNumControl());
            ps.setString(2, cal.getMateria());
            ps.setObject(3, cal.getParcial1());
            ps.setObject(4, cal.getParcial2());
            ps.setObject(5, cal.getParcial3());
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al guardar calificación: " + e.getMessage());
            return false;
        }
    }
    
    public boolean guardarCalificacionesLote(List<Calificacion> calificaciones) {
        String sql = """
            INSERT INTO calificaciones (num_control, materia, parcial_1, parcial_2, parcial_3)
            VALUES (?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE 
                parcial_1 = VALUES(parcial_1),
                parcial_2 = VALUES(parcial_2),
                parcial_3 = VALUES(parcial_3)
            """;
        
        Connection cn = null;
        try {
            cn = conexionDB.conectar();
            cn.setAutoCommit(false);
            
            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                for (Calificacion cal : calificaciones) {
                    ps.setLong(1, cal.getNumControl());
                    ps.setString(2, cal.getMateria());
                    ps.setObject(3, cal.getParcial1());
                    ps.setObject(4, cal.getParcial2());
                    ps.setObject(5, cal.getParcial3());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            
            cn.commit();
            System.out.println("✅ " + calificaciones.size() + " calificaciones guardadas");
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error al guardar calificaciones: " + e.getMessage());
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
    
    public List<CalificacionConAlumno> getCalificacionesGrupo(int grupoId, String materia) {
        List<CalificacionConAlumno> lista = new ArrayList<>();
        String sql = """
            SELECT 
                u.id,
                u.no_control,
                u.nombre,
                u.apellido,
                c.parcial_1,
                c.parcial_2,
                c.parcial_3,
                c.promedio
            FROM usuarios u
            LEFT JOIN calificaciones c ON u.no_control = c.num_control AND c.materia = ?
            WHERE u.grupo_id = ? AND u.role = 'ESTUDIANTE'
            ORDER BY u.apellido, u.nombre
            """;
        
        try (Connection cn = conexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setString(1, materia);
            ps.setInt(2, grupoId);
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new CalificacionConAlumno(
                    rs.getInt("id"),
                    rs.getLong("no_control"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    (Double) rs.getObject("parcial_1"),
                    (Double) rs.getObject("parcial_2"),
                    (Double) rs.getObject("parcial_3"),
                    (Double) rs.getObject("promedio")
                ));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener calificaciones del grupo: " + e.getMessage());
        }
        
        return lista;
    }
    
    // ========== CLASE AUXILIAR ==========
    
    public static class CalificacionConAlumno {
        public int usuarioId;
        public long numControl;
        public String nombre;
        public String apellido;
        public Double parcial1;
        public Double parcial2;
        public Double parcial3;
        public Double promedio;
        
        public CalificacionConAlumno(int usuarioId, long numControl, String nombre, 
                                     String apellido, Double p1, Double p2, Double p3, Double prom) {
            this.usuarioId = usuarioId;
            this.numControl = numControl;
            this.nombre = nombre;
            this.apellido = apellido;
            this.parcial1 = p1;
            this.parcial2 = p2;
            this.parcial3 = p3;
            this.promedio = prom;
        }
        
        public String getNombreCompleto() {
            return apellido + ", " + nombre;
        }
    }
}