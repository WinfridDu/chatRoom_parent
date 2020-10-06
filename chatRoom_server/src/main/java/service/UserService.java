package service;

import dao.UserMapper;
import model.User;
import util.MPUtil;

import java.util.Random;

public class UserService {

    private UserMapper userMapper = MPUtil.getUserMapper();

    private Random random = new Random();

    public void saveUser(User user) {
        user.setId(random.nextInt(900000)+100000);//六位随机数
        userMapper.insert(user);
    }

    public User login(long id, String password) {
        User user = userMapper.selectById(id);
        if(null != user && !password.equals(user.getPassword()))
            return null;
        return user;
    }
}
