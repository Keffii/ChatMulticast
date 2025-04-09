package Chat.GUI;

import Chat.*;
import Chat.Network.ChatMulticastReceiver;
import Chat.Network.ChatMulticastSender;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ChatGUI extends JFrame implements ActionListener, ChatEventListener {
    private boolean connected = false;
    private String username;
    private ChatRoom chatRoom = new ChatRoom();
    private ChatMulticastReceiver receiver;
    private ChatMulticastSender sender;
    private ConnectionPanel connectionPanel;
    private ChatArea chatArea;
    private UserArea userArea;
    private InputArea inputArea;

    public ChatGUI() {
        setupFrame();
        setupUI();
        initializeNetwork();
        setVisible(true);
    }

    private void setupFrame() {
        setTitle("Chat Multicast");
        setSize(580, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                disconnect();

                // Clean up resources
                try {
                    if (sender != null) {
                        sender.close();
                    }
                    if (receiver != null) {
                        receiver.close();
                    }
                } catch (IOException ex) {
                    chatArea.append("Error closing resources: " + ex.getMessage() + "\n");
                }

                System.exit(0);
            }
        });
    }

    private void setupUI() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        connectionPanel = new ConnectionPanel();
        JPanel topPanel = connectionPanel.setupConnectionPanel(this);
        panel.add(topPanel, BorderLayout.NORTH);

        chatArea = new ChatArea();
        JPanel chatPanel = chatArea.setupChatArea();
        panel.add(chatPanel, BorderLayout.CENTER);

        userArea = new UserArea();
        JPanel userPanel = userArea.setupUserArea();
        panel.add(userPanel, BorderLayout.EAST);

        inputArea = new InputArea();
        JPanel inputPanel = inputArea.setupInputArea(this);
        panel.add(inputPanel, BorderLayout.SOUTH);

        add(panel);
    }

    private void initializeNetwork() {
        try {
            sender = new ChatMulticastSender();
            receiver = new ChatMulticastReceiver(this);
            chatArea.append("Nätverket initierades\n");
        } catch (IOException e) {
            chatArea.append("Fel vid initiering av nätverket: " + e.getMessage() + "\n");
        }
    }

    // Handles button clicks and text input events
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == inputArea.getChatInput()) {
            ChatMessage.sendMessage(this);
        } else if (e.getSource() == connectionPanel.getDisconnectButton()) {
            disconnect();
        } else if (e.getSource() == connectionPanel.getConnectButton()) {
            connect();
        } else if (e.getSource() == connectionPanel.getExitButton()) {
            exitApplication();
        }
    }

    private void exitApplication() {
        if (connected) {
            disconnect();
        }

        // Clean up resources
        try {
            if (sender != null) {
                sender.close();
            }
            if (receiver != null) {
                receiver.close();
            }
        } catch (IOException ex) {
            chatArea.append("Error closing resources: " + ex.getMessage() + "\n");
        }

        // Exit the application
        System.exit(0);
    }

    private void connect() {
        username = connectionPanel.getUsernameField().getText().trim();

        try {
            if (receiver == null) {
                receiver = new ChatMulticastReceiver(this);
            }

            updateUIForConnection(true);

            // Add self and update UserList
            chatRoom.addUser(username);
            updateUserList();

            if (sender != null) {
                sender.sendJoin(username);
                sender.requestUserList();
            }
        } catch (IOException e) {
            chatArea.append("Error connecting to chat: " + e.getMessage() + "\n");
        }
    }

    private void disconnect() {
        if (connected) {
            try {
                if (sender != null) {
                    sender.sendLeave(username);
                }
            } catch (IOException e) {
                chatArea.append("Error sending leave message: " + e.getMessage() + "\n");
            }

            // Close and reset receiver
            if (receiver != null) {
                try {
                    receiver.close();
                } catch (IOException e) {
                    chatArea.append("Error closing receiver: " + e.getMessage() + "\n");
                }
                receiver = null;
            }

            updateUIForConnection(false);

            // Clean chat and update UserList
            chatRoom = new ChatRoom();
            updateUserList();

            chatArea.append("Frånkopplad från chatten\n");
        }
    }

    // Updates UI component states based on connection status
    private void updateUIForConnection(boolean isConnected) {
        inputArea.setEnabled(isConnected);
        connectionPanel.getUsernameField().setEnabled(!isConnected);
        connectionPanel.getConnectButton().setEnabled(!isConnected);
        connectionPanel.getDisconnectButton().setEnabled(isConnected);
        connected = isConnected;
    }

    public void updateUserList() {
        synchronized (chatRoom.getActiveUsers()) {
            userArea.updateUserList(chatRoom.getActiveUsers());
        }
    }

    // Getter methods
    public JTextField getChatInput() {
        return inputArea.getChatInput();
    }

    public JTextArea getChatArea() {
        return chatArea.getChatArea();
    }

    public String getUsername() {
        return username;
    }

    public boolean isConnected() {
        return connected;
    }

    public ChatMulticastSender getSender() {
        return sender;
    }

    public ChatRoom getChatRoom() {
        return chatRoom;
    }

    // ChatEventListener
    @Override
    public void onUserJoined(String username) {
        chatRoom.addUser(username);
        chatArea.append("User " + username + " har anslutit till chatten\n");
        updateUserList();
    }

    @Override
    public void onUserLeft(String username) {
        chatRoom.removeUser(username);
        chatArea.append("User " + username + " har lämnat chatten\n");
        updateUserList();
    }

    @Override
    public void onMessageReceived(String sender, String message) {
        String timeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
        chatArea.append("[" + timeStamp + "] " + sender + ": " + message + "\n");
    }
}