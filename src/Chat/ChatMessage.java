package Chat;

import java.io.IOException;

public class ChatMessage {

    public static void sendMessage(ChatSimulatorGUI gui) {
        if (!gui.isConnected()) {
            return;
        }

        String message = gui.getChatInput().getText();
        if (message.trim().isEmpty()) {
            return;
        }

        try {
            // Create a sender if needed (could be stored in the GUI or as a static variable)
            ChatMulticastSender sender = new ChatMulticastSender();


            sender.sendChatMessage(gui.getUsername(), message);

            gui.getChatInput().setText("");
        } catch (IOException e) {
            gui.getChatArea().append("Error sending message: " + e.getMessage() + "\n");
        }
    }
}