/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sdis.backupsystem;

import java.io.IOException;

/**
 *
 * @author Ghost
 */
public class Main {
    
    final static String INET_ADDR = "224.0.0.3";
    final static int PORT = 8889;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // Passar porta e addr por parâmetro
        //MulticastControlChannel mc=new MulticastControlChannel(args[0],Integer.parseInt(args[1]));
        
        
        // Para "teste"
        MulticastControlChannel mc=new MulticastControlChannel(INET_ADDR,PORT);
        
        Thread thread = new Thread(mc);
        thread.start();
    }
    
}
