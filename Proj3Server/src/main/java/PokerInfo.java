import java.io.Serializable;
import java.util.ArrayList;

public class PokerInfo implements Serializable {

    Player player;
    String gameRes;
    String playOrFold;
    boolean antePlaced;
    boolean pairPlusPlaced;


    PokerInfo(Player player, String gameRes, String playOrFold,
              boolean antePlaced, boolean pairPlusPlaced) {

        this.player = player;
        this.gameRes = gameRes;
        this.playOrFold = playOrFold;
        this.antePlaced = antePlaced;
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
                '}';
    }

}
