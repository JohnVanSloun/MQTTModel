package mqtt.src;

import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;

public class Publisher {
    private Socket socket = null;
    private String name;
    private PrintWriter out = null;
    private BufferedReader in = null;

    public Publisher(String name) {
        this.name = name;
    }

    /**
     * Connects to a server and sets the instance's socket field.
     * 
     * param: host is the host ip address to connect to.
     * param: port is the port number to connect the socket to.
     */
    public void connect(String host, int port) {
        return;
    }

    /**
     * Sends a message over the socket to the server.
     * 
     * param: message is the message that is to be sent to the server.
     */
    public void sendMessage(String message) {
        return;
    }

    /**
     * Disconnects from the server.
     */
    public void disconnect() {
        return;
    }

}