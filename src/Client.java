import javax.net.ssl.SSLSocketFactory;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws Exception{
        //SSLSocketFactory sf = Security.getSSLContext().getSocketFactory();
        Socket socket = new Socket(Protocol.HOST,Protocol.PORT);//sf.createSocket(Protocol.HOST,Protocol.PORT);
        Protocol p = new Protocol(socket);
        Scanner scn = new Scanner(System.in);
        String line;
        while((line = scn.nextLine())!=null){
            p.write(new Message(MessageType.TEXT,line.getBytes()));
        }
    }

}
