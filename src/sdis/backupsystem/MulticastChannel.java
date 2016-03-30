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

    public Message receiveMessage() {
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
            
            String received_message[] = received.split("\r\n\r\n");
            Message.MessageType type = null;
            int senderID, chunk_no, replication;
            String params[] = received_message[0].split(" ");
            switch (params.length) {
                case 5:
                    type = Message.MessageType.valueOf(params[0]);
                    senderID = Integer.parseInt(params[2]);
                    chunk_no = Integer.parseInt(params[4]);
                    message = new Message(type, params[1], senderID, params[3], chunk_no);
                    break;
                case 6:
                    type = Message.MessageType.valueOf(params[0]);
                    senderID = Integer.parseInt(params[2]);
                    chunk_no = Integer.parseInt(params[4]);
                    replication = Integer.parseInt(params[5]);
                    message = new Message(type, params[1], senderID, params[3], chunk_no);
                    break;
            }
            message.setBody(received_message[1].getBytes());
            

        } catch (UnsupportedEncodingException ex) {
            return null;
        }
        return message;
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
