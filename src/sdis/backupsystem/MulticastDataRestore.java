package sdis.backupsystem;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MulticastDataRestore extends MulticastChannel implements Runnable {

    //private MulticastControlChannel mc;
    int count_reply = 0;
    Chunk currentChunk;

    public MulticastDataRestore(String addr, int port/*, MulticastControlChannel mc*/) throws IOException {
        super(addr, port);
        super.join();
        //this.mc=mc;
    }

    public void SendChunk(int serverID, String FileID, int ChunkNo, Chunk chunkPiece) {
        Message msg = new Message(Message.MessageType.CHUNK, "1.0", serverID, FileID, ChunkNo);
        try {
            System.out.println(msg.getHeaderByte().length);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(MulticastDataRestore.class.getName()).log(Level.SEVERE, null, ex);
        }
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

    public Message decryptMessage(byte[] received) throws UnsupportedEncodingException {
        Message finalMsg = null;
//        byte[] header = new byte[85];
//        System.arraycopy(received, 0, header, 0, header.length);
//        byte[] body = Arrays.copyOfRange(received, header.length, received.length);
//        String headerString = new String(header, "UTF-8");
        String test = new String(received, "UTF-8");
        String testRN = "\r\n\r\n";
        String test1[] = test.split("\r\n\r\n");
        String testTotHead= test1[0]+testRN;
        System.out.println(test1[0]);
        byte[] temp = testTotHead.getBytes("UTF-8");
        byte[] body = null;
        int leng = temp.length;
        if(leng>85){
            leng = 85 + (leng-85)*2;
        }
        body = Arrays.copyOfRange(received, leng, received.length);
        Message.MessageType type = null;
        int senderID, chunk_no, replication;
        String params[] = test1[0].split(" ");
        switch (params.length) {
            case 5:
                type = Message.MessageType.valueOf(params[0]);
                senderID = Integer.parseInt(params[2]);
                chunk_no = Integer.parseInt(params[4]);
                finalMsg = new Message(type, params[1], senderID, params[3], chunk_no);
                break;
            case 6:
                type = Message.MessageType.valueOf(params[0]);
                senderID = Integer.parseInt(params[2]);
                chunk_no = Integer.parseInt(params[4]);
                finalMsg = new Message(type, params[1], senderID, params[3], chunk_no);
                break;
        }
        finalMsg.setBody(body);
        return finalMsg;
    }

    @Override
    public void run() {
        System.out.println("Socket connect " + addr + " - " + port);
        while (true) {
            /*String msg_received = super.receiveMessage();
            Message msg = decryptMessage(msg_received);*/
            byte[] msg_bytes = receiveMessageBytes();
            Message msg = null;
            try {
                msg = decryptMessage(msg_bytes);
                //String msg_received = super.receiveMessage();
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(MulticastDataBackup.class.getName()).log(Level.SEVERE, null, ex);
            }
            //System.out.println(new String(msg.getFullMesageByte(), StandardCharsets.UTF_8));
            currentChunk = new Chunk(msg.getFileID(), msg.getChunkNo(), msg.getBody());
            count_reply++;
        }
    }
}
