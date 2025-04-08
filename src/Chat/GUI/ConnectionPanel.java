package Chat.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Random;

public class ConnectionPanel {
    private JTextField usernameField;
    private JButton disconnectButton;
    private JButton connectButton;

    public JPanel setupConnectionPanel(ActionListener listener) {
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

        connectButton.addActionListener(listener);
        disconnectButton.addActionListener(listener);

        return topPanel;
    }

    // Getter methods
    public JTextField getUsernameField() {
        return usernameField;
    }

    public JButton getConnectButton() {
        return connectButton;
    }

    public JButton getDisconnectButton() {
        return disconnectButton;
    }
}