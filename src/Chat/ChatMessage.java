package Chat;

import java.io.IOException;

public class ChatMessage {

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