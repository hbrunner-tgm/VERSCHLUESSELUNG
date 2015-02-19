package org.verschluesselung.verschluesselung;

import javax.crypto.SecretKey;
import java.io.Serializable;
import java.security.PublicKey;
import java.util.Objects;

/**
 * A class which contains the message and the public key which will be send over the network
 *
 * Created by helmuthbrunner on 12/02/15.
 */
public class Message implements Serializable {

    private String message;
    private Object object;

    public Message() {
    }

    public Message(String message, Object object) {

        this.message= message;
        this.object= object;

    }

    public String getMessage() {
        return message;
    }

    public Object getObject() {
        return object;
    }
}
