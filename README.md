# MQTTModel

The MQTT Model consists of 4 classes: Publisher, Subscriber, ClientHandler, and Server. 

The Publisher class handles the creation of a publisher client and can connect, publish to, and disconnect from a server. It handles a set of instructions: connect, publish, and disconnect which it receives from terminal input that trigger its methods. The connect command makes the Publisher create a socket and input/output streams through which it can communicate. The publish command publishes a message to a server. The disconnect command disconnects the Publisher from the server and handles teardown of the socket.

The Subscriber class handles the creation of a Subscriber client and can connect, subscribe to, check messages from, and disconnect from a server. It handles as set of instructions: connect, subscribe, check, and disconnect which it receives from terminal input that trigger its methods. The connect command makes the Subscriber create a socket and input/output streams through which it communicates.  The reconnect command allows the user to reconnect a subscriber instance in order to receive messages that were sent to the subscribed subjects while it was offline (disconnected). The Subscribe command subscribes the Subscriber to a specified subject which it will receive messages from. The check command reads all messages that have been sent to the subject that the Subscriber is subscribed to and prints them to the terminal. The disconnect command disconnects the Subscriber from the server and handles teardown of the socket.

The ClientHandler class handles the creation of multiple clients that can interface with the server via multithreading. It receives messages from the respective Publisher's or Subscriber's socket instance that it was created with and processes the message in order to interface with the server. It also sends messages back to the client to indicate the result of the command and if any issues occured.

The Server class handles the storage of subscriber data and interfaces with ClientHandlers to communicate with the clients who publish and subscribe to it. It has the ability to publish messages to subscribers of a specified subject and add clients to the list of subscribers upon request.

The Client class packages relevant subscriber data and the associated ClientHandler in order to handle Server-Subscriber interactions.



How to Run:

In order to execute the program, first you must navigate into the src folder which contains the source code and execute the compilation command: "javac ClientHandler.java Server.java Subscriber.java Publisher.java"

Next you must open multiple terminals. One terminal must be opened for the Server to be run with the "java Server" command. After the server has been established you can open as many subsequent terminals as you desire. Each terminal other than the Server terminal can be either a Publisher (run "java Publisher") or a Subscriber (run "java Subscriber"). Once you have executed the command to create either a Publisher or a Subscriber you will be met with a message that will outline what (case sensitive) commands you can use to interface with the application (connect, publish, disconnect for Publisher) (connect, reconnect, subscribe, check, disconnect for Subscriber). 