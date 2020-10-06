package model;

import lombok.Data;

import java.io.Serializable;

@Data
public class FileInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 消息接收者 */
    private Long toId;
    /** 消息发送者 */
    private String fromNickname;
    private Long fromId;
    /** 源文件名 */
    private String srcName;
    /** 目标文件名 */
    private String destName;
    /** 目标地IP */
    private String destIp;
    /** 目标地端口 */
    private int destPort;
}
