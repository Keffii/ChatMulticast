package Chat;

import Chat.GUI.ChatGUI;
import Chat.Network.ChatMulticastSender;

import java.io.IOException;

// Utility class for sending chat messages through the network
public class ChatMessage {

    // Sends a chat message from the input field and clears the input afterward
    public static void sendMessage(ChatGUI gui) {
        String message = gui.getChatInput().getText();
        if (message.trim().isEmpty()) {
            return;
        }

        try {
            ChatMulticastSender sender = gui.getSender();
            sender.sendChatMessage(gui.getUsername(), message);
            gui.getChatInput().setText("");
        } catch (IOException e) {
            gui.getChatArea().append("Error sending message: " + e.getMessage() + "\n");
        }
    }
}