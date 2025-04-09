package Chat.Network;

import Chat.ChatConfig;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.List;

// Handles sending formatted messages to the multicast group
public class ChatMulticastSender implements Closeable {
    private final String multicastIP = ChatConfig.MULTICAST_IP;
    private final int port = ChatConfig.PORT;
    private InetAddress iadr;
    private MulticastSocket socket;

    public ChatMulticastSender() throws IOException {
        iadr = InetAddress.getByName(multicastIP);
        socket = new MulticastSocket();
    }

    public synchronized void sendJoin(String username) throws IOException {
        sendMessage(ChatConfig.JOIN_PREFIX + username);
    }

    public synchronized void sendLeave(String username) throws IOException {
        sendMessage(ChatConfig.LEAVE_PREFIX + username);
    }

    public void sendChatMessage(String username, String message) throws IOException {
        sendMessage(ChatConfig.MESSAGE_PREFIX + username + ":" + message);
    }

    public void sendUserList(List<String> users) throws IOException {
        String userList = ChatConfig.USERLIST_PREFIX + String.join(",", users);
        sendMessage(userList);
    }

    public void sendMessage(String fullMessage) throws IOException {
        byte[] data = fullMessage.getBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, iadr, port);
        socket.send(packet);
    }

    public void requestUserList() throws IOException {
        sendMessage(ChatConfig.REQUEST_USERLIST);
    }

    @Override
    public void close() throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}