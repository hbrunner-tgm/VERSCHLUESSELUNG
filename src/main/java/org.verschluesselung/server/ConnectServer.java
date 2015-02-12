package org.verschluesselung.server;

import java.io.*;

import java.net.InetSocketAddress;

import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.*;

import org.apache.log4j.*;
import org.verschluesselung.verschluesselung.Message;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * This class is used to create a server-socket connection
 *
 * Created by helmuthbrunner on 29/01/15.
 */
public class ConnectServer implements Runnable {

    private static Logger log= Logger.getLogger(ConnectServer.class.getName());

    private int port;
    private Message message;

    private Message m;

    public ConnectServer(int port, String message, boolean secure) throws NoSuchPaddingException, UnsupportedEncodingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {

        this.port= port;

        if(secure)
            this.message= this.secure(message);
        else
            this.message= new Message(message, null);

        log.info(message + "");
    }

    @Override
    public void run() {
        log.info("Sender Start");

        try {

            ServerSocketChannel ssChannel = ServerSocketChannel.open();
            ssChannel.configureBlocking(true);
            ssChannel.socket().bind(new InetSocketAddress(port));

            while (true) {
                SocketChannel sChannel = ssChannel.accept();

                ObjectOutputStream oos = new ObjectOutputStream(sChannel.socket().getOutputStream());
                oos.writeObject(message);
                oos.close();

                log.info("Connection ended");
            }

        } catch (IOException e) {
            log.error(e);
        }
    }

    public Message secure(String s) throws
            NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, BadPaddingException,
            IllegalBlockSizeException, InvalidAlgorithmParameterException,
            UnsupportedEncodingException {

        String message = "Just Read the Instructions";

        message= s;

        // generate Public and Private Key
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        SecureRandom random = SecureRandom.getInstanceStrong();

        keyPairGenerator.initialize(2048, random);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        // generates synchronous public key
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        SecretKey secretKey = keyGen.generateKey();

        // uses public key from client to encrypt synchronous public key
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedData = cipher.doFinal(secretKey.getEncoded());

        // DECRYPTION of AES Key
        cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        SecretKey aesKey = new SecretKeySpec(cipher.doFinal(encryptedData), "AES");

        // use the AES key for encrypting a message
        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] iv = new byte[16];
        random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, ivParameterSpec);
        encryptedData = cipher.doFinal(message.getBytes("UTF-8"));
        String encrypted = new String(encryptedData);
        log.info("encrypted aes message: " + encrypted);

        log.info("Public Key: " + publicKey.toString());

        m= new Message("Super it Works", publicKey.getEncoded() );

         /*

        String message = "Just Read the Instructions";

        // generate Public and Private Key
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        SecureRandom random = SecureRandom.getInstanceStrong();

        keyPairGenerator.initialize(2048, random);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        // generates synchronous public key
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        SecretKey secretKey = keyGen.generateKey();

        // uses public key from client to encrypt synchronous public key
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedData = cipher.doFinal(secretKey.getEncoded());

        // DECRYPTION of AES Key
        cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        SecretKey aesKey = new SecretKeySpec(cipher.doFinal(encryptedData), "AES");


        logger.info(secretKey.toString() + " algortihm: " + secretKey.getAlgorithm() + " format: " + secretKey.getFormat() + " enc: " + secretKey.getEncoded());
        logger.info(aesKey.toString() + " algortihm: " + aesKey.getAlgorithm() + " format: " + aesKey.getFormat() + " enc: " + aesKey.getEncoded());

        logger.info("" + aesKey.equals(secretKey));
        logger.info("" + aesKey.getClass().equals(secretKey.getClass()));


        logger.info("decrypted the following: " + new String(aesKey.getEncoded()));

        logger.info("" + secretKey.hashCode());
        logger.info("" + aesKey.hashCode());


        // use the AES key for encrypting a message
        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] iv = new byte[16];
        random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, ivParameterSpec);
        encryptedData = cipher.doFinal(message.getBytes("UTF-8"));
        String encrypted = new String(encryptedData);
        logger.info("encrypted aes message: " + encrypted);

        // use AES key for decrypting a message
        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, aesKey,ivParameterSpec);
        String decrypted = new String(cipher.doFinal(encryptedData), "UTF-8");
        logger.info("decrypted aes message: " + decrypted );


         */


        return m;
    }
}
