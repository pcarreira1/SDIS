package sdis.backupsystem;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class SystemFile implements Serializable {

    //Size is in Bytes
    private static final int maxChunkSize = 64000;
    //ID of the "home" server where the file originated from
    //private static int homeId;
    //File ID
    private String fileID;
    //Number of chunks this file will be devided in;
    private int numChunks;

    private String path;

    private byte[] data;

    File file;

    public SystemFile(String _path) throws IOException, NoSuchAlgorithmException {
        Path path = Paths.get(_path);
        file = new File(_path);
        float fileSize = file.length();
        float nChunks = fileSize / maxChunkSize;
        numChunks = (int) Math.ceil(nChunks);
        data = Files.readAllBytes(path);
        fileID = generateFileID(file.getName());
    }

    public String getFileID() {
        return fileID;
    }

    public byte[] getData() {
        return data;
    }

    public int getNumChunks() {
        return numChunks;
    }
    
    
    private String generateFileID(String name) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(name.getBytes(StandardCharsets.UTF_8));
        return byteArrayToHex(hash);

    }

    private static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for (byte b : a) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();

    }
}
