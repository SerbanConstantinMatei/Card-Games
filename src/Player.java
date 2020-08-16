import java.util.ArrayList;

public class Player {
    private String name;
    private Deck deck;
    private ArrayList<Card> hand;
    private int wait;

    public Player(String name, Deck deck) {
        this.name = name;
        this.deck = deck;
        this.hand = new ArrayList<>();
        this.wait = 0;
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    public void setHand(ArrayList<Card> hand) {
        this.hand = hand;
    }

    public void addCardToHand(Card card) {
        this.hand.add(card);
    }

    public void printHand() {
        System.out.println();
        System.out.println(name + ", you have:");
        int n = hand.size();
        for (int j = 1; j <= n; j++) {
            System.out.println("(" + j + ") " + hand.get(j-1).name);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Deck getDeck() {
        return deck;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public int getWait() {
        return wait;
    }

    public void setWait(int wait) {
        this.wait = wait;
    }
}
