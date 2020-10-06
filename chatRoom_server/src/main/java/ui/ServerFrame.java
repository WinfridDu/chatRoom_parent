package ui;

import controller.Controller;
import domain.ObjectIO;
import domain.Session;
import model.Response;
import model.ResponseType;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ServerFrame extends JFrame {

    private JTextField textField = new JTextField();
    private JList<User> jList;
    private static OnlineUserListModel onlineUserListModel = new OnlineUserListModel();

    public ServerFrame(){
        init();
        setVisible(true);
    }

    private void init(){
        setTitle("在线用户列表");
        Font segoe = new Font("Segoe", Font.PLAIN, 20);
        JPanel contentPane = (JPanel) getContentPane();
        setSize(new Dimension(400,300));
        //居中
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = getSize();
        setLocation((screenSize.width - frameSize.width) / 2,
                (screenSize.height - frameSize.height) / 2);

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());
        contentPane.add(jPanel,BorderLayout.NORTH);

        textField.addFocusListener(new JTextFieldHintListener(textField, "群发消息"));
        textField.setFont(segoe);
        jPanel.add(textField);
        JButton jButton = new JButton("发送(S)");
        jButton.setFont(segoe);
        jButton.addActionListener(e -> sysSend());
        jPanel.add(jButton,BorderLayout.EAST);

        jList = new JList<>(onlineUserListModel);
        jList.setCellRenderer(new MyCellRenderer());
        //设置为单选模式
        jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contentPane.add(new JScrollPane(jList), BorderLayout.CENTER);

        //右键弹出菜单
        JPopupMenu pop = new JPopupMenu();// 弹出菜单对象
        JMenuItem kick = new JMenuItem("从群聊中移除");// 菜单项对象
        kick.setFont(new Font("Segoe", Font.PLAIN, 14));
        kick.addActionListener(e -> kick());// 弹出菜单上的事件监听器对象
        pop.add(kick);
        jList.setComponentPopupMenu(pop);

        textField.addKeyListener(new KeyAdapter(){
            public void keyPressed(KeyEvent e){
                if(e.getKeyCode() == Event.ENTER){
                    sysSend();
                }
            }
        });

        //关闭窗口
        this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e) {
                int select = JOptionPane.showConfirmDialog(null, "确定关闭服务器吗", "服务器关闭提示",JOptionPane.YES_NO_OPTION);
                if (select == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }else{
                    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
                }
            }
        });
    }

    /** 广播 */
    public void sysSend(){
        Response response = new Response();
        response.setStatus(true);
        response.setType(ResponseType.BOARD);
        String msg = textField.getText();
        response.setData(" "+"系统消息：\n  "+msg+"\n");
        Controller.response2All(response);
        textField.setText("");
    }

    /** 踢掉 */
    private void kick(){
        User selectedValue = jList.getSelectedValue();
        Response response = new Response();
        response.setStatus(true);
        response.setType(ResponseType.REMOVE);
        ObjectIO io = Session.getOnlineUserIOMap().get(selectedValue.getId());
        Controller.writeObject(response, io);
    }

    public static void addItem(User user){
        onlineUserListModel.addElement(user);
    }

    public static void removeItem(User user){
        onlineUserListModel.removeElement(user);
    }
}
