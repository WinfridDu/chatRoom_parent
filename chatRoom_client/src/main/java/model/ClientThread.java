package model;

import ui.ChatFrame;
import util.IOUtil;

import javax.swing.*;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientThread extends Thread {
    private ChatFrame currentFrame;  //当前窗体

    public ClientThread(JFrame frame){
        currentFrame = (ChatFrame)frame;
    }

    public void run() {
        try {
            while (IOUtil.getSocket().isConnected()) {
                Response response = (Response) (IOUtil.getOis().readObject());

                ResponseType type = response.getType();

                if (type == ResponseType.LOGIN) {
                    User newUser = (User)response.getData();
                    GlobalData.getOnlineUserListModel().addElement(newUser);
                    currentFrame.append2TextArea("【系统消息】用户"+newUser.getNickname() + "上线了！\n");
                }
                else if(type == ResponseType.LOGOUT){
                    User newUser = (User)response.getData();
                    GlobalData.getOnlineUserListModel().removeElement(newUser);
                    currentFrame.append2TextArea("【系统消息】用户"+newUser.getNickname() + "下线了！\n");
                }
                else if(type == ResponseType.CHAT){ //聊天
                    String msg = (String)response.getData();
                    currentFrame.append2TextArea(msg);
                }
                else if(type == ResponseType.TOSENDFILE){ //准备发送文件
                    toSendFile(response);
                }else if(type == ResponseType.AGREERECEIVEFILE){ //对方同意接收文件
                    sendFile(response);
                }else if(type == ResponseType.REFUSERECEIVEFILE){ //对方拒绝接收文件
                    currentFrame.append2TextArea("【文件消息】对方拒绝接收，文件发送失败！\n");
                }else if(type == ResponseType.BOARD){
                    String msg = (String)response.getData();
                    currentFrame.append2TextArea(msg);
                }else if(type == ResponseType.REMOVE){
                    currentFrame.remove();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /** 发送文件 */
    private void sendFile(Response response) {
        final FileInfo sendFile = (FileInfo)response.getData();

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        Socket socket = null;
        try {
            socket = new Socket(sendFile.getDestIp(),sendFile.getDestPort());//套接字连接
            bis = new BufferedInputStream(new FileInputStream(sendFile.getSrcName()));//文件读入
            bos = new BufferedOutputStream(socket.getOutputStream());//文件写出

            byte[] buffer = new byte[1024];
            int n;
            while ((n = bis.read(buffer)) != -1){
                bos.write(buffer, 0, n);
            }
            bos.flush();
            synchronized (this) {
                currentFrame.append2TextArea("【文件消息】文件发送完毕!\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            IOUtil.close(bis,bos,socket);
        }
    }

    /** 接收文件 */
    private void receiveFile(FileInfo sendFile) {

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        ServerSocket serverSocket = null;
        Socket socket;
        try {
            serverSocket = new ServerSocket(sendFile.getDestPort());
            socket = serverSocket.accept();
            bis = new BufferedInputStream(socket.getInputStream());
            bos = new BufferedOutputStream(new FileOutputStream(sendFile.getDestName()));

            byte[] buffer = new byte[1024];
            int n;
            while ((n = bis.read(buffer)) != -1){
                bos.write(buffer, 0, n);
            }
            bos.flush();
            synchronized (this) {
                currentFrame.append2TextArea("【文件消息】文件接收完毕!存放在["
                        + sendFile.getDestName()+"]\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            IOUtil.close(bis,bos,serverSocket);
        }
    }

    /** 准备发送文件	 */
    private void toSendFile(Response response) {
        FileInfo sendFile = (FileInfo)response.getData();

        String fromName = sendFile.getFromNickname();
        String fileName = sendFile.getSrcName()
                .substring(sendFile.getSrcName().lastIndexOf(File.separator)+1);

        Request request = new Request();
        request.setAttribute("sendFile", sendFile);

        new Thread(()->{
            int select = JOptionPane.showConfirmDialog(this.currentFrame,
                    fromName + " 向您发送文件 [" + fileName+ "]!\n同意接收吗?",
                    "接收文件", JOptionPane.YES_NO_OPTION);
            try{
                if (select == JOptionPane.YES_OPTION) {
                    JFileChooser jfc = new JFileChooser();
                    jfc.setSelectedFile(new File(fileName));
                    int result = jfc.showSaveDialog(this.currentFrame);

                    if (result == JFileChooser.APPROVE_OPTION){
                        //设置目的地文件名
                        sendFile.setDestName(jfc.getSelectedFile().getCanonicalPath());
                        //设置目标地的IP和接收文件的端口
                        sendFile.setDestIp(InetAddress.getLocalHost().getHostAddress());
                        sendFile.setDestPort(6666);
                        request.setAction("agreeReceiveFile");
                        currentFrame.append2TextArea("【文件消息】您已同意接收来自 "
                                + fromName +" 的文件，正在接收文件 ...\n");
                        new Thread(()->receiveFile(sendFile)).start();
                    } else {
                        request.setAction("refuseReceiveFile");
                        currentFrame.append2TextArea("【文件消息】您已拒绝接收来自 "
                                + fromName +" 的文件!\n");
                    }
                } else {
                    request.setAction("refuseReceiveFile");
                    currentFrame.append2TextArea("【文件消息】您已拒绝接收来自 "
                            + fromName +" 的文件!\n");
                }
                IOUtil.sendReq(request);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}

