package Main;


import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class DrawingMethods {
    LinearHelper linearHelper;
    Canvas canvas;

    DrawingMethods(BufferedImage image, double PIXEL_SIZE, Canvas canvas) {
        this.canvas = canvas;
        linearHelper = new LinearHelper(image, PIXEL_SIZE, canvas);

    }

    public void drawPixel(int mouseX, int mouseY, BufferedImage image, MouseEvent e, Color color) {
        Point p = linearHelper.screenToWorld(mouseX, mouseY);

        if (e.getModifiersEx() == MouseEvent.BUTTON1_DOWN_MASK) {
            linearHelper.expandPixels(p, color.getRGB(), image);

        } else if (e.getModifiersEx() == MouseEvent.BUTTON3_DOWN_MASK) {
            linearHelper.expandPixels(p, 0x00000000, image);
        }
    }

    public void eraser(int mouseX, int mouseY, BufferedImage image) {
        Point p = linearHelper.screenToWorld(mouseX, mouseY);

        linearHelper.expandPixels(p, 0x00000000, image);
    }

    public Color getColor(BufferedImage image, MouseEvent e, Color color) {
        Point p = linearHelper.screenToWorld(e.getX(), e.getY());

        if (image.getRGB(p.x, p.y) == 0) return color;

        color = new Color(image.getRGB(p.x, p.y));
        RightTools.ColorPanel.setBackground(color);
        return color;
    }

    public void setShade(BufferedImage image, MouseEvent e, boolean isDragging, Point prevPoint) {
        Point p = linearHelper.screenToWorld(e.getX(), e.getY());

        if (p.equals(prevPoint) && isDragging) return;
        canvas.prevPoint = p;

        if (image.getRGB(p.x, p.y) == 0) return;

        Color newColor = new Color(image.getRGB(p.x, p.y));
        if (e.getModifiersEx() == MouseEvent.BUTTON1_DOWN_MASK) {
            newColor = newColor.brighter();
        } else if (e.getModifiersEx() == MouseEvent.BUTTON3_DOWN_MASK) {
            newColor = newColor.darker();
        }
        linearHelper.expandPixels(p, newColor.getRGB(), image);


    }


    public void dragMethod(MouseEvent e, Point dragStart, double zoomFactor, Point zoomOrigin) {

        Point dragEnd = e.getPoint();
        int dx = dragEnd.x - dragStart.x;
        int dy = dragEnd.y - dragStart.y;

        // Вычисляем потенциальные новые координаты
        int newOriginX = zoomOrigin.x + dx;
        int newOriginY = zoomOrigin.y + dy;

        // Рассчитываем максимальные смещения с учетом текущего масштаба
        int maxOffsetX = (int) (canvas.getCanvasWidth() * zoomFactor) - canvas.getCanvasWidth();
        int maxOffsetY = (int) (canvas.getCanvasHeight() * zoomFactor) - canvas.getCanvasHeight();

        // Ограничиваем смещения
        zoomOrigin.x = Math.min(0, Math.max(-maxOffsetX, newOriginX));
        zoomOrigin.y = Math.min(0, Math.max(-maxOffsetY, newOriginY));

        canvas.setDragStart(dragEnd);
    }


    public void drawCursive(Point prevPoint, Point currPoint, BufferedImage image, Color color, MouseEvent e) {
        if (prevPoint == null) return;
        ArrayList<Point> linePoints = linearHelper.getBresenhamLine(prevPoint, currPoint);
        for (Point p : linePoints) {

            if (e.getModifiersEx() == MouseEvent.BUTTON1_DOWN_MASK) {
                linearHelper.expandPixels(p, color.getRGB(), image);
            } else if (e.getModifiersEx() == MouseEvent.BUTTON3_DOWN_MASK) {
                linearHelper.expandPixels(p, 0x00000000, image);
            }
        }
        linePoints.clear();

    }

    public void drawFigure(Point worldStart, Point worldCurrent, BufferedImage tempImage, Color color, Command command, ArrayList<Point> currentFigurePoints) {
        clearPixels(currentFigurePoints, tempImage);

        ArrayList<Point> newFigure = switch (command) {
            case LINE -> linearHelper.getBresenhamLine(worldStart, worldCurrent);
            case ELLIPSE -> linearHelper.getBresenhamEllipse(worldStart, worldCurrent);
            case CIRCLE -> linearHelper.getBresenhamPerfectCircle(worldStart, worldCurrent);
            case RECT -> linearHelper.getRect(worldStart, worldCurrent);
            default -> null;
        };


        for (Point p : newFigure) {
            if (p.x < 0 || p.x > tempImage.getWidth() || p.y < 0 || p.y > tempImage.getHeight()) continue;
            linearHelper.expandPixels(p, color.getRGB(), tempImage);
        }

        canvas.currentFigurePoints = newFigure;
    }

    private void clearPixels(ArrayList<Point> points, BufferedImage tempImage) {
        for (Point p : points) {

            if (p.x < 0 || p.x > tempImage.getWidth() || p.y < 0 || p.y > tempImage.getHeight()) continue;
            linearHelper.expandPixels(p, 0x00000000, tempImage);

        }
    }

    public void eraserCursive(Point prevPoint, Point currPoint, BufferedImage image) {
        if (prevPoint == null) return;
        ArrayList<Point> linePoints = linearHelper.getBresenhamLine(prevPoint, currPoint);

        for (Point p : linePoints) {
            linearHelper.expandPixels(p, 0x00000000, image);

        }
    }

    public void fill(int x, int y, BufferedImage image, Color replacementColor) {
        Point p = linearHelper.screenToWorld(x, y);

        int targetRGB = image.getRGB(p.x, p.y);

        if (targetRGB == replacementColor.getRGB()) return;
        floodFill(p, targetRGB, replacementColor, image);
    }

    private void floodFill(Point startPoint, int targetRGB, Color replacementColor, BufferedImage image) {
        Queue<Point> queue = new LinkedList<>();
        queue.add(startPoint);

        int[] dx = {0, 0, -1, 1};
        int[] dy = {-1, 1, 0, 0};


        while (!queue.isEmpty()) {
            Point currPoint = queue.poll();

            if (currPoint.x < 0 || currPoint.x >= image.getWidth() ||
                    currPoint.y < 0 || currPoint.y >= image.getHeight() ||
                    image.getRGB(currPoint.x, currPoint.y) != targetRGB) continue;


//            System.out.println(currPoint);
            canvas.dirtAreas.put(currPoint, image.getRGB(currPoint.x, currPoint.y));
            image.setRGB(currPoint.x, currPoint.y, replacementColor.getRGB());

            for (int i = 0; i < 4; i++) {
                int x = currPoint.x + dx[i];
                int y = currPoint.y + dy[i];

                if (x < 0 || x >= image.getWidth() ||
                        y < 0 || y >= image.getHeight() ||
                        image.getRGB(x, y) != targetRGB) continue;

                queue.add(new Point(x, y));
            }

        }

    }


    public void resetCanvas(BufferedImage image) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                image.setRGB(x, y, 0x00000000);
            }
        }
    }


}
