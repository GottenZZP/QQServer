package qqCommon;

public interface MessageType {
    String MESSAGE_LOGIN_SUCCEED = "1"; // 表示登录成功
    String MESSAGE_LOGIN_FAIL = "2"; // 表示登录失败
    String MESSAGE_COMM_MES = "3"; // 表示普通信息包
    String MESSAGE_GET_ONLINE_FRIEND = "4"; // 表示要求在线好友的包
    String MESSAGE_RET_ONLINE_FRIEND = "5"; // 返回在线好友的包
    String MESSAGE_CLIENT_EXIT = "6"; // 表示客户端请求退出
    String MESSAGE_TO_ALL_MES = "7"; // 表示群发消息
    String MESSAGE_FILE_MES = "8"; // 表示文件消息
    String MESSAGE_RET_FILE_MES = "9"; // 表示返回文件消息
    String REGISTER_SUCCEED = "10"; // 表示注册成功
    String REGISTER_FAIL = "11"; // 表示注册失败
}
