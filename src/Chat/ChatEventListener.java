package Chat;

public interface ChatEventListener {
    void onUserJoined(String username);
    void onUserLeft(String username);
    void onMessageReceived(String sender, String message);
}