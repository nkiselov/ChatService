import java.io.Serializable;

public class ClientData implements Serializable {
    public String name;
    public String color;

    public ClientData(String name, String color) {
        this.name = name;
        this.color = color;
    }
}
