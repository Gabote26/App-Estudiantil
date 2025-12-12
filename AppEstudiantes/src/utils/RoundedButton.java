package utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

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
        setBorderPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        setFont(getEmojiCompatibleFont());
        setForeground(new Color(45, 45, 45));
        setBackground(new Color(247, 248, 250));

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
                repaint();
            }
        });
    }

    private Font getEmojiCompatibleFont() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return new Font("Segoe UI Emoji", Font.PLAIN, 16);
        } else if (os.contains("mac")) {
            return new Font("Apple Color Emoji", Font.PLAIN, 16);
        } else {
            return new Font("Noto Color Emoji", Font.PLAIN, 16);
        }
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
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Limpiar el área con el fondo del contenedor (no negro)
        if (getParent() != null) {
            g2.setColor(getParent().getBackground());
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        // Definir forma redondeada
        Shape round = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius);

        // Aplicar clip para evitar pintura fuera de la forma
        g2.setClip(round);

        // Color de fondo según hover
        Color fillColor = hover ? hoverBg : getBackground();
        g2.setColor(fillColor);
        g2.fill(round);

        // Texto centrado
        g2.setFont(getFont());
        g2.setColor(getForeground());
        FontMetrics fm = g2.getFontMetrics();
        String text = getText();
        Rectangle textBounds = fm.getStringBounds(text, g2).getBounds();

        int textX = (getWidth() - textBounds.width) / 2;
        int textY = (getHeight() - textBounds.height) / 2 + fm.getAscent();
        g2.drawString(text, textX, textY);

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
