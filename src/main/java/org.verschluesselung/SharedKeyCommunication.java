package org.verschluesselung;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author Ari Ayvazyan
 * @version 19.02.2015
 */
public class SharedKeyCommunication {
    private byte[] key;//... secret sequence of bytes

    public SharedKeyCommunication(byte[] sharedKey) {
        key = sharedKey;
    }

    public SharedKeyCommunication() throws NoSuchAlgorithmException {
        this(generateSyncKey());
    }

    public byte[] getKey(){
        return key;
    }

    public byte[] encrypt(byte[] toBeEncrypted) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher c = Cipher.getInstance("AES");
        SecretKeySpec k = new SecretKeySpec(key, "AES");
        c.init(Cipher.ENCRYPT_MODE, k);

        byte[] encryptedData = c.doFinal(toBeEncrypted);
        return encryptedData;
    }

    public byte[] decrypt(byte[] toBeDecrypted) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher c = Cipher.getInstance("AES");
        SecretKeySpec k = new SecretKeySpec(key, "AES");
        c.init(Cipher.DECRYPT_MODE, k);
        byte[] decryptedData = c.doFinal(toBeDecrypted);
        return decryptedData;
    }

    public static byte[] generateSyncKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        SecretKey secretKey = keyGen.generateKey();
        return secretKey.getEncoded();
    }
}
