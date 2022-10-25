package academy.prog;

import java.util.ArrayList;
import java.util.List;

public class JsonMessages {
    private final List<Message> list = new ArrayList<>();

    public JsonMessages(List<Message> sourceList, int fromIndex, String[] rooms) {
        boolean put;
        for (int i = fromIndex; i < sourceList.size(); i++) {
            if (sourceList.get(i).getText().startsWith("@")) {
                put = true;
                for (String room : rooms) {
                    if (sourceList.get(i).getText().startsWith(room)) {
                        list.add(sourceList.get(i));
                        put = false;
                        break;
                    }
                }
                if (put) {
                    Message emptyMessage = new Message("", "", "");
                    list.add(emptyMessage);
                }
            } else {
                list.add(sourceList.get(i));
            }
        }
    }
}
