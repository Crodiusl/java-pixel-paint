package Main;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class Canvas extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {

    public int CANVAS_WIDTH = 800;
    public int CANVAS_HEIGHT = 800;

    public int WIDTH;
    public int HEIGHT;

    public double PIXEL_SIZE;
    Color currentColor = Color.BLACK;

    BufferedImage background;

    public static BufferedImage image; // static - временное решение (и плохое)
    BufferedImage tempImage;

    public static boolean isGridOn; // работает не как надо, пока static

    public Stack<HashMap<Point, Integer>> undo = new Stack<>();
    public Stack<HashMap<Point, Integer>> redo = new Stack<>();


    Command command = Command.PENCIL;

    DrawingMethods drawingMethods;
    LinearHelper linearHelper;
    ArrayList<Point> currentFigurePoints = new ArrayList<>();
    Point worldStart;
    Point dragStart;
    Point prevPoint;
    Point samePoint;

    double zoomFactor = 1.0;
    Point zoomOrigin = new Point(0, 0);


    HashMap<Point, Integer> dirtAreas = new HashMap<>();

    Canvas(int width, int height, BufferedImage newImage) {

        this.WIDTH = width;
        this.HEIGHT = height;
        image = newImage;

        PIXEL_SIZE = (WIDTH >= HEIGHT ? (double) CANVAS_WIDTH / WIDTH : (double) CANVAS_HEIGHT / HEIGHT);

        if (WIDTH != HEIGHT) {
            CANVAS_WIDTH = (int) (WIDTH * PIXEL_SIZE);
            CANVAS_HEIGHT = (int) (HEIGHT * PIXEL_SIZE);
        }

        tempImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);

        background = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        drawBackGround();

        drawingMethods = new DrawingMethods(image, PIXEL_SIZE, this);
        linearHelper = new LinearHelper(image, PIXEL_SIZE, this);

        this.setBackground(Color.WHITE);
        this.setFocusable(true);


        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
        this.addKeyListener(new myKeyAdapter());

        this.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
        this.setMinimumSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
        this.setMaximumSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
    }


    public void paintComponent(Graphics g) {
        Graphics2D g2D = null;
        try {
            g2D = (Graphics2D) g;

            super.paintComponent(g2D);

            g2D.translate(zoomOrigin.x, zoomOrigin.y);
            g2D.scale(zoomFactor, zoomFactor);

            g2D.drawImage(background, 0, 0,
                    (int) (background.getWidth() * PIXEL_SIZE), (int) (background.getHeight() * PIXEL_SIZE), null);

            g2D.drawImage(image, 0, 0,
                    (int) (image.getWidth() * PIXEL_SIZE), (int) (image.getHeight() * PIXEL_SIZE), null);

            g2D.drawImage(tempImage, 0, 0,
                    (int) (tempImage.getWidth() * PIXEL_SIZE), (int) (tempImage.getHeight() * PIXEL_SIZE), null);

            if (isGridOn) {

                for (int i = 0; i < CANVAS_WIDTH / PIXEL_SIZE; i++) {
                    g2D.drawLine((int) (i * PIXEL_SIZE), 0, (int) (i * PIXEL_SIZE), CANVAS_HEIGHT);
                }
                for (int i = 0; i < CANVAS_HEIGHT / PIXEL_SIZE; i++) {
                    g2D.drawLine(0, (int) (i * PIXEL_SIZE), CANVAS_WIDTH, (int) (i * PIXEL_SIZE));
                }

            }

        } finally {
            if (g2D != null) {
                g2D.dispose();
            }

        }

    }


    @Override
    public void mousePressed(MouseEvent e) {


        switch (command) {
            case PENCIL: // pencil
                drawingMethods.drawPixel(e.getX(), e.getY(), image, e, currentColor);
                break;
            case DRAG: //drag
                dragStart = e.getPoint();
                break;
            case LINE, ELLIPSE, CIRCLE, RECT:
                worldStart = new Point(e.getX(), e.getY());
                currentFigurePoints.clear();
                break;
            case COLOR_PICKER: //picker
                currentColor = drawingMethods.getColor(image, e, currentColor);
                break;
            case ERASER: // eraser
                drawingMethods.eraser(e.getX(), e.getY(), image);
                break;
            case SHADE: // shade
                drawingMethods.setShade(image, e, false, prevPoint);
                break;
            case FILL: // fill
                drawingMethods.fill(e.getX(), e.getY(), image, currentColor);
                break;
        }

        repaint();
    }


    @Override
    public void mouseReleased(MouseEvent e) {

        prevPoint = null; // сброс точки
        samePoint = null;

        Graphics2D g = null;
        Graphics2D gTemp = null;
        try {
            g = image.createGraphics();
            g.drawImage(tempImage, 0, 0, null);

            gTemp = tempImage.createGraphics();
            gTemp.setComposite(AlphaComposite.Clear);
            gTemp.fillRect(0, 0, tempImage.getWidth(), tempImage.getHeight());
        } finally {
            if (g != null) g.dispose();
            if (gTemp != null) gTemp.dispose();
        }

        currentFigurePoints.clear();


        if (undo.size() > 100) undo.removeFirst();
        undo.push(new HashMap<>(dirtAreas));
        redo.clear();
        dirtAreas.clear();
    }


    @Override
    public void mouseDragged(MouseEvent e) {
        Point p = linearHelper.screenToWorld(e.getX(), e.getY());
        RightTools.coordinates.setText(String.format("X:%3d  Y:%3d", p.x, p.y));

        if (p.equals(samePoint) && command != Command.DRAG) return;
        samePoint = p;

        switch (command) {
            case PENCIL: // pencil
                drawingMethods.drawCursive(prevPoint, e.getPoint(), image, currentColor, e);
                break;
            case DRAG: //drag
                drawingMethods.dragMethod(e, dragStart, zoomFactor, zoomOrigin);
                break;
            case LINE, ELLIPSE, CIRCLE, RECT:
                drawingMethods.drawFigure(worldStart, e.getPoint(), tempImage, currentColor, command, currentFigurePoints);
                break;
            case ERASER: // eraser
                drawingMethods.eraserCursive(prevPoint, e.getPoint(), image);
                break;
            case SHADE: // shade
                drawingMethods.setShade(image, e, true, prevPoint);
                break;
        }
        if (command != Command.SHADE) {
            prevPoint = e.getPoint();
        }
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Point p = linearHelper.screenToWorld(e.getX(), e.getY());
        RightTools.coordinates.setText(String.format("X:%3d  Y:%3d", p.x, p.y));
    }


    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        double MAX_ZOOM = PIXEL_SIZE > 10 ? PIXEL_SIZE - 10 : PIXEL_SIZE + 10;

        double MIN_ZOOM = 1;
        Point mousePos = e.getPoint();
        double worldX = (mousePos.x - zoomOrigin.x) / zoomFactor;
        double worldY = (mousePos.y - zoomOrigin.y) / zoomFactor;

        double zoomChange = 1.0 - e.getWheelRotation() * 0.1;
        double newZoom = zoomFactor * zoomChange;
        newZoom = Math.max(MIN_ZOOM, Math.min(newZoom, MAX_ZOOM));


        // Вычисляем новые координаты центра
        int newOriginX = mousePos.x - (int) (worldX * newZoom);
        int newOriginY = mousePos.y - (int) (worldY * newZoom);

        // Ограничиваем смещения
        int maxOffsetX = (int) (CANVAS_WIDTH * zoomFactor) - CANVAS_WIDTH;
        int maxOffsetY = (int) (CANVAS_HEIGHT * zoomFactor) - CANVAS_HEIGHT;

        zoomOrigin.x = Math.min(0, Math.max(-maxOffsetX, newOriginX));
        zoomOrigin.y = Math.min(0, Math.max(-maxOffsetY, newOriginY));

        zoomFactor = newZoom;
        repaint();
    }

    public class myKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {

            switch (e.getKeyCode()) {
                case 81: //Q
                    Color newColor = JColorChooser.showDialog(null, "", Color.BLACK);
                    currentColor = newColor;
                    RightTools.ColorPanel.setBackground(newColor);
                    break;
                case 49: //1
                    command = Command.PENCIL;
                    System.out.println("Pencil");
                    break;
                case 50: //2
                    command = Command.DRAG;
                    System.out.println("Drag");
                    break;
                case 51: //3
                    command = Command.LINE;
                    System.out.println("Line");
                    break;
                case 52: //4
                    command = Command.ELLIPSE;
                    System.out.println("Ellipse");
                    break;
                case 53: //5
                    command = Command.CIRCLE;
                    System.out.println("Perfect Circle");
                    break;
                case 54: //6
                    command = Command.RECT;
                    System.out.println("Rect");
                    break;
                case 55: //7
                    command = Command.FILL;
                    System.out.println("Fill");
                    break;
                case 69: //E reset size
                    zoomFactor = 1;
                    zoomOrigin = new Point(0, 0);
                    repaint();
                    break;
                case 82: //R reset canvas
                    drawingMethods.resetCanvas(image);
                    repaint();
                    System.gc();
                    break;
                case 71: //G
                    System.out.println(isGridOn);
                    isGridOn = !isGridOn;
                    TopTools.checkBox.setSelected(!TopTools.checkBox.isSelected());
                    repaint();
                    break;
                case 67: //C
                    command = Command.COLOR_PICKER;
                    System.out.println("Color Picker");
                    break;
                case 83: //S
                    command = Command.SHADE;
                    System.out.println("Shade");
                    break;
                case 48: //0
                    command = Command.ERASER;
                    System.out.println("Eraser");
                    break;
                case 90: //z
                    if (e.isControlDown() && !undo.isEmpty()) {

                        doUndoRedo(undo, redo);
                        System.out.println("ctrl+z");
                    }
                    break;
                case 89: // y
                    if (e.isControlDown() && !redo.isEmpty()) {

                        doUndoRedo(redo, undo);
                        System.out.println("ctrl+y");
                    }
                    break;
            }
        }
    }


    private void doUndoRedo(Stack<HashMap<Point, Integer>> stackToWork, Stack<HashMap<Point, Integer>> stackToAddTo) {
        final byte MAX_UNDO_REDO_STEPS = 100;
        if (stackToWork.isEmpty()) return;
        HashMap<Point, Integer> currentMap = stackToWork.pop();
        for (Point p : currentMap.keySet()) {
            dirtAreas.put(p, image.getRGB(p.x, p.y));
            image.setRGB(p.x, p.y, currentMap.get(p));
        }

        if (stackToAddTo.size() > MAX_UNDO_REDO_STEPS) stackToAddTo.removeFirst();
        stackToAddTo.push(new HashMap<>(dirtAreas));
        dirtAreas.clear();

        repaint();
    }

    private void drawBackGround() {
        Graphics2D g2d = null;
        try {
            g2d = background.createGraphics();
            Color lightGray = new Color(211, 211, 211);
            for (int x = 0; x < background.getWidth(); x++) {
                for (int y = 0; y < background.getHeight(); y++) {
                    g2d.setColor((x + y) % 2 == 0 ? lightGray : Color.WHITE);
                    g2d.fillRect(x, y, 1, 1);
                }
            }
        } finally {
            if (g2d != null) {
                g2d.dispose();
            }
        }
    }

    public Color getCurrentColor() {
        return currentColor;
    }

    public void setCurrentColor(Color color) {
        this.currentColor = color;
        RightTools.ColorPanel.setBackground(color); // Update UI
    }


    public void setCommand(Command cmd) {
        this.command = cmd;
    }


    public int getCanvasWidth() {
        return CANVAS_WIDTH;
    }

    public int getCanvasHeight() {
        return CANVAS_HEIGHT;
    }

    public double getZoomFactor() {
        return zoomFactor;
    }

    public Point getZoomOrigin() {
        return zoomOrigin;
    }

    public void setDragStart(Point newDragStart) {
        dragStart = newDragStart;
    }

    public static void setGridFlag(boolean b) {
        isGridOn = b;
    }


    public void cleanup() {
        this.removeMouseListener(this);
        this.removeMouseMotionListener(this);
        this.removeMouseWheelListener(this);

        for (KeyListener kl : this.getKeyListeners()) {
            this.removeKeyListener(kl);
        }

        if (image != null) {
            image.flush();
            image = null;
        }
        if (tempImage != null) {
            tempImage.flush();
            tempImage = null;
        }
        if (background != null) {
            background.flush();
            background = null;
        }

        undo.clear();
        redo.clear();

        LinearHelper.newSize = 1;
        isGridOn = false;

        drawingMethods = null;
        linearHelper = null;
        currentFigurePoints = null;
        System.out.println("Canvas cleaned");
    }

    public static BufferedImage getImage() {
        return image;
    }

    // не используется
    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
