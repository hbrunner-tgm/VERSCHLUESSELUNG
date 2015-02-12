package org.verschluesselung.client;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import com.sun.javafx.tools.packager.Log;
import org.apache.log4j.Logger;
import org.verschluesselung.verschluesselung.Message;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 * This class is used to create a client-socket connection
 * <p/>
 * Created by helmuthbrunner on 29/01/15.
 */
public class ConnectClient implements Runnable {

    private static Logger log = Logger.getLogger(ConnectClient.class.getName());

    private int port;
    private String host;

    public ConnectClient(int port, String host) {

        this.port = port;
        this.host = host;

    }

    @Override
    public void run() {
        log.info("Receiver Start");

        try {

            SocketChannel sChannel = SocketChannel.open();
            sChannel.configureBlocking(true);
            if (sChannel.connect(new InetSocketAddress(host, port))) {

                ObjectInputStream ois = new ObjectInputStream(sChannel.socket().getInputStream());

                Message s = (Message) ois.readObject();
                if (s.getKey() == null) {
                    log.info("Message is: " + s.getMessage());
                }

                log.info("Message decrypted: " + s.getMessage() + "'");
            }

            log.info("End Receiver");
        } catch (ClassNotFoundException e) {
            log.error(e);
        } catch (IOException e) {
            log.error(e);
        }
    }

    public PublicKey decodeKey(byte[] pubKey) throws Exception {
        X509EncodedKeySpec ks = new X509EncodedKeySpec(pubKey);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(ks);
    }

    public String encrypt(byte[] pubKey, String message) throws Exception {
        PublicKey publicKey = this.decodeKey(pubKey);

        //// Erzeugt synchrone öffentlichen Schlüssel
        //// generates synchronous public key
        //KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        //keyGen.init(128);
        //SecretKey secretKey = keyGen.generateKey();

        //// Verwendet den öffentlichen Schlüssel vom Client an den Synchron-Public-Key-Verschlüsselung
        //// uses public key from client to encrypt synchronous public key
        //Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        //cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        //byte[] encryptedData = cipher.doFinal(secretKey.getEncoded());

        //// DECRYPTION of AES Key
        //cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        //cipher.init(Cipher.DECRYPT_MODE, privateKey);
        //SecretKey aesKey = new SecretKeySpec(cipher.doFinal(encryptedData), "AES");

        //// use the AES key for encrypting a message
        //cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        //byte[] iv = new byte[16];
        //SecureRandom random = new SecureRandom();
        //random.nextBytes(iv);
        //IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        //cipher.init(Cipher.ENCRYPT_MODE, aesKey, ivParameterSpec);
        //encryptedData = cipher.doFinal(message.getBytes("UTF-8"));
        //String encrypted = new String(encryptedData);
        //log.info("encrypted aes message: " + encrypted);

        return "";
    }

}
