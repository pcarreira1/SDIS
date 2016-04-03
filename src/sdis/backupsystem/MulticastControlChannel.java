package sdis.backupsystem;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import static sdis.backupsystem.PeerBase.peer_id;

public class MulticastControlChannel extends MulticastChannel implements Runnable {

    private MulticastDataRestore mdr;

    public MulticastControlChannel(String addr, int port) throws IOException {
        super(addr, port);
    }

    public MulticastControlChannel(String addr, int port, MulticastDataRestore mdr) throws IOException {
        super(addr, port);
        this.mdr = mdr;
    }

    //needs arguments
    public void BackupReply(int serverID, String FileID, int ChunkNo) {
        Message msg = new Message(Message.MessageType.STORED, "1.0", serverID, FileID, ChunkNo);
        sendMessage(msg.getHeader().getBytes());
    }

    //needs arguments
    public void RestoreRequest(int serverID, String FileID, int ChunkNo) {
        Message msg = new Message(Message.MessageType.GETCHUNK, "1.0", serverID, FileID, ChunkNo);
        sendMessage(msg.getHeader().getBytes());
    }

    //needs arguments
    public void Delete(int serverID, String FileID) {
        Message msg = new Message(Message.MessageType.DELETE, "1.0", serverID, FileID);
        sendMessage(msg.getHeader().getBytes());
    }

    public void SpaceReclaim(int serverID, String FileID, int ChunkNo) {
        Message msg = new Message(Message.MessageType.REMOVED, "1.0", serverID, FileID, ChunkNo);
        sendMessage(msg.getHeader().getBytes());
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
            case 4:
                type = Message.MessageType.valueOf(params[0]);
                senderID = Integer.parseInt(params[2]);
                finalMsg = new Message(type, params[1], senderID, params[3]);

                Database data = new Database();
                try {
                    data.loadDatabase();
                    ArrayList<Chunk> temp = data.backedUp;
                    for (int i = 0; i < temp.size(); i++) {
                        if (temp.get(i).getFileID().equals(finalMsg.getFileID())) {
                            Chunk chun = temp.get(i);
                            temp.remove(i);
                            data.saveDatabase();
                            SpaceReclaim(peer_id, chun.getFileID(), chun.getChunkNo());
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(MulticastDataBackup.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(MulticastDataBackup.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            case 5:
                type = Message.MessageType.valueOf(params[0]);
                senderID = Integer.parseInt(params[2]);
                chunk_no = Integer.parseInt(params[4]);
                finalMsg = new Message(type, params[1], senderID, params[3], chunk_no);
                break;
        }
        return finalMsg;
    }

    public int count_reply = 0;

    @Override
    public void run() {
        if (super.join()) {
            System.out.println("Socket connect " + addr + " - " + port);
            while (true) {
                String msg_received = super.receiveMessage();
                Message msg = decryptMessage(msg_received);
                System.out.println(msg.getHeader());
                if (msg.getType().equals("GETCHUNK") && msg.getSenderID() != peer_id) {
                    Database temp = new Database();
                    try {
                        temp.loadDatabase();
                        for(int i =0; i<temp.getBackedUp().size(); i++){
                            if(temp.getBackedUp().get(i).getFileID().equals(msg.getFileID()) && temp.getBackedUp().get(i).getChunkNo() == msg.getChunkNo()){
                                mdr.SendChunk(peer_id, msg.getFileID(), msg.getChunkNo(), temp.getBackedUp().get(i));
                            }
                        }
                        
                    } catch (IOException ex) {
                        Logger.getLogger(MulticastControlChannel.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(MulticastControlChannel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                count_reply++;
            }
        }
    }
}
