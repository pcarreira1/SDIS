package sdis.backupsystem;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SystemFile implements Serializable{
    
    //Size is in Bytes
    private static final int maxChunkSize = 64000;
    //ID of the "home" server where the file originated from
    //private static int homeId;
    //File ID
    private String fileID;
    //Number of chunks this file will be devided in;
    private int numChunks;
    
    private Path path;
    
    private byte[] data;
    
    public SystemFile(int fileSize, String _path) throws IOException {
        int nChunks = fileSize/maxChunkSize;
        numChunks = (int) Math.ceil(nChunks);
        path = Paths.get(_path);
        data = Files.readAllBytes(path);
    }
    
    public String getFileID(){
        return fileID;
    }

    
    
}
