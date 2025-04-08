package Chat.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class InputArea {
    private JTextField chatInput;

    public InputArea() {
        chatInput = new JTextField();
        chatInput.setEnabled(false);
    }

    public JPanel setupInputArea(ActionListener listener) {
        chatInput.addActionListener(listener);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(chatInput, BorderLayout.CENTER);

        return inputPanel;
    }

    public JTextField getChatInput() {
        return chatInput;
    }

    public void setEnabled(boolean enabled) {
        chatInput.setEnabled(enabled);
    }
}