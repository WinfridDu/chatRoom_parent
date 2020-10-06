package ui;

import model.*;
import util.IOUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatFrame extends JFrame{
    private static final long serialVersionUID = 1L;
    /**聊天对方的信息Label*/
    private JLabel otherInfoLbl;
    /**聊天信息列表区域 */
    public static JTextArea msgListArea;
    /**要发送的信息区域 */
    public static JTextArea sendArea;
    /** 在线用户列表 */
    public static JList<User> userList;
    /** 准备发送的文件 */
    public static FileInfo sendFile;
    /** 私聊复选框 */
    public JCheckBox rybqBtn;

    public ChatFrame() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        this.init();
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setVisible(true);
    }

    public void init() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        int height = 760;
        int width = 1005;
        int screenHeight = 1080;
        int screenWidth = 1920;
        this.setSize(width, height);

        this.setLocation((screenWidth - width) / 2, (screenHeight-height)/ 2);

        //左边用户面板
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        //右边主面板
        JPanel userListPanel = new JPanel();
        userListPanel.setLayout(new BorderLayout());

        // 创建一个分隔窗格
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                userListPanel, mainPanel);
        splitPane.setDividerLocation(315);
        splitPane.setDividerSize(0);
        splitPane.setOneTouchExpandable(true);
        this.add(splitPane, BorderLayout.CENTER);

        //右上边信息显示面板
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BorderLayout());
        //右下边发送消息面板
        JPanel sendPanel = new JPanel();
        sendPanel.setLayout(new BorderLayout());

        // 创建一个分隔窗格
        JSplitPane splitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, infoPanel, sendPanel);
        splitPane2.setDividerLocation(530);
        splitPane2.setDividerSize(1);
        mainPanel.add(splitPane2, BorderLayout.CENTER);

        otherInfoLbl = new JLabel("当前状态：群聊中...");
        infoPanel.add(otherInfoLbl, BorderLayout.NORTH);

        msgListArea = new JTextArea();
        msgListArea.setFont(new Font("Segoe", Font.PLAIN, 20));
        msgListArea.setLineWrap(true);
        infoPanel.add(new JScrollPane(msgListArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));

        JPanel tempPanel = new JPanel();
        tempPanel.setLayout(new BorderLayout());
        sendPanel.add(tempPanel, BorderLayout.NORTH);

        // 聊天按钮面板
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        tempPanel.add(btnPanel, BorderLayout.CENTER);

        //发送文件按钮
        JButton sendFileBtn = new JButton(new ImageIcon(this.getClass().getResource("/images/sendPic.png")));
        sendFileBtn.setMargin(new Insets(0,0,0,0));
        sendFileBtn.setToolTipText("向对方发送文件");
        btnPanel.add(sendFileBtn);

        //私聊按钮
        rybqBtn = new JCheckBox("私聊");
        tempPanel.add(rybqBtn, BorderLayout.EAST);

        //要发送的信息的区域
        sendArea = new JTextArea();
        sendArea.setFont(new Font("Segoe", Font.PLAIN, 20));
        sendArea.setLineWrap(true);
        sendPanel.add(new JScrollPane(sendArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));

        // 聊天按钮面板
        JPanel btn2Panel = new JPanel();
        btn2Panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        this.add(btn2Panel, BorderLayout.SOUTH);
        JButton submitBtn = new JButton("发送(S)");
        submitBtn.setToolTipText("按Enter键发送消息");
        btn2Panel.add(submitBtn);
        sendPanel.add(btn2Panel, BorderLayout.SOUTH);

        //获取在线用户并缓存
        GlobalData.setOnlineUserListModel();
        //在线用户列表
        userList = new JList<>(GlobalData.getOnlineUserListModel());
        userList.setCellRenderer(new MyCellRenderer());
        //设置为单选模式
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userListPanel.add(new JScrollPane(userList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));

        //关闭窗口
        this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e) {
                logout();
            }
        });

        //选择某个用户私聊
        rybqBtn.addActionListener(e -> {
            if(rybqBtn.isSelected()){
                User selectedUser = userList.getSelectedValue();
                if(null == selectedUser){
                    otherInfoLbl.setText("当前状态：私聊(选中在线用户列表中某个用户进行私聊)...");
                }else if(GlobalData.getCurrentUser().getId() == selectedUser.getId()){
                    otherInfoLbl.setText("当前状态：想自言自语?...系统不允许");
                }else{
                    otherInfoLbl.setText("当前状态：与 "+ selectedUser.getNickname());
                }
            }else{
                otherInfoLbl.setText("当前状态：群聊...");
            }
        });

        //选择某个用户
        userList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                User selectedUser = userList.getSelectedValue();
                if(rybqBtn.isSelected()){
                    if(GlobalData.getCurrentUser().getId() == selectedUser.getId()){
                        otherInfoLbl.setText("当前状态：想自言自语?...系统不允许");
                    }else{
                        otherInfoLbl.setText("当前状态：与 "+ selectedUser.getNickname());
                    }
                }
            }
        });

        //发送文本消息
        sendArea.addKeyListener(new KeyAdapter(){
            public void keyPressed(KeyEvent e){
                if(e.getKeyCode() == Event.ENTER){
                    sendTxtMsg();
                }
            }
        });
        submitBtn.addActionListener(event -> sendTxtMsg());

        //发送文件
        sendFileBtn.addActionListener(event -> sendFile());
        new ClientThread(this).start();
    }

    /** 发送文本消息 */
    public void sendTxtMsg(){
        String content = sendArea.getText();
        if ("".equals(content)) { //无内容
            JOptionPane.showMessageDialog(ChatFrame.this, "不能发送空消息!",
                    "不能发送", JOptionPane.ERROR_MESSAGE);
        } else { //发送
            User selectedUser = userList.getSelectedValue();
            Request request = new Request();
            if(rybqBtn.isSelected()){  //私聊
                if(null == selectedUser){
                    JOptionPane.showMessageDialog(ChatFrame.this, "没有选择私聊对象!",
                            "不能发送", JOptionPane.ERROR_MESSAGE);
                    return;
                }else if (GlobalData.getCurrentUser().getId() == selectedUser.getId()){
                    JOptionPane.showMessageDialog(ChatFrame.this, "不能给自己发送消息!",
                            "不能发送", JOptionPane.ERROR_MESSAGE);
                    return;
                }else{
                    request.setAttribute("toUser",selectedUser);
                }
            }
            request.setAction("chat");
            request.setAttribute("msg", content);
            IOUtil.sendReq(request);

            //JTextArea中按“Enter”时，清空内容并回到首行
            InputMap inputMap = sendArea.getInputMap();
            ActionMap actionMap = sendArea.getActionMap();
            Object transferTextActionKey = "TRANSFER_TEXT";
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0),transferTextActionKey);
            actionMap.put(transferTextActionKey,new AbstractAction() {
                private static final long serialVersionUID = 1L;
                public void actionPerformed(ActionEvent e) {
                    sendArea.setText("");
                    sendArea.requestFocus();
                }
            });
            sendArea.setText("");
        }
    }

    /** 发送文件 */
    private void sendFile() {
        User selectedUser = userList.getSelectedValue();
        if(null != selectedUser){
            if(GlobalData.getCurrentUser().getId() == selectedUser.getId()){
                JOptionPane.showMessageDialog(ChatFrame.this, "不能给自己发送文件!",
                        "不能发送", JOptionPane.ERROR_MESSAGE);
            }else{
                JFileChooser jfc = new JFileChooser();
                if (jfc.showOpenDialog(ChatFrame.this) == JFileChooser.APPROVE_OPTION) {
                    File file = jfc.getSelectedFile();
                    sendFile = new FileInfo();
                    sendFile.setToId(selectedUser.getId());
                    try {
                        sendFile.setSrcName(file.getCanonicalPath());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    Request request = new Request();
                    request.setAction("toSendFile");
                    request.setAttribute("fileInfo", sendFile);
                    IOUtil.sendReq(request);

                    append2TextArea("【文件消息】向 "
                            + selectedUser.getNickname() + "发送文件 ["
                            + file.getName() + "]，等待对方接收...\n");
                }
            }
        }else{
            JOptionPane.showMessageDialog(ChatFrame.this, "不能给所有在线用户发送文件!",
                    "不能发送", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** 关闭客户端 */
    private void logout() {
        int select = JOptionPane.showConfirmDialog(ChatFrame.this,
                "确定退出吗？\n\n退出程序将中断与服务器的连接!", "退出聊天室",
                JOptionPane.YES_NO_OPTION);
        if (select == JOptionPane.YES_OPTION) {
            Request req = new Request();
            req.setAction("logout");
            IOUtil.sendReq(req);
            System.exit(0);
        }else{
            this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        }
    }

    /** 把指定文本添加到消息列表文本域中 */
    public void append2TextArea(String txt) {
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        ChatFrame.msgListArea.append(" "+df.format(new Date())+txt);
        //把光标定位到文本域的最后一行
        ChatFrame.msgListArea.setCaretPosition(ChatFrame.msgListArea.getDocument().getLength());
    }

    /*踢除*/
    public void remove() {
        JOptionPane.showMessageDialog(null, "您已被踢出聊天室", "",JOptionPane.ERROR_MESSAGE);
        Request req = new Request();
        req.setAction("logout");
        IOUtil.sendReq(req);
        System.exit(0);
    }
}
