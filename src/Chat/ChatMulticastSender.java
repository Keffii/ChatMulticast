package Chat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.List;

public class ChatMulticastSender {
    String multicastIP = "234.235.236.237";
    InetAddress iadr;
    int port = 12540;
    MulticastSocket socket;

    public ChatMulticastSender() throws IOException {
        iadr = InetAddress.getByName(multicastIP);
        socket = new MulticastSocket();
    }

    public void sendJoin(String username) throws IOException {
        sendMessage("Join:" + username);
    }

    public void sendLeave(String username) throws IOException {
        sendMessage("Leave:" + username);
    }

    public void sendChatMessage(String username, String message) throws IOException {
        sendMessage("Message:" + username + ":" + message);
    }

    public void sendUserList(List<String> users) throws IOException {
        String userList = "UserList:" + String.join(",", users);
        sendMessage(userList);
    }

    private void sendMessage(String fullMessage) throws IOException {
        byte[] data = fullMessage.getBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, iadr, port);
        socket.send(packet);
    }
}
