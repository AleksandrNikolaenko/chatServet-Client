package academy.prog;

import java.util.*;

public class Users {

    private static final Users users = new Users();
    private static final Map<String, Long> usersMap = new HashMap<>();
    private static final Map<String, List<String>> rooms = new HashMap<>();

    private Users() {
    }

    public static Users getInstance() {
        return users;
    }

    public boolean isUserOnline(String user) {
        Long timeNow = new Date().getTime();
        return (timeNow - usersMap.get(user)) < 10_000;
    }

    public boolean isUser(String user) {
        if (usersMap.get(user) == null) return false;
        return true;
    }

    public void setTime(String user, Long time) {
        usersMap.put(user,time);
    }

    public void addUserTime(String user, Long time) {
        usersMap.put(user, time);
    }

    public List<String> getUsersOnline() {
        List<String> usersOnline = new ArrayList<>();
        Long timeNow = new Date().getTime();
        for (String user : usersMap.keySet()) {
            if ((timeNow - usersMap.get(user)) < 10_000) usersOnline.add(user);
        }
        return usersOnline;
    }
}
