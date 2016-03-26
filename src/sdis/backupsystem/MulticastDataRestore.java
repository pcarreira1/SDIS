package sdis.backupsystem;

import java.io.IOException;
import java.net.MulticastSocket;

public class MulticastDataRestore extends MulticastChannel{
    
    public MulticastDataRestore(String addr, int port) throws IOException {
        super(addr, port);
    }
    
    public void SendChunk(int serverID, String FileID, int ChunkNo, Chunk chunkPiece){
         Message msg = new Message(Message.MessageType.PUTCHUNK, "1.0", serverID, FileID, ChunkNo);
        msg.setBody(chunkPiece.getInformation());
        sendMessage(msg.getFullMessage());
    }
}
