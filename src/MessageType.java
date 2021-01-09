import java.util.HashMap;
import java.util.Map;

public enum MessageType {
    AUDIO(0),
    TEXT(1),
    CONNECT(2),
    GET_ROOMS(3);

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
