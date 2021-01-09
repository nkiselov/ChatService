import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;

public class ChatRoom {
    private Set<String> clientUUIDs;
    private Set<String> colorsLeft;

    public ChatRoom(){
        clientUUIDs = new HashSet<>();
        colorsLeft = new HashSet<>(){{add("RED");add("RED");add("RED");}};
    }

    public void addParticipant(String cuuid) throws IOException{
        clientUUIDs.add(cuuid);
        sendPost(new Post(PostType.INFO_JOIN,Server.clientData.get(cuuid),null),null);
    }

    public void removeParticipant(String cuuid) throws IOException{
        clientUUIDs.remove(cuuid);
        sendPost(new Post(PostType.INFO_LEAVE,Server.clientData.get(cuuid),null),null);
    }

    public synchronized void sendPost(Post post, String senderUUID) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out;
        out = new ObjectOutputStream(bos);
        out.writeObject(post);
        out.flush();
        Message msg = new Message(MessageType.POST,bos.toByteArray());
        for (String cuuid:clientUUIDs){
            if(senderUUID != null && !cuuid.equals(senderUUID)) {
                Server.connections.get(cuuid).write(msg);
            }
        }
    }

    public boolean isEmpty(){
        return clientUUIDs.isEmpty();
    }
}
