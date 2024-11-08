import java.net.ServerSocket;
import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

public class Server {
    private int port;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public Server(int port) {
        this.port = port;
    }

    public void connect() {
        try {
            serverSocket = new ServerSocket(port);
            clientSocket = serverSocket.accept();
            System.out.println("Server connected");

            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String message = in.readLine();
            System.out.println(message);

            message = in.readLine();
            System.out.println(message);
            if(message.equals("DISC")) {
                out.println("DISC_ACK");
                System.out.println("Server closing");
                serverSocket.close();
            } else {
                out.println("No ACK");
            }
        } catch(IOException e) {
            System.out.println("Error setting up server socket on port");
        }
    }

    public static void main(String[] args) {
        Server server = new Server(4444);
        server.connect();
    }
}