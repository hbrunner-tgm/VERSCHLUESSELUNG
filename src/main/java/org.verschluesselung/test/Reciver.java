package org.verschluesselung.test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * Created by helmuthbrunner on 12/02/15.
 */
public class Reciver {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        System.out.println("Receiver Start");

        SocketChannel sChannel = SocketChannel.open();
        sChannel.configureBlocking(true);
        if (sChannel.connect(new InetSocketAddress("localhost", 12345))) {

            ObjectInputStream ois = new ObjectInputStream(sChannel.socket().getInputStream());

            String s = (String)ois.readObject();
            System.out.println("String is: '" + s + "'");
        }

        System.out.println("End Receiver");
    }

}
