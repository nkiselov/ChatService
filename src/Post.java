import java.io.Serializable;

public class Post implements Serializable {
    public PostType type;
    public ClientData cd;
    public byte[] buffer;

    public Post(PostType type, ClientData cd, byte[] buffer) {
        this.type = type;
        this.cd = cd;
        this.buffer = buffer;
    }
}
