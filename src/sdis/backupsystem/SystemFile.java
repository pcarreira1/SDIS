package sdis.backupsystem;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import static sun.security.krb5.Confounder.bytes;

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

    public SystemFile(String _path, boolean loadAll) throws IOException, NoSuchAlgorithmException {
        Path path = Paths.get(_path);
        file = new File(_path);
        fileID = generateFileID(file.getName());
        float fileSize = file.length();
        float nChunks = fileSize / maxChunkSize;
        numChunks = (int) Math.ceil(nChunks);
        if (loadAll) {
            data = Files.readAllBytes(path);
        }
    }

    public SystemFile(ArrayList<Chunk> chunks) throws IOException {
        fileID = chunks.get(0).getFileID();
        byte[] temp;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (int i = 0; i < chunks.size(); i++) {
            outputStream.write(chunks.get(i).getInformation());
        }
        temp = outputStream.toByteArray();
        data = temp;
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

    public byte[] cutDataForChunk(int ChunkNo) {
        byte[] information;
        if ((ChunkNo+1) != numChunks) {
            information = Arrays.copyOfRange(data, ChunkNo * maxChunkSize, (ChunkNo + 1) * maxChunkSize);
        } else {
            information = Arrays.copyOfRange(data, ChunkNo * maxChunkSize, data.length);
        }
        return information;
    }
}
