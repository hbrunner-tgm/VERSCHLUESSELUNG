package org.verschluesselung;

import org.verschluesselung.client.ConnectClient;

/**
 * A start-class to start the client
 *
 * Created by helmuthbrunner on 29/01/15.
 */
public class StartClient {
    public static void main(String[] args) {

        ConnectClient cc= new ConnectClient();

        cc.connect();

        cc.sendData("Hello");

    }
}
