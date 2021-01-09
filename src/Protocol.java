import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Protocol {
    public static final int PORT = 10010;
    public static final String HOST = "10.0.1.137";
    private BufferedInputStream is;
    private BufferedOutputStream os;
    private Socket socket;

    public Protocol(Socket socket) throws IOException{
        os = new BufferedOutputStream(socket.getOutputStream());
        is = new BufferedInputStream(socket.getInputStream());
        this.socket = socket;
    }

    public Message read() throws IOException {
        byte[] sizeBuffer = readExact(is,4);
        if(sizeBuffer == null){
            throw new IOException("Connection closed");
        }
        int length = ByteBuffer.wrap(sizeBuffer).getInt();
        byte[] typeBuffer = readExact(is,1);
        if(typeBuffer == null){
            throw new IOException("Connection closed");
        }
        byte[] dataBuffer = readExact(is,length-1);
        if(dataBuffer == null){
            throw new IOException("Connection closed");
        }
        return new Message(MessageType.get(typeBuffer[0]), dataBuffer);
    }

    public void write(Message msg) throws IOException{
        if(msg.buffer == null) {
            os.write(ByteBuffer.allocate(4).putInt(1).array());
            os.write((byte) msg.type.val);
        }else{
            os.write(ByteBuffer.allocate(4).putInt(msg.buffer.length + 1).array());
            os.write((byte) msg.type.val);
            os.write(msg.buffer);
        }
        os.flush();
    }

    private static byte[] readExact(BufferedInputStream is, int length) throws IOException{
        int off = 0;
        byte[] buffer = new byte[length];
        while(off<length){
            int read = is.read(buffer,off,length-off);
            if (read == 0) {
                return null;
            }
            off+=read;
        }
        return buffer;
    }

    public void close() throws IOException {
        socket.close();
    }
}
