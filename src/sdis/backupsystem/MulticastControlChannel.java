package sdis.backupsystem;

import java.io.IOException;
import java.net.MulticastSocket;

public class MulticastControlChannel extends MulticastChannel{

    public MulticastControlChannel(String addr, int port) throws IOException {
        super(addr, port);
    }
    
    //needs arguments
    public void BackupReply(int serverID, String FileID, int ChunkNo){
        Message msg = new Message(Message.MessageType.STORED, "1.0", serverID, FileID, ChunkNo);
        sendMessage(msg.getHeader());
    }
    
    //needs arguments
    public void RestoreReply(int serverID, String FileID, int ChunkNo) {
        Message msg = new Message(Message.MessageType.GETCHUNK, "1.0", serverID, FileID, ChunkNo);
        sendMessage(msg.getHeader());
    }
    
    //needs arguments
    public void Delete(int serverID, String FileID, int ChunkNo) {
        Message msg = new Message(Message.MessageType.DELETE, "1.0", serverID, FileID, ChunkNo);
        sendMessage(msg.getHeader());
    }
    
    public void SpaceReclaim(int serverID, String FileID, int ChunkNo){
        Message msg = new Message(Message.MessageType.REMOVED, "1.0", serverID, FileID, ChunkNo);
        sendMessage(msg.getHeader());
    }
}
