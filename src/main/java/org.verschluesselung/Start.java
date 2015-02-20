package org.verschluesselung;

import org.verschluesselung.client.ConnectClient;
import org.verschluesselung.server.ConnectServer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A start-class to start the server
 * <p/>
 * Created by helmuthbrunner on 30/01/15.
 */
public class Start {
    public static void main(String[] args) throws Exception {
        try {
            final ExecutorService executorService1 = Executors.newSingleThreadExecutor();
            final ExecutorService executorService2 = Executors.newSingleThreadExecutor();

            if (args.length == 0) {
                System.out.println("Executing the default Configuration- type help for a list of available commands");
                System.out.println("Starting Server and client at localhost with port 8888");
                String secretMessage = "SecretMessage";
                //Start the Server
                ConnectServer server = new ConnectServer(8888, "localhost", secretMessage, true);
                executorService1.execute(server);
                //Start the Client
                ConnectClient client = new ConnectClient(8888, "localhost", true);
                executorService2.execute(client);
                //Wait until it is finished,- this could be done in a better way
                Thread.sleep(10000l);
                executorService1.shutdownNow();
                executorService2.shutdownNow();
            } else if (args.length == 4) {
                //Client
                if (!args[0].equalsIgnoreCase("client")) throw new Exception("Invalid Parameters");
                int port = Integer.parseInt(args[1]);
                String ip = args[2];
                boolean secure = true;
                if (args[3].equalsIgnoreCase("n")) secure = false;

                ConnectClient client = new ConnectClient(port, ip, secure);
                executorService2.execute(client);
            } else if (args.length == 5) {
                //Server
                if (!args[0].equalsIgnoreCase("server")) throw new Exception("Invalid Parameters");
                int port = Integer.parseInt(args[1]);
                String ip = args[2];
                boolean secure = true;
                String message = args[3];
                if (args[4].equalsIgnoreCase("n")) secure = false;

                ConnectServer server = new ConnectServer(port, ip, message, secure);
                executorService1.execute(server);
            } else {
                throw new Exception("Invalid Parameters");
            }
        } catch (Exception e) {
            System.out.println("Available commands:");
            System.out.println("client [port] [targetIP] [secure Y/N]");
            System.out.println("server [port] [targetIP] [message] [secure Y/N]");
            System.out.println("Example:");
            System.out.println("client 8888 localhost Y");
            System.out.println("server 8888 localhost Message Y");
        }
    }
}
