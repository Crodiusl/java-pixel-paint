package Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RightTools extends JPanel {
    public static JLabel ColorPanel;
    public static JLabel coordinates;
    Canvas canvas;

    RightTools(Canvas canvas) {
        this.canvas = canvas;

        this.setBackground(new Color(35, 35, 35));
        this.setPreferredSize(new Dimension(200, 800));

        ColorPanel = new JLabel();
        ColorPanel.setPreferredSize(new Dimension(150, 150));
        ColorPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        ColorPanel.setOpaque(true);
        ColorPanel.setBackground(canvas.currentColor);

        this.add(ColorPanel);


        coordinates = new JLabel(String.format("X:%3d  Y:%3d", 0, 0));
        coordinates.setFont(new Font(Font.MONOSPACED, Font.BOLD, 25));
        coordinates.setForeground(Color.WHITE);

        JPanel basicColorsPanel = createBasicColorsPanel();
        this.add(basicColorsPanel);

        JButton colorWheelBtn = new JButton("Color Wheel");
        colorWheelBtn.setFocusable(false);

        colorWheelBtn.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(null, "Choose Color", canvas.getCurrentColor());
            if (newColor != null) {
                setCurrentColor(newColor);
            }
        });

        this.add(colorWheelBtn);

        this.add((coordinates));
    }


    private JPanel createBasicColorsPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 4, 5, 5));

        panel.setBorder(BorderFactory.createTitledBorder("Basic Colors"));

        Color[] basicColors = {
                Color.BLACK, Color.WHITE, Color.RED, Color.GREEN,
                Color.BLUE, Color.YELLOW, Color.CYAN, Color.MAGENTA,
                Color.ORANGE, Color.PINK, Color.GRAY, Color.DARK_GRAY,
                Color.LIGHT_GRAY, new Color(122, 61, 17),
                new Color(128, 0, 128),
                new Color(0, 128, 0)
        };

        for (Color color : basicColors) {
            JButton colorBtn = new JButton();
            colorBtn.setBackground(color);
            colorBtn.setPreferredSize(new Dimension(30, 30));
            colorBtn.addActionListener(e -> setCurrentColor(color));
            colorBtn.setFocusable(false);

            panel.add(colorBtn);

        }

        return panel;
    }


    public void setCurrentColor(Color newColor) {
        canvas.setCurrentColor(newColor);
        ColorPanel.setBackground(newColor);
    }

}