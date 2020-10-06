package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class JPasswordFieldHintListener implements FocusListener {
    private String hintText;
    private JPasswordField passwordField;

    public JPasswordFieldHintListener(JPasswordField jPasswordField, String hintText) {
        this.passwordField = jPasswordField;
        this.hintText = hintText;
        jPasswordField.setText(hintText);  //默认直接显示
        jPasswordField.setForeground(Color.GRAY);
        jPasswordField.setEchoChar('\0');
    }

    @Override
    public void focusGained(FocusEvent e) {
        //获取焦点时，清空提示内容
        String temp = new String(passwordField.getPassword());
        if(temp.equals(hintText)) {
            passwordField.setText("");
            passwordField.setForeground(Color.BLACK);
        }
        passwordField.setEchoChar('\u25CF');
    }

    @Override
    public void focusLost(FocusEvent e) {
        //失去焦点时，没有输入内容，显示提示内容
        String temp = new String(passwordField.getPassword());
        if(temp.equals("")) {
            passwordField.setForeground(Color.GRAY);
            passwordField.setText(hintText);
        }
        if(temp.length()<5){
            passwordField.setEchoChar('\0');
        }
    }

}
