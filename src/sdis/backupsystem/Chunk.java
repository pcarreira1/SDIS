package sdis.backupsystem;

import java.io.Serializable;

public class Chunk implements Serializable{
    
    private String FileID;
    private int chunkNo;
    private byte[] information;

    public Chunk(String FileID, int chunkNo) {
        this.FileID = FileID;
        this.chunkNo = chunkNo;
    }

    public int getChunkNo() {
        return chunkNo;
    }

    public String getFileID() {
        return FileID;
    }

    public byte[] getInformation() {
        return information;
    }
    
    
}
