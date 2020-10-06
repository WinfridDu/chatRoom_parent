package controller;

import domain.ObjectIO;
import domain.Session;
import model.*;
import service.UserService;
import ui.ServerFrame;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class Controller implements Runnable{

    private Socket socket;
    private ObjectIO objectIO;
    private UserService userService = new UserService();
    private User user;

    public Controller(Socket socket){
        this.socket = socket;
        try {
            objectIO = new ObjectIO(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        boolean running = true;
        while (running){
            try {
                Request request = (Request)objectIO.getOis().readObject();
                String action = request.getAction();
                System.out.println("服务器读取了客户端的请求:" + action);
                Method method = this.getClass().getMethod(action, Request.class, Response.class);
                method.invoke(this, request, new Response());
                if (action.equals("logout"))
                    running = false;
            } catch (IOException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /** 注册 */
    public void registe(Request request, Response response){
        User user = (User)request.getAttribute("user");
        userService.saveUser(user);

        response.setStatus(true);
        response.setData(user);

        writeObject(response);
    }

    /** 登录 */
    public void login(Request request, Response response){
        String idStr = (String)request.getAttribute("id");
        String password = (String)request.getAttribute("password");
        user = userService.login(Long.parseLong(idStr), password);
        if(null == user){
            response.setStatus(false);
            response.setMsg("账号或密码不正确");
            writeObject(response);
            return;
        }
        if(Session.getonlineUsersMap().containsKey(user.getId())){
            response.setStatus(false);
            response.setMsg("您已登录"+user.getId()+"，不能重复登录");
            writeObject(response);
            return;
        }
        Session.getonlineUsersMap().put(user.getId(), user); //添加到在线用户
        Session.getOnlineUserIOMap().put(user.getId(), objectIO);

        Map<String,Object> map = new HashMap<>();
        map.put("onlineUsers",new CopyOnWriteArrayList<>(Session.getonlineUsersMap().values()));
        map.put("user", user);
        ServerFrame.addItem(user);

        response.setStatus(true);
        response.setData(map);
        writeObject(response);

        //通知其它用户有人上线了
        Response respToAll = new Response();
        respToAll.setType(ResponseType.LOGIN);
        respToAll.setData(user);
        response2Others(respToAll);
    }

    /** 聊天 */
    public void chat(Request request, Response response){
        String msg = (String)request.getAttribute("msg");
        response.setStatus(true);
        response.setType(ResponseType.CHAT);

        if(null != request.getAttribute("toUser")){ //私聊
            response.setData(" "+user.getNickname()+"\n  "+msg+"\n");
            ObjectIO io = Session.getOnlineUserIOMap().get(((User)request.getAttribute("toUser")).getId());
            writeObject(response, io);
            writeObject(response);
        }else{  //群聊
            response.setData(" "+user.getNickname()+"对大家说\n  "+msg+"\n");
            response2All(response);
        }
    }

    /** 准备发送文件 */
    public void toSendFile(Request request, Response response){
        response.setStatus(true);
        response.setType(ResponseType.TOSENDFILE);
        FileInfo sendFile = (FileInfo)request.getAttribute("fileInfo");
        sendFile.setFromNickname(user.getNickname());
        sendFile.setFromId(user.getId());
        response.setData(sendFile);
        //给文件接收方转发文件发送方的请求
        ObjectIO objectIO = Session.getOnlineUserIOMap().get(sendFile.getToId());
        writeObject(response, objectIO);
    }

    /** 拒绝接收文件 */
    public void refuseReceiveFile(Request request, Response response){
        FileInfo sendFile = (FileInfo)request.getAttribute("sendFile");
        response.setType(ResponseType.REFUSERECEIVEFILE);
        response.setData(sendFile);
        response.setStatus(true);
        //向请求方的输出流输出响应
        ObjectIO objectIO = Session.getOnlineUserIOMap().get(sendFile.getFromId());
        writeObject(response, objectIO);
    }

    /** 同意接收文件 */
    public void agreeReceiveFile(Request request, Response response){
        FileInfo sendFile = (FileInfo)request.getAttribute("sendFile");
        //向请求方(发送方)的输出流输出响应
        response.setType(ResponseType.AGREERECEIVEFILE);
        response.setData(sendFile);
        response.setStatus(true);
        ObjectIO objectIO = Session.getOnlineUserIOMap().get(sendFile.getFromId());
        writeObject(response, objectIO);
    }

    /** 客户端退出 */
    public void logout(Request request, Response response){
        System.out.println(socket.getInetAddress().getHostAddress()
                + ":" + socket.getPort() + "退出");
        response.setType(ResponseType.LOGOUT);
        response.setData(user);
        response2Others(response);
        try {
            Thread.sleep(200);//延缓socket关闭，避免客户端报错
            socket.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        if(null!=user){
            Session.getOnlineUserIOMap().remove(user.getId());
            Session.getonlineUsersMap().remove(user.getId());
            ServerFrame.removeItem(user);
        }
    }

    /** 给其他在线客户都发送响应 */
    private void response2Others(Response response){
        for(ObjectIO onlineUserIO : Session.getOnlineUserIOMap().values()){
            if(!objectIO.equals(onlineUserIO)){
                writeObject(response,onlineUserIO);
            }
        }
    }

    /** 给所有在线客户都发送响应 */
    public static void response2All(Response response){
        for(ObjectIO onlineUserIO : Session.getOnlineUserIOMap().values()){
            writeObject(response,onlineUserIO);
        }
    }

    private void writeObject(Response response){
        try {
            objectIO.getOos().writeObject(response);  //把响应对象往客户端写
            objectIO.getOos().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeObject(Response response, ObjectIO objectIO){
        try {
            objectIO.getOos().writeObject(response);  //把响应对象往客户端写
            objectIO.getOos().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
