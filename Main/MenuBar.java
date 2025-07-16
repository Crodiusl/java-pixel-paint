package Main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class MenuBar extends JMenuBar implements ActionListener {

    JMenuItem newItem;
    JMenuItem loadItem;
    JMenuItem saveItem;
    JMenuItem exit;

    JPanel central;
    private final String picturesPath = System.getProperty("user.home") + "\\Pictures";

    MenuBar(JPanel panel) {
        this.central = panel;

        newItem = new JMenuItem("New");
        loadItem = new JMenuItem("Load");  // items in menus
        saveItem = new JMenuItem("Save");
        exit = new JMenuItem("Exit");

        newItem.addActionListener(this);
        loadItem.addActionListener(this);
        saveItem.addActionListener(this);
        exit.addActionListener(this);

        this.add(newItem);
        this.add(loadItem);
        this.add(saveItem);
        this.add(exit);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == exit) {
            System.exit(0);
        } else if (e.getSource() == loadItem) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(picturesPath));  // or dot for project folder

            int response = fileChooser.showOpenDialog(null); // select file to open

            if (response == JFileChooser.APPROVE_OPTION) {
                File file = new File(fileChooser.getSelectedFile().getAbsolutePath());
                try {
                    BufferedImage loadedImage = ImageIO.read(file);
                    if (loadedImage == null) {
                        System.out.println("Что-то пошло не так," +
                                " возможно файл поврежден или содержит данные," +
                                " неподдерживаемые приложением");
                        System.out.println("Попробуйте пересохранить файл как png еще раз");
                        return;
                    }

                    cleanupCurrentCanvas();

                    JPanel canvasWrapper = new JPanel(new GridBagLayout());
                    canvasWrapper.setBackground(new Color(35, 35, 35));

                    Canvas canvas = new Canvas(loadedImage.getWidth(), loadedImage.getHeight(), loadedImage);
                    canvasWrapper.add(canvas);

                    JPanel cTpWrapper = new JPanel(new BorderLayout());
                    cTpWrapper.add(new TopTools(canvas), BorderLayout.NORTH);
                    cTpWrapper.add(canvasWrapper);

                    central.addKeyListener(canvas.getKeyListeners()[0]);

                    central.add(new LeftTools(canvas), BorderLayout.WEST);
                    central.add(new RightTools(canvas), BorderLayout.EAST);
                    central.add(cTpWrapper);

                    central.revalidate();

                    System.out.println("Image uploaded");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

        } else if (e.getSource() == saveItem) {

            BufferedImage bimg = Canvas.getImage();

            bimg = bimg.getSubimage(0, 0, bimg.getWidth(), bimg.getHeight());
            assert bimg.getHeight() > 0;
            assert bimg.getWidth() > 0;

            String name = JOptionPane.showInputDialog("Enter file name");
            if (name == null || name.isBlank()) name = "placeholder";

            File imgfile = new File(picturesPath + "/" + name + ".png");
            try {
                ImageIO.write(bimg, "png", imgfile);
                System.out.println("Image saved");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            assert imgfile.length() > 0;

        } else {
            cleanupCurrentCanvas();

            central.add(new ConstructPanel());
            central.revalidate();

        }

    }


    private void cleanupCurrentCanvas() {
        // Находим все компоненты с центральной панели
        Component[] components = central.getComponents();

        for (Component c : components) {
            if (c instanceof JPanel) {
                // Ищем Canvas внутри панелей
                findAndCleanupCanvas((JPanel) c);
            }
        }
        if (central.getKeyListeners().length != 0) {
            central.removeKeyListener(central.getKeyListeners()[0]);
        }

        central.removeAll();
    }

    private void findAndCleanupCanvas(JPanel panel) {
        for (Component c : panel.getComponents()) {
            if (c instanceof Canvas) {
                ((Canvas) c).cleanup();
                System.gc();

            } else if (c instanceof JPanel) {
                findAndCleanupCanvas((JPanel) c);
            }
        }
    }
}
