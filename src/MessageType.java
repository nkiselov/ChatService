import java.util.HashMap;
import java.util.Map;

public enum MessageType {
    POST(0),
    CONNECT(1),
    JOIN_ROOM(2),
    LEAVE_ROOM(3),
    CREATE_ROOM(4);

    public int val;

    private static final Map<Integer, MessageType> lookup = new HashMap<>();

    static {
        for (MessageType t : MessageType.values()) {
            lookup.put(t.val, t);
        }
    }

    MessageType(int val){
        this.val = val;
    }

    public static MessageType get(int val){
        return lookup.get(val);
    }
}
