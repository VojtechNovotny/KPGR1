package model;

import rasterize.FilledLineRasterizer;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Polygon {

    private final List<Point> points;
    private final int color;
    private Point lastPoint;

    public Polygon() {
        this(new ArrayList<>(), Color.WHITE.getRGB()); // Když nespecifikujeme barvu bude použita bílá
    }

    public Polygon(int color) {
        this(new ArrayList<>(), color);
    }

    public Polygon(List<Point> points, int color) {
        this.points = points;
        this.color = color;
    }

    public void addPoints(Point... newPoints) {
        points.addAll(Arrays.asList(newPoints));
    }

    public void addPoints(List<Point> newPoints) {
        points.addAll(newPoints);
    }

    public List<Point> getPoints() {
        return points;
    }

    public Point getLastPoint() {
        return lastPoint;
    }

    public void setLastPoint(Point newLastPoint) {
        lastPoint = newLastPoint;
    }

    public int getColor() {
        return color;
    }
}
