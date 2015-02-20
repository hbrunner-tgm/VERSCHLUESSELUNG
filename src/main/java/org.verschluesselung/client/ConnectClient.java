package org.verschluesselung.client;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.apache.log4j.Logger;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.verschluesselung.encryptionHelpers.AsyncKeyCommunication;
import org.verschluesselung.encryptionHelpers.SharedKeyCommunication;
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

    private PublicKey publicKey;
    private SharedKeyCommunication clientSharedKeyCommunication;

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

                if (s.getMessage().equals("pubkey")) {
                    publicKey = (PublicKey) s.getObject();
                    log.info("Publickey recived");
                }
                if (sChannel.finishConnect()) {
                    sChannel = null;
                }
                ois.close();
            }

            ServerSocketChannel ssChannel = ServerSocketChannel.open();
            ssChannel.configureBlocking(true);
            ssChannel.socket().bind(new InetSocketAddress(port +1));

            while (true) {
                sChannel = ssChannel.accept();

                ObjectOutputStream oos = new ObjectOutputStream(sChannel.socket().getOutputStream());
                log.info("Send the response");
                oos.writeObject(this.response(publicKey)); // the public key

                if (sChannel.isOpen())
                    break;

                oos.close();
            }

            ssChannel.close();
            ssChannel = null;

            try {
                Thread.sleep(1000l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            sChannel = SocketChannel.open();
            sChannel.configureBlocking(true);
            if (sChannel.connect(new InetSocketAddress(host, port + 2))) {

                ObjectInputStream ois = new ObjectInputStream(sChannel.socket().getInputStream());

                Message s = (Message) ois.readObject();

                if (s.getMessage().equals("message")) {

                    byte[] en= (byte[]) s.getObject();

                    log.info("Encrypted Message received: " + new String(en) );

                    String message = this.decrypt(en);
                    log.info("Unencrypted Message received: " + message);
                }
            }

        } catch (ClassNotFoundException e) {
            log.error(e);
        } catch (IOException e) {
            log.error(e);
        } catch (Exception e) {
            log.error(e);
        }

    }

    public Message response(PublicKey publicKey) {
        Message m = null;

        //Client
        //The client generates a sharedKey that will be encrypted and sent to the server
        AsyncKeyCommunication clientAsyncKey = new AsyncKeyCommunication(publicKey);
        clientSharedKeyCommunication = null;
        try {
            clientSharedKeyCommunication = new SharedKeyCommunication();
        } catch (NoSuchAlgorithmException e) {
            log.error(e);
        }
        //The client encrypts the shared key with the public key
        byte[] clientSharedKey = clientSharedKeyCommunication.getKey();
        byte[] encryptedSharedKey = new byte[0];
        try {
            encryptedSharedKey = clientAsyncKey.encrypt(clientSharedKey);
        } catch (Exception e) {
            log.error(e);
        }
        //The client transmits the encrypted shared key to the server
        byte[] transmittedEncryptedSharedKey = encryptedSharedKey;

        return new Message("sharedkey", transmittedEncryptedSharedKey);
    }

    public String decrypt(byte[] message) {
        //Client
        //The client decrypts the message
        byte[] decryptedMsgFromServer = new byte[0];
        try {
            decryptedMsgFromServer = clientSharedKeyCommunication.decrypt(message);
        } catch (NoSuchPaddingException e) {
            log.error(e);
        } catch (NoSuchAlgorithmException e) {
            log.error(e);
        } catch (InvalidKeyException e) {
            log.error(e);
        } catch (BadPaddingException e) {
            log.error(e);
        } catch (IllegalBlockSizeException e) {
            log.error(e);
        }
        return new String(decryptedMsgFromServer);
    }
}
