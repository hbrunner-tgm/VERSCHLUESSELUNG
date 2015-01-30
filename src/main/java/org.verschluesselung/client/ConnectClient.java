package org.verschluesselung.client;

import java.io.*;
import java.net.Socket;
import org.apache.log4j.Logger;

/**
 * This class is used to create a client-socket connection
 *
 * Created by helmuthbrunner on 29/01/15.
 */
public class ConnectClient {

    private static Logger log = Logger.getLogger(ConnectClient.class.getName());

    private String host;
    private int port;
    private Socket socket;

    private ObjectOutputStream ops;
    private ObjectInputStream ips;

    public ConnectClient(int port, String host) {

        this.port= port;
        this.host= host;

    }

    public ConnectClient() {
        host= "localhost";
        port= 8888;
    }

    public boolean connect() {
        if (socket == null || socket.isClosed() == true) {
            try {
                log.info("New socket for client");
                socket= new Socket(this.host, this.port);

                ops= new ObjectOutputStream( socket.getOutputStream() );
                ips= new ObjectInputStream( socket.getInputStream() );

            } catch (IOException e) {
                log.error(e);
            }
        } else {
            log.info("Socket is established");
            return false;
        }

        return true;
    }

    public Object readData() {
        try {
            return ips.readObject();
        } catch (IOException e) {
            log.error(e);
        } catch (ClassNotFoundException e) {
            log.error(e);
        }
        return false;
    }

    public boolean sendData(String data) {
        try {
            ops.write(data.getBytes());
            return true;
        } catch (IOException e) {
            log.error(e);
        }
        return false;
    }

    public void close() {
        try {
            ips.close();
            ops.close();
            socket.close();
        } catch (IOException e) {
            log.error(e);
        }
    }
}
