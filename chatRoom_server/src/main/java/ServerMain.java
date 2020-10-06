import controller.Controller;
import ui.ServerFrame;

import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {

    @SuppressWarnings("InfiniteLoopStatement")
    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(2020);
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            new ServerFrame();
            while (true) {
                // 监听客户端的连接
                Socket socket = serverSocket.accept();
                System.out.println("用户"
                        + socket.getInetAddress().getHostAddress()
                        + ":" + socket.getPort() + "建立连接");

                //针对每个客户端启动一个线程，在线程中调用请求处理器来处理每个客户端的请求
                new Thread(new Controller(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
