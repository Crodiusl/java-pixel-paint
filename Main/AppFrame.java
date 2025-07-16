package Main;

import javax.swing.*;
import java.awt.*;

public class AppFrame extends JFrame {
    AppFrame() {
        this.setTitle("Wizard Paint");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel centerPanel = new JPanel(new BorderLayout());

        ConstructPanel constructPanel = new ConstructPanel();

        centerPanel.add(constructPanel, BorderLayout.CENTER);


        mainPanel.add(centerPanel, BorderLayout.CENTER);

        ImageIcon image = new ImageIcon("src//Assets//wizard.png");

        centerPanel.setFocusable(true);

        this.setJMenuBar(new MenuBar(centerPanel));

        this.add(mainPanel);
        this.pack();
        this.setResizable(true);
        this.setLocationRelativeTo(null);
        this.setIconImage(image.getImage());
        this.setVisible(true);

    }
}
