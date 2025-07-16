package Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LeftTools extends JPanel implements ActionListener {
    JButton[]  buttons = ToolsConstructor.getLeftButtons();
    Canvas canvas;

    LeftTools(Canvas canvas) {
        this.canvas = canvas;
        this.setBackground(Color.WHITE);
        this.setFocusable(true);

        this.setLayout(new GridLayout(5,2,5,5));

        for (JButton button : buttons) {

            button.addActionListener(this);
            this.add(button);
        }

        this.setBackground(new Color(35, 35, 35));
        this.setPreferredSize(new Dimension(200, 800));
    }



    @Override
    public void actionPerformed(ActionEvent e) {
        Command command = Command.PENCIL;

        if (e.getSource() == buttons[0]) {
            command = Command.PENCIL;
            System.out.println("Pencil");
        } else if (e.getSource() == buttons[1]) {
            command = Command.ERASER;
            System.out.println("Eraser");
        } else if (e.getSource() == buttons[2]) {
            command = Command.DRAG;
            System.out.println("Drag");
        } else if (e.getSource() == buttons[3]) {
            command = Command.LINE;
            System.out.println("Line");
        } else if (e.getSource() == buttons[4]) {
            command = Command.ELLIPSE;
            System.out.println("Ellipse");
        } else if (e.getSource() == buttons[5]) {
            command = Command.CIRCLE;
            System.out.println("Perfect Circle");
        } else if (e.getSource() == buttons[6]) {
            command = Command.RECT;
            System.out.println("Rect");
        } else if (e.getSource() == buttons[7]) {
            command = Command.SHADE;
            System.out.println("Shade");
        } else if (e.getSource() == buttons[8]) {
            command = Command.COLOR_PICKER;
            System.out.println("Color Picker");
        } else if (e.getSource() == buttons[9]) {
            command = Command.FILL;
            System.out.println("Fill");
        }

        canvas.setCommand(command);

    }
}
