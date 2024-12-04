import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

public class Server {

    private ArrayList<ClientThread> clients = new ArrayList<>();
    private TheServer server;
    private Consumer<Serializable> callback;
    int count = 1;

    public Server(Consumer<Serializable> call) {
        callback = call;
    }

    public void start(int portNumber) {
        server = new TheServer(portNumber);
        server.start();
    }


    class TheServer extends Thread {
        private int portNumber;

        public TheServer(int portNumber) {
            this.portNumber = portNumber;
        }

        public void run() {
            try (ServerSocket mysocket = new ServerSocket(portNumber)) {
                callback.accept("Server started on port: " + portNumber);

                while (true) {
                    ClientThread clientThread = new ClientThread(mysocket.accept(), count);

                    synchronized (clients) {
                        clients.add(clientThread);
                    }

                    callback.accept("Client " + count + " connected.");
                    clientThread.start();
                }
            } catch (Exception e) {
                callback.accept("Server socket did not launch");
            }
        }
    }

    class ClientThread extends Thread {

        Socket connection;
        int count;
        ObjectInputStream in;
        ObjectOutputStream out;

        ClientThread(Socket socket, int count) {
            this.connection = socket;
            this.count = count;
        }


        public void run() {
            try {
                in = new ObjectInputStream(connection.getInputStream());
                out = new ObjectOutputStream(connection.getOutputStream());

                while (true) {
                    try {
                        String data = in.readObject().toString();
                        callback.accept("Client says: " + data);
                    } catch (Exception e) {
                        break;
                    }
                }
            } catch (Exception e) {
                callback.accept("Client error: " + e.getMessage());
            } finally {
                synchronized (clients) {
                    clients.remove(this);
                }
            }
        }
    }
}
