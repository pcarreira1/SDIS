package sdis.backupsystem;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import static sdis.backupsystem.PeerBase.peer_id;

public class MulticastDataBackup extends MulticastChannel implements Runnable {

    private MulticastControlChannel mc;
    private Database data;

    public MulticastDataBackup(String addr, int port, MulticastControlChannel mc) throws IOException {
        super(addr, port);
        this.mc = mc;
    }

    public MulticastDataBackup(String addr, int port, MulticastControlChannel mc, Database data) throws IOException {
        super(addr, port);
        this.mc = mc;
        this.data = data;
    }

    public void BackupRequest(int serverID, String FileID, int ChunkNo, int ReplicationDeg, Chunk chunkPiece) {
        if (super.join()) {
            long freeSpace = new File("/").getFreeSpace() / 1024;
            if (freeSpace < 64) {
                //Database data = new Database();
                Chunk temp = data.backedUp.get(0);
                mc.SpaceReclaim(peer_id, temp.getFileID(), temp.getChunkNo());
            }
            Message msg = new Message(Message.MessageType.PUTCHUNK, "1.0", serverID, FileID, ChunkNo, ReplicationDeg);
            msg.setBody(chunkPiece.getInformation());
            byte[] test = msg.getFullMesageByte();
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

    public Message decryptMessage(byte[] received) throws UnsupportedEncodingException {
        Message finalMsg = null;
        byte[] header = new byte[90];
        System.arraycopy(received, 0, header, 0, header.length);
        byte[] body = Arrays.copyOfRange(received, header.length, received.length);
        String headerString = new String(header, "UTF-8");
        Message.MessageType type = null;
        int senderID, chunk_no, replication;
        String params[] = headerString.split(" ");
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
            case 7:
                type = Message.MessageType.valueOf(params[0]);
                senderID = Integer.parseInt(params[2]);
                chunk_no = Integer.parseInt(params[4]);
                replication = Integer.parseInt(params[5]);
                finalMsg = new Message(type, params[1], peer_id, params[3], chunk_no);
                break;
        }
        finalMsg.setBody(body);
        return finalMsg;
    }

    @Override
    public void run() {
        if (super.join()) {
            System.out.println("Socket connect " + addr + " - " + port);
            while (true) {
                byte[] msg_bytes = receiveMessageBytes();
                Message msg = null;
                try {
                    msg = decryptMessage(msg_bytes);
                    //String msg_received = super.receiveMessage();
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(MulticastDataBackup.class.getName()).log(Level.SEVERE, null, ex);
                }

                //Message msg = decryptMessage(msg_received);
                try {
                    //store message
                    //ArrayList<Chunk> chunks = new ArrayList<Chunk>();
                    //                    try {
                    //                        data.loadDatabase();
                    //                    } catch (Exception ex) {
                    //                        Logger.getLogger(MulticastDataBackup.class.getName()).log(Level.SEVERE, null, ex);
                    //                    }
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
