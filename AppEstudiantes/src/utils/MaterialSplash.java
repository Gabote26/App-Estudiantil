package utils;

import javax.swing.*;
import java.awt.*;

public class MaterialSplash extends JWindow {

	private static final long serialVersionUID = 1L;

	public MaterialSplash() {

        JPanel p = new JPanel();
        p.setBackground(new Color(30, 30, 30));
        p.add(new JLabel(new ImageIcon(getClass().getResource("/icons/user.png"))));
        p.add(new JLabel("<html><font color='white' size='5'>Cargando...</font></html>"));

        add(p);
        setSize(300, 200);
        setLocationRelativeTo(null);

        boolean opacitySupported = true;

        try {
            setOpacity(0f);
        } catch (Exception e) {
            opacitySupported = false;
        }

        setVisible(true);

        if (!opacitySupported) {
            return;
        }

        Timer timer = new Timer(15, null);

        timer.addActionListener(e -> {
            float o = getOpacity() + 0.05f;
            o = Math.min(1f, o);

            try {
                setOpacity(o);
            } catch (Exception ex) {
                timer.stop();
                return;
            }

            if (o >= 1f) {
                timer.stop();
            }
        });

        timer.start();
    }
}
