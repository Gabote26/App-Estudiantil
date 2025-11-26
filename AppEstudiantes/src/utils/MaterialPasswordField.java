package utils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class MaterialPasswordField extends MaterialTextField {

	private static final long serialVersionUID = 1L;
	private JPasswordField passField;
    private boolean visible = false;

    private Icon eyeOn;
    private Icon eyeOff;

    // área clickeable del ojo
    private Rectangle eyeBounds;

    public MaterialPasswordField(String label, Icon icon, Icon eyeOn, Icon eyeOff) {
        super(label, icon);

        this.eyeOn = eyeOn;
        this.eyeOff = eyeOff;
        remove(super.field);

        passField = new JPasswordField();
        passField.setOpaque(false);
        passField.setBorder(new EmptyBorder(8, (icon == null ? 5 : icon.getIconWidth() + 10), 8, eyeOn.getIconWidth() + 20));
        passField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        passField.setForeground(Color.WHITE);
        passField.setCaretColor(Color.WHITE);
        passField.setEchoChar('•');
        add(passField);

        passField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                focused = true;
                startAnim(true);
            }

            public void focusLost(java.awt.event.FocusEvent e) {
                focused = false;
                if (passField.getPassword().length == 0) startAnim(false);
            }
        });

        passField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { validateField(); }
            @Override public void removeUpdate(DocumentEvent e) { validateField(); }
            @Override public void changedUpdate(DocumentEvent e) { validateField(); }
        });

        // click en el ojo
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (eyeBounds != null && eyeBounds.contains(e.getPoint())) {
                    toggleVisibility();
                } else {
                    passField.requestFocusInWindow();
                }
            }
        });
    }

    public void toggleVisibility() {
        visible = !visible;
        passField.setEchoChar(visible ? (char) 0 : '•');
        repaint();
    }

    public String getText() {
        return new String(passField.getPassword());
    }

    public void setText(String t) {
        passField.setText(t);
        repaint();
    }

    @Override
    public void doLayout() {
        int iconPadding = (icon == null ? 0 : icon.getIconWidth() + 10);
        passField.setBounds(iconPadding, 22, getWidth() - iconPadding - 35, getHeight() - 22);
    }

    @Override
    protected void paintChildren(Graphics g) {
        super.paintChildren(g);

        Graphics2D g2 = (Graphics2D) g.create();

        Icon eye = visible ? eyeOff : eyeOn;

        int y = getHeight() / 2 - (eye.getIconHeight() / 2);
        int x = getWidth() - eye.getIconWidth() - 10;

        // actualizar área clickeable
        eyeBounds = new Rectangle(x, y, eye.getIconWidth(), eye.getIconHeight());

        // dibujar encima
        eye.paintIcon(this, g2, x, y);

        g2.dispose();
    }
}
