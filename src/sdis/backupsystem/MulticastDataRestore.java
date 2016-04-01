package sdis.backupsystem;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;

public class MulticastDataRestore extends MulticastChannel implements Runnable {

    private MulticastControlChannel mc;
    int count_reply=0;
    public MulticastDataRestore(String addr, int port, MulticastControlChannel mc) throws IOException {
        super(addr, port);
        super.join();
        this.mc=mc;
    }

    public void SendChunk(int serverID, String FileID, int ChunkNo, Chunk chunkPiece) {
        Message msg = new Message(Message.MessageType.PUTCHUNK, "1.0", serverID, FileID, ChunkNo);
        msg.setBody(chunkPiece.getInformation());
        sendMessage(msg.getFullMesageByte());
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
                finalMsg = new Message(type, params[1], senderID, params[3], chunk_no);
                break;
        }
        finalMsg.setBody(received_message[1].getBytes());
        return finalMsg;
    }

    @Override
    public void run() {
        System.out.println("Socket connect " + addr + " - " + port);
        while (true) {
            String msg_received = super.receiveMessage();
            Message msg=decryptMessage(msg_received);
            System.out.println(new String(msg.getFullMesageByte(), StandardCharsets.UTF_8));
            count_reply++;
        }
    }
}
