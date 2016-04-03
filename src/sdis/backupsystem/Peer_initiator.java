/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sdis.backupsystem;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author carre
 */
public class Peer_initiator extends PeerBase {

    //INIT ------------------- SERVER_ID     IP     PORT    IP     PORT    IP     PORT
    //java -jar SDIS-BackupSystem.jar 100 localhost 8889 localhost 8888 localhost 8887
    public static void main(String[] args) throws NoSuchAlgorithmException {

        //for test /////////////////////
        args = new String[7];
        args[0] = "102";
        args[1] = "224.0.0.3";
        args[2] = "8889";
        args[3] = "224.0.0.3";
        args[4] = "8888";
        args[5] = "224.0.0.3";
        args[6] = "8887";
        ////////////////////////////////

        peer_id = Integer.parseInt(args[0]);
        try {
            //Socket Objects

            Database database = new Database();
            try {
                database.loadDatabase();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Peer_initiator.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Peer_initiator.class.getName()).log(Level.SEVERE, null, ex);
            }
            //MulticastControlChannel MC = new MulticastControlChannel(args[1], Integer.parseInt(args[2]));
            MulticastDataRestore MDR = new MulticastDataRestore(args[5], Integer.parseInt(args[6]));
            MulticastControlChannel MC = new MulticastControlChannel(args[1], Integer.parseInt(args[2]), MDR);
            //MulticastDataBackup MDB = new MulticastDataBackup(args[3], Integer.parseInt(args[4]), MC);
            MulticastDataBackup MDB = new MulticastDataBackup(args[3], Integer.parseInt(args[4]), MC, database);
            //MulticastDataRestore MDR = new MulticastDataRestore(args[5], Integer.parseInt(args[6]), MC);

            Thread MC_Thread = new Thread(MC);
            MC_Thread.start();

            Thread MDR_Thread = new Thread(MDR);
            MDR_Thread.start();

            //backupFile("C:\\Users\\carre\\Desktop\\logo.png",MC,MDB);
            //deleteFile("C:\\Users\\carre\\Desktop\\logo.png",MC);
            //restoreFile("C:\\Users\\carre\\Desktop\\logo.png",MC,MDR);
            //backupFile("C:\\Users\\Ghost\\Desktop\\diablo_pitch.pdf", MC, MDB);
            restoreFile("C:\\Users\\Ghost\\Desktop\\diablo_pitch.pdf", MC, MDR);
        } catch (IOException ex) {
            Logger.getLogger(Peer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void deleteFile(String filePath, MulticastControlChannel MC) {
        SystemFile file = null;
        try {
            file = new SystemFile(filePath, false);
            MC.Delete(peer_id, file.getFileID());
        } catch (IOException ex) {
            Logger.getLogger(Peer_initiator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Peer_initiator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void restoreFile(String filePath, MulticastControlChannel MC, MulticastDataRestore MDR) {
        SystemFile file = null;
        try {
            file = new SystemFile(filePath, false);
            ArrayList<Chunk> chunksRetrieved = new ArrayList<>();
            for (int i = 0; i < file.getNumChunks(); i++) {
                MDR.count_reply = 0;
                MC.RestoreRequest(peer_id, file.getFileID(), i);

//                if (MDR.count_reply > 0) {
//                    break;
//                }
                while (MDR.count_reply <= 0) {

                }
                chunksRetrieved.add(MDR.currentChunk);
            }
            SystemFile temp = new SystemFile(chunksRetrieved);
            /*FileOutputStream fout = new FileOutputStream("go.gif");
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(temp);
            oos.close();
            fout.close();*/
            byte[] dataForRestoredFile = temp.getData();
            FileOutputStream out = new FileOutputStream("diablo_pitch.pdf");
            out.write(dataForRestoredFile);
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(Peer_initiator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Peer_initiator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void backupFile(String filePath, MulticastControlChannel MC, MulticastDataBackup MDB) {
        SystemFile file = null;
        try {
            file = new SystemFile(filePath, true);
        } catch (IOException ex) {
            Logger.getLogger(Peer_initiator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Peer_initiator.class.getName()).log(Level.SEVERE, null, ex);
        }
        Chunk chunk;
        int replication = 1;
        for (int i = 0; i < file.getNumChunks(); i++) {
            int timeout = 1000;
            for (int j = 0; j < 5; j++) {
                MC.count_reply = 0;
                chunk = new Chunk(file.getFileID(), i, file.cutDataForChunk(i));
                MDB.BackupRequest(peer_id, file.getFileID(), i, replication, chunk);
                try {
                    Thread.sleep(timeout);
                } catch (InterruptedException ex) {
                    Logger.getLogger(MulticastDataBackup.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (MC.count_reply >= replication) {
                    break;
                }
                timeout *= 2;
            }
        }
    }
}
