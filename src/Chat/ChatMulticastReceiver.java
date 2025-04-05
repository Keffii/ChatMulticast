package Chat;

import java.io.IOException;
import java.net.*;

public class ChatMulticastReceiver implements Runnable {

    private ChatSimulatorGUI gui;
    private MulticastSocket socket;
    private boolean running = true;
    private final String multicastIP = "234.235.236.237";
    private final int port = 12540;

    public ChatMulticastReceiver(ChatSimulatorGUI gui) throws IOException {
        this.gui = gui;
        InetAddress iadr = InetAddress.getByName(multicastIP);

        this.socket = new MulticastSocket(port);
        InetSocketAddress group = new InetSocketAddress(iadr, port);


        NetworkInterface netIf = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
        if (netIf == null) {
            // Fallback to a common interface name if available
            netIf = NetworkInterface.getByName("wlan0");
            // If that doesn't work either, you might need to list interfaces and select one
            if (netIf == null) {
                gui.getChatArea().append("Could not find suitable network interface. Please check your network configuration.\n");
                return;
            }
        }

        socket.joinGroup(group, netIf);

        // Receiver tråd
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
            if (message.startsWith("Join:")) {
                String username = message.substring(5);
                gui.getChatRoom().addUser(username);
                gui.getChatArea().append(username + " har anslutit till chatten\n");
                gui.updateUserList();
            }
            else if (message.startsWith("Leave:")) {
                String username = message.substring(6);
                gui.getChatRoom().removeUser(username);
                gui.getChatArea().append(username + " har lämnat chatten\n");
                gui.updateUserList();
            }
            else if (message.startsWith("Message:")) {
                String[] parts = message.split(":", 3);
                if (parts.length == 3) {
                    String sender = parts[1];
                    String msg = parts[2];
                    gui.getChatArea().append(sender + ": " + msg + "\n");
                }
            }
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
        }
    }

    // Stäng socket
    public void stop() {
        running = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}