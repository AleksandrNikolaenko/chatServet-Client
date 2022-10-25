package academy.prog;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Main {

	private static User user = new User();
	private static Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {
		try {
			printHelp();
			joinToChat();
            System.out.println("Enter your message: ");
			while (true) {
				String text = scanner.nextLine();
				if (text.equals("exit")) break;
				if (text.startsWith("@createRoom")) {
					createRoom(text);
					continue;
				}
				if (text.startsWith("@inviteToRoom")) {
					if (inviteToRoom(text)) return;
					continue;
				}
				if (text.startsWith("@escapeFrom")) {
					escapeFrom(text);
					continue;
				}
				if (sendMessage(text)) return;
			}
		} catch (IOException | NoSuchMethodException ex) {
			ex.printStackTrace();
		} finally {
			scanner.close();
		}
	}

	private static void printHelp() {
		System.out.println("""
    @reg 										-> регистраци нового пользователя (только при входе в чат)
    exit 										-> выход из чата
    @имя_пользователя 							-> отправить пользователю приватное сообщение
    @createRoom имя_комнаты 					-> создать приватную комнату
    @inviteToRoom имя_комнаты имя_пользователя 	-> пригласить пользователя в приватную комнату
    @escapeFrom имя_комнаты 					-> покинуть приватную комнату
    @getUserStatus имя_пользователя 			-> узнать в сети пользователь или нет
    @getUsersOnline 							-> получить список пользователей в сети
				""");
	}

	private static void joinToChat() throws IOException {
		String login;
		while (true) {
			System.out.println("Enter your login:\nTo create a new user enter @reg");
			login = scanner.nextLine();
			if ("@reg".equals(login)) {
				String userString = getNewUserString();
				if (!userString.startsWith("Rejected")) {
					user = user.fromJSON(userString);
					break;
				}
				else {
					System.out.println(userString);
					continue;
				}
			}
			System.out.println(("Enter password:"));
			String password = scanner.nextLine();
			String userString = getUserString(login, password);
			if (!userString.startsWith("Rejected")) {
				user = user.fromJSON(userString);
				break;
			} else {
				System.out.println(userString);
			}
		}
		Thread th = new Thread(new GetThread());
		th.setDaemon(true);
		th.start();
	}

	private static String getUserString(String login, String password) throws IOException {
		URL urlForEnter = new URL(Utils.getURL() + "/join?login=" + login + "&password=" + password);
		HttpURLConnection joinConnect = (HttpURLConnection) urlForEnter.openConnection();
		joinConnect.setDoOutput(true);
		InputStream in = joinConnect.getInputStream();
		return new Scanner(in).nextLine();
	}

	private static String getNewUserString() throws IOException {
		System.out.println("Enter login for new user:");
		String loginForNewUser = scanner.nextLine();
		System.out.println("Enter password for new user:");
		String passwordForNewUser = scanner.nextLine();

		URL urlForRegistration = new URL(Utils.getURL() + "/registration?login=" + loginForNewUser + "&password=" + passwordForNewUser);
		HttpURLConnection registrationConnect = (HttpURLConnection) urlForRegistration.openConnection();
		registrationConnect.setDoOutput(true);
		InputStream in = registrationConnect.getInputStream();
		return new Scanner(in).nextLine();
	}

	private static void createRoom(String text) {
		String[] textArr = text.split(" ");
		if (textArr.length <2) {
			System.out.println("no correct input");
			return;
		}
		String room = "@" + textArr[1];
		if (!user.getRooms().contains(room)) user.getRooms().add(room);
		System.out.println("room created");
	}

	private static boolean inviteToRoom(String text) throws IOException, NoSuchMethodException {
		String[] textArr = text.split(" ");
		if (textArr.length < 3) {
			System.out.println("no correct input");
			return false;
		}
		String newText = "@" + textArr[2] + " inviteToRoom @" + textArr[1];
		Message message = new Message(user.getLogin(), textArr[2], newText);
		int res = message.send(Utils.getURL() + "/add");
		if (res != 200) {
			System.out.println("HTTP error occurred: " + res);
			return true;
		}
		System.out.println("invite message sent");
		return false;
	}

	private static void escapeFrom(String text) {
		String[] textArr = text.split(" ");
		if (textArr.length < 2) {
			System.out.println("no correct room");
			return;
		}
		if (user.getRooms().remove("@" + textArr[1])) System.out.println("room " + textArr[1] + " removed");
		else System.out.println("no correct room");
	}

	private static boolean sendMessage(String text) throws IOException, NoSuchMethodException {
		Message message;
		if (text.startsWith("@") && !text.startsWith("@getUserStatus") && !text.startsWith("@getUsersOnline")) {
			int index = text.indexOf(" ");
			if (index < 0) index = text.length();
			String newText = text.substring(0, index) + " private message from " + user.getLogin() + "@" + text.substring(index);
			String to = text.substring(1, index);
			message = new Message(user.getLogin(), to, newText);
			System.out.println("private message sent");
		} else {
			message = new Message(user.getLogin(), text);
		}
		int res = message.send(Utils.getURL() + "/add");
		if (res != 200) {
			System.out.println("HTTP error occurred: " + res);
			return true;
		}
		return false;
	}

	public static User getUser() {
		return user;
	}
}
