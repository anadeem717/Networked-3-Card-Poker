import server.PokerInfo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.*;
import java.net.Socket;

public class ClientGUI extends Application {
    private static final String SERVER_ADDRESS = "localhost"; // or use a config file to specify this
    private static final int SERVER_PORT = 12345;

    private ObjectInputStream in;
    private ObjectOutputStream out;

    @Override
    public void start(Stage primaryStage) {
        try {
            // Connect to the server
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());

            // Load the FXML for the game UI
            FXMLLoader loader = new FXMLLoader(getClass().getResource("gameplay.fxml"));
            BorderPane root = loader.load();
            GameplayController controller = loader.getController();
            controller.setClientGUI(this);

            Scene scene = new Scene(root, 800, 600);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Three Card Poker");
            primaryStage.show();

            // Start a thread to listen for incoming data from the server
            new Thread(() -> {
                try {
                    while (true) {
                        PokerInfo pokerInfo = (PokerInfo) in.readObject();
                        System.out.println("Received from server: " + pokerInfo);
                        // Update the controller with new game state
                        controller.updateGameState(pokerInfo);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Error receiving data from server: " + e.getMessage());
                }
            }).start();

        } catch (IOException e) {
            System.out.println("Error connecting to server: " + e.getMessage());
        }
    }

    public void sendPokerInfo(PokerInfo pokerInfo) {
        try {
            out.writeObject(pokerInfo);
            out.flush();
        } catch (IOException e) {
            System.out.println("Error sending data to server: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}