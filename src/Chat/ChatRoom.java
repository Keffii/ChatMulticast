package Chat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Maintains a thread-safe list of active users in the chat
public class ChatRoom {
    private final List<String> activeUsers = Collections.synchronizedList(new ArrayList<>());

    public void addUser(String username) {
        if (!activeUsers.contains(username)) {
            activeUsers.add(username);
        }
    }

    public void removeUser(String username) {
        activeUsers.remove(username);
    }

    public List<String> getActiveUsers() {
        synchronized (activeUsers) {
            return new ArrayList<>(activeUsers);
        }
    }
}