package sdis.backupsystem;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import static sdis.backupsystem.PeerBase.peer_id;

public class MulticastDataBackup extends MulticastChannel implements Runnable {

    private MulticastControlChannel mc;

    public MulticastDataBackup(String addr, int port, MulticastControlChannel mc) throws IOException {
        super(addr, port);
        this.mc = mc;
    }

    public void BackupRequest(int serverID, String FileID, int ChunkNo, int ReplicationDeg, Chunk chunkPiece) {
        if (super.join()) {
            long freeSpace = new File("/").getFreeSpace() / 1024;
            if (freeSpace < 64) {
                Database data=new Database();
                try {
                    data.loadDatabase();
                    Chunk temp=data.backedUp.get(0);
                    mc.SpaceReclaim(peer_id, temp.getFileID(), temp.getChunkNo());
                } catch (IOException ex) {
                    Logger.getLogger(MulticastDataBackup.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(MulticastDataBackup.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            Message msg = new Message(Message.MessageType.PUTCHUNK, "1.0", serverID, FileID, ChunkNo, ReplicationDeg);
            msg.setBody(chunkPiece.getInformation());
            sendMessage(msg.getFullMesageByte());

        }
    }

    public void joinSocket() {
        super.join();
    }

    public Message decryptMessage(String received) {
        Message finalMsg = null;
        String received_message[] = received.split("\r\n\r\n");
        Message.MessageType type = null;
        int senderID, chunk_no, replication;
        String params[] = received_message[0].split(" ");
        switch (params.length) {
            case 5:
                type = Message.MessageType.valueOf(params[0]);
                senderID = Integer.parseInt(params[2]);
                chunk_no = Integer.parseInt(params[4]);
                finalMsg = new Message(type, params[1], peer_id, params[3], chunk_no);
                break;
            case 6:
                type = Message.MessageType.valueOf(params[0]);
                senderID = Integer.parseInt(params[2]);
                chunk_no = Integer.parseInt(params[4]);
                replication = Integer.parseInt(params[5]);
                finalMsg = new Message(type, params[1], peer_id, params[3], chunk_no);
                break;
        }
        finalMsg.setBody(received_message[1].getBytes());
        return finalMsg;
    }

    @Override
    public void run() {
        if (super.join()) {
            System.out.println("Socket connect " + addr + " - " + port);
            while (true) {
                String msg_received = super.receiveMessage();
                Message msg = decryptMessage(msg_received);

                try {
                    //store message
                    ArrayList<Chunk> chunks = new ArrayList<Chunk>();
                    Database data = new Database();
                    data.addChunk(new Chunk(msg.getFileID(), msg.getChunkNo(), msg.getBody()));
                    data.saveDatabase();
                } catch (Exception ex) {
                    Logger.getLogger(MulticastDataBackup.class.getName()).log(Level.SEVERE, null, ex);
                }

                try {
                    Random rand = new Random();
                    int max = 400;
                    int min = 0;
                    Thread.sleep(rand.nextInt((max - min) + 1) + max);
                } catch (InterruptedException ex) {
                    Logger.getLogger(MulticastDataBackup.class.getName()).log(Level.SEVERE, null, ex);
                }
                mc.BackupReply(peer_id, msg.getFileID(), msg.getChunkNo());
            }
        }
    }
}
