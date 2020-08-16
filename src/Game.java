import java.util.ArrayList;

public class Game {
    String name;
    int numPlayers;
    ArrayList<Player> players;

    public Game(String name, int numPlayers) {
        this.name = name;
        this.numPlayers = numPlayers;
        this.players = new ArrayList<>();
    }

    public int drawCards(Player player, int numCards) {
        for (int i = 0; i < numCards; i++) {
            Card newCard = player.getDeck().drawCard();
            if (newCard == null) {
                return numCards - i;
            }

            player.addCardToHand(newCard);
        }

        return 0;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }
}
