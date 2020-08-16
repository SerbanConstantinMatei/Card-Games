import java.util.ArrayList;
import java.util.Arrays;

public class SuitedCard extends Card {
    private String suit;
    private String type;
    private String sourceFile;

    public SuitedCard(String name, String suit, String type) {
        super(name);
        this.suit = suit;
        this.type = type;
        this.sourceFile = "img/" + type + suit.toUpperCase().charAt(0) + ".png";
    }

    public String getSuit() {
        return suit;
    }

    public void setSuit(String suit) {
        this.suit = suit;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSourceFile() { return sourceFile; }

    static ArrayList<Card> getAllSuitedCards() {
        ArrayList<Card> allCards = new ArrayList<>();
        ArrayList<String> suits = getAllSuits();

        ArrayList<String> types = new ArrayList<String>(
                Arrays.asList("2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"));

        int i, j, n = types.size(), m = suits.size();

        for (i = 0; i < n; i++) {
            for (j = 0; j < m; j++) {
                String name = types.get(i) + " of " + suits.get(j);
                SuitedCard newCard = new SuitedCard(name, suits.get(j), types.get(i));
                allCards.add(newCard);
            }
        }

        return allCards;
    }

    static boolean checkIfAce(SuitedCard card) {
        return card.getType().equals("A");
    }

    static boolean checkIfFour(SuitedCard card) {
        return card.getType().equals("4");
    }

    static boolean checkIfThree(SuitedCard card) {
        return card.getType().equals("3");
    }

    static boolean checkIfTwo(SuitedCard card) {
        return card.getType().equals("2");
    }

    static boolean checkIfAddUp(SuitedCard card) {
        return checkIfTwo(card) || checkIfThree(card);
    }

    static ArrayList<String> getAllSuits() {
        return new ArrayList<>(Arrays.asList("hearts", "diamonds", "spades", "clubs"));
    }
}
