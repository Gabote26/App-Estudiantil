package utils;

import javax.swing.*;
import java.awt.*;

public class FadeUtils {

    private static float clamp(float v) {
        return Math.max(0f, Math.min(1f, v));
    }

    private static boolean supportsOpacity(Window w) {
        try {
            w.setOpacity(1f);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // -------------------------------
    // FADE OUT
    // -------------------------------
    public static void fadeOut(JFrame frame, int duration, Runnable afterFade) {

        if (!supportsOpacity(frame)) {
            // Sistema NO soporta opacity → sin fade
            if (afterFade != null) afterFade.run();
            return;
        }

        Timer timer = new Timer(15, null);
        long start = System.currentTimeMillis();

        timer.addActionListener(e -> {
            float progress = (System.currentTimeMillis() - start) / (float) duration;
            float alpha = clamp(1f - progress);

            try {
                frame.setOpacity(alpha);
            } catch (Exception ex) {
                timer.stop();
                if (afterFade != null) afterFade.run();
                return;
            }

            if (alpha <= 0f) {
                timer.stop();
                if (afterFade != null) afterFade.run();
            }
        });

        timer.start();
    }

    // -------------------------------
    // FADE IN
    // -------------------------------
    public static void fadeIn(JFrame frame, int duration) {

        boolean ok = supportsOpacity(frame);

        frame.setVisible(true);

        if (!ok) {
            return; // Sistema no soporta opacity → mostrar normal
        }

        try {
            frame.setOpacity(0f);
        } catch (Exception ignored) {
            return;
        }

        Timer timer = new Timer(15, null);
        long start = System.currentTimeMillis();

        timer.addActionListener(e -> {
            float progress = (System.currentTimeMillis() - start) / (float) duration;
            float alpha = clamp(progress);

            try {
                frame.setOpacity(alpha);
            } catch (Exception ex) {
                timer.stop();
                return;
            }

            if (alpha >= 1f) {
                timer.stop();
            }
        });

        timer.start();
    }
}
