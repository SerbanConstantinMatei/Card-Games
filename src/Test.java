import javax.crypto.Mac;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Test extends JFrame{
    private JPanel mainPanel;
    private JTextField textNameField;
    private JLabel label1;
    private JButton buttonOk;
    private Macao macao;

    public Test(String title, Macao macao){
        super(title);

        this.macao = macao;
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);
        this.pack();

        buttonOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = textNameField.getText();
                macao.getPlayers().add(new Player(name, macao.getDeck()));
                setVisible(false);
                macao.runGame();
            }
        });
    }
}
