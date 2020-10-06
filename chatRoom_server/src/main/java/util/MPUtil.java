package util;

import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import dao.UserMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.io.IOException;
import java.io.InputStream;

public class MPUtil {

    private static UserMapper userMapper;

    static{
        try {
            String config = "mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(config);
            SqlSessionFactory factory = new MybatisSqlSessionFactoryBuilder().build(inputStream);
            factory.getConfiguration().addMapper(UserMapper.class);
            SqlSession sqlSession = factory.openSession(true);
            userMapper = sqlSession.getMapper(UserMapper.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static UserMapper getUserMapper(){
        return userMapper;
    }
}
