package Chat.Network;

import Chat.ChatConfig;
import Chat.ChatEventListener;
import Chat.GUI.ChatGUI;

import java.io.IOException;
import java.net.*;

public class ChatMulticastReceiver implements Runnable {

    private ChatEventListener listener;
    private MulticastSocket socket;
    private boolean running = true;
    private final String multicastIP = ChatConfig.MULTICAST_IP;
    private final int port = ChatConfig.PORT;

    public ChatMulticastReceiver(ChatEventListener listener) throws IOException {
        this.listener = listener;
        InetAddress iadr = InetAddress.getByName(multicastIP);

        this.socket = new MulticastSocket(port);
        InetSocketAddress group = new InetSocketAddress(iadr, port);

        NetworkInterface netIf = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
        if (netIf == null) {
            netIf = NetworkInterface.getByName("wlan0");
            if (netIf == null) {
                throw new IOException("Could not find suitable network interface.");
            }
        }

        socket.joinGroup(group, netIf);

        // Starta receiver tråd
        Thread receiverThread = new Thread(this);
        receiverThread.start();
    }

    @Override
    public void run() {
        byte[] data = new byte[256];
        DatagramPacket packet = new DatagramPacket(data, data.length);

        while (running) {
            try {
                data = new byte[256];
                packet.setData(data);

                socket.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength());
                processMessage(message);
            } catch (IOException e) {
                if (running) {
                    System.err.println("Error receiving multicast: " + e.getMessage());
                }
            }
        }
    }

    private void processMessage(String message) {
        try {
            if (message.equals(ChatConfig.REQUEST_USERLIST)) {
                handleUserListRequest();
                return;
            }

            boolean isConnected = false;
            if (listener instanceof ChatGUI) {
                isConnected = ((ChatGUI) listener).isConnected();
            }

            if (!isConnected) {
                return;
            }

            if (message.startsWith(ChatConfig.JOIN_PREFIX)) {
                handleJoinMessage(message.substring(ChatConfig.JOIN_PREFIX.length()).trim());
            } else if (message.startsWith(ChatConfig.LEAVE_PREFIX)) {
                handleLeaveMessage(message.substring(ChatConfig.LEAVE_PREFIX.length()).trim());
            } else if (message.startsWith(ChatConfig.MESSAGE_PREFIX)) {
                handleChatMessage(message);
            } else if (message.startsWith(ChatConfig.USERLIST_PREFIX)) {
                handleUserListUpdate(message.substring(ChatConfig.USERLIST_PREFIX.length()));
            }
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
        }
    }

    private void handleJoinMessage(String username) {
        listener.onUserJoined(username);
    }

    private void handleLeaveMessage(String username) {
        listener.onUserLeft(username);
    }

    private void handleChatMessage(String message) {
        String[] parts = message.split(":", 3);
        if (parts.length == 3) {
            String sender = parts[1].trim();
            String msg = parts[2].trim();
            listener.onMessageReceived(sender, msg);
        }
    }

    private void handleUserListRequest() {
        if (listener instanceof ChatGUI) {
            ChatGUI gui = (ChatGUI) listener;
            if (gui.isConnected() && gui.getSender() != null) {
                try {
                    gui.getSender().sendUserList(gui.getChatRoom().getActiveUsers());
                } catch (IOException e) {
                    System.err.println("Error sending user list: " + e.getMessage());
                }
            }
        }
    }

    private void handleUserListUpdate(String userListStr) {

        String[] users = userListStr.split(",");
        if (listener instanceof ChatGUI) {
            ChatGUI gui = (ChatGUI) listener;
            for (String user : users) {
                if (!user.trim().isEmpty()) {
                    gui.getChatRoom().addUser(user.trim());
                }
            }
            gui.updateUserList();
        }
    }

    public void stop() {
        running = false;
        if (socket != null && !socket.isClosed()) {
            try {
                socket.leaveGroup(InetAddress.getByName(multicastIP));
            } catch (IOException e) {
            }
            socket.close();
        }
    }
}