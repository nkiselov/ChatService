public class Message {
    public MessageType type;
    public byte[] buffer;

    public Message(MessageType type, byte[] buffer) {
        this.type = type;
        this.buffer = buffer;
    }
}
