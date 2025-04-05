package Chat;

import java.util.ArrayList;
import java.util.List;

public class ChatRoom {
    private List<String> activeUsers = new ArrayList<>();

    public void addUser(String username) {
        activeUsers.add(username);
    }

    public void removeUser(String username) {
        activeUsers.remove(username);
    }

    public List<String> getActiveUsers() {
        return new ArrayList<>(activeUsers);
    }
}