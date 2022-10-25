package academy.prog;

import jakarta.servlet.http.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/*
    POST(json) -> /add -> AddServlet -> MessageList
    GET -> /get?from=x -> GetListServlet <- MessageList
        <- json[...]
 */

public class AddServlet extends HttpServlet {

	private MessageList msgList = MessageList.getInstance();

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		byte[] buf = requestBodyToArray(req);
        String bufStr = new String(buf, StandardCharsets.UTF_8);
		Message msg = Message.fromJSON(bufStr);
        if (msg != null) {
            if (msg.getText().startsWith("@")) {
                if (msg.getText().startsWith("@getUserStatus")) {
                    getUserStatus(msg);
                } else {
                    if (msg.getText().startsWith("@getUsersOnline")) {
                        getUsersOnline(msg);
                    } else {
                        msgList.add(msg);
                    }
                }
            } else {
                msgList.add(msg);
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
	}

	private byte[] requestBodyToArray(HttpServletRequest req) throws IOException {
        InputStream is = req.getInputStream();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[10240];
        int r;

        do {
            r = is.read(buf);
            if (r > 0) bos.write(buf, 0, r);
        } while (r != -1);

        return bos.toByteArray();
    }

    private void getUserStatus(Message msg) {
        String[] strArr = msg.getText().split(" ");
        String user;
        if (strArr.length < 2) user = "";
        else user = strArr[1];
        Users users = Users.getInstance();
        Message message;
        if (users.isUser(user)) {
            String newText = "@" + msg.getFrom() + " user " + user;
            if (users.isUserOnline(user)) message = new Message("System", msg.getFrom(), newText + " online");
            else message = new Message("System", msg.getFrom(), newText + " offline");
            msgList.add(message);
        } else {
            message = new Message("System", msg.getFrom(), user + " not exist");
            msgList.add(message);
        }
    }

    private void getUsersOnline(Message msg) {
        Users users = Users.getInstance();
        List<String> usersList = users.getUsersOnline();
        String newText = "@" + msg.getFrom() + " users online:\n";
        for (String userOnline : usersList) newText += userOnline + "\n";
        newText = newText.substring(0, newText.length() - 1);
        Message message = new Message("System", msg.getFrom(), newText);
        msgList.add(message);
    }
}
