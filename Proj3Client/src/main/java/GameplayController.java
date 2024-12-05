import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class GameplayController {
    @FXML ImageView playerCard1, playerCard2, playerCard3, dealerCard1, dealerCard2, dealerCard3;

    private ClientGUI clientGUI;
    private ObjectOutputStream out;

    @FXML
    private Label winningsLabel;


    @FXML
    private Button anteButton;

    @FXML
    private Button pairPlusButton;

    @FXML
    private Button playButton;

    @FXML
    private Text gameInfoText;

    boolean cardsDealt = false;

    Player player;
    Player dealer;

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
        // todo setup logic
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

            // card display logic
            if (pokerInfo.isDealer) { dealer = pokerInfo.player; }
            else { player = pokerInfo.player; }

            if (dealer != null && player != null) {
                cardsDealt = true;
                findCardImages(dealer.getHand(), player.getHand());
            }
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

    private void findCardImages(ArrayList<Card> dealerHand, ArrayList<Card> playerOneHand) {

        if (cardsDealt) {

            // Show Player 1's and Player 2's cards after dealing
            playerCard1.setImage(new Image(getCardImagePath(playerOneHand.get(0))));
            playerCard2.setImage(new Image(getCardImagePath(playerOneHand.get(1))));
            playerCard3.setImage(new Image(getCardImagePath(playerOneHand.get(2))));

            dealerCard1.setImage(new Image(getCardImagePath(dealerHand.get(0))));
            dealerCard2.setImage(new Image(getCardImagePath(dealerHand.get(1))));
            dealerCard3.setImage(new Image(getCardImagePath(dealerHand.get(2))));


        } else {
            // Initially show only back of cards
            dealerCard1.setImage(new Image("/CardImages/back.png"));
            dealerCard2.setImage(new Image("/CardImages/back.png"));
            dealerCard3.setImage(new Image("/CardImages/back.png"));

            playerCard1.setImage(new Image("/CardImages/back.png"));
            playerCard2.setImage(new Image("/CardImages/back.png"));
            playerCard3.setImage(new Image("/CardImages/back.png"));

        }
    }

    private String getCardImagePath(Card card) {
        String valueString = getCardValueString(card.value);
        char suit = card.suit;

        String suitString = "";
        if (suit == 'C') {
            suitString = "clubs";
        } else if (suit == 'D') {
            suitString = "diamonds";
        } else if (suit == 'H') {
            suitString = "hearts";
        } else if (suit == 'S') {
            suitString = "spades";
        }

        return "/CardImages/" + valueString + "_of_" + suitString + ".png";
    }

    private String getCardValueString(int value) {
        if (value == 11) return "jack";
        else if (value == 12) return "queen";
        else if (value == 13) return "king";
        else if (value == 14) return "ace";
        else return String.valueOf(value);
    }
}