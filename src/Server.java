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
    private HashMap<String, List<Client>> subscribers;

    public Server(int port) {
        try {
            serverSocket = new ServerSocket(port);

            subscribers = new HashMap<String, List<Client>>();
            subscribers.put("WEATHER", new ArrayList<Client>());
            subscribers.put("NEWS", new ArrayList<Client>());
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
    public boolean subscribe(String subject, String name, ClientHandler sub) {
        try {
            Socket subSocket = sub.getSocket();
            PrintWriter subOut = new PrintWriter(subSocket.getOutputStream(), true);

            if(subscribers.containsKey(subject)) {
                Client client = new Client(name, sub);
                subscribers.get(subject).add(client);
                subOut.println("SUB_ACK");
                return true;
            } else {
                subOut.println("Failed to subscribe to given subject");
                return false;
            }
        } catch(IOException e) {
            e.printStackTrace();
            return false;
        }
        
    }

    /**
     * param sub, the ClientHandler to be unsubscribed
     */
    public void unsubscribe(String name) {
        List<Client> news = subscribers.get("NEWS");
        List<Client> weather = subscribers.get("WEATHER");

        for (Client sub : news) {
            if(sub.getName().equals(name)) {
                news.remove(sub);
            }
        }

        for (Client sub : weather) {
            if(sub.getName().equals(name)) {
                weather.remove(sub);
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
        if(subscribers.containsKey(subject) && !subscribers.get(subject).isEmpty()) {
            List<Client> subList = subscribers.get(subject);

            for(Client sub : subList) {
                sub.sendMsg(message);
            }

            return true;
        } else {
            return false;
        }
    }

    public ServerSocket getSocket() {
        return serverSocket;
    }

    public static void main(String[] args) {
        Server server = new Server(4444);
        

        try {
            //server.subscribe("NEWS", new ClientHandler(new Socket(InetAddress.getLocalHost(), 4444), server));
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