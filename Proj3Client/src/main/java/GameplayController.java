import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import java.io.ObjectOutputStream;

public class GameplayController {
    private ClientGUI clientGUI;
    private ObjectOutputStream out;

    @FXML
    private Label winningsLabel;

    @FXML
    private HBox playerCardsBox;

    @FXML
    private HBox dealerCardsBox;

    @FXML
    private Button anteButton;

    @FXML
    private Button pairPlusButton;

    @FXML
    private Button playButton;

    @FXML
    private Text gameInfoText;

    // Set the ClientGUI instance
    public void setClientGUI(ClientGUI clientGUI) {
        this.clientGUI = clientGUI;
    }

    // Set the output stream for sending messages
    public void setConnection(ObjectOutputStream out) {
        this.out = out;
    }

    @FXML
    public void placeAnte() {
        sendPokerInfo("Ante placed", true, false);
    }

    @FXML
    public void placePairPlus() {
        sendPokerInfo("Pair Plus placed", false, true);
    }

    @FXML
    public void playGame() {
        sendPokerInfo("Playing", true, true);
    }

    // Generic method to send poker info
    private void sendPokerInfo(String action, boolean antePlaced, boolean pairPlusPlaced) {
        try {
            PokerInfo pokerInfo = new PokerInfo(
                    new Player(),
                    action,
                    "play",
                    antePlaced,
                    pairPlusPlaced
            );
            clientGUI.sendPokerInfo(pokerInfo);
        } catch (Exception e) {
            showAlert("Error", "An error occurred: " + e.getMessage());
        }
    }

    // Update game state when receiving info from server
    public void updateGameState(PokerInfo pokerInfo) {
        Platform.runLater(() -> {
            if (pokerInfo.player != null) {
                winningsLabel.setText("$" + pokerInfo.player.getTotalWinnings());
            } else {
                winningsLabel.setText("$0");
            }

            gameInfoText.setText(pokerInfo.gameRes);

            // Clear previous card displays
            playerCardsBox.getChildren().clear();
            dealerCardsBox.getChildren().clear();

            // TODO: Add code to display cards
            // Placeholder for card display logic
        });
    }

    // Reset winnings for fresh start
    public void resetWinnings() {
        // TODO: Implement reset logic
        winningsLabel.setText("$0");
        gameInfoText.setText("Game Reset");
    }

    // Show an alert with a given title and message
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}