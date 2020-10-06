package util;

import model.Request;
import model.Response;

import java.io.*;
import java.net.Socket;

public class IOUtil {
    private static Socket socket;
    private static ObjectInputStream ois;
    private static ObjectOutputStream oos;

    public static void init(Socket socket) throws IOException {

        IOUtil.socket = socket;
        oos = new ObjectOutputStream(socket.getOutputStream());
        ois = new ObjectInputStream(socket.getInputStream());
    }

    public static Socket getSocket(){
        return socket;
    }

    public static ObjectInputStream getOis(){
        return ois;
    }

    /** 发送请求对象,主动接收响应 */
    public static Response sendReqAndResp(Request request){
        Response response = null;
        try {
            oos.writeObject(request);
            oos.flush();
            response = (Response) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return response;
    }

    /** 发送请求对象,主动接收响应 */
    public static void sendReq(Request request){
        try {
            oos.writeObject(request);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void close(Closeable... closeables){
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
