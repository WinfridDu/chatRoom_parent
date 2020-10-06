package ui;

import model.GlobalData;
import model.Request;
import model.Response;
import model.User;
import util.IOUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private JTextField idTxt;
    private JPasswordField pwdFld;

    public LoginFrame() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        this.init();
        setVisible(true);
    }

    public void init() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        int height = 410;
        int width = 540;
        int screenHeight = 1080;
        int screenWidth = 1920;
        this.setSize(width, height);
        this.setLocation((screenWidth - width) / 2, (screenHeight-height)/ 2);
        this.setResizable(false);

        //把Logo放置到JFrame的北边
        Icon icon = new ImageIcon(this.getClass().getResource("/images/login.png"));
        JLabel label = new JLabel(icon);
        label.setPreferredSize(new Dimension(536,191));
        this.add(label, BorderLayout.NORTH);

        //登录信息面板
        JPanel mainPanel = new JPanel();
        this.add(mainPanel, BorderLayout.CENTER);
        mainPanel.setLayout(null);

        idTxt = new JTextField();
        idTxt.setFont(new Font("Segoe", Font.PLAIN, 20));
        idTxt.addFocusListener(new JTextFieldHintListener(idTxt, "账号"));
        idTxt.setBounds(147, 18, 242, 38);
        mainPanel.add(idTxt);

        pwdFld = new JPasswordField();
        pwdFld.setBounds(147, 56, 242, 38);
        pwdFld.setFont(new Font("Segoe", Font.PLAIN, 20));
        pwdFld.addFocusListener(new JPasswordFieldHintListener(pwdFld, "密码"));
        mainPanel.add(pwdFld);

        JButton registeBtn = new JButton("注册");
        registeBtn.setBounds(320,100,71,21);
        registeBtn.setContentAreaFilled(false);
        mainPanel.add(registeBtn);
        JButton submitBtn = new JButton("登  录");
        submitBtn.setBounds(147,133,242,38);
        mainPanel.add(submitBtn);

        pwdFld.addKeyListener(new KeyAdapter(){
            public void keyPressed(KeyEvent e){
                if(e.getKeyCode() == Event.ENTER){
                    try {
                        login();
                    } catch (ClassNotFoundException | UnsupportedLookAndFeelException | InstantiationException | IllegalAccessException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        idTxt.addKeyListener(new KeyAdapter(){
            public void keyPressed(KeyEvent e){
                if(e.getKeyCode() == Event.ENTER){
                    try {
                        login();
                    } catch (ClassNotFoundException | UnsupportedLookAndFeelException | InstantiationException | IllegalAccessException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        //关闭窗口
        this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e) {
                Request req = new Request();
                req.setAction("logout");
                IOUtil.sendReq(req);
                System.exit(0);
            }
        });

        //注册
        registeBtn.addActionListener(e -> {
            try {
                new RegisterFrame();  //打开注册窗体
            } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException ex) {
                ex.printStackTrace();
            }
        });

        //登录
        submitBtn.addActionListener(e -> {
            try {
                login();
            } catch (ClassNotFoundException | UnsupportedLookAndFeelException | InstantiationException | IllegalAccessException ex) {
                ex.printStackTrace();
            }
        });
    }

    /** 登录 */
    @SuppressWarnings("unchecked")
    private void login() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {

        if (0 == idTxt.getText().length() || 0 == pwdFld.getPassword().length) {
            JOptionPane.showMessageDialog(LoginFrame.this, "账号和密码是必填的", "输入有误",JOptionPane.ERROR_MESSAGE);
            idTxt.requestFocusInWindow();
            return;
        }
        if(!idTxt.getText().matches("^\\d*$")){
            JOptionPane.showMessageDialog(LoginFrame.this, "账号必须是数字",
                    "输入有误",JOptionPane.ERROR_MESSAGE);
            idTxt.requestFocusInWindow();
            return;
        }
        Request req = new Request();
        req.setAction("login");
        req.setAttribute("id", idTxt.getText());
        req.setAttribute("password", new String(pwdFld.getPassword()));

        //获取响应
        Response response = IOUtil.sendReqAndResp(req);

        if(!response.isStatus()){
            JOptionPane.showMessageDialog(LoginFrame.this, "服务器内部错误，请稍后再试",
                    "登录失败",JOptionPane.ERROR_MESSAGE);
            return;
        }
        //获取当前用户
        Map<String,Object> map = (HashMap<String, Object>) response.getData();
        User user = (User)map.get("user");
        if(null == user){
            String str = response.getMsg();
            JOptionPane.showMessageDialog(LoginFrame.this, str,
                    "登录失败",JOptionPane.ERROR_MESSAGE);
        }else{
            GlobalData.setCurrentUser(user);
            GlobalData.setOnlineUsers((List<User>)map.get("onlineUsers"));
            LoginFrame.this.dispose();
            new ChatFrame();  //打开聊天窗体
        }
    }
}
