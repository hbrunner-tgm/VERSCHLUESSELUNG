package org.verschluesselung;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.StandardOutputStreamLog;
import org.verschluesselung.client.ConnectClient;
import org.verschluesselung.server.ConnectServer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Ari Ayvazyan
 * @version 19.02.2015
 */
public class SocketTest {
    @Rule
    public final StandardOutputStreamLog log = new StandardOutputStreamLog();

    @Test
    public void socketTest() throws Exception {
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
        Thread.sleep(2000);
        Assert.assertTrue(log.getLog().contains("Message received: " + secretMessage));
        executorService1.shutdownNow();
        executorService2.shutdownNow();
    }
}
