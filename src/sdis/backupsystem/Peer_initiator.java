/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sdis.backupsystem;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carre
 */
public class Peer_initiator {

    private static int serverID;
    //INIT ------------------- SERVER_ID     IP     PORT    IP     PORT    IP     PORT
    //java -jar SDIS-BackupSystem.jar 100 localhost 8889 localhost 8888 localhost 8887
    public static void main(String[] args) throws NoSuchAlgorithmException {
        
        //for test /////////////////////
        args=new String[7];
        args[0]="101";
        args[1]="224.0.0.3";
        args[2]="8889";
        args[3]="224.0.0.3";
        args[4]="8888";
        args[5]="224.0.0.3";
        args[6]="8887";
        ////////////////////////////////
        
        serverID=Integer.parseInt(args[0]);
        try {
            //Socket Objects
            MulticastControlChannel MC = new MulticastControlChannel(args[1], Integer.parseInt(args[2]));
            MulticastDataBackup MDB = new MulticastDataBackup(args[3], Integer.parseInt(args[4]));
            MulticastControlChannel MDR = new MulticastControlChannel(args[5], Integer.parseInt(args[6]));
            

            SystemFile file=new SystemFile("C:\\Users\\carre\\Desktop\\logo.png");
            Chunk chunk;
            for(int i=1;i<=file.getNumChunks();i++){
                chunk=new Chunk(file.getFileID(), i, file.cutDataForChunk(i));
                MDB.BackupRequest(serverID,file.getFileID(),file.getNumChunks(),3,chunk);
            }
            
        } catch (IOException ex) {
            Logger.getLogger(Peer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}