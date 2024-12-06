import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Subscriber {
    private String name;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean isSubscribed;

    public Subscriber(String name) {
        this.name = name;
        this.isSubscribed = false;
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
            isSubscribed = true;
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Disconnects from the server.
     */
    public void disconnect() {
        try {
            out.println("DISC," + name);
            String disconnectAck = in.readLine();

            System.out.println(disconnectAck);
            out.close();
            in.close();
            socket.close();

            isSubscribed = false;
        } catch(IOException e) {
            System.out.println("Error disconnecting");
        }
    }

    public void reconnect(InetAddress host, int port) {
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println("RECONNECT," + name);

            String reconnectAck = in.readLine();

            System.out.println(reconnectAck);

            isSubscribed = true;
        } catch(IOException e) {
            System.out.println("Error reconnecting");
        }
    }

    /**
     * Reads all messages received by the subscriber and prints them
     */
    public void checkMessages() {
        try {
            socket.setSoTimeout(1000);
            String message;
            
            while (true) {
                try {
                    message = in.readLine();
                    System.out.println(message);
                } catch(SocketTimeoutException e) {
                    break;
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the subscriber is currently subscribed to a subject
     */
    public boolean isSubscribed() {
        return isSubscribed;
    }
    
    public static void main(String[] args) {
        try {
            Subscriber subscriber = null;
            Scanner userIn = new Scanner(System.in);
            String command = "";
            System.out.println("Commands:\n- connect\n- reconnect\n- subscribe\n- check\n- disconnect\n- quit");

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
                } else if(command.equals("reconnect")) {
                    if(subscriber == null) {
                        System.out.println("Please connect a subscriber first");
                        continue;
                    } else {
                        subscriber.reconnect(InetAddress.getLocalHost(), 4444);
                    }
                } else if(command.equals("disconnect")) {
                    if(subscriber == null) {
                        System.out.println("Please connect a subscriber first");
                        continue;
                    } else {
                        subscriber.disconnect();
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
                } else if(command.equals("check")) {
                    if(subscriber == null) {
                        System.out.println("Please connect a subscriber first");
                        continue;
                    } else {
                        System.out.println("Received messages:");
                        subscriber.checkMessages();
                        System.out.println("End of messages");
                    }
                } else if(command.equals("quit")) {
                    break;
                } else {
                    System.out.println("Please enter a valid command,");
                    System.out.println("Commands:\n- connect\n- subscribe\n- check\n- disconnect");
                }
            }
        } catch(UnknownHostException e) {
            System.out.println("Host not known");
        }
        
    }
}