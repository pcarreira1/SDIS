package sdis.backupsystem;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastChannel {
    
//    private final static int PACKETSIZE = 64000;
    InetAddress addr;
    int port;
    MulticastSocket socket;

    public MulticastChannel(String addr, int port, MulticastSocket socket) throws IOException {
        this.addr = InetAddress.getByName(addr);
        this.port = port;
        socket = new MulticastSocket(port);
    }
    
    public boolean sendMessage(String buffer){
        
        byte[] bytes = buffer.getBytes();

        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, addr, port);

        try {
            socket.send(packet);

            return true;
        } catch (IOException e) {
            e.printStackTrace();

            return false;
        }
    }
    
}
