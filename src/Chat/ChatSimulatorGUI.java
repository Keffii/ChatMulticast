package Chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Random;

public class ChatSimulatorGUI extends JFrame implements ActionListener {
    private JTextField chatInput;
    private JTextArea chatArea;
    private JTextArea userArea;
    private JButton disconnectButton;
    private JButton connectButton;
    private JTextField usernameField;
    private boolean connected = false;
    private String username = "Anonymous";
    private ChatRoom chatRoom = new ChatRoom();
    private ChatMulticastReceiver receiver;
    private ChatMulticastSender sender;

    ChatSimulatorGUI() {
        setTitle("Chat Rum 1");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                disconnect();
                if (receiver != null) {
                    receiver.stop();
                }
                System.exit(0);
            }
        });

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Anslutningspanel
        JPanel topPanel = new JPanel(new BorderLayout(5, 0));
        JPanel connectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel nameLabel = new JLabel("Användarnamn:");
        usernameField = new JTextField(15);
        usernameField.setText("Chattare" + new Random().nextInt(1000)); //Chattare+random nummer vid uppstart
        connectButton = new JButton("Anslut");
        disconnectButton = new JButton("Koppla ner");
        disconnectButton.setEnabled(false);

        connectionPanel.add(nameLabel);
        connectionPanel.add(usernameField);
        connectionPanel.add(connectButton);
        connectionPanel.add(disconnectButton);

        topPanel.add(connectionPanel, BorderLayout.WEST);
        panel.add(topPanel, BorderLayout.NORTH);

        // Chat område
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        chatScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        chatScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panel.add(chatScrollPane, BorderLayout.CENTER);

        // Användarområde
        userArea = new JTextArea(10, 15);
        userArea.setEditable(false);
        userArea.setText("I chatten just nu:\n");
        JScrollPane userScrollPane = new JScrollPane(userArea);
        userScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panel.add(userScrollPane, BorderLayout.EAST);

        // Indata område
        chatInput = new JTextField();
        chatInput.setEnabled(false);
        panel.add(chatInput, BorderLayout.SOUTH);

        add(panel);

        // Action listeners
        connectButton.addActionListener(this);
        disconnectButton.addActionListener(this);
        chatInput.addActionListener(this);


        try {
            // Initiera multicast sender
            sender = new ChatMulticastSender();

            // Initiera multicast receiver
            receiver = new ChatMulticastReceiver(this);

            chatArea.append("Network initialized successfully\n");
        } catch (IOException e) {
            chatArea.append("Error initializing network: " + e.getMessage() + "\n");
        }

        setVisible(true);
    }

    //Hålla reda på källan för listener och gör det som önskas
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == chatInput) {
            ChatMessage.sendMessage(this);
        } else if (e.getSource() == disconnectButton) {
            disconnect();
        } else if (e.getSource() == connectButton) {
            connect();
        }
    }

    // Ansluta metod
    private void connect() {
        username = usernameField.getText().trim();
        if (username.isEmpty()) {
            username = "Anonymous";
            usernameField.setText(username);
        }

        // Aktivera/inaktivera komponenter
        chatInput.setEnabled(true);
        usernameField.setEnabled(false);
        connectButton.setEnabled(false);
        disconnectButton.setEnabled(true);
        connected = true;


        try {
            if (sender != null) {
                sender.sendJoin(username);

                new Thread(() -> {
                    try {
                        sender.sendUserList(chatRoom.getActiveUsers());
                    } catch (Exception e) {
                        chatArea.append("Error synchronizing user list: " + e.getMessage() + "\n");
                    }
                }).start();
            }
        } catch (IOException e) {
            chatArea.append("Error sending join message: " + e.getMessage() + "\n");
        }
    }

    // Koppla ner metod
    private void disconnect() {
        if (connected) {
            try {
                if (sender != null) {
                    sender.sendLeave(username);
                }
            } catch (IOException e) {
                if (chatArea != null) {
                    chatArea.append("Error sending leave message: " + e.getMessage() + "\n");
                }
            }

            // Aktivera/inaktivera komponenter
            chatInput.setEnabled(false);
            usernameField.setEnabled(true);
            connectButton.setEnabled(true);
            disconnectButton.setEnabled(false);
            connected = false;

            // Ta bort från aktiva användare
            chatRoom.removeUser(username);
            updateUserList();

            chatArea.append("Frånkopplad från chatten\n");
        }
    }

    // Method for updating active users list
    void updateUserList() {
        userArea.setText("I chatten just nu:\n");
        for (String user : chatRoom.getActiveUsers()) {
            userArea.append("- " + user + "\n");
        }
    }

    // Getters
    public JTextField getChatInput() {
        return chatInput;
    }

    public JTextArea getChatArea() {
        return chatArea;
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
}