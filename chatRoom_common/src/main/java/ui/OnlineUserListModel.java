package ui;

import model.User;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class OnlineUserListModel extends AbstractListModel<User> {
    private static final long serialVersionUID = 1L;
    private List<User> onlineUsers;

    public OnlineUserListModel(List<User> onlineUsers) {
        this.onlineUsers = onlineUsers;
    }

    public OnlineUserListModel() {
        onlineUsers = new ArrayList<>();
    }

    public void addElement(User user) {
        if (onlineUsers.contains(user)) {
            return;
        }
        int index = onlineUsers.size();
        onlineUsers.add(user);
        fireIntervalAdded(this, index, index);
    }

    public void removeElement(User user) {
        int index = onlineUsers.indexOf(user);
        if (index >= 0) {
            fireIntervalRemoved(this, index, index);
        }
        onlineUsers.remove(user);
    }

    public int getSize() {
        return onlineUsers.size();
    }

    public User getElementAt(int i) {
        return onlineUsers.get(i);
    }
}
