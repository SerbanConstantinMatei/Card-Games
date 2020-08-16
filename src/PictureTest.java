import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class PictureTest extends JFrame {
    private ArrayList<Card> hand;
    private int height;
    private int width;
    private ArrayList<JLabel> labels;
    private MouseHandler mouseHandler;

    public PictureTest(ArrayList<Card> hand) {
        setLayout(new FlowLayout());
        this.hand = hand;
        this.height = 150;
        this.width = 100;
        this.mouseHandler = new MouseHandler();
        this.labels = new ArrayList<>();

        update(this.hand);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();

        setVisible(true);
    }

    public void update(ArrayList<Card> hand) {
        /*Component[] components = getComponents();
        for (Component component : components) {
            remove(component);
        }

        revalidate();
        repaint();
        */
        for (Card card : hand) {
            SuitedCard suitedCard = (SuitedCard) card;
            BufferedImage img = null;
            try {
                img = ImageIO.read(new File(suitedCard.getSourceFile()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            Image dimg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            JLabel jLabel = new JLabel(new ImageIcon(dimg));
            jLabel.addMouseListener(mouseHandler);
            labels.add(jLabel);
            add(jLabel);
        }
    }

    private class MouseHandler extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent evt) {
            JLabel source = (JLabel) evt.getSource();
            for (Iterator<JLabel> iterator = labels.iterator(); iterator.hasNext(); ) {
                JLabel label = iterator.next();
                if (label == source) {
                    remove(label);
                    revalidate();
                    repaint();
                    iterator.remove();
                }
            }
        }
    }
}
