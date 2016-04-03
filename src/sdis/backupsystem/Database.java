package sdis.backupsystem;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class Database implements Serializable {

    ArrayList<SystemFile> myFiles;
    ArrayList<Chunk> backedUp;

    public Database() {
        this.myFiles = new ArrayList<>();
        this.backedUp = new ArrayList<>();
    }

    public Database(ArrayList<SystemFile> myFiles, ArrayList<Chunk> backedUp) {
        this.myFiles = myFiles;
        this.backedUp = backedUp;
    }
    
    

    public ArrayList<Chunk> getBackedUp() {
        return backedUp;
    }

    public ArrayList<SystemFile> getMyFiles() {
        return myFiles;
    }

    public void saveDatabase() throws FileNotFoundException, IOException {
        FileOutputStream fout = new FileOutputStream("databaseTestRestore2");
        ObjectOutputStream oos = new ObjectOutputStream(fout);
        Database temp = new Database(myFiles, backedUp);
        oos.writeObject(temp);
        oos.close();
        fout.close();
    }

    public void loadDatabase() throws FileNotFoundException, IOException, ClassNotFoundException {
        FileInputStream streamIn = new FileInputStream("databaseTestRestore2");
        ObjectInputStream objectinputstream = new ObjectInputStream(streamIn);
        Database temp = (Database) objectinputstream.readObject();
        myFiles = temp.getMyFiles();
        backedUp = temp.getBackedUp();
        objectinputstream.close();
        streamIn.close();
    }
    
    public void addFile(SystemFile file){
        if(!myFiles.contains(file))
            myFiles.add(file);
    }
    
    public void deleteFile(String fileID){
        
        for(int i=0; i<myFiles.size();i++){
            if(myFiles.get(i).getFileID().equals(fileID)){
                myFiles.remove(i);
            }
        }
    }
    
    public void addChunk(Chunk chunk){
        //if(!backedUp.contains(chunk))
        boolean exists = false;
        for(int i=0; i<backedUp.size(); i++)
        {
            if(backedUp.get(i).getFileID().equals(chunk.getFileID()) && backedUp.get(i).getChunkNo() == chunk.getChunkNo()){
                //backedUp.add(chunk);
                exists = true;
            }
        }
//        if(0 == backedUp.size()){
//            backedUp.add(chunk);
//        }
        if(!exists){
            backedUp.add(chunk);
        }
    }
    
     public void deleteChunk(Chunk chunk){
        for(int i=0; i<backedUp.size();i++){
            if(backedUp.get(i).getFileID().equals(chunk.getFileID())){
                backedUp.remove(i);
            }
        }
    }
    

}
