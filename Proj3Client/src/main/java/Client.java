import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;

public class Client extends Thread {

    private Socket socketClient;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Consumer<Serializable> callback;
    private String serverIp;
    private int serverPort;

    public Client(Consumer<Serializable> callback, String serverIp, int serverPort) {
        this.callback = callback;
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }

    public void run() {
        new Thread(() -> {
            try {

                socketClient = new Socket(serverIp, serverPort);
                in = new ObjectInputStream(socketClient.getInputStream());
                out = new ObjectOutputStream(socketClient.getOutputStream());
                socketClient.setTcpNoDelay(true);

                // Start a thread to listen for incoming data from the server
                listenForIncomingData();

            } catch (UnknownHostException e) {
                callback.accept("Unknown host: " + serverIp);
            } catch (IOException e) {
                callback.accept("Error connecting to server: " + e.getMessage());
            }
        }).start();
    }

    private void listenForIncomingData() {
        new Thread(() -> {
            try {
                while (true) {
                    PokerInfo receivedData = (PokerInfo) in.readObject();
                    callback.accept(receivedData);
                }
            } catch (Exception e) {
                callback.accept("Error reading from server: " + e.getMessage());
            }
        }).start();
    }



    public void send(Serializable data) {
        new Thread(() -> {
            try {
                out.writeObject(data);
                out.flush();
                callback.accept("Data sent to server: " + data.toString());
            } catch (IOException e) {
                callback.accept("Error sending data to server: " + e.getMessage());
            }
        }).start();
    }

    public void close() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socketClient != null) socketClient.close();
            callback.accept("Connection closed.");
        } catch (IOException e) {
            callback.accept("Error closing connection: " + e.getMessage());
        }
    }
}