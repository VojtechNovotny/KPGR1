package model;

public class Line {

    private final int  x1, y1, x2, y2;
    private final int color;

    public Line(int x1, int y1, int x2, int y2, int color) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.color = color;
    }

    public Line(Point p1, Point p2, int color) {
        this(p1.x, p1.y, p2.x, p2.y, color);
    }

    public int getX1() {
        return x1;
    }

    public int getY1() {
        return y1;
    }

    public int getX2() {
        return x2;
    }

    public int getY2() {
        return y2;
    }

    public int getColor() {
        return color;
    }
}
