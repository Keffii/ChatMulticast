package Chat.GUI;

import javax.swing.*;
import java.awt.*;

public class ChatArea {
    private JTextArea chatArea;

    public JPanel setupChatArea() {
        JPanel chatPanel = new JPanel(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);

        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        chatScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        chatScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        chatPanel.add(chatScrollPane, BorderLayout.CENTER);

        return chatPanel;
    }

    // Getter method for the chat area
    public JTextArea getChatArea() {
        return chatArea;
    }

    // Helper method to append messages
    public void append(String message) {
        chatArea.append(message + "\n");
    }
}