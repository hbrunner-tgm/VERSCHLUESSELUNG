package org.verschluesselung;

import javax.crypto.Cipher;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author Ari Ayvazyan
 * @version 19.02.2015
 */
public class AsyncKeyCommunication {
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private String xform = "RSA/ECB/PKCS1Padding";

    /**
     * Generates a public and private key
     */
    public AsyncKeyCommunication() throws Exception {
        // Generate a key-pair
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(512); // 512 is the keysize.
        KeyPair kp = kpg.generateKeyPair();
        this.publicKey = kp.getPublic();
        this.privateKey = kp.getPrivate();
    }

    /**
     * @param publicKey the public key to encrypt messages (decrypting wont be possible)
     */
    public AsyncKeyCommunication(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public byte[] encrypt(byte[] toEncrypt) throws Exception {
        Cipher cipher = Cipher.getInstance(xform);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(toEncrypt);
    }

    public byte[] decrypt(byte[] toDecrypt) throws Exception {
        Cipher cipher = Cipher.getInstance(xform);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(toDecrypt);
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }
}
