import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientGUI extends Application {
    private static Stage primaryStage;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private GameplayController gameplayController;
    private WelcomePageController welcomePageController;
    Client clientConnection;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        primaryStage.setTitle("Three Card Poker Client");

        // Load the welcome screen initially
        FXMLLoader loader = new FXMLLoader(getClass().getResource("welcome.fxml"));
        Parent root = loader.load();

        // Get the controller and set the ClientGUI reference
        welcomePageController = loader.getController();
        welcomePageController.setClientGUI(this);

        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    // Method to initialize connection from the welcome screen
    public void initializeConnection(String serverIp, int port) {
        new Thread(() -> {
            try {
                // Attempt to establish socket connection
                socket = new Socket(serverIp, port);
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
                socket.setTcpNoDelay(true);

                // If connection successful, switch to gameplay scene
                Platform.runLater(() -> {
                    try {
                        switchToGameplay();
                    } catch (Exception e) {
                        showConnectionError("Error switching to gameplay: " + e.getMessage());
                    }
                });

                // Start listening for server messages
                startMessageListener();

            } catch (Exception e) {
                Platform.runLater(() -> {
                    showConnectionError("Connection failed: " + e.getMessage());
                });
            }
        }).start();
    }

    // Method to listen for incoming messages
    private void startMessageListener() {
        new Thread(() -> {
            try {
                while (true) {
                    PokerInfo pokerInfo = (PokerInfo) in.readObject();
                    Platform.runLater(() -> {
                        if (gameplayController != null) {
                            gameplayController.updateGameState(pokerInfo);
                        }
                    });
                }
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showConnectionError("Error receiving server messages: " + e.getMessage());
                });
            }
        }).start();
    }

    // Switch to gameplay scene
    private void switchToGameplay() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("gameplay.fxml"));
        Parent gameRoot = loader.load();

        gameplayController = loader.getController();
        gameplayController.setClientGUI(this);
        gameplayController.setConnection(out);  // Pass output stream for sending messages

        primaryStage.setScene(new Scene(gameRoot, 800, 600));
        primaryStage.setTitle("Three Card Poker - Gameplay");
    }

    // Send PokerInfo to server
    public void sendPokerInfo(PokerInfo pokerInfo) {
        try {
            out.writeObject(pokerInfo);
            out.flush();
        } catch (Exception e) {
            showConnectionError("Error sending data: " + e.getMessage());
        }
    }

    // Show connection error dialog
    private void showConnectionError(String message) {
        // Implement error dialog or fallback to welcome screen
        System.err.println(message);
        welcomePageController.showErrorAlert("Connection Error", message);
    }

    public static void main(String[] args) {
        launch(args);
    }
}