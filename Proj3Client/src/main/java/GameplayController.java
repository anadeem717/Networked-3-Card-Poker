import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class GameplayController {
    @FXML
    private ImageView playerCard1, playerCard2, playerCard3, dealerCard1, dealerCard2, dealerCard3;

    @FXML
    private Label winningsLabel;

    @FXML
    private Button anteButton, pairPlusButton, playButton, foldButton;

    @FXML
    private Text gameInfoText;

    @FXML
    private TextField anteInput, pairPlusInput;

    private ClientGUI clientGUI;
    private ObjectOutputStream out;
    private boolean cardsDealt = false;
    private Player player;
    private Player dealer;

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
        try {
            int anteValue = Integer.parseInt(anteInput.getText());
            sendPokerInfo("Ante placed", anteValue, 0, false);
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid numeric value for Ante.");
        }
    }

    @FXML
    public void placePairPlus() {
        try {
            int pairPlusValue = Integer.parseInt(pairPlusInput.getText());
            sendPokerInfo("Pair Plus placed", 0, pairPlusValue, false);
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid numeric value for Pair Plus.");
        }
    }

    @FXML
    public void playGame() {
        sendPokerInfo("Playing", 0, 0, true);
    }

    @FXML
    public void foldGame() {
        sendPokerInfo("Fold", 0, 0, false);
    }

    // Generic method to send poker info
    private void sendPokerInfo(String action, int anteValue, int pairPlusValue, boolean isPlay) {
        try {
            PokerInfo pokerInfo = new PokerInfo(
                    player,                // Player instance
                    "Action: " + action,   // Game action description
                    isPlay ? "Play" : "Fold", // Play or fold action
                    anteValue > 0,         // Ante placed (true if anteValue > 0)
                    pairPlusValue > 0,     // Pair Plus placed (true if pairPlusValue > 0)
                    false                  // Is dealer (always false for player actions)
            );

            clientGUI.sendPokerInfo(pokerInfo); // Send PokerInfo to the server
        } catch (Exception e) {
            showAlert("Error", "An error occurred while sending PokerInfo to the server: " + e.getMessage());
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

            // Card display logic
            if (pokerInfo.isDealer) {
                dealer = pokerInfo.player;
            } else {
                player = pokerInfo.player;
            }

            if (dealer != null && player != null) {
                cardsDealt = true;
                findCardImages(dealer.getHand(), player.getHand());
            }
        });
    }

    // Reset winnings for fresh start
    public void resetWinnings() {
        winningsLabel.setText("$0");
        gameInfoText.setText("Game Reset");
        anteInput.clear();
        pairPlusInput.clear();
    }

    // Show an alert with a given title and message
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void findCardImages(ArrayList<Card> dealerHand, ArrayList<Card> playerHand) {
        if (cardsDealt) {
            playerCard1.setImage(new Image(getCardImagePath(playerHand.get(0))));
            playerCard2.setImage(new Image(getCardImagePath(playerHand.get(1))));
            playerCard3.setImage(new Image(getCardImagePath(playerHand.get(2))));

            dealerCard1.setImage(new Image(getCardImagePath(dealerHand.get(0))));
            dealerCard2.setImage(new Image(getCardImagePath(dealerHand.get(1))));
            dealerCard3.setImage(new Image(getCardImagePath(dealerHand.get(2))));
        } else {
            String backImagePath = "/CardImages/back.png";
            dealerCard1.setImage(new Image(backImagePath));
            dealerCard2.setImage(new Image(backImagePath));
            dealerCard3.setImage(new Image(backImagePath));

            playerCard1.setImage(new Image(backImagePath));
            playerCard2.setImage(new Image(backImagePath));
            playerCard3.setImage(new Image(backImagePath));
        }
    }

    private String getCardImagePath(Card card) {
        String valueString = getCardValueString(card.value);
        String suitString = "";

        switch (card.suit) {
            case 'C':
                suitString = "clubs";
                break;
            case 'D':
                suitString = "diamonds";
                break;
            case 'H':
                suitString = "hearts";
                break;
            case 'S':
                suitString = "spades";
                break;
            default:
                suitString = "unknown";
        }

        return "/CardImages/" + valueString + "_of_" + suitString + ".png";
    }

    private String getCardValueString(int value) {
        switch (value) {
            case 11:
                return "jack";
            case 12:
                return "queen";
            case 13:
                return "king";
            case 14:
                return "ace";
            default:
                return String.valueOf(value);
        }
    }
}