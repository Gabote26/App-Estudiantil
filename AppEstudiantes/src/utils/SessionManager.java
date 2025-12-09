package utils;

import java.io.*;
import java.util.Properties;

public class SessionManager {

    private static final String SESSION_FILE = "session.properties";

    // Guardar la sesión (Credenciales en el archivo session.properties)
    public static void guardarSesion(String email, String role, long numControl) {
        try {
            Properties props = new Properties();
            props.setProperty("isLogged", "true");
            props.setProperty("email", email);
            props.setProperty("role", role);
            props.setProperty("numControl", String.valueOf(numControl));

            FileOutputStream out = new FileOutputStream(SESSION_FILE);
            props.store(out, "User session");
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Cargar la sesión exixtente (Si es falso = iniciar sesion, si es true = entrar a la aplicación)
    public static Properties cargarSesion() {
        try {
            File f = new File(SESSION_FILE);
            if (!f.exists()) return null;

            Properties props = new Properties();
            FileInputStream in = new FileInputStream(f);
            props.load(in);
            in.close();

            if ("true".equals(props.getProperty("isLogged"))) {
                return props;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Cerrar la sesión actual
    public static void cerrarSesion() {
        try {
            File f = new File(SESSION_FILE);
            if (f.exists()) f.delete();
        } catch (Exception ignored) {}
    }
}
