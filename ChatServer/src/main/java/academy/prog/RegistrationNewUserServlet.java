package academy.prog;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class RegistrationNewUserServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String stringUser;
        String login = request.getParameter("login");
        File userFile = new File("users\\" + login + ".udb");
        if (userFile.exists()) {
            PrintWriter out = new PrintWriter(response.getWriter());
            out.println("Rejected. A user with this login already exists.");
            return;
        }
        String password = request.getParameter("password");
        List<String> rooms = new ArrayList<>();
        rooms.add("@" + login);
        int messagesIndex;
        try {
            MessageList ml = MessageList.getInstance();
            Field massagesField = ml.getClass().getDeclaredField("list");
            massagesField.setAccessible(true);
            List<Message> messages = (LinkedList<Message>) massagesField.get(ml);
            messagesIndex = messages.size() - 100;
            if (messagesIndex < 0) messagesIndex = 0;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        User user = new User(login, messagesIndex, rooms);
        Gson gson = new GsonBuilder().create();
        stringUser = gson.toJson(user);
        PrintWriter out = new PrintWriter(response.getWriter());
        out.println(stringUser);
        if (!new File("users").exists()) new File("users").mkdir();
        try (PrintWriter pw = new PrintWriter("users\\" + login + ".udb")) {
            pw.println(password);
        }
    }
}
