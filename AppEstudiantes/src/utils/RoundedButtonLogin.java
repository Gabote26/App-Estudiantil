package utils;

import javax.swing.*;
import java.awt.*;

public class RoundedButtonLogin extends JButton {

    private int arc;
    private int rippleX, rippleY, rippleSize;
    private boolean rippling = false;

    public RoundedButtonLogin(String text, int arc) {
        super(text);
        this.arc = arc;

        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Color.WHITE);

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                rippleX = e.getX();
                rippleY = e.getY();
                rippleSize = 0;
                rippling = true;

                Timer t = new Timer(15, ev -> {
                    rippleSize += 10;
                    if (rippleSize > getWidth()) rippling = false;
                    repaint();
                });
                t.start();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

        if (rippling) {
            g2.setColor(new Color(255, 255, 255, 60));
            g2.fillOval(rippleX - rippleSize / 2, rippleY - rippleSize / 2, rippleSize, rippleSize);
        }

        super.paintComponent(g2);
        g2.dispose();
    }
}
