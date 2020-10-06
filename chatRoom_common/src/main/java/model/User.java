package model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.INPUT)
    private long id;
    private String password;
    private String nickname;
    private int head;
    private char sex;

    public User(String password, String nickname, char sex, int head){
        this.password = password;
        this.sex = sex;
        this.head = head;
        if(null==nickname||nickname.equals(""))
        {
            this.nickname = "未命名";
        }else{
            this.nickname = nickname;
        }
    }



    public ImageIcon getHeadIcon(){

        ImageIcon imageIcon = new ImageIcon(this.getClass().getResource("/images/" + head + ".jpeg"));
        imageIcon.setImage(imageIcon.getImage().getScaledInstance(80,80, Image.SCALE_DEFAULT));
        return imageIcon;
    }
}
