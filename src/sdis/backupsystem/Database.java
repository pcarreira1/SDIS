package sdis.backupsystem;

import java.util.ArrayList;

public class Database {
    
    ArrayList<SystemFile> myFiles;
    ArrayList<Chunk> backedUp;

    public Database() {
        this.myFiles = new ArrayList<>();
        this.backedUp = new ArrayList<>();
    }

    public ArrayList<Chunk> getBackedUp() {
        return backedUp;
    }

    public ArrayList<SystemFile> getMyFiles() {
        return myFiles;
    }
    
    
    
}
