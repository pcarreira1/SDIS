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

//    private final static int PACKETSIZE = 64000;
    InetAddress addr;
    int port;
    int PACKETSIZE = 100;
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

        String message;
        try {
            message = new String(packet.getData(), packet.getOffset(), packet.getLength(), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            return "";
        }

        return message;
    }

    public boolean sendMessage(String buffer) {
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
