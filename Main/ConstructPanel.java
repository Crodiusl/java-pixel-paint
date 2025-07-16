package Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class ConstructPanel extends JPanel implements ActionListener {

    JButton[] buttons;
    Canvas canvas;


    ConstructPanel () {

        this.setPreferredSize(new Dimension(1300, 900));
        this.setBackground(new Color(35, 35, 35));
        this.setLayout(new GridLayout(0,4,5,5));

        buttons = ToolsConstructor.getSizeButtons();

        for(JButton b : buttons){
            b.addActionListener(this);
            this.add(b);
        }


    }





    @Override
    public void actionPerformed(ActionEvent e) {
        for (int i = 0; i < buttons.length; i++) {
            if (e.getSource() == buttons[i]) {
                int width;
                int height;
                if(i == buttons.length-1){
                    JTextField xField = new JTextField(5);
                    JTextField yField = new JTextField(5);

                    JPanel myPanel = new JPanel();
                    myPanel.add(new JLabel("width:"));
                    myPanel.add(xField);
                    myPanel.add(Box.createHorizontalStrut(15)); // a spacer
                    myPanel.add(new JLabel("height:"));
                    myPanel.add(yField);

                    int result = JOptionPane.showConfirmDialog(null, myPanel,
                            "Enter width and height values", JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        try {
                            width = Math.min(1024,Integer.parseInt(xField.getText()));
                            height = Math.min(1024,Integer.parseInt(yField.getText()));
                        } catch (NumberFormatException er){
                            width = 64;
                            height = 64;
                            System.out.println(er);
                            System.out.println("width: 64 height: 64");
                        }

                    } else {
                        return;
                    }

                } else {
                    String[] dimensions = ToolsConstructor.sizes[i].split("x");
                    width = Integer.parseInt(dimensions[0]);
                    height = Integer.parseInt(dimensions[1]);
                }


                BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

                Container central = this.getParent();

                Component[] components = central.getComponents();
                for (Component c : components) {
                    if (c instanceof JPanel) {
                        findAndCleanupCanvas((JPanel) c);
                    }
                }
                if(central.getKeyListeners().length !=0){
                    central.removeKeyListener(central.getKeyListeners()[0]);
                }
                central.removeAll();

                JPanel canvasWrapper = new JPanel(new GridBagLayout());
                canvasWrapper.setBackground(new Color(35, 35, 35));

                Canvas canvas = new Canvas(width, height, newImage);
                this.canvas = canvas;
                canvasWrapper.add(canvas);

                JPanel cTpWrapper = new JPanel(new BorderLayout());
                cTpWrapper.add(new TopTools(canvas), BorderLayout.NORTH);
                cTpWrapper.add(canvasWrapper);

                central.addKeyListener(canvas.getKeyListeners()[0]);
                central.add(new LeftTools(canvas), BorderLayout.WEST);
                central.add(new RightTools(canvas), BorderLayout.EAST);
                central.add(cTpWrapper);

                central.revalidate();
                central.repaint();

                break;
            }
        }
    }

    public Canvas getCanvas() {
        return canvas;
    }
    private void findAndCleanupCanvas(JPanel panel) {
        for (Component c : panel.getComponents()) {
            if (c instanceof Canvas) {
                ((Canvas) c).cleanup();
            } else if (c instanceof JPanel) {
                findAndCleanupCanvas((JPanel) c);
            }
        }
    }

}
