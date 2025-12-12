package utils;

import java.util.prefs.Preferences;

public class ConfigManager {
    
    private static final Preferences prefs = Preferences.userNodeForPackage(ConfigManager.class);
    
    // Variables para la configuracion
    private static final String KEY_TEMA = "tema";
    private static final String KEY_IDIOMA = "idioma";
    private static final String KEY_NOTIFICACIONES = "notificaciones";
    
    // Valores establecidos por defecto
    private static final String DEFAULT_TEMA = "Claro";
    private static final String DEFAULT_IDIOMA = "Español";
    private static final boolean DEFAULT_NOTIFICACIONES = true;
    
    // =================== MÉTODOS DE TEMA ===================
    
    // Guardar el tema seleccionado
    public static void setTema(String tema) {
        prefs.put(KEY_TEMA, tema);
    }
    
    // Obtener el tema configurado por el usuario
    public static String getTema() {
        return prefs.get(KEY_TEMA, DEFAULT_TEMA);
    }
    
    // Comprobacion para verificar si debe establecerse el modo obscuro
    public static boolean isDarkMode() {
        String tema = getTema();
        
        switch (tema) {
            case "Oscuro":
                return true;
            case "Claro":
                return false;
            case "Automático":
                return isSystemDarkMode();
            default:
                return false;
        }
    }
    
    // Deteccion del modo en que este el sistema (Claro / obscuro)
    public static boolean isSystemDarkMode() {
        String os = System.getProperty("os.name").toLowerCase();
        
        try {
            if (os.contains("win")) {
                return isWindowsDarkMode();
            } else if (os.contains("mac")) {
                return isMacOSDarkMode();
            } else if (os.contains("nix") || os.contains("nux")) {
                return isLinuxDarkMode();
            }
        } catch (Exception e) {
            System.err.println("Error al detectar tema del sistema: " + e.getMessage());
        }
        
        return false;
    }
    
    // Detectar modo obscuro en windows
    @SuppressWarnings("deprecation")
	private static boolean isWindowsDarkMode() {
        try {
            // Lectura del registro de Windows para determinar el tema
            String command = "reg query \"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize\" /v AppsUseLightTheme";
            Process process = Runtime.getRuntime().exec(command);
            
            java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(process.getInputStream())
            );
            
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("AppsUseLightTheme")) {
                    // 0x0 es obscuro, 0x1 es claro
                    return line.contains("0x0");
                }
            }
        } catch (Exception e) {
            System.err.println("No se pudo detectar el tema de Windows: " + e.getMessage());
        }
        return false;
    }
    
    // Detectar modo obscuro en macos
    @SuppressWarnings("deprecation")
	private static boolean isMacOSDarkMode() {
        try {
            String command = "defaults read -g AppleInterfaceStyle";
            Process process = Runtime.getRuntime().exec(command);
            
            java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(process.getInputStream())
            );
            
            String result = reader.readLine();
            return "Dark".equalsIgnoreCase(result);
        } catch (Exception e) {
            return false;
        }
    }
    
    // Detectar modo obscuro en linux
    @SuppressWarnings("deprecation")
	private static boolean isLinuxDarkMode() {
        try {
            String command = "gsettings get org.gnome.desktop.interface gtk-theme";
            Process process = Runtime.getRuntime().exec(command);
            
            java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(process.getInputStream())
            );
            
            String theme = reader.readLine();
            return theme != null && theme.toLowerCase().contains("dark");
        } catch (Exception e) {
            try {
                String command = "kreadconfig5 --group General --key ColorScheme";
                Process process = Runtime.getRuntime().exec(command);
                
                java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getInputStream())
                );
                
                String colorScheme = reader.readLine();
                return colorScheme != null && colorScheme.toLowerCase().contains("dark");
            } catch (Exception ex) {
                return false;
            }
        }
    }
    
    // =================== MÉTODOS DE IDIOMA ===================
    
    public static void setIdioma(String idioma) {
        prefs.put(KEY_IDIOMA, idioma);
    }
    
    public static String getIdioma() {
        return prefs.get(KEY_IDIOMA, DEFAULT_IDIOMA);
    }
    
    // =================== MÉTODOS DE NOTIFICACIONES ===================
    
    public static void setNotificaciones(boolean habilitado) {
        prefs.putBoolean(KEY_NOTIFICACIONES, habilitado);
    }
    
    public static boolean getNotificaciones() {
        return prefs.getBoolean(KEY_NOTIFICACIONES, DEFAULT_NOTIFICACIONES);
    }
    
    // Resetear configuracion a valores por defecto
    public static void resetConfig() {
        setTema(DEFAULT_TEMA);
        setIdioma(DEFAULT_IDIOMA);
        setNotificaciones(DEFAULT_NOTIFICACIONES);
    }
}