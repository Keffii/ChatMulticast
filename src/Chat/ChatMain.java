package Chat;

import javax.swing.SwingUtilities;
import java.io.IOException;

public class ChatMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ChatSimulatorGUI();
            }
        });
    }
}