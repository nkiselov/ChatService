import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

public class Server {
    private static boolean running;
    private static Map<String,Protocol> connections;

    public static void main(String[] args) throws Exception {
        SSLServerSocketFactory ssf = Security.getSSLContext().getServerSocketFactory();
        ServerSocket s = ssf.createServerSocket(Protocol.PORT);
        connections = new HashMap<>();
        running = true;
//        new Thread(() -> {
//            Scanner scn = new Scanner(System.in);
//            scn.nextLine();
//            Server.running=false;
//            System.exit(0);
//        }).start();
        while (running) {
            Socket client = s.accept();
            String uuid = UUID.randomUUID().toString();
            System.out.println("Connection established: "+uuid+", "+client.getInetAddress());
            connections.put(uuid, new Protocol(client));
            new ClientHandler(uuid).start();
        }
    }

    private static synchronized void disconnected(String uuid){
        connections.remove(uuid);
        System.out.println("Connection closed: "+uuid);
    }

    private static class ClientHandler extends Thread{
        private String uuid;
        private Protocol p;

        public ClientHandler(String uuid) throws IOException {
            this.uuid = uuid;
            p = Server.connections.get(uuid);
        }

        @Override
        public void run(){
            try {
                while (Server.running) {
                    Message msg = p.read();
                    System.out.println(msg.type.toString()+": "+new String(msg.buffer));
                }
            }catch (IOException e){
                Server.disconnected(uuid);
            }
        }
    }
}
