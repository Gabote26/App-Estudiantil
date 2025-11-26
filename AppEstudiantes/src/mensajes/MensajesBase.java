package mensajes;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import db.ConexionMysql;
import modelos.Mensaje;

public class MensajesBase {
    private final ConexionMysql conexionDB = new ConexionMysql();
    
    // Enviar mensaje y obtener el id que se le asigno a este
    public int enviarMensaje(Mensaje mensaje, List<Integer> destinatariosIds) {
        String sqlMensaje = """
            INSERT INTO mensajes (remitente_id, tipo_mensaje, asunto, contenido)
            VALUES (?, ?, ?, ?)
            """;
        
        String sqlDestinatarios = """
            INSERT INTO mensajes_destinatarios (mensaje_id, destinatario_id)
            VALUES (?, ?)
            """;
        
        Connection cn = null;
        int mensajeId = -1;
        
        try {
            cn = conexionDB.conectar();
            cn.setAutoCommit(false);
            
            // 1. Insertar mensaje en la base de datos
            try (PreparedStatement ps = cn.prepareStatement(sqlMensaje, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, mensaje.getRemitenteId());
                ps.setString(2, mensaje.getTipoMensaje());
                ps.setString(3, mensaje.getAsunto());
                ps.setString(4, mensaje.getContenido());
                ps.executeUpdate();
                
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    mensajeId = rs.getInt(1);
                }
            }
            
            // 2. Insertar destinatarios en la base de datos
            if (mensajeId > 0 && destinatariosIds != null && !destinatariosIds.isEmpty()) {
                try (PreparedStatement ps = cn.prepareStatement(sqlDestinatarios)) {
                    for (int destinatarioId : destinatariosIds) {
                        ps.setInt(1, mensajeId);
                        ps.setInt(2, destinatarioId);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }
            
            cn.commit();
            System.out.println("✅ Mensaje enviado correctamente. ID: " + mensajeId);
            
        } catch (SQLException e) {
            System.err.println("❌ Error al enviar mensaje: " + e.getMessage());
            if (cn != null) {
                try {
                    cn.rollback(); // Revertir cambios
                } catch (SQLException ex) {
                    System.err.println("Error al revertir los cambios: " + ex.getMessage());
                }
            }
            return -1;
        } finally {
            if (cn != null) {
                try {
                    cn.setAutoCommit(true);
                    cn.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar la conexión: " + e.getMessage());
                }
            }
        }
        
        return mensajeId;
    }
    
    
     // Obtener mensajes de la bandeja de entrada de un usuario
    public List<Mensaje> obtenerMensajesUsuario(int usuarioId) {
        List<Mensaje> mensajes = new ArrayList<>();
        String sql = """
            SELECT m.id, m.remitente_id, m.tipo_mensaje, m.asunto, 
                   m.contenido, m.fecha_envio, md.leido
            FROM mensajes m
            INNER JOIN mensajes_destinatarios md ON m.id = md.mensaje_id
            WHERE md.destinatario_id = ?
            ORDER BY m.fecha_envio DESC
            """;
        
        try (Connection cn = conexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setInt(1, usuarioId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Mensaje mensaje = new Mensaje(
                    rs.getInt("id"),
                    rs.getInt("remitente_id"),
                    rs.getString("tipo_mensaje"),
                    rs.getString("asunto"),
                    rs.getString("contenido"),
                    rs.getTimestamp("fecha_envio").toLocalDateTime()
                );
                mensajes.add(mensaje);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener mensajes: " + e.getMessage());
        }
        
        return mensajes;
    }
    
    // Marcar el mensaje como leido
    public boolean marcarComoLeido(int mensajeId, int usuarioId) {
        String sql = """
            UPDATE mensajes_destinatarios
            SET leido = TRUE, fecha_lectura = NOW()
            WHERE mensaje_id = ? AND destinatario_id = ?
            """;
        
        try (Connection cn = conexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setInt(1, mensajeId);
            ps.setInt(2, usuarioId);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al marcar mensaje como leído: " + e.getMessage());
            return false;
        }
    }
    
    // Contador de mensajes no leidos por el usuario
    public int contarMensajesNoLeidos(int usuarioId) {
        String sql = """
            SELECT COUNT(*) as total
            FROM mensajes_destinatarios
            WHERE destinatario_id = ? AND leido = FALSE
            """;
        
        try (Connection cn = conexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setInt(1, usuarioId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total");
            }
            
        } catch (SQLException e) {
            System.err.println("Error al contar mensajes no leídos: " + e.getMessage());
        }
        
        return 0;
    }
    
   // Ids de los estudiantes de un grupo
    public List<Integer> obtenerEstudiantesDeGrupo(int grupoId) {
        List<Integer> ids = new ArrayList<>();
        String sql = """
            SELECT id FROM usuarios
            WHERE grupo_id = ? AND role = 'ESTUDIANTE'
            """;
        
        try (Connection cn = conexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            ps.setInt(1, grupoId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                ids.add(rs.getInt("id"));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener estudiantes del grupo: " + e.getMessage());
        }
        
        return ids;
    }
    
    // Buscar a los estudiantes por nombre o por correo
    public List<Integer> buscarEstudiantes(String texto) {
        List<Integer> ids = new ArrayList<>();
        String sql = """
            SELECT id FROM usuarios
            WHERE role = 'ESTUDIANTE' 
            AND (nombre LIKE ? OR apellido LIKE ? OR email LIKE ?)
            """;
        
        try (Connection cn = conexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            
            String busqueda = "%" + texto + "%";
            ps.setString(1, busqueda);
            ps.setString(2, busqueda);
            ps.setString(3, busqueda);
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                ids.add(rs.getInt("id"));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al buscar estudiantes: " + e.getMessage());
        }
        
        return ids;
    }
}