package sdis.backupsystem;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MulticastChannel {

    private final static int PACKETSIZE = 200000;
    InetAddress addr;
    int port;
    MulticastSocket socket;

    public MulticastChannel(String addr, int port) {
        try {
            this.addr = InetAddress.getByName(addr);
            this.port = port;
        } catch (UnknownHostException ex) {
            System.out.println("Socket error");
        }
    }

    public boolean join() {
        try {
            socket = new MulticastSocket(port);
        } catch (IOException ex) {
            return false;
        }
        try {
            socket.joinGroup(addr);
        } catch (IOException ex) {
            return false;
        }
        return true;
    }

    public String receiveMessage() {
        byte[] buffer = new byte[PACKETSIZE];
        DatagramPacket packet = new DatagramPacket(buffer, PACKETSIZE);

        try {
            socket.receive(packet);
        } catch (IOException e) {
            System.err.println("Failed to receive message");
        }
        Message message = null;
        String received;
        try {
            received = new String(packet.getData(), packet.getOffset(), packet.getLength(), "UTF-8");

        } catch (UnsupportedEncodingException ex) {
            return null;
        }
        return received;
    }
    
    public byte[] receiveMessageBytes(){
        byte[] buffer = new byte[PACKETSIZE];
        DatagramPacket packet = new DatagramPacket(buffer, PACKETSIZE);

        try {
            socket.receive(packet);
        } catch (IOException e) {
            System.err.println("Failed to receive message");
        }
        byte[] finalBytes = new byte[packet.getLength()];
        System.arraycopy(packet.getData(), packet.getOffset(), finalBytes, 0, packet.getLength());
        return finalBytes;
    }

    public boolean sendMessage(byte[] bytes) {
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
