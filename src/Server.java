import java.net.ServerSocket;
import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.net.InetAddress;

public class Server {
    private ServerSocket serverSocket;
    private List<Client> subscribers;

    public Server(int port) {
        try {
            serverSocket = new ServerSocket(port);

            subscribers = new ArrayList<Client>();
        } catch(IOException e) {
            System.out.println("Error instantiating server");
        }
    }

    /**
     * Subscribes a ClientHandler to the subject provided.
     * 
     * param: subject, the subject to be subscribed to
     * param: sub, the ClientHandler to subscribe to the subject
     */
    public boolean subscribe(String subject, String name, ClientHandler handler) {
        try {
            Socket subSocket = handler.getSocket();
            PrintWriter subOut = new PrintWriter(subSocket.getOutputStream(), true);

            if (!subject.equals("NEWS") && !subject.equals("WEATHER")) {
                subOut.println("Failed to subscribe to given subject");
                return false;
            }

            boolean exists = false;

            for (Client sub : subscribers) {
                if (sub.getName().equals(name)) {
                    exists = true;

                    if (!sub.getSubjects().contains(subject)) {
                        sub.getSubjects().add(subject);
                    }
                }
            }

            if (!exists) {
                Client client = new Client(name, handler);
                client.getSubjects().add(subject);

                subscribers.add(client);
            }

            subOut.println("SUB_ACK");
            return true;

        } catch(IOException e) {
            e.printStackTrace();
            return false;
        }
        
    }

    /**
     * param sub, the ClientHandler to be unsubscribed
     */
    public void unsubscribe(String name) {
        for (Client sub : subscribers) {
            if (sub.getName().equals(name)) {
                subscribers.remove(sub);
            }
        }
    }

    /**
     * Publish a message to a given subject
     * 
     * param: subject the subject to be published to
     * param: message, the message to be published
     */
    public boolean publish(String subject, String message) {
        boolean published = false;

        for (Client sub : subscribers) {
            if (sub.getSubjects().contains(subject)) {
                published = true;
                sub.sendMsg(message);
            }
        }

        return published;
    }

    public void discSub(String subName) {
        for (Client sub : subscribers) {
            if (sub.getName().equals(subName)) {
                sub.disconnect();
            }
        }
    }

    public boolean reconnSub(String subName, ClientHandler handler) {
        boolean reconnected = false;

        for (Client sub : subscribers) {
            if (sub.getName().equals(subName)) {
                sub.reconnect(handler);
                reconnected = true;
            }
        }

        return reconnected;
    }

    public ServerSocket getSocket() {
        return serverSocket;
    }

    public static void main(String[] args) {
        Server server = new Server(4444);
        

        try {
            while(true) {
                Socket clientSocket = server.getSocket().accept();

                ClientHandler clientHandler = new ClientHandler(clientSocket, server);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch(IOException e) {
            System.out.println("Error connecting");
        }
    }
}