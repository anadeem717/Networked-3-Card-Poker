import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

public class GameplayController {
    @FXML
    private ImageView playerCard1, playerCard2, playerCard3, dealerCard1, dealerCard2, dealerCard3;
    @FXML
    private Button dealButton, playButton, foldButton, anteButton, pairPlusButton;
    @FXML
    private Text gameInfoText;
    @FXML
    private TextField anteInput, pairPlusInput;
    @FXML
    private Label winningsLabel;

    private ClientGUI clientGUI;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean cardsDealt = false;

    // Initial card back image path
    private final String cardBackImagePath = "/CardImages/back.png";

    // Set client GUI
    public void setClientGUI(ClientGUI clientGUI) {
        this.clientGUI = clientGUI;
    }

    // Set connection with socket
    public void setConnection(Socket socket) {
        try {
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
            socket.setTcpNoDelay(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setConnection(ObjectOutputStream out, ObjectInputStream in) {
        this.out = out;
        this.in = in;
    }

    @FXML
    public void initialize() {
        // Set initial card backs when the UI is initialized
        setCardBacks();
        // Disable play and fold buttons initially
        playButton.setDisable(true);
        foldButton.setDisable(true);
    }

    @FXML
    public void dealCards() {
        try {
            PokerInfo request = new PokerInfo();
            request.setIsDealer(false); // Indicate that we're requesting the player's hand
            out.writeObject(request);
            out.flush();

            // Wait for the server to send the player's hand
            new Thread(() -> {
                try {
                    PokerInfo pokerInfo = (PokerInfo) in.readObject();
                    Platform.runLater(() -> {
                        if (pokerInfo != null && pokerInfo.getPlayer() != null && pokerInfo.getPlayer().getHand() != null) {
                            updateGameState(pokerInfo);
                            cardsDealt = true; // Set flag to true after dealing the cards
                        } else {
                            System.out.println("Error: pokerInfo or pokerInfo.player is null");
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void placeAnteBet() {
        try {
            double anteBet = Double.parseDouble(anteInput.getText().trim());
            if (anteBet > 0) {
                PokerInfo betInfo = new PokerInfo();
                betInfo.setAntePlaced(true);
                // You might want to add ante amount logic here if needed
                out.writeObject(betInfo);
                out.flush();
                System.out.println("Ante bet placed: $" + anteBet);
            } else {
                System.out.println("Invalid ante bet amount.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input for ante bet. Please enter a valid number.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void placePairPlusBet() {
        try {
            double pairPlusBet = Double.parseDouble(pairPlusInput.getText().trim());
            if (pairPlusBet > 0) {
                PokerInfo betInfo = new PokerInfo();
                betInfo.setPairPlusPlaced(true);
                // You might want to add pair plus amount logic here if needed
                out.writeObject(betInfo);
                out.flush();
                System.out.println("Pair Plus bet placed: $" + pairPlusBet);
            } else {
                System.out.println("Invalid pair plus bet amount.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input for pair plus bet. Please enter a valid number.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void playGame() {
        // Send a request to the server to reveal the dealer's hand
        try {
            PokerInfo request = new PokerInfo();
            request.setIsDealer(true); // Indicates that we are requesting the dealer's hand
            out.writeObject(request);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Update game info text
        gameInfoText.setText("Dealer's cards revealed. Game in progress.");
        playButton.setDisable(true);
        foldButton.setDisable(true);
    }

    @FXML
    public void foldGame() {
        // Logic for folding...
        playButton.setDisable(true);
        foldButton.setDisable(true);
        gameInfoText.setText("You folded. Game over.");
    }

    public void updateGameState(PokerInfo pokerInfo) {
        Platform.runLater(() -> {
            // Update player winnings and game info
            if (pokerInfo.getPlayer() != null) {
                winningsLabel.setText("$" + pokerInfo.getPlayer().getTotalWinnings());
            } else {
                winningsLabel.setText("$0");
            }

            gameInfoText.setText(pokerInfo.getGameRes());

            // Check if cards have been dealt before showing player cards
            if (cardsDealt) {
                try {
                    // Display the player's cards
                    playerCard1.setImage(new Image(getCardImagePath(pokerInfo.getPlayer().getHand().get(0))));
                    playerCard2.setImage(new Image(getCardImagePath(pokerInfo.getPlayer().getHand().get(1))));
                    playerCard3.setImage(new Image(getCardImagePath(pokerInfo.getPlayer().getHand().get(2))));
                } catch (Exception e) {
                    System.out.println("Error loading card images: " + e.getMessage());
                }
            }
        });
    }

    private void setCardBacks() {
        dealerCard1.setImage(new Image(cardBackImagePath));
        dealerCard2.setImage(new Image(cardBackImagePath));
        dealerCard3.setImage(new Image(cardBackImagePath));
        playerCard1.setImage(new Image(cardBackImagePath));
        playerCard2.setImage(new Image(cardBackImagePath));
        playerCard3.setImage(new Image(cardBackImagePath));
    }

    private String getCardImagePath(Card card) {
        String valueString = getCardValueString(card.value);
        String suitString = "";

        switch (card.suit) {
            case 'C': suitString = "clubs"; break;
            case 'D': suitString = "diamonds"; break;
            case 'H': suitString = "hearts"; break;
            case 'S': suitString = "spades"; break;
            default: suitString = "unknown";
        }

        return "/CardImages/" + valueString + "_of_" + suitString + ".png";
    }

    private String getCardValueString(int value) {
        switch (value) {
            case 11: return "jack";
            case 12: return "queen";
            case 13: return "king";
            case 14: return "ace";
            default: return String.valueOf(value);
        }
    }
}