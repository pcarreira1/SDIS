package sdis.backupsystem;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Peer {

    //INIT ------------------- SERVER_ID     IP     PORT    IP     PORT    IP     PORT
    //java -jar SDIS-BackupSystem.jar 100 localhost 8889 localhost 8888 localhost 8887
    public static void main(String[] args) {
        
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
        
        try {
            //Socket Objects
            MulticastControlChannel MC = new MulticastControlChannel(args[1], Integer.parseInt(args[2]));
            MulticastDataBackup MDB = new MulticastDataBackup(args[3], Integer.parseInt(args[4]));
            MulticastControlChannel MDR = new MulticastControlChannel(args[5], Integer.parseInt(args[6]));

            //Init all threads
            Thread MC_Thread = new Thread(MC);
            MC_Thread.start();

            Thread MDB_Thread = new Thread(MDB);
            MDB_Thread.start();

            Thread MDR_Thread = new Thread(MDR);
            MDR_Thread.start();
        } catch (IOException ex) {
            Logger.getLogger(Peer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
