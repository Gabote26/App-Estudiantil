package utils;

import guiBase.BaseMainFrame;
import java.util.ArrayList;
import java.util.List;

public class ThemeManager {
    
    private static final List<BaseMainFrame> registeredFrames = new ArrayList<>();
    
    public static void registerFrame(BaseMainFrame frame) {
        if (!registeredFrames.contains(frame)) {
            registeredFrames.add(frame);
        }
    }
    
    public static void unregisterFrame(BaseMainFrame frame) {
        registeredFrames.remove(frame);
    }
    
    public static void applyThemeToAll() {
        boolean darkMode = ConfigManager.isDarkMode();
        
        for (BaseMainFrame frame : registeredFrames) {
            if (frame != null && frame.isDisplayable()) {
                frame.setDarkMode(darkMode);
                frame.aplicarTema();
            }
        }
    }
    
    public static int getRegisteredFramesCount() {
        return registeredFrames.size();
    }

    public static void cleanupClosedFrames() {
        registeredFrames.removeIf(frame -> frame == null || !frame.isDisplayable());
    }
}