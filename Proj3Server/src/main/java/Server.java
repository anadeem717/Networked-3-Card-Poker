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
    Dealer serverDealer;

    public Server(Consumer<Serializable> call) {
        callback = call;
    }

    public void start(int portNumber) {
        server = new TheServer(portNumber);
        server.start();
    }

    public void stopServer() {
        server.stopServer();
    }


    class TheServer extends Thread {
        private int portNumber;

        public TheServer(int portNumber) {
            this.portNumber = portNumber;
        }

        public void run() {

            serverDealer = new Dealer();

            try (ServerSocket mysocket = new ServerSocket(portNumber)) {
                callback.accept("Server started on port: " + portNumber);

                while (true) {
                    ClientThread clientThread = new ClientThread(mysocket.accept(), count);

                    synchronized (clients) {
                        clients.add(clientThread);
                    }

                    callback.accept("Client " + count + " connected.");
                    count++;
                    clientThread.start();
                }
            } catch (Exception e) {
                callback.accept("Server socket did not launch");
            }
        }

        // Stop the server when the button is clicked
        public void stopServer() {
            if (server != null) {
                System.exit(0);// Close the application
            }
        }
    }

    public class ClientThread extends Thread {

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

                // send info about dealer's hand to client
                PokerInfo dealerInfo = new PokerInfo();
                serverDealer.dealDealerHand();
                dealerInfo.player.setHand(serverDealer.getHand());
                dealerInfo.isDealer = true;
                sendPokerInfoToClient(dealerInfo);

                // send info about dealer's hand to client
                PokerInfo playerInfo = new PokerInfo();
                playerInfo.player.setHand(serverDealer.dealHand());
                playerInfo.isDealer = false;
                sendPokerInfoToClient(playerInfo);

                while (true) {
                    try {
                        // Receive PokerInfo object from the client
                        PokerInfo pokerInfo = (PokerInfo) in.readObject();
                        callback.accept("Received from client: " + pokerInfo.toString());

                        // Process the poker game logic in a separate function
                        processPokerGame(pokerInfo);

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

        private void processPokerGame(PokerInfo pokerInfo) {


        }

        public void sendPokerInfoToClient(PokerInfo pokerInfo) {
            try {
                out.writeObject(pokerInfo);
                out.flush();  // Ensure the data is sent immediately
            } catch (Exception e) {
                callback.accept("Error sending PokerInfo to client: " + e.getMessage());
            }
        }


    }
}
