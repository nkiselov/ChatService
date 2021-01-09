import javax.net.ssl.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

public class Server {
    private static boolean running;
    public static Map<String,Protocol> connections;
    private static Map<String,String> inRoom;
    private static Map<String,ChatRoom> rooms;
    public static Map<String,ClientData> clientData;

    public static void main(String[] args) throws Exception {
        SSLServerSocketFactory ssf = Security.getSSLContext().getServerSocketFactory();
        ServerSocket s = ssf.createServerSocket(Protocol.PORT);
        connections = new HashMap<>();
        clientData = new HashMap<>();
        rooms = new HashMap<>();
        inRoom = new HashMap<>();
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
        try {
            String roomuuid = inRoom.get(uuid);
            ChatRoom rm = rooms.get(roomuuid);
            rm.removeParticipant(uuid);
            if(rm.isEmpty()){
                rooms.remove(roomuuid);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        clientData.remove(uuid);
        connections.remove(uuid);
        System.out.println("Connection closed: "+uuid);
    }

    private static class ClientHandler extends Thread{
        private String uuid;
        private Protocol p;

        public ClientHandler(String uuid) {
            this.uuid = uuid;
            p = Server.connections.get(uuid);
        }

        @Override
        public void run(){
            try {
                while (Server.running) {
                    Message msg = p.read();
                    switch(msg.type){
                        case CONNECT:
                            ObjectInputStream obj = new ObjectInputStream(new ByteArrayInputStream(msg.buffer));
                            ClientData cd = (ClientData) obj.readObject();
                            clientData.put(uuid,cd);
                            p.write(new Message(MessageType.CONNECT,null));
                            break;
//                        case GET_ROOMS:
//                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                            ObjectOutputStream out;
//                            out = new ObjectOutputStream(bos);
//                            out.writeObject(rooms);
//                            out.flush();
//                            p.write(new Message(MessageType.GET_ROOMS,bos.toByteArray()));
//                            break;
                        case JOIN_ROOM:
                            ObjectInputStream obj2 = new ObjectInputStream(new ByteArrayInputStream(msg.buffer));
                            String roomUUID = (String)obj2.readObject();
                            if(rooms.containsKey(roomUUID)){
                                inRoom.put(uuid,roomUUID);
                                rooms.get(roomUUID).addParticipant(uuid);
                            }else{
                                p.write(new Message(MessageType.JOIN_ROOM,"No such room".getBytes()));
                            }
                            p.write(new Message(MessageType.JOIN_ROOM,null));
                            break;
                        case LEAVE_ROOM:
                            rooms.get(inRoom.get(uuid)).removeParticipant(uuid);
                            inRoom.remove(uuid);
                            p.write(new Message(MessageType.LEAVE_ROOM,null));
                            break;
                        case CREATE_ROOM:
                            String roomUUID3 = UUID.randomUUID().toString();
                            rooms.put(roomUUID3,new ChatRoom());
                            inRoom.put(uuid,roomUUID3);
                            p.write(new Message(MessageType.CREATE_ROOM,null));
                            break;
                        case POST:
                            ObjectInputStream obj4 = new ObjectInputStream(new ByteArrayInputStream(msg.buffer));
                            Post post = (Post)obj4.readObject();
                            post.cd = clientData.get(uuid);
                            rooms.get(inRoom.get(uuid)).sendPost(post,uuid);
                    }
                }
            }catch (IOException | ClassNotFoundException e){
                Server.disconnected(uuid);
            }
        }
    }
}
