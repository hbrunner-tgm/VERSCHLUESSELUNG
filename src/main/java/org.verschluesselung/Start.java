package org.verschluesselung;

import org.verschluesselung.client.ConnectClient;
import org.verschluesselung.server.ConnectServer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A start-class to start the server
 *
 * Created by helmuthbrunner on 30/01/15.
 */
public class Start {
    public static void main(String[] args) throws Exception {

        // Start the threads
        //Executors.newSingleThreadExecutor().execute(new ConnectServer(8888, "localhost", "Hello, who are you?", false));
        //Executors.newSingleThreadExecutor().execute(new ConnectClient(8888, "localhost", false));

        String secretMessage = "SecretMessage";
        //Start the Server
        ConnectServer server = new ConnectServer(8888, "localhost", secretMessage, true);
        final ExecutorService executorService1 = Executors.newSingleThreadExecutor();
        executorService1.execute(server);
        //Start the Client
        ConnectClient client = new ConnectClient(8888, "localhost", true);
        final ExecutorService executorService2 = Executors.newSingleThreadExecutor();
        executorService2.execute(client);
        //Wait until it is finished,- this could be done in a better way
        Thread.sleep(10000l);
        executorService1.shutdownNow();
        executorService2.shutdownNow();

    }
}
