package utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RoundedButton extends JButton {
    private static final long serialVersionUID = 1L;
    private int radius;
    private boolean hover = false;
    private Color originalBg;
    private Color hoverBg;

    public RoundedButton(String text, int radius) {
        super(text);
        this.radius = radius;
        setFocusPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setFont(new Font("Segoe UI", Font.PLAIN, 14));
        setForeground(new Color(45, 45, 45));
        setBackground(new Color(247, 248, 250));
        setBorderPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        this.originalBg = getBackground();
        this.hoverBg = deriveHoverColor(originalBg);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hover = true;
                hoverBg = deriveHoverColor(originalBg);
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hover = false;
                SwingUtilities.invokeLater(() -> repaint());
            }
        });
    }

    private Color deriveHoverColor(Color base) {
        int r = Math.max(0, Math.min(255, base.getRed() - 8));
        int g = Math.max(0, Math.min(255, base.getGreen() - 8));
        int b = Math.max(0, Math.min(255, base.getBlue() - 8));
        return new Color(r, g, b);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (getParent() != null) {
            g2.setColor(getParent().getBackground());
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        Color fillColor = hover ? hoverBg : getBackground();
        g2.setColor(fillColor);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

        FontMetrics fm = g2.getFontMetrics();
        int textX = (getWidth() - fm.stringWidth(getText())) / 2;
        int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;

        g2.setColor(getForeground());
        g2.setFont(getFont());
        g2.drawString(getText(), textX, textY);

        g2.dispose();
    }

    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);
        this.originalBg = bg;
        this.hoverBg = deriveHoverColor(bg);
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
        repaint();
    }
}
