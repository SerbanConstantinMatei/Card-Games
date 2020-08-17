import javax.swing.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Macao extends Game {
    private Deck deck;
    private int startingHandNumber;
    private Stack<Card> cardStack;
    private Scanner keyboard;
    private int wait;
    private int draw;


    Macao(String name, int numPlayers) throws Exception {
        super(name, numPlayers);
        if (numPlayers > 8) {
            throw new Exception("Too many players!");
        }

        if (numPlayers < 2) {
            throw new Exception("Too few players!");
        }

        wait = 0;
        draw = 0;
        startingHandNumber = 5;

        cardStack = new Stack<>();
        keyboard = new Scanner(System.in);
        deck = new Deck("Main Deck", SuitedCard.getAllSuitedCards());
        deck.shuffle();
    }

    void startGame(boolean singlePlayer) {
        System.out.println("What is your name?");
        JFrame frame = new Test("Enter your name", this);
        frame.setVisible(true);
    }

    void runGame() {
        for (int i = 2; i <= numPlayers; i++) {
            players.add(new Player("Player" + " " + i, this.deck));
        }

        for (int i = 0; i < numPlayers; i++) {
            drawCards(players.get(i), startingHandNumber);
        }

        SuitedCard suitedTopCard = (SuitedCard) deck.peek();
        while (SuitedCard.checkIfAce(suitedTopCard) || SuitedCard.checkIfAddUp(suitedTopCard)
                || SuitedCard.checkIfFour(suitedTopCard)) {
            deck.shuffle();
            suitedTopCard = (SuitedCard) deck.peek();
        }

        cardStack.push(deck.drawCard());
        int playerIdx = 0;
        PrimeThread primeThread = new PrimeThread(players.get(0));
        primeThread.run();

        while (true) {
            Player currentPlayer = players.get(playerIdx);
            System.out.println("Current player is: " + currentPlayer.getName());
            sleep(1000);

            Card topCard = getTopCard();
            ArrayList<Card> playerHand = currentPlayer.getHand();
            int choice = 0;
            boolean human = playerIdx == 0;

            int playerWait = currentPlayer.getWait();
            if (playerWait > 0) {
                currentPlayer.setWait(playerWait - 1);
                playerIdx = (playerIdx + 1) % numPlayers;
                continue;
            }

            if (wait > 0) {
                playWait(human, currentPlayer);
                playerIdx = (playerIdx + 1) % numPlayers;

                if (hasWon(currentPlayer)) {
                    return;
                }

                continue;
            }

            if (draw > 0) {
                playDraw(human, currentPlayer);
                playerIdx = (playerIdx + 1) % numPlayers;

                if (hasWon(currentPlayer)) {
                    return;
                }

                continue;
            }

            if (human) {
                currentPlayer.printHand();
                System.out.println("The top card is: " + topCard.name);
                System.out.println();
                System.out.println("Type the card number that you want to play or '0' to draw a card:");
                choice = readInt(keyboard);

                while (!validCard(choice, playerHand, topCard)) {
                    System.out.println("Your choice is invalid. Please choose a valid card to play:");
                    choice = readInt(keyboard);
                }
            }
            else {
                choice = aiChoice(playerHand, topCard);
            }

            if (choice > 0) {
                Card previousTop = topCard;
                Card chosenCard = playerHand.get(choice - 1);
                playerHand.remove(choice - 1);
                cardStack.push(chosenCard);

                System.out.println(currentPlayer.getName() + " plays " + getTopCard().name);
                SuitedCard suitedCard = (SuitedCard) chosenCard;
                sleep(500);

                if (human) {
                    handleDoubleMove(currentPlayer, playerHand, getTopCard());
                }
                else {
                    sleep(1000);
                }

                playAce(suitedCard, human, playerHand, previousTop);

                if (SuitedCard.checkIfFour(suitedCard)) {
                    wait += 1;
                }

                if (SuitedCard.checkIfAddUp(suitedCard)) {
                    draw += Integer.parseInt(suitedCard.getType());
                }
            }
            else {
                correctDraw(currentPlayer, 1);
                System.out.println(currentPlayer.getName() + " drew 1 card");
                sleep(500);
                if (!human) {
                    sleep(1000);
                }
            }

            if (hasWon(currentPlayer)) {
                return;
            }

            System.out.println(currentPlayer.getName() + " has " + playerHand.size() + " cards left");
            playerIdx = (playerIdx + 1) % numPlayers;
        }
    }

    private boolean validChoice(int choice, int limit) {
        return (choice > 0 && choice <= limit);
    }

    private boolean validCard(int choice, ArrayList<Card> hand, Card topCard) {
        if (choice == 0) {
            return true;
        }

        if (!validChoice(choice, hand.size())) {
            return false;
        }

        SuitedCard suitedCard = (SuitedCard) hand.get(choice-1);
        SuitedCard suitedTopCard = (SuitedCard) topCard;

        if (suitedCard.getType().equals("A")) {
            return true;
        }

        if (suitedCard.getSuit().equals(suitedTopCard.getSuit())) {
            return true;
        }

        if (suitedCard.getType().equals(suitedTopCard.getType())) {
            return true;
        }

        return false;
    }

    private void correctDraw(Player player, int numCards) {
        int reminder = drawCards(player, numCards);
        if (reminder > 0) {
            Card topCard = getTopCard();
            cardStack.pop();

            ArrayList<Card> newDeck = new ArrayList(cardStack);
            deck.setCardList(newDeck);
            deck.shuffle();

            for (int j = 0; j < numPlayers; j++) {
                players.get(j).setDeck(deck);
            }

            cardStack.push(topCard);
            System.out.println("Reconstructed the deck!");
        }

        System.out.println(deck.getCardList().size());
        drawCards(player, reminder);
    }

    private int aiChoice(ArrayList<Card> aiHand, Card topCard) {
        int handSize = aiHand.size();
        ArrayList<Integer> validChoices = new ArrayList<>();

        for (int i = 1; i <= handSize; i++) {
            if (validCard(i, aiHand, topCard)) {
                validChoices.add(i);
            }
        }

        if (validChoices.size() == 0) {
            return 0;
        }

        Random random = new Random();
        return validChoices.get(random.nextInt(validChoices.size()));
    }

    private void handleDoubleMove(Player player, ArrayList<Card> hand, Card topCard) {
        ArrayList<Card> combos = new ArrayList<>();
        SuitedCard suitedTopCard = (SuitedCard) topCard;

        for (Card aHand : hand) {
            SuitedCard suitedCard = (SuitedCard) aHand;
            if (suitedCard.getType().equals(suitedTopCard.getType())) {
                combos.add(suitedCard);
            }
        }

        String message = "You can play an extra card or '0' to skip:";
        extraMenu(combos, message, player);
    }

    private int extraMenu(ArrayList<Card> combos, String message, Player player) {
        Card topCard = getTopCard();
        ArrayList<Card> hand = player.getHand();
        boolean firstTime = true;

        while (combos.size() > 0) {
            System.out.println(message);
            for (int i = 1; i <= combos.size(); i++) {
                System.out.println("(" + i + ") " + combos.get(i-1).name);
            }

            System.out.println("The top card is " + topCard.name);
            int choice = readInt(keyboard);

            while (!(choice >= 0 && choice <= combos.size())) {
                System.out.println("Your choice is invalid. Please choose a valid card to play:");
                choice = readInt(keyboard);
            }

            if (choice == 0) {
                if (firstTime) {
                    return -1;
                }
                else {
                    return 1;
                }
            }

            Card previousTop = topCard;
            Card chosenCard = combos.get(choice - 1);
            hand.remove(chosenCard);
            combos.remove(choice - 1);
            cardStack.push(chosenCard);
            topCard = getTopCard();
            System.out.println(player.getName() + " plays " + topCard.name);
            sleep(500);

            SuitedCard suitedCard = (SuitedCard) chosenCard;
            playAce(suitedCard, true, hand, previousTop);

            if (SuitedCard.checkIfFour(suitedCard)) {
                wait += 1;
            }

            if (SuitedCard.checkIfAddUp((suitedCard))) {
                draw += Integer.parseInt(suitedCard.getType());
            }

            firstTime = false;
        }

        return 1;
    }

    private void playDraw(boolean human, Player player) {
        String name = player.getName();
        ArrayList<Card> hand = player.getHand();
        ArrayList<Card> choices = new ArrayList<>();
        SuitedCard topCard = (SuitedCard) getTopCard();

        for (Card card : hand) {
            SuitedCard suitedCard = (SuitedCard) card;
            if (canAddUp(suitedCard, topCard)) {
                choices.add(card);
            }
        }

        if (choices.size() == 0) {
            System.out.println(name + " has to draw " + draw + " cards");
            correctDraw(player, draw);
            draw = 0;
            sleep(500);

            return;
        }

        if (human) {
            String message = "You can play a counter card or press 0 to skip:";
            System.out.println(message);
            for (int j = 1; j <= choices.size(); j++) {
                System.out.println("(" + j + ") " + choices.get(j-1).name);
            }

            int choice = readInt(keyboard);

            while (!(choice >= 0 && choice <= choices.size())) {
                System.out.println("Your choice is invalid. Please choose a valid card to play:");
                choice = readInt(keyboard);
            }

            if (choice == 0) {
                System.out.println(name + " has to draw " + draw + " cards");
                correctDraw(player, draw);
                draw = 0;

                return;
            }

            Card chosenCard = choices.get(choice - 1);
            hand.remove(chosenCard);
            choices.remove(choice - 1);
            cardStack.push(chosenCard);
            topCard = (SuitedCard) getTopCard();
            System.out.println(player.getName() + " plays " + topCard.name);
            sleep(500);

            String type = topCard.getType();
            draw += Integer.parseInt(type);
            ArrayList<Card> combos = new ArrayList<>();

            for (Card card : choices) {
                SuitedCard suitedCard = (SuitedCard) card;
                if (suitedCard.getType().equals(type)) {
                    combos.add(card);
                }
            }

            if (combos.size() != 0) {
                extraMenu(combos, message, player);
            }
        }
        else {
            ArrayList<Card> twos = new ArrayList<>();
            ArrayList<Card> threes = new ArrayList<>();

            for (Card card : choices) {
                SuitedCard suitedCard = (SuitedCard) card;
                if (SuitedCard.checkIfThree(suitedCard)) {
                    threes.add(card);
                }
                else {
                    twos.add(card);
                }
            }

            ArrayList<Card> better;
            if (twos.size() > threes.size()) {
                better = new ArrayList(twos);
            }
            else {
                better = new ArrayList(threes);
            }

            int drawNum = Integer.parseInt(((SuitedCard) better.get(0)).getType());
            int bestSuitIndex = getBestSuitIndex(aiComboSuits(better), hand);
            String bestSuit = SuitedCard.getAllSuits().get(bestSuitIndex);
            int index = 0;

            for (int i = 0; i < better.size(); i++) {
                Card card = better.get(i);
                SuitedCard suitedCard = (SuitedCard) card;
                if (!suitedCard.getSuit().equals(bestSuit)) {
                    aiCombo(card, player);
                    draw += drawNum;
                }
                else {
                    index = i;
                }
            }

            aiCombo(better.get(index), player);
            draw += drawNum;
        }
    }

    private boolean canAddUp(SuitedCard card, SuitedCard topCard) {
        if (card.getType().equals(topCard.getType())) {
            return true;
        }

        return card.getSuit().equals(topCard.getSuit()) && SuitedCard.checkIfAddUp(card);
    }

    private void playWait(boolean human, Player player) {
        String name = player.getName();
        ArrayList<Card> hand = player.getHand();
        ArrayList<Card> fours = new ArrayList<>();

        for (Card card : hand) {
            SuitedCard suitedCard = (SuitedCard) card;
            if (SuitedCard.checkIfFour(suitedCard)) {
                fours.add(card);
            }
        }

        if (fours.size() == 0) {
            if (wait > 1) {
                System.out.println(name + " has to wait " + wait + " turns");
            } else {
                System.out.println(name + " has to wait 1 turn");
            }

            player.setWait(wait - 1);
            wait = 0;
            sleep(500);

            return;
        }

        if (human) {
            String message = "You can play a counter wait card or '0' to skip:";
            int result = extraMenu(fours, message, player);
            if (result < 1) {
                player.setWait(wait - 1);
                wait = 0;
            }
        }
        else {
            int bestSuitIndex = getBestSuitIndex(aiComboSuits(fours), hand);
            String bestSuit = SuitedCard.getAllSuits().get(bestSuitIndex);
            int foursIndex = 0;

            for (int i = 0; i < fours.size(); i++) {
                Card card = fours.get(i);
                SuitedCard suitedCard = (SuitedCard) card;
                if (!suitedCard.getSuit().equals(bestSuit)) {
                    aiCombo(card, player);
                    wait += 1;
                }
                else {
                    foursIndex = i;
                }
            }

            aiCombo(fours.get(foursIndex), player);
            wait += 1;
        }
    }

    private void aiCombo(Card card, Player ai) {
        String name = ai.getName();
        ArrayList<Card> hand = ai.getHand();

        cardStack.push(card);
        Card topCard = getTopCard();
        System.out.println(name + " plays " + topCard.name);
        sleep(500);
        hand.remove(card);
    }

    private ArrayList<String> aiComboSuits (ArrayList<Card> combos) {
        if (combos.size() == 0) {
            return null;
        }

        ArrayList<String> suits = new ArrayList<>();
        for (Card card : combos) {
            SuitedCard suitedCard = (SuitedCard) card;
            suits.add(suitedCard.getSuit());
        }

        return suits;
    }

    private void playAce(SuitedCard card, boolean human, ArrayList<Card> hand, Card prev) {
        if (!SuitedCard.checkIfAce(card)) {
            return;
        }

        ArrayList<String> suits = SuitedCard.getAllSuits();
        suits.remove(((SuitedCard) prev).getSuit());
        int choice = 0;

        if (human) {
            System.out.println("Change the suit into one of the following:");
            for (int i = 1; i <= 3; i++) {
                System.out.println("(" + i + ") " + suits.get(i-1));
            }

            choice = readInt(keyboard);
            while (choice < 1 || choice > 3) {
                System.out.println("Your choice is invalid. Please choose a valid card to play:");
                choice = readInt(keyboard);
            }
        }
        else {
            choice = getBestSuitIndex(suits, hand);
        }

        System.out.println();
        System.out.println("Suit changed into " + suits.get(choice-1));
        card.setSuit(suits.get(choice-1));
        sleep(500);
    }

    private int getBestSuitIndex(ArrayList<String> suits, ArrayList<Card> hand) {
        if (suits.size() <= 0) {
            return -1;
        }

        ArrayList<Integer> count = new ArrayList<>();
        for (int i = 0; i < suits.size(); i++) {
            count.add(0);
        }

        for (Card hCard : hand) {
            SuitedCard currentCard = (SuitedCard) hCard;
            int index = suits.indexOf(currentCard.getSuit());
            if (index < 0) {
                continue;
            }

            int value = count.get(index);
            count.set(index, value);
        }

        return count.indexOf(Collections.max(count)) + 1;
    }

    private boolean hasWon(Player player) {
        if (player.getHand().size() == 0) {
            System.out.println(player.getName() + " has won!");
            return true;
        }

        return false;
    }

    private int readInt(Scanner in) {
        int result;

        try {
            result = in.nextInt();
        }
        catch (InputMismatchException e) {
            System.out.println("Enter a number!");
            in.nextLine();
            result = readInt(in);
        }

        return result;
    }

    private void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        }
        catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
    }

    public Deck getDeck() {
        return deck;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public Card getTopCard() {
        return cardStack.peek();
    }
}
