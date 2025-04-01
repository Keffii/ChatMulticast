import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatSimulatorGUI extends JFrame implements ActionListener {
    private JTextField chatInput;
    private JTextArea chatArea;
    private JTextArea userArea;

    ChatSimulatorGUI() {
        setTitle("Chat Rum 1");
        setSize(400, 300);
        setLocation(500, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());

        // Koppla ner knapp
        JButton disconnectButton = new JButton("Koppla ner");
        panel.add(disconnectButton, BorderLayout.NORTH);

        // Chat område
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        panel.add(chatArea, BorderLayout.CENTER);
        chatArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Användarområde
        userArea = new JTextArea();
        userArea.setEditable(false);
        userArea.setSize(new Dimension(150, 0));
        userList();
        panel.add(userArea, BorderLayout.EAST);

        // Indata område
        chatInput = new JTextField();
        panel.add(chatInput, BorderLayout.SOUTH);
        chatInput.addActionListener(this); // Lägg till ActionListener för att lyssna på Enter

        add(panel);
        setVisible(true);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        chatMessage();
    }

    private void chatMessage() {
        String chatText = chatInput.getText();
        chatArea.append("Chat user 1: " + chatText);
        chatInput.setText(""); //Rensa chatruta efter meddelande
        }

    public void userList(){
        userArea.append("I chatten just nu: ");

    }


    public static void main(String[] args) {
        ChatSimulatorGUI g = new ChatSimulatorGUI();
    }
}
