package org.verschluesselung.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.*;

/**
 * This class is used to create a server-socket connection
 *
 * Created by helmuthbrunner on 29/01/15.
 */
public class ConnectServer {

    private static Logger log= Logger.getLogger(ConnectServer.class.getName());

    private int port;

    private ServerSocket ss;
    private Socket socket;
    private ObjectInputStream ips;
    private ObjectOutputStream ops;

    public ConnectServer(int port) {
        this.port= port;
        ips= null;
        ops= null;
    }

    public ConnectServer() {
        this.port= 8888;
        ips= null;
        ops= null;
    }

    public boolean connect() {
        try {
            ss= new ServerSocket(this.port);
            log.info("Waiting for client");
            socket= ss.accept();

            ips= new ObjectInputStream( socket.getInputStream() );
            ops= new ObjectOutputStream( socket.getOutputStream() );

        } catch (IOException e) {
            log.error(e);
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
        return null;
    }

    public boolean senddata(String data) {
        try {
            ops.write(data.getBytes());
            return true;
        } catch (IOException e) {
            log.error(e);
        }
        return false;
    }

    public void flush() {
        try {
            ops.flush();
        } catch (IOException e) {
            log.error(e);
        }
    }

    public void close() {
        try {
            ss.close();
            socket.close();
            ips.close();
            ops.close();
        }catch(IOException e) {
            log.error(e);
        }
    }
}
