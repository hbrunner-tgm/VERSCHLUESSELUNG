package org.verschluesselung;

import org.junit.Test;

import static org.junit.Assert.*;

public class AsyncKeyCommunicationTest {

    @Test
    public void testEncryptAndDecrypt() throws Exception {
        AsyncKeyCommunication akc=new AsyncKeyCommunication();
        byte[] dataBytes = "J2EE Security for Servlets, EJBs and Web Services".getBytes();

        byte[] encBytes = akc.encrypt(dataBytes);
        byte[] decBytes = akc.decrypt(encBytes);

        boolean expectedTrue = java.util.Arrays.equals(dataBytes, decBytes);
        assertTrue(expectedTrue);
    }

    @Test
    public void testEncryptAndDecryptWithDifferentConstructors() throws Exception {
        AsyncKeyCommunication akc=new AsyncKeyCommunication();
        AsyncKeyCommunication akc2=new AsyncKeyCommunication(akc.getPublicKey());
        byte[] dataBytes = "J2EE Security for Servlets, EJBs and Web Services".getBytes();

        byte[] encBytes = akc2.encrypt(dataBytes);
        byte[] decBytes = akc.decrypt(encBytes);

        boolean expectedTrue = java.util.Arrays.equals(dataBytes, decBytes);
        assertTrue(expectedTrue);
    }

    @Test
    public void testGetPublicKey() throws Exception {
        assertNotNull(new AsyncKeyCommunication().getPublicKey().getEncoded());
    }
}