import server.PokerInfo;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.image.ImageView;

public class GameplayController {

    private ClientGUI clientGUI;

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

    public void setClientGUI(ClientGUI clientGUI) {
        this.clientGUI = clientGUI;
    }

    @FXML
    public void placeAnte() {
        PokerInfo pokerInfo = new PokerInfo(null, "Ante placed", "play", true, false);
        clientGUI.sendPokerInfo(pokerInfo);
    }

    @FXML
    public void placePairPlus() {
        PokerInfo pokerInfo = new PokerInfo(null, "Pair Plus placed", "play", false, true);
        clientGUI.sendPokerInfo(pokerInfo);
    }

    @FXML
    public void playGame() {
        PokerInfo pokerInfo = new PokerInfo(null, "Playing", "play", true, true);
        clientGUI.sendPokerInfo(pokerInfo);
    }

    public void updateGameState(PokerInfo pokerInfo) {
        // Update the UI based on the received PokerInfo object
        winningsLabel.setText("$" + pokerInfo.player.getWinnings());
        gameInfoText.setText(pokerInfo.getGameRes());

        // Display cards for the player and dealer
        // Note: You may need a method to add images for each card.
        playerCardsBox.getChildren().clear();
        dealerCardsBox.getChildren().clear();
        // Add images for the cards here as needed
    }
}