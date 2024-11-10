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

                if(msgParts.size() == 1 && msgParts.get(0).equals("DISC")) {
                    System.out.println("Client Disconecting");
                    out.println("DISC_ACK");
                    break;
                } else if(msgParts.size() == 2) {
                    System.out.println("Client: " + msgParts.get(0) + " has connected");
                    out.println("CONN_ACK");
                } else if(msgParts.size() == 3 && msgParts.get(1).equals("SUB")) {
                    out.println("ACK");
                } else if(msgParts.size() == 4 && msgParts.get(1).equals("PUB")) {
                    if(server.publish(msgParts.get(2), msgParts.get(3))) {
                        out.println("ACK");
                        System.out.println("Received: " + message);
                    } else {
                        out.println("ERROR: Not Subscribed");
                        System.out.println("ERROR: Not Subscribed");
                    }
                } else {
                    out.println("ACK");
                }
            }

            // out.close();
            // in.close();
            // socket.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}