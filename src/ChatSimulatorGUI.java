import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatSimulatorGUI extends JFrame implements ActionListener {
    private JTextField chatInput;
    private JTextArea chatArea;
    private JTextArea userArea;
    private JButton disconnectButton;

    ChatSimulatorGUI(){
        setTitle("Chat Rum 1");
        setSize(400, 300);
        setLocation(500, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());

        // Koppla ner knapp
        disconnectButton = new JButton("Koppla ner");
        disconnectButton.addActionListener(this);
        panel.add(disconnectButton, BorderLayout.NORTH);

        // Chat område
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane chatScrollPane = new JScrollPane(chatArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panel.add(chatScrollPane, BorderLayout.CENTER);

        // Användarområde
        userArea = new JTextArea();
        userArea.setEditable(false);
        userArea.append("I chatten just nu:"+ "\n" );
        activeUsers();
        panel.add(userArea, BorderLayout.EAST);

        // Indata område
        chatInput = new JTextField();
        panel.add(chatInput, BorderLayout.SOUTH);
        chatInput.addActionListener(this); // Lägg till ActionListener för att lyssna på Enter

        add(panel);
        setVisible(true);
    }

    //Hålla reda på källan för listener och gör det som önskas
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == chatInput){
            chatMessage();
        }else if(e.getSource() == disconnectButton){
            System.exit(0);
        }
    }

    //Metoid för chatmeddelanden
    private void chatMessage(){
        String chatText = chatInput.getText();
        chatArea.append("Chat user 1: " + chatText + "\n");
        chatInput.setText(""); //Rensa chatruta efter meddelande
        }

    //Metod för aktiva användare
    public void activeUsers(){
    userArea.append("User 1");

    }


    public static void main(String[] args) {
        ChatSimulatorGUI c = new ChatSimulatorGUI();
    }
}
