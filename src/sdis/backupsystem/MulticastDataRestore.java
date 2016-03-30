package sdis.backupsystem;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;

public class MulticastDataRestore extends MulticastChannel implements Runnable {

    public MulticastDataRestore(String addr, int port) throws IOException {
        super(addr, port);
        super.join();
    }

    public void SendChunk(int serverID, String FileID, int ChunkNo, Chunk chunkPiece) {
        Message msg = new Message(Message.MessageType.PUTCHUNK, "1.0", serverID, FileID, ChunkNo);
        msg.setBody(chunkPiece.getInformation());
        sendMessage(msg.getFullMesageByte());
    }

    @Override
    public void run() {
        System.out.println("Socket connect " + addr + " - " + port);
        while (true) {
            Message msg = super.receiveMessage();
            System.out.println(new String(msg.getFullMesageByte(), StandardCharsets.UTF_8));
        }
    }
}
