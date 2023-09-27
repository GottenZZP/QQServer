package qqServer.server;

import qqCommon.Message;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 管理客户端和服务器端保持通信的线程
 * @author GottenZZP
 * @version 1.0
 */
public class ManageClientThreads {
    private static HashMap<String, ServerConnectClientThread> hm = new HashMap<>();
    private static HashMap<String, ArrayList<Message>> offlineMessages = new HashMap<>();

    public static HashMap<String, ArrayList<Message>> getOfflineMessages() {
        return offlineMessages;
    }

    public static void addOfflineMessage(String userId, Message message) {
        // 如果离线消息哈希表中存在了该用户的离线消息，就将该消息追加到该用户的离线消息集合中
        if (offlineMessages.containsKey(userId)) {
            offlineMessages.get(userId).add(message);
        } else { // 否则就创建一个新的离线消息集合，将该消息放入到该集合中，再将该集合放入到离线消息哈希表中
            ArrayList<Message> messages = new ArrayList<>();
            messages.add(message);
            offlineMessages.put(userId, messages);
        }
    }

    public static void setOfflineMessages(HashMap<String, ArrayList<Message>> offlineMessages) {
        ManageClientThreads.offlineMessages = offlineMessages;
    }

    // 把创建好的ServerConnectClientThread线程放入到HashMap, key为userId, value为ServerConnectClientThread
    public static void addClientThread(String userId, ServerConnectClientThread scct) {
        hm.put(userId, scct);
    }
    // 根据userId返回该用户的ServerConnectClientThread线程
    public static ServerConnectClientThread getClientThread(String userId) {
        return hm.get(userId);
    }

    public static HashMap<String, ServerConnectClientThread> getHm() {
        return hm;
    }

    /**
     * 返回当前在线用户的情况
     * @return 当前在线用户的情况
     */
    public static String getOnlineUser() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String userId : hm.keySet()) {
            stringBuilder.append(userId).append(" ");
        }
        return stringBuilder.toString();
    }

    /**
     * 根据userId移除该用户的ServerConnectClientThread线程
     * @param userId 用户id
     */
    public static void removeClientThread(String userId) {
        hm.remove(userId);
    }

    public static boolean isOnline(String userId) {
        return hm.containsKey(userId);
    }
}
