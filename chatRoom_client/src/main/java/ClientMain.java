import ui.LoginFrame;
import util.IOUtil;

import javax.swing.*;
import java.io.IOException;
import java.net.Socket;

public class ClientMain {
    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        try {
            Socket socket = new Socket("localhost",2020);
            IOUtil.init(socket);
            new LoginFrame();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(new JFrame(),
                    "连接服务器失败，请检查!","服务器未连上", JOptionPane.ERROR_MESSAGE);//否则连接失败
            System.exit(0);
        }
    }
}
