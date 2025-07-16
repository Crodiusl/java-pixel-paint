package Main;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.image.BufferedImage;


public class TopTools extends JPanel implements  ChangeListener {

    JSlider slider;
    public static JCheckBox checkBox;
    Canvas panel;


    TopTools(Canvas panel) {
        this.panel  = panel;

        this.setBackground(new Color(35, 35, 35));

        this.setPreferredSize(new Dimension(800, 80));
        this.setMinimumSize(new Dimension(800, 80));
        this.setMaximumSize(new Dimension(800, 80));

        checkBox = ToolsConstructor.getGridCheckBox();
        checkBox.addChangeListener(this);
        slider = ToolsConstructor.getSlider();
        slider.addChangeListener(this);



        this.add(checkBox);
        this.add(slider);
    }



    @Override
    public void stateChanged(ChangeEvent e) {
        if(e.getSource() == slider){
            LinearHelper.newSize = slider.getValue();
        } else if(e.getSource() == checkBox) {
            Canvas.setGridFlag(checkBox.isSelected());
            panel.repaint();
        }
    }
}