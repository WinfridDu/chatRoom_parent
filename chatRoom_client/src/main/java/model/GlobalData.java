package model;

import ui.OnlineUserListModel;

import java.util.List;

public class GlobalData {
    /** 当前客户端的用户信息 */
    private static User currentUser;
    /** 在线用户列表 */
    private static List<User> onlineUsers;
    /** 在线用户JList的Model */
    private static OnlineUserListModel onlineUserListModel;

    public static void setCurrentUser(User currentUser) {
        GlobalData.currentUser = currentUser;
    }

    public static void setOnlineUsers(List<User> onlineUsers) {
        GlobalData.onlineUsers = onlineUsers;
    }

    public static void setOnlineUserListModel() {
        GlobalData.onlineUserListModel = new OnlineUserListModel(onlineUsers);
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static OnlineUserListModel getOnlineUserListModel() {
        return onlineUserListModel;
    }
}
