
import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

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
    public void connect(InetAddress host, int port) {
        try {
            socket = new Socket(host, port);
            System.out.println("Connected");

            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println(this.name + ",CONN");

            System.out.println(in.readLine());
        } catch(IOException e) {
            System.out.println("Error opening a connection with the server.");
        }
    }

    /**
     * Publish a message over the socket to the server.
     * 
     * param: message is the message that is to be sent to the server.
     */
    public void publish(String subject, String message) {
        try {
            out.println(this.name + ",PUB," + subject + "," + message);

            String servRep = in.readLine();

            System.out.println(servRep);
        } catch(IOException e) {
            System.out.println("Communication error with server while publishing");
        }
    }

    /**
     * Disconnects from the server.
     */
    public void disconnect() {
        try {
            out.println("DISC, " + name);
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
            Publisher publisher = null; //= new Publisher("pub1");
            Scanner userIn = new Scanner(System.in);
            String command = "";
            System.out.println("Commands:\n- connect\n- publish\n- disconnect\n- quit");

            while(true) {
                System.out.print("Enter Command: ");
                command = userIn.nextLine();

                if(command.equals("connect")) {
                    if(publisher != null) {
                        System.out.println("A publisher has already been connected");
                        continue;
                    } else {
                        System.out.println("Choose a name for the publisher: ");

                        String name = userIn.nextLine();
                        publisher = new Publisher(name);

                        publisher.connect(InetAddress.getLocalHost(), 4444);
                    }
                } else if(command.equals("disconnect")) {
                    if(publisher == null) {
                        System.out.println("Please connect a publisher first");
                        continue;
                    } else {
                        publisher.disconnect();
                    }
                } else if(command.equals("publish")) {
                    if(publisher == null) {
                        System.out.println("Please connect a publisher first");
                        continue;
                    } else {
                        System.out.println("Enter Subject (e.g. NEWS or WEATHER): ");
                        String subject = userIn.nextLine();

                        System.out.println("Enter Message: ");
                        String message = userIn.nextLine();

                        publisher.publish(subject, message);
                    }
                } else if(command.equals("quit")) {
                    break;
                } else {
                    System.out.println("Please enter a valid command,");
                    System.out.println("Commands:\n- connect\n- publish\n- disconnect");
                }
            }

        } catch(UnknownHostException e) {
            System.out.println("Host not known");
        }
    }
}
