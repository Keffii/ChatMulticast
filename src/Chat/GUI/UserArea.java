package Chat.GUI;

import javax.swing.*;
import java.awt.*;

public class UserArea {
    private JTextArea userArea;

    public UserArea() {
        userArea = new JTextArea(10, 15);
        userArea.setEditable(false);
        userArea.setText("I chatten just nu:\n");
    }

    public JPanel setupUserArea() {
        JScrollPane userScrollPane = new JScrollPane(userArea);
        userScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JPanel userPanel = new JPanel(new BorderLayout());
        userPanel.add(userScrollPane, BorderLayout.CENTER);

        return userPanel;
    }

    public JTextArea getUserArea() {
        return userArea;
    }

    public void updateUserList(java.util.List<String> activeUsers) {
        userArea.setText("I chatten just nu:\n");
        for (String user : activeUsers) {
            userArea.append("- " + user + "\n");
        }
    }
}