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
    private HashMap<String, List<ClientHandler>> subscribers;

    public Server(int port) {
        try {
            serverSocket = new ServerSocket(port);

            subscribers = new HashMap<String, List<ClientHandler>>();
            subscribers.put("WEATHER", new ArrayList<ClientHandler>());
            subscribers.put("NEWS", new ArrayList<ClientHandler>());
        } catch(IOException e) {
            System.out.println("Error instantiating server");
        }
    }

    public boolean subscribe(String subject, ClientHandler sub) {
        if(subscribers.containsKey(subject)) {
            subscribers.get(subject).add(sub);
            return true;
        } else {
            return false;
        }
    }

    public void unsubscribe(ClientHandler sub) {
        subscribers.remove(sub);
    }

    public boolean publish(String subject, String message) {
        try {
            if(subscribers.containsKey(subject) && !subscribers.get(subject).isEmpty()) {
                List<ClientHandler> subList = subscribers.get(subject);

                for(ClientHandler sub : subList) {
                    Socket subSocket = sub.getSocket();
                    PrintWriter subOut = new PrintWriter(subSocket.getOutputStream(), true);

                    subOut.println(message);
                }

                return true;
            } else {
                return false;
            }
        } catch(IOException e) {
            System.out.println("Error publishing message");
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