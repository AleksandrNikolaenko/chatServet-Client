package academy.prog;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.lang.reflect.Field;

public class JoinChat extends HttpServlet{

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String login = request.getParameter("login");
        String password = request.getParameter("password");
        File userFile = new File("users\\" + login + ".udb");
        String filePassword;

        if (userFile.exists()) {
            try (Scanner scanner = new Scanner(userFile)) {
                filePassword = scanner.nextLine();
            }

            if (password.equals(filePassword)) {
                int massagesIndex;
                try {
                    MessageList ml = MessageList.getInstance();
                    Field massagesField = ml.getClass().getDeclaredField("list");
                    massagesField.setAccessible(true);
                    List<Message> messages = (LinkedList<Message>) massagesField.get(ml);
                    massagesIndex = messages.size() - 100;
                    if (massagesIndex < 0) massagesIndex = 0;
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                List<String> list = new ArrayList<>();
                list.add("@" + login);

                User user = new User(login, massagesIndex, list);
                String userString = user.toJSON();
                PrintWriter out = new PrintWriter(response.getWriter());
                out.println(userString);

            } else {
                PrintWriter out = new PrintWriter(response.getWriter());
                out.println("Rejected. Wrong password");
            }
        } else {
            PrintWriter out = new PrintWriter(response.getWriter());
            out.println("Rejected. Wrong login");
        }
    }
}
