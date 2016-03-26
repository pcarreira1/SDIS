package sdis.backupsystem;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastChannel implements Runnable{
    
//    private final static int PACKETSIZE = 64000;
    InetAddress addr;
    int port;
    int PACKETSIZE = 100;
    MulticastSocket socket;

    public MulticastChannel(String addr, int port) throws IOException {
        this.addr = InetAddress.getByName(addr);
        this.port = port;
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

    @Override
    public void run() {     
        // Open a new DatagramSocket, which will be used to send the data.
        try (DatagramSocket serverSocket = new DatagramSocket(port)) {
            System.out.println("Socket init port:"+port);
            for (;;) {
                DatagramPacket packet = new DatagramPacket(new byte[PACKETSIZE], PACKETSIZE);
                serverSocket.receive(packet);
                
                String message = new String(packet.getData(), packet.getOffset(), packet.getLength(), "UTF-8");
                System.out.println(message);

                
                String msg = "Command Received";
                DatagramPacket msgPacket = new DatagramPacket(msg.getBytes(),msg.getBytes().length, addr, port);
                serverSocket.send(msgPacket);
     
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
}
