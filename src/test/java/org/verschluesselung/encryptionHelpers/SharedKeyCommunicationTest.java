package org.verschluesselung.encryptionHelpers;

import org.junit.Test;
import org.verschluesselung.encryptionHelpers.SharedKeyCommunication;

import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

public class SharedKeyCommunicationTest {

    @Test
    public void TestGenerateKey() throws NoSuchAlgorithmException {
        assertNotNull(SharedKeyCommunication.generateSyncKey());
    }

    @Test
    public void testEncryptAndDecrypt() throws Exception {
        SharedKeyCommunication skc= new SharedKeyCommunication(SharedKeyCommunication.generateSyncKey());
        String originalMessage="Hello";

        byte[] encrypted=skc.encrypt(originalMessage.getBytes());
        assertFalse(originalMessage.compareTo(new String(encrypted))==0);

        byte[] decrypted=skc.decrypt(encrypted);
        String processedMessage=new String(decrypted);

        assertTrue(originalMessage.compareTo(processedMessage)==0);
    }

    @Test
    public void testConstructors() throws Exception {
        String originalMessage="Hello";

        SharedKeyCommunication skc= new SharedKeyCommunication();
        byte[] key=skc.getKey();
        SharedKeyCommunication skcReceiver= new SharedKeyCommunication(key);
        byte[] encrypted=skc.encrypt(originalMessage.getBytes());
        byte[] decrypted=skcReceiver.decrypt(encrypted);

        String processedMessage=new String(decrypted);
        assertTrue(originalMessage.compareTo(processedMessage)==0);
    }


}