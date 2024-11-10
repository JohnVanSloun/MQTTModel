import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Publisher {
    private String name;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public Publisher(String name) {
        this.name = name;
    }

    /**
     * Connects to a server and sets the instance's socket field.
     * 
     * param: host is the host ip address to connect to.
     * param: port is the port number to connect the socket to.
     */
    public boolean connect(InetAddress host, int port) {
        try {
            socket = new Socket(host, port);
            System.out.println("Connected");

            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println(this.name + ",CONN");

            if(!in.readLine().equals("CONN_ACK")) {
                return false;
            }
            return true;
        } catch(IOException e) {
            System.out.println("Error opening a connection with the server.");
            return false;
        }
    }

    /**
     * Publish a message over the socket to the server.
     * 
     * param: message is the message that is to be sent to the server.
     */
    public boolean publish(String subject, String message) {
        try {
            out.println(this.name + ",PUB," + subject + "," + message);

            String servRep = in.readLine();

            return !servRep.equals("ERROR: Subject Not Found");
        } catch(IOException e) {
            System.out.println("Communication error with server while publishing");
            return false;
        }
    }

    /**
     * Disconnects from the server.
     */
    public void disconnect() {
        try {
            out.println("DISC");
            String disconnectAck = in.readLine();

            System.out.println(disconnectAck);
            out.close();
            in.close();
            socket.close();
        } catch(IOException e) {
            System.out.println("Error disconnecting");
        }
    }

    public static void main(String[] args) {
        try {
            Publisher publisher = new Publisher("pub1");
            publisher.connect(InetAddress.getLocalHost(), 4444);
            publisher.publish("NEWS", "A plane has hit the second tower");
            publisher.disconnect();
        } catch(UnknownHostException e) {
            System.out.println("Host not known");
        }
        
    }
}