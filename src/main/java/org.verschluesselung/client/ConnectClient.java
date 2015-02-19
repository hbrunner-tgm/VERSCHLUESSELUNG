package org.verschluesselung.client;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

import org.apache.log4j.Logger;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.verschluesselung.verschluesselung.Message;

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

            SocketChannel sChannel= SocketChannel.open();
            sChannel.configureBlocking(true);
            if (sChannel.connect(new InetSocketAddress(host, port))) {

                ObjectInputStream ois = new ObjectInputStream(sChannel.socket().getInputStream());

                Message s = (Message) ois.readObject();
                if (s.getKeyPub() == null) {
                    log.info("Message is: " + s.getMessage());
                }

                String en= null;
                log.info("Message decrypted: " + s.getMessage());
                if(s.getKeyPub()!=null) {
                    log.info("Message decrypted: " + s.getMessage());
                    en= this.encrypt(s.getKeyPub(), s.getMessage(), this.decodeSecret(s.getSk()));
                } else {
                    en= s.getMessage();
                }
                log.info("Message encrypted: " + en);
            }

            log.info("End Receiver");
        } catch (ClassNotFoundException e) {
            log.error(e);
        } catch (IOException e) {
            log.error(e);
        } catch (Exception e) {
            log.error(e);
        }

    }

    public PublicKey decodeKey(byte[] pubKey) throws Exception {
        X509EncodedKeySpec ks = new X509EncodedKeySpec(pubKey);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(ks);
    }

    public SecretKey decodeSecret(String sk) {
        byte[] decodedKey = Base64.getDecoder().decode(sk);
        // rebuild key using SecretKeySpec
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    public String encrypt(byte[] pubKey, String message, SecretKey sk) throws Exception {
        PublicKey publicKey = this.decodeKey(pubKey);

        SecureRandom random;

        // generates synchronous public key
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        SecretKey secretKey = keyGen.generateKey();

        // uses public key from client to encrypt synchronous public key
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedData = cipher.doFinal(secretKey.getEncoded());

        // use the AES key for encrypting a message
        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] iv = new byte[16];
        random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, sk, ivParameterSpec);
        encryptedData = cipher.doFinal(message.getBytes("UTF-8"));
        String encrypted = new String(encryptedData);

        log.info("encrypted aes message: " + encrypted);

        return encrypted;
    }
}
