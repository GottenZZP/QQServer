package qqCommon;

import java.io.Serializable;

/**
 * 表示客户端和服务器端通信的消息对象
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    private String mesType; // 消息类型
    private String sender; // 发送者
    private String getter; // 接收者
    private String content; // 消息内容
    private String sendTime; // 发送时间
    private byte[] data; // 传输文件的字节数组
    private int lenFile; // 传输文件的字节数组的长度
    private String src; // 传输文件的源地址
    private String dest; // 传输文件的目的地址

    public Message(String mesType, String sender, String getter, String content, String sendTime) {
        this.mesType = mesType;
        this.sender = sender;
        this.getter = getter;
        this.content = content;
        this.sendTime = sendTime;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getLenFile() {
        return lenFile;
    }

    public void setLenFile(int lenFile) {
        this.lenFile = lenFile;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public Message() {
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getMesType() {
        return mesType;
    }

    public String getSender() {
        return sender;
    }

    public String getGetter() {
        return getter;
    }

    public String getContent() {
        return content;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setMesType(String mesType) {
        this.mesType = mesType;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setGetter(String getter) {
        this.getter = getter;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }
}
