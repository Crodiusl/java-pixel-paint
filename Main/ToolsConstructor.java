package Main;


import javax.swing.*;
import java.awt.*;

public class ToolsConstructor {

    static String[] assetsNames = {
            "Pencil",
            "Eraser",
            "Drag",
            "Line",
            "Ellipse",
            "Circle",
            "Square",
            "Shade",
            "Picker",
            "Fill",
    };

    static String[] sizes = {
            "16x16",
            "32x32",
            "64x64",
            "100x100",
            "128x128",
            "256x144",
            "500x500",
            "CUSTOM"
    };


    public static JButton[] getLeftButtons() {
        JButton[] buttons = new JButton[10];

        for (int i = 0; i < buttons.length; i++) {

            buttons[i] = new JButton();
            buttons[i].setFocusable(false);
            buttons[i].setToolTipText(assetsNames[i]);

            buttons[i].setBackground(new Color(151, 148, 151));

            ImageIcon icon = new ImageIcon("src//Assets//" + assetsNames[i].toLowerCase() + ".png");
            buttons[i].setIcon(icon);
        }

        return buttons;
    }

    public static JSlider getSlider() {
        JSlider slider = new JSlider(1, 10, 1);

        slider.setPreferredSize(new Dimension(300, 60));
        slider.setBackground(new Color(35, 35, 35));
        slider.setForeground(Color.WHITE);
        slider.setPaintTicks(true); // making ticks visible
        slider.setMinorTickSpacing(1); // how often

        slider.setPaintTrack(true); // making big tracks visible default - true
        slider.setMajorTickSpacing(1); // how often big ones

        slider.setPaintLabels(true); //labels go to Major ticks
        slider.setFocusable(false);
        return slider;
    }

    public static JCheckBox getGridCheckBox(){
        JCheckBox checkBox = new JCheckBox();
        checkBox.setBackground(new Color(35, 35, 35));
        checkBox.setForeground(Color.WHITE);
        checkBox.setText("Grid");
        checkBox.setFont(new Font("",Font.PLAIN,20));
        checkBox.setFocusable(false);

        return checkBox;
    }



    public static JButton[] getSizeButtons(){
        JButton[] buttons = new JButton[8];

        for (int i = 0; i < buttons.length; i++) {

            buttons[i] = new JButton();
            buttons[i].setFocusable(false);
            buttons[i].setPreferredSize(new Dimension(100,100));
            buttons[i].setToolTipText(sizes[i]);

            buttons[i].setBackground(new Color(151, 148, 151));

            buttons[i].setText(sizes[i]);
        }

        return buttons;
    }


}
