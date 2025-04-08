package Chat;

import Chat.GUI.ChatGUI;

import javax.swing.SwingUtilities;

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