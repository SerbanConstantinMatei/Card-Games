import java.util.ArrayList;
import java.util.Collections;

public class Main {
    public static void main(String[] args) {
        Game myGame = null;
        try {
            myGame = new Macao("Macao", 4);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        ((Macao) myGame).startGame(true);
        /*
        ArrayList<Card> allCards = SuitedCard.getAllSuitedCards();
        Collections.shuffle(allCards);
        ArrayList<Card> cards = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            cards.add(allCards.get(i));
        }
        PictureTest pictureTest = new PictureTest(cards);
        */
    }
}
