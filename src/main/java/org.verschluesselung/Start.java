package org.verschluesselung;

import org.verschluesselung.client.ConnectClient;
import org.verschluesselung.server.ConnectServer;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executors;

/**
 * A start-class to start the server
 *
 * Created by helmuthbrunner on 30/01/15.
 */
public class Start {
    public static void main(String[] args) throws NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {

        // Start the threads
        Executors.newSingleThreadExecutor().execute(new ConnectServer(8888, "localhost", "Hello, who are you?", true));
        Executors.newSingleThreadExecutor().execute(new ConnectClient(8888, "localhost"));
    }
}
