package org.verschluesselung.server;

import java.io.*;

import java.net.InetSocketAddress;

import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.*;
import java.util.Base64;

import org.apache.log4j.*;
import org.apache.log4j.or.ThreadGroupRenderer;
import org.verschluesselung.encryptionHelpers.AsyncKeyCommunication;
import org.verschluesselung.encryptionHelpers.SharedKeyCommunication;
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
    private String host, text;
    private Message message;

    private AsyncKeyCommunication serverAsyncKey;
    private SharedKeyCommunication serverSharedKeyCommunication;

    private SocketChannel sChannel;

    public ConnectServer(int port, String host, String message, boolean secure) {

        this.port= port;
        this.text= message;
        this.host= host;

        if(!secure)
            this.message= this.secure(message);
        else
            this.message= new Message(message, null);

        log.info("Message: "+ message);
    }

    @Override
    public void run() {
        log.info("Sender Start");

        try {

            ServerSocketChannel ssChannel = ServerSocketChannel.open();
            ssChannel.configureBlocking(true);
            ssChannel.socket().bind(new InetSocketAddress(port));

            while (true) {
                sChannel = ssChannel.accept();

                ObjectOutputStream oos = new ObjectOutputStream(sChannel.socket().getOutputStream());

                oos.writeObject(this.generatePublickey()); // the public key

                if( sChannel.isOpen() ) {
                    break;
                }
                oos.close();
            }
            log.info("Public key sent");

            ssChannel.close();
            ssChannel= null;

            try {
                Thread.sleep(100l);
            } catch (InterruptedException e) {
                log.error(e);
            }

            SocketChannel sChannel= SocketChannel.open();
            sChannel.configureBlocking(true);
            if (sChannel.connect(new InetSocketAddress(host, port + 1))) {

                ObjectInputStream ois = new ObjectInputStream(sChannel.socket().getInputStream());

                Message s = (Message) ois.readObject();

                if(s.getMessage().equals("sharedkey")) {
                    log.info("SharedKey recevied");
                    this.genSharedKey( (byte[]) s.getObject() );
                }
            }
            sChannel.close();

            log.info("Message will be send");

            ssChannel = ServerSocketChannel.open();
            ssChannel.configureBlocking(true);
            ssChannel.socket().bind(new InetSocketAddress(port+2));

            while (true) {
                sChannel = ssChannel.accept();

                ObjectOutputStream oos = new ObjectOutputStream(sChannel.socket().getOutputStream());

                Message en= this.secure(text);
                log.info("Uncrpyted Message send: " + text);
                log.info("Encrpyted Message send: " + new String((byte[]) en.getObject()));

                oos.writeObject(en); // the message

                oos.close();

                if(sChannel.isOpen() )
                    break;
            }
            ssChannel.close();

        } catch (IOException e) {
            log.error(e);
        } catch (ClassNotFoundException e) {
            log.error(e);
        }
    }

    public Message generatePublickey() {
        //Server
        //The server generates a private + public key
        serverAsyncKey = null;
        try {
            serverAsyncKey = new AsyncKeyCommunication();
        } catch (Exception e) {
            log.error(e);
        }
        //The public key ( serverAsyncKey.getPublicKey() )is sent to the client
        PublicKey transmittedPublicKey = serverAsyncKey.getPublicKey();

        return new Message("pubkey", transmittedPublicKey);
    }

    public void genSharedKey(byte[] transmittedEncryptedSharedKey) {

        //Server
        //The server decrypts the encrypted shared key, using its private key
        byte[] decryptedSharedKey = new byte[0];
        try {
            decryptedSharedKey = serverAsyncKey.decrypt(transmittedEncryptedSharedKey);
        } catch (Exception e) {
            log.error(e);
        }
        //The server uses the decrypted shared key for further encrypted communication
        serverSharedKeyCommunication = new SharedKeyCommunication(decryptedSharedKey);

    }

    public Message secure(String s) {

        //Server
        String messageFromServer = s;
        //The server encrypts the message using the shared key
        byte[] encryptedMsgFromServer = new byte[0];
        try {
            encryptedMsgFromServer = serverSharedKeyCommunication.encrypt(messageFromServer.getBytes());
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
        //The server transmits this message to the client
        byte[] transmittedMsgFromServer = encryptedMsgFromServer;

        return new Message("message", transmittedMsgFromServer);
    }
}
