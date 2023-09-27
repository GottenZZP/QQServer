package qqServer.server;

import qqCommon.Message;
import qqCommon.MessageType;
import qqCommon.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 服务器端
 */
public class QQServer {
    private ServerSocket serverSocket;
    private static HashMap<String, User> validUsers = new HashMap<>();

    static {
        validUsers.put("100", new User("100", "123456"));
        validUsers.put("200", new User("200", "123456"));
        validUsers.put("300", new User("300", "123456"));
    }

    // 检查用户是否合法
    private boolean checkUser(User user) {
        if (validUsers.containsKey(user.getUserId())) {
            return validUsers.get(user.getUserId()).getPasswd().equals(user.getPasswd());
        }
        return false;
    }

    private boolean registerUser(User user) {
        if (validUsers.containsKey(user.getUserId())) {
            return false;
        }
        validUsers.put(user.getUserId(), user);
        return true;
    }

    private void sendOfflineMessages(String userId, Socket socket) {
        HashMap<String, ArrayList<Message>> offlineMessages = ManageClientThreads.getOfflineMessages();
        // 如果有离线消息，就将离线消息发给客户端
        if (offlineMessages.containsKey(userId)) {
            // 获取离线消息的message集合
            ArrayList<Message> messages = offlineMessages.get(userId);
            // 将离线消息发给客户端
            for (Message message : messages) {
                try {
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    objectOutputStream.writeObject(message);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            // 发送完成后，将该用户的离线消息从离线消息哈希表中删除
            offlineMessages.remove(userId);
        }
    }

    public QQServer() {
        try {
            serverSocket = new ServerSocket(9999);
            System.out.println("服务器启动成功");
            while (true) { // 不停地等待客户端的连接, 不是只接受一个客户端的连接
                Socket socket = serverSocket.accept();

                // 接收客户端第一次连接发来的用户登陆信息
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                User user = (User) objectInputStream.readObject();
                // 判断用户是否合法
                Message message = new Message();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                if (user.isRegister()) {
                    if (registerUser(user)) {
                        System.out.println("用户" + user.getUserId() + "注册成功");
                        message.setMesType(MessageType.REGISTER_SUCCEED);
                        // 将返回的消息发给客户端
                    } else {
                        System.out.println("用户" + user.getUserId() + "注册失败");
                        message.setMesType(MessageType.REGISTER_FAIL);
                        // 将返回的消息发给客户端
                    }
                    objectOutputStream.writeObject(message);
                    socket.close();
                } else if (checkUser(user)) {
                    System.out.println("用户" + user.getUserId() + "上线了");
                    message.setMesType(MessageType.MESSAGE_LOGIN_SUCCEED);
                    // 将返回的消息发给客户端
                    objectOutputStream.writeObject(message);
                    // 创建一个该客户端和服务器端保持通信的线程
                    ServerConnectClientThread serverConnectClientThread = new ServerConnectClientThread(socket, user.getUserId());
                    serverConnectClientThread.start();
                    // 将该线程加入到集合中, 以便管理
                    ManageClientThreads.addClientThread(user.getUserId(), serverConnectClientThread);
                    // 检查该用户是否有离线消息，如果有就将离线消息发给客户端
                    sendOfflineMessages(user.getUserId(), serverConnectClientThread.getSocket());
                } else {
                    System.out.println("用户" + user.getUserId() + "尝试登录，但是密码错误");
                    message.setMesType(MessageType.MESSAGE_LOGIN_FAIL);
                    objectOutputStream.writeObject(message);
                    socket.close();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
