package Chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class ChatGUI extends JFrame implements ActionListener, ChatEventListener {
    private JTextField chatInput;
    private JTextArea chatArea;
    private JTextArea userArea;
    private JButton disconnectButton;
    private JButton connectButton;
    private JTextField usernameField;
    private boolean connected = false;
    private String username;
    private ChatRoom chatRoom = new ChatRoom();
    private ChatMulticastReceiver receiver;
    private ChatMulticastSender sender;

    ChatGUI() {
        setupFrame();
        setupUI();
        initializeNetwork();
        setVisible(true);
    }

    private void setupFrame() {
        setTitle("Chat Multicast");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                disconnect();
                if (receiver != null) {
                    receiver.stop();
                }
                if (sender != null) {
                    sender.close();
                }
                System.exit(0);
            }
        });
    }

    private void setupUI() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        setupConnectionPanel(panel);
        setupChatArea(panel);
        setupUserArea(panel);
        setupInputArea(panel);

        add(panel);
    }

    private void setupConnectionPanel(JPanel mainPanel) {
        JPanel topPanel = new JPanel(new BorderLayout(5, 0));
        JPanel connectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel nameLabel = new JLabel("Användarnamn:");
        usernameField = new JTextField(15);
        usernameField.setText("Chattare" + new Random().nextInt(100));
        connectButton = new JButton("Anslut");
        disconnectButton = new JButton("Koppla ner");
        disconnectButton.setEnabled(false);

        connectionPanel.add(nameLabel);
        connectionPanel.add(usernameField);
        connectionPanel.add(connectButton);
        connectionPanel.add(disconnectButton);

        topPanel.add(connectionPanel, BorderLayout.WEST);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Action listeners
        connectButton.addActionListener(this);
        disconnectButton.addActionListener(this);
    }

    private void setupChatArea(JPanel mainPanel) {
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        chatScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        chatScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        mainPanel.add(chatScrollPane, BorderLayout.CENTER);
    }

    private void setupUserArea(JPanel mainPanel) {
        userArea = new JTextArea(10, 15);
        userArea.setEditable(false);
        userArea.setText("I chatten just nu:\n");
        JScrollPane userScrollPane = new JScrollPane(userArea);
        userScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        mainPanel.add(userScrollPane, BorderLayout.EAST);
    }

    private void setupInputArea(JPanel mainPanel) {
        chatInput = new JTextField();
        chatInput.setEnabled(false);
        chatInput.addActionListener(this); // Listener för chatInput
        mainPanel.add(chatInput, BorderLayout.SOUTH);
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

    // Action performed för listeners
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

    private void connect() {
       username = usernameField.getText().trim();

        try {
            if (receiver == null) {
                receiver = new ChatMulticastReceiver(this);
            }

            updateUIForConnection(true);

            // Lägg till själv till userlist och uppdatera
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

            // Stoppa och rensa receiver
            if (receiver != null) {
                receiver.stop();
                receiver = null;
            }

            updateUIForConnection(false);

            // Rensa chat och uppdatera userlist
            chatRoom = new ChatRoom();
            updateUserList();

            chatArea.append("Frånkopplad från chatten\n");
        }
    }

    private void updateUIForConnection(boolean isConnected) {
        chatInput.setEnabled(isConnected);
        usernameField.setEnabled(!isConnected);
        connectButton.setEnabled(!isConnected);
        disconnectButton.setEnabled(isConnected);
        connected = isConnected;
    }

    void updateUserList() {
        userArea.setText("I chatten just nu:\n");
        synchronized (chatRoom.getActiveUsers()) {
            for (String user : chatRoom.getActiveUsers()) {
                userArea.append("- " + user + "\n");
            }
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