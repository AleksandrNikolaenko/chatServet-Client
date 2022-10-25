package academy.prog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class GetThread implements Runnable {
    private final Gson gson;
    private int n;
    private List<String> rooms;

    public GetThread() {
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        n = Main.getUser().getN();
    }

    @Override
    public void run() {
        try {
            while ( ! Thread.interrupted()) {
                rooms = Main.getUser().getRooms();
                if (!rooms.isEmpty()) {
                    int count = 1;
                    String urlString = "/get?from=" + n + "&login=" + Main.getUser().getLogin() + "&rooms=";
                    for (String room : rooms) {
                        if (count < rooms.size()) urlString += room + "-";
                        else urlString += room;
                        count++;
                    }
                    URL url = new URL(Utils.getURL() + urlString);
                    HttpURLConnection http = (HttpURLConnection) url.openConnection();

                    try (InputStream is = http.getInputStream()) {
                        byte[] buf = responseBodyToArray(is);
                        String strBuf = new String(buf, StandardCharsets.UTF_8);
                        JsonMessages list = gson.fromJson(strBuf, JsonMessages.class);
                        if (list != null) {
                            for (Message m : list.getList()) {
                                if (m.getText() != null && !m.getText().equals("") && !m.getText().equals(" ")) {
                                    if (m.getText().startsWith("@" + Main.getUser().getLogin() + " inviteToRoom")) {
                                        String[] mesArr = m.getText().split(" ");
                                        if (Main.getUser().getRooms().contains(mesArr[2])) {
                                            n++;
                                            continue;
                                        }
                                        Main.getUser().getRooms().add(mesArr[2]);
                                        System.out.println(m.getFrom() + " added you to a private room " + mesArr[2]);
                                        n++;
                                        continue;
                                    }
                                    System.out.println(m);
                                }
                                n++;
                            }
                        }
                    }
                }

                Thread.sleep(500);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private byte[] responseBodyToArray(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[10240];
        int r;

        do {
            r = is.read(buf);
            if (r > 0) bos.write(buf, 0, r);
        } while (r != -1);

        return bos.toByteArray();
    }
}
