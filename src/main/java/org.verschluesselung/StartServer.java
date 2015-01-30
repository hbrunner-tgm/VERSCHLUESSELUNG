package org.verschluesselung;

import org.verschluesselung.server.ConnectServer;

/**
 * A start-class to start the server
 *
 * Created by helmuthbrunner on 30/01/15.
 */
public class StartServer {
    public static void main(String[] args) {

        ConnectServer cs= new ConnectServer();

        cs.connect();

        System.out.println( cs.readData() );
    }
}
