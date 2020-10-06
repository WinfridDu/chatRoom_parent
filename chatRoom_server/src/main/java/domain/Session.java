package domain;

import model.User;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private static Map<Long, ObjectIO> onlineUserIOMap;
    private static Map<Long, User> onlineUsersMap;

    static {
        onlineUserIOMap = new HashMap<>();
        onlineUsersMap = new HashMap<>();
    }

    public static Map<Long, ObjectIO> getOnlineUserIOMap(){
        return onlineUserIOMap;
    }

    public static Map<Long, User> getonlineUsersMap(){
        return onlineUsersMap;
    }
}
