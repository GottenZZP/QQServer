package qqServer.server;

import qqCommon.Message;
import qqCommon.MessageType;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ServerConnectClientThread extends Thread {
    private Socket socket;
    private String userId;

    public ServerConnectClientThread(Socket socket, String userId) {
        this.socket = socket;
        this.userId = userId;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public void run() {
        boolean loop = true;
        while (loop) {
            // 不停地读取从客户端发来的消息
            try {
                System.out.println("服务器端和客户端" + userId + "保持通信的线程开始工作");
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) objectInputStream.readObject();

                switch (message.getMesType()) {
                    case MessageType.MESSAGE_GET_ONLINE_FRIEND: // 要求返回在线用户列表
                        System.out.println("服务器端收到了" + userId + "发来的要求返回在线用户列表的消息");
                        // 返回一个在线用户的消息
                        Message message1 = new Message();
                        message1.setMesType(MessageType.MESSAGE_RET_ONLINE_FRIEND);
                        message1.setContent(ManageClientThreads.getOnlineUser());
                        message1.setGetter(userId);

                        // 将该消息转发给客户端
                        ObjectOutputStream objectOutputStream1 = new ObjectOutputStream(socket.getOutputStream());
                        objectOutputStream1.writeObject(message1);
                        break;
                    case MessageType.MESSAGE_CLIENT_EXIT: // 客户端请求下线
                        System.out.println("服务器端收到了" + userId + "发来的下线消息");

                        Message message2 = new Message();
                        message2.setMesType(MessageType.MESSAGE_CLIENT_EXIT);
                        message2.setGetter(userId);
                        // 将该消息转发给客户端
                        ObjectOutputStream objectOutputStream2 = new ObjectOutputStream(socket.getOutputStream());
                        objectOutputStream2.writeObject(message2);

                        // 将该客户端的线程从集合中删除
                        ManageClientThreads.removeClientThread(userId);
                        // 关闭socket
                        socket.close();
                        loop = false;
                        break;
                    case MessageType.MESSAGE_COMM_MES: // 转发消息
                        System.out.println("服务器端收到了" + userId + "发来的消息");
                        // 转发消息
                        String getter = message.getGetter();
                        // 判断该用户是否在线
                        // TODO 还需添加一个检测用户是否是已注册的用户的功能
                        if (!ManageClientThreads.isOnline(getter)) {
                            // 将该消息存入离线消息集合中
                            ManageClientThreads.addOfflineMessage(getter, message);
                            System.out.println("用户" + getter + "不在线，已存入离线消息集合中");
                        } else {
                            // 获得该用户的通信线程
                            ServerConnectClientThread clientThread = ManageClientThreads.getClientThread(getter);
                            ObjectOutputStream objectOutputStream3 = new ObjectOutputStream(clientThread.getSocket().getOutputStream());
                            objectOutputStream3.writeObject(message);
                        }
                        break;
                    case MessageType.MESSAGE_TO_ALL_MES: // 群发消息
                        System.out.println("服务器端收到了" + userId + "发来的群发消息");
                        // 获得所有的通信线程
                        HashMap<String, ServerConnectClientThread> hm = ManageClientThreads.getHm();
                        ArrayList<ServerConnectClientThread> clientThreads = new ArrayList<>(hm.values());
                        // 转发消息
                        for (ServerConnectClientThread clientThread : clientThreads) {
                            if (!clientThread.getUserId().equals(userId)) {
                                ObjectOutputStream objectOutputStream4 = new ObjectOutputStream(clientThread.getSocket().getOutputStream());
                                objectOutputStream4.writeObject(message);
                            }
                        }
                        break;
                    case MessageType.MESSAGE_FILE_MES: // 转发文件消息
                        System.out.println("服务器端收到了" + userId + "发来的文件消息");
                        // 转发消息
                        String getter1 = message.getGetter();
                        // 判断该用户是否在线
                        if (!ManageClientThreads.isOnline(getter1)) {
                            System.out.println("用户" + getter1 + "不在线");
                        } else {
                            // 获得该用户的通信线程
                            ObjectOutputStream objectOutputStream5 = new ObjectOutputStream(ManageClientThreads.getClientThread(getter1).getSocket().getOutputStream());
                            objectOutputStream5.writeObject(message);
                        }
                        break;
                    default:
                        System.out.println("服务器端返回了未知的消息类型");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
