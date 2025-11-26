package utils;

import javax.swing.*;
import java.awt.*;

public class MaterialLoader extends JDialog {

	private static final long serialVersionUID = 1L;
	private float angle = 0;

    public MaterialLoader(JFrame parent) {
        super(parent, true);
        setUndecorated(true);
        setSize(80, 80);
        setLocationRelativeTo(parent);
        setBackground(new Color(0, 0, 0, 0));

        Timer timer = new Timer(15, e -> {
            angle += 6;
            repaint();
        });
        timer.start();
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(new Color(255, 255, 255, 40));
        g2.fillOval(10, 10, 60, 60);

        g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(0, 200, 255));

        g2.rotate(Math.toRadians(angle), getWidth() / 2.0, getHeight() / 2.0);
        g2.drawArc(20, 20, 40, 40, 0, 270);
    }
}
