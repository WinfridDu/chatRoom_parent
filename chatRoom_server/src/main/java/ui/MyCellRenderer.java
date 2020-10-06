package ui;

import model.User;

import javax.swing.*;
import java.awt.*;

public class MyCellRenderer extends JLabel implements ListCellRenderer<User> {
    private static final long serialVersionUID = 1L;

    @Override
    public Component getListCellRendererComponent(JList<? extends User> list, User value, int index, boolean isSelected, boolean cellHasFocus) {
        setText(value.getId()+"("+value.getNickname()+")");
        setFont(new Font("Segoe", Font.PLAIN, 20));
        setHorizontalAlignment(SwingConstants.CENTER);
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        setEnabled(list.isEnabled());
        setOpaque(true);
        return this;
    }
}
