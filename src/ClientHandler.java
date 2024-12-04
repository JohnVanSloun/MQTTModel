import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    private Socket socket;
    private Server server;

    public ClientHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String message;
            while(true) {
                message = in.readLine();
                List<String> msgParts = Arrays.asList(message.split(","));

                if(msgParts.size() == 2 && msgParts.get(0).equals("DISC")) {
                    server.discSub(msgParts.get(1));
                    out.println("DISC_ACK");
                    System.out.println("Client Disconecting");
                    break;
                } else if(msgParts.size() == 2 && msgParts.get(1).equals("CONN")) {
                    System.out.println("Client: " + msgParts.get(0) + " has connected");
                    out.println("CONN_ACK");
                } else if(msgParts.size() == 2 && msgParts.get(0).equals("RECONNECT")) {
                    if(server.reconnSub(msgParts.get(1), this)) {
                        System.out.println("Client: " + msgParts.get(1) + " is reconnected");
                    } else {
                        out.println("Error Reconnecting");
                        System.out.println("Client was unable to reconnect");
                    }
                } else if(msgParts.size() == 3 && msgParts.get(1).equals("SUB")) {
                    if(server.subscribe(msgParts.get(2), msgParts.get(0), this)) {
                        System.out.println(msgParts.get(0) + " subscribed");
                    } else {
                        System.out.println("Error: Could not subscribe");
                    }
                } else if(msgParts.size() == 4 && msgParts.get(1).equals("PUB")) {
                    if(server.publish(msgParts.get(2), msgParts.get(3))) {
                        out.println("ACK");
                        System.out.println("Received: " + message);
                    } else {
                        out.println("ERROR: Not Subscribed");
                        System.out.println("ERROR: Not Subscribed");
                    }
                } else {
                    System.out.println("Here");
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}