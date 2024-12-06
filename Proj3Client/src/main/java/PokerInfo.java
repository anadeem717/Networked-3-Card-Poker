import java.io.Serializable;

public class PokerInfo implements Serializable {
    private static final long serialVersionUID = 2403158786615537613L;  // Choose a unique value for this class
    private Player player;
    private String gameRes;
    private String playOrFold;
    private boolean antePlaced;
    private boolean pairPlusPlaced;
    private boolean isDealer;

    // Default constructor
    public PokerInfo() {
        this.player = new Player();
        this.gameRes = "";
        this.playOrFold = "";
        this.antePlaced = false;
        this.pairPlusPlaced = false;
        this.isDealer = false;
    }

    // Constructor with parameters
    public PokerInfo(Player player, String gameRes, String playOrFold,
                     boolean antePlaced, boolean pairPlusPlaced, boolean isDealer) {
        this.player = player;
        this.gameRes = gameRes;
        this.playOrFold = playOrFold;
        this.antePlaced = antePlaced;
        this.pairPlusPlaced = pairPlusPlaced;
        this.isDealer = isDealer;
    }

    // Getter and Setter for isDealer
    public boolean getIsDealer() {
        return isDealer;
    }

    public void setIsDealer(boolean isDealer) {
        this.isDealer = isDealer;
    }

    // Getter and Setter for player
    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    // Getter and Setter for gameRes
    public String getGameRes() {
        return gameRes;
    }

    public void setGameRes(String gameRes) {
        this.gameRes = gameRes;
    }

    // Getter and Setter for playOrFold
    public String getPlayOrFold() {
        return playOrFold;
    }

    public void setPlayOrFold(String playOrFold) {
        this.playOrFold = playOrFold;
    }

    // Getter and Setter for antePlaced
    public boolean isAntePlaced() {
        return antePlaced;
    }

    public void setAntePlaced(boolean antePlaced) {
        this.antePlaced = antePlaced;
    }

    // Getter and Setter for pairPlusPlaced
    public boolean isPairPlusPlaced() {
        return pairPlusPlaced;
    }

    public void setPairPlusPlaced(boolean pairPlusPlaced) {
        this.pairPlusPlaced = pairPlusPlaced;
    }

    @Override
    public String toString() {
        return "PokerInfo{" +
                "player=" + player +
                ", gameRes='" + gameRes + '\'' +
                ", playOrFold='" + playOrFold + '\'' +
                ", antePlaced=" + antePlaced +
                ", pairPlusPlaced=" + pairPlusPlaced +
                ", isDealer=" + isDealer +
                '}';
    }
}
