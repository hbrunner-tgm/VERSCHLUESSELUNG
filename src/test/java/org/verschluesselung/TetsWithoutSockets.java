package org.verschluesselung;

import junit.framework.Assert;
import org.junit.Test;

import java.security.PublicKey;

/**
 * @author Ari Ayvazyan
 * @version 19.02.2015
 */
public class TetsWithoutSockets {
    @Test
    public void testWithoutSockets() throws Exception {
        //Server
        //The server generates a private + public key
        AsyncKeyCommunication serverAsyncKey = new AsyncKeyCommunication();
        //The public key ( serverAsyncKey.getPublicKey() )is sent to the client
        PublicKey transmittedPublicKey = serverAsyncKey.getPublicKey();

        //Client
        //The client generates a sharedKey that will be encrypted and sent to the server
        AsyncKeyCommunication clientAsyncKey = new AsyncKeyCommunication(transmittedPublicKey);
        SharedKeyCommunication clientSharedKeyCommunication = new SharedKeyCommunication();
        //The client encrypts the shared key with the public key
        byte[] clientSharedKey = clientSharedKeyCommunication.getKey();
        byte[] encryptedSharedKey = clientAsyncKey.encrypt(clientSharedKey);
        //The client transmits the encrypted shared key to the server
        byte[] transmittedEncryptedSharedKey = encryptedSharedKey;

        //Server
        //The server decrypts the encrypted shared key, using its private key
        byte[] decryptedSharedKey = serverAsyncKey.decrypt(transmittedEncryptedSharedKey);
        //The server uses the decrypted shared key for further encrypted communication
        final SharedKeyCommunication serverSharedKeyCommunication = new SharedKeyCommunication(decryptedSharedKey);

        //Communication - Everything is now set up
        //
        //Server
        String messageFromServer = "Hey There!";
        //The server encrypts the message using the shared key
        byte[] encryptedMsgFromServer = serverSharedKeyCommunication.encrypt(messageFromServer.getBytes());
        //The server transmits this message to the client
        byte[] transmittedMsgFromServer = encryptedMsgFromServer;

        //Client
        //The client decrypts the message
        byte[] decryptedMsgFromServer = clientSharedKeyCommunication.decrypt(transmittedMsgFromServer);
        String resultingMessage=new String(decryptedMsgFromServer);

        //Final check
        Assert.assertTrue(messageFromServer.equals(resultingMessage));
    }
}
