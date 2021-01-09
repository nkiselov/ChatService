import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static boolean running;
    private static boolean connected;
    private static int joined;

    public static void main(String[] args) throws Exception{
        SSLSocketFactory sf = Security.getSSLContext().getSocketFactory();
        Socket socket = sf.createSocket(Protocol.HOST,Protocol.PORT);
        Protocol p = new Protocol(socket);
        Scanner scn = new Scanner(System.in);
        System.out.println("Welcome to the chat service");
        System.out.println("What is your name");
        String name = scn.nextLine();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out;
        out = new ObjectOutputStream(bos);
        out.writeObject(new ClientData(name,null));
        out.flush();
        running = true;
        new Listener(p).start();
        p.write(new Message(MessageType.CONNECT,bos.toByteArray()));
        while(!connected){
            Thread.sleep(10);
        }
        System.out.println("Connected to chat service");
        while(running) {
            System.out.println("Would you like to:\n[0] Join chat room\n[1] Create chat room");
            int choice;
            while(true) {
                try {
                    choice = Integer.parseInt(scn.nextLine());
                    if(choice>=0 && choice<=1){
                        break;
                    }
                } catch (NumberFormatException ignored) {}
                System.out.println("Please enter a number between 0 an 1");
            }
            if(choice==0) {
                System.out.println("Please enter chat room id to join");
                String id = scn.nextLine();
                p.write(new Message(MessageType.JOIN_ROOM, id.getBytes()));
            }else{
                p.write(new Message(MessageType.CREATE_ROOM,null));
            }
            while(joined == 0){
                Thread.sleep(10);
            }
            while(joined == 1){
                System.out.println("Type /audio to record audio message, type /exit to exit");
                System.out.print("Your messsage: ");
                String input = scn.nextLine();
                if(input.equals("/exit")){
                    p.write(new Message(MessageType.LEAVE_ROOM,null));
                }else{
                    p.write(new Message(MessageType.POST,input.getBytes()));
                }
            }
            System.out.println("Left room");
        }
    }

    private static class Listener extends Thread{
        private Protocol p;

        public Listener(Protocol p) {
            this.p = p;
        }

        @Override
        public void run() {
            try {
                while (Client.running) {
                    Message msg = p.read();
                    switch(msg.type){
                        case CONNECT:
                            connected = true;
                            break;
                        case JOIN_ROOM:
                            if(msg.buffer == null) {
                                joined = 1;
                            }else{
                                System.out.println(new String(msg.buffer));
                                joined = -1;
                            }
                            break;
                        case CREATE_ROOM:
                            joined = 1;
                            break;
                        case LEAVE_ROOM:
                            joined = -1;
                            break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
