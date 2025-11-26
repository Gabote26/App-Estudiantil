package utils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class MaterialTextField extends JPanel {

	private static final long serialVersionUID = 1L;
	protected JTextField field;
    protected String label;
    protected Icon icon;          // icono visual (en la izquierda)
    protected Icon clickableIcon;

    protected Rectangle clickableBounds;

    protected boolean focused = false;
    protected float anim = 0f;

    protected Timer animTimer;

    // colores
    protected Color normalColor = new Color(120, 120, 120);
    protected Color focusColor  = new Color(33, 150, 243);
    protected Color errorColor  = new Color(244, 67, 54);
    protected Color validColor  = new Color(76, 175, 80);

    protected Color currentLineColor = normalColor;
    protected boolean valid = true;

    public MaterialTextField(String label, Icon icon) {
        this.label = label;
        this.icon = icon;

        setOpaque(false);
        setLayout(null);

        field = new JTextField();
        field.setOpaque(false);
        field.setBorder(new EmptyBorder(8, icon == null ? 5 : icon.getIconWidth() + 10, 8, 5));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);

        add(field);

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                focused = true;
                startAnim(true);
            }

            public void focusLost(java.awt.event.FocusEvent e) {
                focused = false;
                if (field.getText().trim().isEmpty()) startAnim(false);
            }
        });

        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { validateField(); }
            @Override public void removeUpdate(DocumentEvent e) { validateField(); }
            @Override public void changedUpdate(DocumentEvent e) { validateField(); }
        });

        // clic en el icono clickeable
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (clickableIcon != null && clickableBounds != null) {
                    if (clickableBounds.contains(e.getPoint())) {
                        onClickableIconPressed();
                    }
                }
            }
        });
    }

    public JTextField getField() { return field; }

    public String getText() { return field.getText().trim(); }

    public void setText(String t) {
        field.setText(t);
        repaint();
    }

    public void validateField() {
        String t = field.getText().trim();

        if (t.isEmpty()) {
            valid = true;
            currentLineColor = normalColor;
        } else if (t.length() < 3) {
            valid = false;
            currentLineColor = errorColor;
        } else {
            valid = true;
            currentLineColor = validColor;
        }

        repaint();
    }

    public void startAnim(boolean directionUp) {
        if (animTimer != null) animTimer.stop();

        animTimer = new Timer(15, e -> {
            float speed = 0.09f;
            anim += directionUp ? speed : -speed;

            if (anim < 0f) anim = 0f;
            if (anim > 1f) anim = 1f;

            if (anim == 0f || anim == 1f) animTimer.stop();

            repaint();
        });

        animTimer.start();
    }

    // icono clickeable
    public void setClickableIcon(Icon icon) {
        this.clickableIcon = icon;
        repaint();
    }

    protected void onClickableIconPressed() {
        // SIn funcion por defecto, usamos funcion para poder establecer valores desde otras clases
    }

    @Override
    public void doLayout() {
        int padLeft = (icon == null ? 0 : icon.getIconWidth() + 10);
        int padRight = (clickableIcon == null ? 5 : clickableIcon.getIconWidth() + 15);
        field.setBounds(padLeft, 22, getWidth() - padLeft - padRight, getHeight() - 22);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Floating label
        float y = 24 - (anim * 14);
        float size = 15 - (anim * 3);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, (int) size));
        if (focused) g2.setColor(focusColor);
        else g2.setColor(valid ? new Color(180, 180, 180) : errorColor);

        int labelX = (icon == null ? 3 : icon.getIconWidth() + 12);
        g2.drawString(label, labelX, y);

        // icono izquierdo
        if (icon != null) {
            int yIcon = (getHeight() / 2) - (icon.getIconHeight() / 2);
            icon.paintIcon(this, g2, 5, yIcon);
        }

        // linea inferior
        g2.setStroke(new BasicStroke(2f));
        g2.setColor(focused ? focusColor : currentLineColor);
        g2.drawLine(0, getHeight() - 3, getWidth(), getHeight() - 3);

        g2.dispose();
    }

    @Override
    protected void paintChildren(Graphics g) {
        super.paintChildren(g);

        if (clickableIcon != null) {
            Graphics2D g2 = (Graphics2D) g.create();

            int y = getHeight() / 2 - clickableIcon.getIconHeight() / 2;
            int x = getWidth() - clickableIcon.getIconWidth() - 10;

            clickableBounds = new Rectangle(x, y, clickableIcon.getIconWidth(), clickableIcon.getIconHeight());

            clickableIcon.paintIcon(this, g2, x, y);
            g2.dispose();
        }
    }
}
