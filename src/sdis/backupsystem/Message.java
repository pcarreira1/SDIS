package sdis.backupsystem;

import java.util.Arrays;

public class Message {

    Message() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    //Available MessageTypes
    public enum MessageType {
        PUTCHUNK, STORED, GETCHUNK, CHUNK, DELETE, REMOVED
    }
    private String type;
    //Version must follow the regex <n>'.'<m> where <n> and <m> are digits
    private String version;
    //Peer-initiator's ID
    private int senderID;
    //FileID obtained using SHA256
    private String fileID;
    //The specific chunk of the file
    private int chunkNo;
    //Replication degree, can go up to 9
    private int replicationDeg;
    //contains the above
    private String header;
    //Contains the information inside the file  
    private byte[] body;

    public String getHeader() {
        //MessageType[] types = MessageType.values();
        //header = ""+types[type]+' '+version+' '+senderID+' '+fileID+' '+chunkNo+' '+replicationDeg+' '+"\r\n\r\n";
        return header;
    }
    
    public byte[] getHeaderByte(){
        return header.getBytes();
    }

    public Message(MessageType _type, String _version, int _senderID, String _fileID, int _chunkNo, int _replicationDeg) {
        this.type = _type.toString();
        this.version = _version;
        this.senderID = _senderID;
        this.fileID = _fileID;
        this.chunkNo = _chunkNo;
        this.replicationDeg = _replicationDeg;
        header = "" + type + ' ' + version + ' ' + senderID + ' ' + chunkNo + ' ' + replicationDeg + ' ' + "\r\n\r\n";
    }

    public Message(MessageType _type, String _version, int _senderID, String _fileID, int _chunkNo) {
        this.type = _type.toString();
        this.version = _version;
        this.senderID = _senderID;
        this.fileID = _fileID;
        this.chunkNo = _chunkNo;
        header = "" + type + ' ' + version + ' ' + senderID + ' ' + chunkNo + ' ' + "\r\n\r\n";
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

//    public String getFullMessage() {
//        String bodyString = Arrays.toString(body);
//        return header + bodyString;
//    }

    public byte[] getFullMesageByte() {
        byte[] headerBytes = header.getBytes();
        byte[] c = new byte[headerBytes.length + body.length];
        System.arraycopy(headerBytes, 0, c, 0, headerBytes.length);
        System.arraycopy(body, 0, c, headerBytes.length, body.length);
        return c;
    }
}
