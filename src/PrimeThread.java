public class PrimeThread extends Thread {
    private Player player;

    PrimeThread(Player player) {
        this.player = player;
    }

    public void run() {
        PictureTest pictureTest = new PictureTest(player.getHand());
    }
}
