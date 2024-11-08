

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

            out.println("<" + this.name + ",CONN>");
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
        out.println("<" + this.name + ",SUB," + subject + ">");
    }

    /**
     * Disconnects from the server.
     */
    public void disconnect() {
        try {
            out.println("<DISC>");
            String disconnectAck = in.readLine();

            if(disconnectAck.equals("<DISC_ACK>")) {
                System.out.println("Client closing");
                out.close();
                in.close();
                socket.close();
            }
        } catch(IOException e) {
            System.out.println("Error disconnecting");
        }
    }
}