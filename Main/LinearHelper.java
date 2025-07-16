package Main;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class LinearHelper {

    double PIXEL_SIZE;
    BufferedImage image;
    public static int newSize = 1;
    Canvas canvas;

    LinearHelper(BufferedImage image, double PIXEL_SIZE, Canvas canvas) {
        this.image = image;
        this.PIXEL_SIZE = PIXEL_SIZE;
        this.canvas = canvas;
    }

    public Point screenToWorld(int X, int Y) {
        double zoomFactor = canvas.getZoomFactor();
        Point zoomOrigin = canvas.getZoomOrigin();

        int x = (int) (((double) X - zoomOrigin.x) / (PIXEL_SIZE * zoomFactor));
        int y = (int) (((double) Y - zoomOrigin.y) / (PIXEL_SIZE * zoomFactor));
        x = Math.max(0, Math.min(x, image.getWidth() - 1));
        y = Math.max(0, Math.min(y, image.getHeight() - 1));

        return new Point(x, y);

    }

    public ArrayList<Point> getBresenhamLine(Point start, Point end) {
        ArrayList<Point> line = new ArrayList<>();
        Point startPoint = screenToWorld(start.x, start.y);
        Point endPoint = screenToWorld(end.x, end.y);

        int x1 = startPoint.x;
        int y1 = startPoint.y;
        int x2 = endPoint.x;
        int y2 = endPoint.y;

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);

        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;

        if (Math.abs(x2 - x1) == Math.abs(y2 - y1)) {  // идеальная прямая
            for (int i = 0; i <= Math.abs(x2 - x1); i++) {
                int x = (x1 + i * sx);
                int y = (y1 + i * sy);

                line.add(new Point(x, y));
            }
            return line;
        }

        int err = dx - dy;

        while (true) {
            line.add(new Point(x1, y1));

            if (x1 == x2 && y1 == y2) break;

            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x1 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y1 += sy;
            }
        }
        return line;
    }

    public ArrayList<Point> getBresenhamPerfectCircle(Point center, Point end) {
        ArrayList<Point> circle = new ArrayList<>();
        Point centerPoint = screenToWorld(center.x, center.y);
        Point endPoint = screenToWorld(end.x, end.y);

        final int X = (int) centerPoint.getX();
        final int Y = (int) centerPoint.getY();
        int x = 0;
        int y = (int) Math.abs(endPoint.getX() - X);
        int delta = 1 - 2 * y;
        int error;


        while (y >= x) {


            circle.add(new Point(((X + x)), ((Y + y))));
            circle.add(new Point(((X + x)), ((Y - y))));
            circle.add(new Point(((X - x)), ((Y + y))));
            circle.add(new Point(((X - x)), ((Y - y))));

            circle.add(new Point(((X + y)), ((Y + x))));
            circle.add(new Point(((X + y)), ((Y - x))));
            circle.add(new Point(((X - y)), ((Y + x))));
            circle.add(new Point(((X - y)), ((Y - x))));


            error = 2 * (delta + y) - 1;

            if ((delta < 0) && (error <= 0)) {
                delta += 2 * ++x + 1;
                continue;
            }

            if ((delta > 0) && (error > 0)) {
                delta -= 2 * --y + 1;
                continue;

            }

            delta += 2 * (++x - --y);

        }


        return circle;
    }

    public ArrayList<Point> getBresenhamEllipse(Point center, Point end) {
        ArrayList<Point> oval = new ArrayList<>();
        Point centerPoint = screenToWorld(center.x, center.y);
        Point endPoint = screenToWorld(end.x, end.y);


        final int X = (int) centerPoint.getX();
        final int Y = (int) centerPoint.getY();
        int rx = Math.abs((int) endPoint.getX() - X);
        int ry = Math.abs((int) endPoint.getY() - Y);

        int rxSq = rx * rx;
        int rySq = ry * ry;
        int x = 0;
        int y = ry;
        int p;
        int px = 0;
        int py = 2 * rxSq * y;

        p = rySq - (rxSq * ry) + (rxSq / 4);
        while (px < py) {
            x++;
            px += 2 * rySq;
            if (p < 0) {
                p += rySq + px;
            } else {
                y--;
                py -= 2 * rxSq;
                p += rySq + px - py;
            }

            oval.add(new Point((X + x), (Y + y)));
            oval.add(new Point((X - x), (Y + y)));
            oval.add(new Point((X + x), (Y - y)));
            oval.add(new Point((X - x), (Y - y)));
        }

        p = (int) (rySq * (x + 0.5) * (x + 0.5) + rxSq * (y - 1) * (y - 1) - rxSq * rySq);
        while (y > 0) {
            y--;
            py -= 2 * rxSq;
            if (p > 0) {
                p += rxSq - py;
            } else {
                x++;
                px += 2 * rySq;
                p += rxSq - py + px;
            }

            oval.add(new Point((X + x), (Y + y)));
            oval.add(new Point((X - x), (Y + y)));
            oval.add(new Point((X + x), (Y - y)));
            oval.add(new Point((X - x), (Y - y)));
        }

        oval.add(new Point((X + rx), Y));
        oval.add(new Point((X - rx), Y));
        oval.add(new Point(X, (Y + ry)));
        oval.add(new Point(X, (Y - ry)));

        return oval;
    }

    public ArrayList<Point> getRect(Point start, Point end) {
        ArrayList<Point> rect = new ArrayList<>();

        Point A = new Point(start.x, start.y);  //нет деления тк getBresenhamLine его выполняет
        Point B = new Point(end.x, start.y);
        Point C = new Point(end.x, end.y);
        Point D = new Point(start.x, end.y);


        Point X = A;
        Point Y = B;

        for (int i = 0; i < 4; i++) {
            ArrayList<Point> line = getBresenhamLine(X, Y);
            rect.addAll(line);
            X = Y;
            Y = switch (i) {
                case 0 -> C;
                case 1 -> D;
                case 2 -> A;
                default -> Y;
            };
        }

        return rect;
    }


    public void expandPixels(Point p, int colorRGB, BufferedImage image) {

        for (int y = 0; y < newSize; y++) {
            for (int x = 0; x < newSize; x++) {
                if (p.y + y >= image.getHeight()) continue;
                if (p.x + x >= image.getWidth()) continue;

                if (!canvas.dirtAreas.containsKey(new Point(p.x + x, p.y + y))) {
                    canvas.dirtAreas.put(new Point(p.x + x, p.y + y), this.image.getRGB(p.x + x, p.y + y));
                }
                image.setRGB(p.x + x, p.y + y, colorRGB);

            }
        }


    }

//    public long pack(int x, int y) {
//        return ((long) x << 32) | (y & 0xFFFFFFFFL);
//    }
//
//    public int unpackX(long packed) {
//        return (int) (packed >>> 32);
//    }
//
//    public int unpackY(long packed) {
//        return (int) (packed & 0xFFFFFFFFL);
//    }


}
