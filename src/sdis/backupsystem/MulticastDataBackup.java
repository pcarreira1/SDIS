package sdis.backupsystem;

import java.awt.Color;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MulticastDataBackup extends MulticastChannel implements Runnable{
    
    public MulticastDataBackup(String addr, int port) throws IOException {
        super(addr, port);
    }
    
    public void BackupRequest(int serverID, String FileID, int ChunkNo, int ReplicationDeg, Chunk chunkPiece){
         if (super.join()) {
            Message msg = new Message(Message.MessageType.PUTCHUNK, "1.0", serverID, FileID, ChunkNo, ReplicationDeg);
            msg.setBody(chunkPiece.getInformation());
            sendMessage(msg.getFullMesageByte());
        }
    }
    
    public void joinSocket(){
        super.join();
    }
    
    @Override
    public void run() {
        if (super.join()) {
            System.out.println("Socket connect "+addr+" - "+port);
            while(true){
                Message msg=super.receiveMessage();
                try {
                    //store message
                    ArrayList<Chunk> chunks=new ArrayList<Chunk>();
                    Database data=new Database();
                    data.addChunk(new Chunk(msg.getFileID(),msg.getChunkNo(),msg.getBody()));
                    data.saveDatabase();
                } catch (Exception ex) {
                    Logger.getLogger(MulticastDataBackup.class.getName()).log(Level.SEVERE, null, ex);
                } 
                System.out.println("STORED "+msg.getVersion()+" "+msg.getSenderID()+" "+msg.getFileID()+" "+msg.getChunkNo());
                
            }
        }
    }
}
