import java.net.Socket;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Client {
    private String name;
    private ClientHandler handler;
    private boolean online;
    private List<String> subjects;
    private Queue<String> msgQueue;

    public Client(String name, ClientHandler handler) {
        this.name = name;
        this.handler = handler;

        this.online = true;
        this.subjects = new ArrayList<String>();
        this.msgQueue = new LinkedList<String>();
    }

    public void addSubject(String subject) {
        subjects.add(subject);
    }

    public void sendMsg(String msg) {
        if (online) {
            try {
                Socket socket = handler.getSocket();
                PrintWriter handlerOut = new PrintWriter(socket.getOutputStream(), true);

                handlerOut.println(msg);
            } catch (IOException e) {
                System.out.println("Error publishing message");
            }
        } else {
            msgQueue.add(msg);
        }
    }

    public void disconnect() {
        online = false;
    }

    public void reconnect() {
        online = true;

        try {
            Socket socket = handler.getSocket();
            PrintWriter handlerOut = new PrintWriter(socket.getOutputStream(), true);

            while (!msgQueue.isEmpty()) {
                String msg = msgQueue.remove();

                handlerOut.println(msg);
            }
        } catch (IOException e) {
            System.out.println("Error creating writer");
        }
    }

    public String getName() {
        return name;
    }

    public boolean getOnlineStatus() {
        return online;
    }
}