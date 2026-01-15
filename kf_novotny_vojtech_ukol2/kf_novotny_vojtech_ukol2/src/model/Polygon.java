package model;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Polygon {

    private final List<Point> points;
    private final int color;

    public Polygon(int color) {
        this.points = new ArrayList<>();
        this.color = color;
    }

    public void addPoints(Point... newPoints) {
        points.addAll(Arrays.asList(newPoints));
    }

    public List<Point> getPoints() {
        return points;
    }

    public int getColor() {
        return color;
    }
}
