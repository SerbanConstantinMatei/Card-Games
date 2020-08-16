import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    private String name;
    private ArrayList<Card> cardList;

    public Deck(String name, ArrayList<Card> cardList) {
        this.name = name;
        this.cardList = cardList;
    }

    public void shuffle() {
        Collections.shuffle(this.cardList);
    }

    public Card drawCard() {
        if (cardList.size() > 0) {
            Card card = cardList.get(0);
            cardList.remove(0);
            return card;
        }

        return null;
    }

    Card peek() {
        if (cardList.size() > 0) {
            return cardList.get(0);
        }

        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Card> getCardList() {
        return cardList;
    }

    public void setCardList(ArrayList<Card> cardList) {
        this.cardList = cardList;
    }
}
