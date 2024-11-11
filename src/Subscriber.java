import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Subscriber {
    private String name;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public Subscriber(String name) {
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
     * Subscribe to a subject in the server to receive messages from
     * 
     * param: subject the subject
     */
    public void subscribe(String subject) {
        out.println(this.name + ",SUB," + subject);

        try {
            String message = in.readLine();

            System.out.println(message);
        } catch(IOException e) {
            e.printStackTrace();
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
            Subscriber subscriber = null;
            Scanner userIn = new Scanner(System.in);
            String command = "";
            System.out.println("Commands:\n- connect\n- subscribe\n- disconnect");

            while(true) {
                System.out.print("Enter Command: ");
                command = userIn.nextLine();

                if(command.equals("connect")) {
                    if(subscriber != null) {
                        System.out.println("A subscriber has already been connected");
                        continue;
                    } else {
                        System.out.println("Choose a name for the subscriber: ");

                        String name = userIn.nextLine();
                        subscriber = new Subscriber(name);

                        subscriber.connect(InetAddress.getLocalHost(), 4444);
                    }
                } else if(command.equals("disconnect")) {
                    if(subscriber == null) {
                        System.out.println("Please connect a subscriber first");
                        continue;
                    } else {
                        subscriber.disconnect();
                        break;
                    }
                } else if(command.equals("subscribe")) {
                    if(subscriber == null) {
                        System.out.println("Please connect a subscriber first");
                        continue;
                    } else {
                        System.out.println("Enter Subject (e.g. NEWS or WEATHER): ");
                        String subject = userIn.nextLine();

                        subscriber.subscribe(subject);
                    }
                } else {
                    System.out.println("Please enter a valid command,");
                    System.out.println("Commands:\n- connect\n- subscribe\n- disconnect");
                }
            }

            // subscriber.connect(InetAddress.getLocalHost(), 4444);
            // subscriber.subscribe("NEWS");
            // subscriber.disconnect();
        } catch(UnknownHostException e) {
            System.out.println("Host not known");
        }
        
    }
}