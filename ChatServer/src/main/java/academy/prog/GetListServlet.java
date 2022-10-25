package academy.prog;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import jakarta.servlet.http.*;

public class GetListServlet extends HttpServlet {
	
	private MessageList msgList = MessageList.getInstance();

    @Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String[] rooms = req.getParameter("rooms").split("-");
		String login = req.getParameter("login");
		String fromString = req.getParameter("from");

		Users users = Users.getInstance();
		users.addUserTime(login, new Date().getTime());

		int from = 0;
		try {
			from = Integer.parseInt(fromString);
			if (from < 0) from = 0;
		} catch (Exception ex) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		resp.setContentType("application/json");
		
		String json = msgList.toJSON(rooms, from);
		if (json != null) {
			OutputStream os = resp.getOutputStream();
            byte[] buf = json.getBytes(StandardCharsets.UTF_8);
			os.write(buf);

			//PrintWriter pw = resp.getWriter();
			//pw.print(json);
		}
	}
}
