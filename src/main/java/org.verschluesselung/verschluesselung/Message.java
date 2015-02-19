package org.verschluesselung.verschluesselung;

import javax.crypto.SecretKey;
import java.io.Serializable;

/**
 * A class which contains the message and the public key which will be send over the network
 *
 * Created by helmuthbrunner on 12/02/15.
 */
public class Message implements Serializable {

    private String message;
    private byte[] keyPub;
    private String sk;

    public Message() {
    }

    public Message(String message, byte[] keyPub, String sk) {

        this.message= message;
        this.keyPub= keyPub;
        this.sk= sk;

    }

    public String getMessage() {
        return message;
    }

    public byte[] getKeyPub() {
        return keyPub;
    }

    public String getSk() {
        return sk;
    }

}
