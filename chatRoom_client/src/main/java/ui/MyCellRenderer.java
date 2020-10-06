package ui;

import lombok.SneakyThrows;
import model.User;

import javax.swing.*;
import java.awt.*;

public class MyCellRenderer extends JLabel implements ListCellRenderer<User> {
    private static final long serialVersionUID = 1L;

    @SneakyThrows
    @Override
    public Component getListCellRendererComponent(JList<? extends User> list, User value, int index, boolean isSelected, boolean cellHasFocus) {
        String name = value.getNickname();
        setText("<html>&nbsp;"+name+"<br/>&nbsp;<html/>");
        setFont(new Font("Segoe", Font.PLAIN, 24));
        setIcon(value.getHeadIcon());
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
