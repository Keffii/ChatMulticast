package Chat;

import Chat.GUI.ChatGUI;

import javax.swing.SwingUtilities;

// Application entry point that launches the chat interface on the Event Dispatch Thread
public class ChatMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ChatGUI();
            }
        });
    }
}