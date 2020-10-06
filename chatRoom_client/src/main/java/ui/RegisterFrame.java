package ui;

import model.Request;
import model.Response;
import model.User;
import util.IOUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class RegisterFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private JPasswordField pwdFld;
    private JPasswordField pwd2Fld;
    private JTextField nickname;
    private JComboBox<ImageIcon> head;
    private JRadioButton sex0;

    public RegisterFrame() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        this.init();
        setVisible(true);
    }

    public void init() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        this.setTitle("注册账号");//设置标题
        int height = 410;
        int width = 540;
        int screenHeight = 1080;
        int screenWidth = 1920;
        setBounds((screenWidth - width)/2,
                (screenHeight - height)/2,
                width, height);
        getContentPane().setLayout(null);
        setResizable(false);

//        JLabel label = new JLabel("昵称"); //label显示
//        label.setBounds(120, 65, 59, 17);
//        label.setFont(new Font("Segoe", Font.PLAIN, 20));
//        getContentPane().add(label);

        nickname = new JTextField(); //昵称
        nickname.setBounds(120, 54, 292, 38);
        nickname.setFont(new Font("Segoe", Font.PLAIN, 20));
        nickname.addFocusListener(new JTextFieldHintListener(nickname, "昵称"));
        getContentPane().add(nickname);

//        JLabel label5 = new JLabel("密码");
//        label5.setFont(new Font("Segoe", Font.PLAIN, 20));
//        label5.setBounds(120, 128, 50, 17);
//        getContentPane().add(label5);

//        JLabel label3 = new JLabel("确认密码");
//        label3.setFont(new Font("Segoe", Font.PLAIN, 20));
//        label3.setBounds(80, 191, 80, 17);
//        getContentPane().add(label3);

        pwdFld = new JPasswordField();//密码框
        pwdFld.setBounds(120, 117, 292, 38);
        pwdFld.setFont(new Font("Segoe", Font.PLAIN, 20));
        pwdFld.addFocusListener(new JPasswordFieldHintListener(pwdFld, "密码"));
        getContentPane().add(pwdFld);

        pwd2Fld = new JPasswordField();
        pwd2Fld.setBounds(120, 180, 292, 38);
        pwd2Fld.setFont(new Font("Segoe", Font.PLAIN, 20));
        pwd2Fld.addFocusListener(new JPasswordFieldHintListener(pwd2Fld, "确认密码"));
        getContentPane().add(pwd2Fld);

        JRadioButton sex1 = new JRadioButton("男", true);
        sex1.setBounds (243, 243,44, 25);
        getContentPane().add(sex1);
        sex0 = new JRadioButton("女");
        sex0.setBounds(243, 268, 44, 25);
        getContentPane().add(sex0);
        ButtonGroup buttonGroup = new ButtonGroup();//单选按钮组
        buttonGroup.add(sex0);
        buttonGroup.add(sex1);

        JLabel label6 = new JLabel("头像");
        label6.setFont(new Font("Segoe", Font.PLAIN, 20));
        label6.setBounds(120, 255, 50, 17);
        getContentPane().add(label6);

        head = new JComboBox<>();//下拉列表图标
        head.setBounds(170, 243, 65, 45);
        head.setMaximumRowCount(5);
        for (int i = 0; i < 6; i++) {
            head.addItem(new ImageIcon(new ImageIcon(this.getClass().getResource("/images/"+i+".jpeg")).getImage().getScaledInstance(40,40,Image.SCALE_DEFAULT)));
        }
        head.setSelectedIndex(0);
        getContentPane().add(head);

        JButton ok = new JButton("立即注册");
        ok.setFont(new Font("Segoe", Font.PLAIN, 20));
        ok.setBounds(290, 243, 122, 45);
        getContentPane().add(ok);

        //关闭窗口
        this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e) {
                RegisterFrame.this.dispose();
            }
        });

        //确认按钮监听事件处理
        ok.addActionListener(e -> {
            if (0 == pwdFld.getPassword().length || 0 == pwd2Fld.getPassword().length) {
                JOptionPane.showMessageDialog(RegisterFrame.this, "请您完整输入再注册!");
            } else if (!new String(pwdFld.getPassword()).equals(new String(pwd2Fld.getPassword()))) {
                JOptionPane.showMessageDialog(RegisterFrame.this, "两次输入密码不一致!");
                pwdFld.setText("");
                pwd2Fld.setText("");
                pwdFld.requestFocusInWindow();
            } else {
                User user = new User(new String(pwdFld.getPassword()), nickname.getText(),
                        sex0.isSelected() ? 'M' : 'F', head.getSelectedIndex());
                registe(user);
            }
        });
    }

    //注册方法
    private void registe(User user){
        Request request = new Request();
        request.setAction("registe");
        request.setAttribute("user", user);

        //获取响应
        Response response = IOUtil.sendReqAndResp(request);
        if(response.isStatus()){
            user = (User)response.getData();
            JOptionPane.showMessageDialog(RegisterFrame.this, "注册成功，账号:"+ user.getId(),
                    "注册成功",JOptionPane.INFORMATION_MESSAGE);
            this.setVisible(false);
        }else{
            JOptionPane.showMessageDialog(RegisterFrame.this,
                    "注册失败，请稍后再试!","服务器内部错误!",JOptionPane.ERROR_MESSAGE);
        }
    }
}