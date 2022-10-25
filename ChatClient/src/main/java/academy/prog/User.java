package academy.prog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

public class User {

    private String login;
    private int n;
    private List<String> rooms;

    public User() {
    }

    public User(String login, int n, List<String> rooms) {
        this.login = login;
        this.n = n;
        this.rooms = rooms;
    }

    public String toJSON() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this);
    }

    public User fromJSON(String str) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(str, User.class);
    }

    public String getLogin() {
        return login;
    }

    public int getN() {
        return n;
    }

    public List<String> getRooms() {
        return rooms;
    }

    @Override
    public String toString() {
        return "User{" +
                "login='" + login + '\'' +
                ", n=" + n +
                ", rooms=" + rooms +
                '}';
    }
}
