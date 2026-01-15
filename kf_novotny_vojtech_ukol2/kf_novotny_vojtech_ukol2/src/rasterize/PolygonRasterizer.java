package rasterize;

import model.Polygon;
import model.Point;

public class PolygonRasterizer {

    private Polygon polygon;
    private final LineRasterizer filledLineRasterizer;

    public PolygonRasterizer(LineRasterizer lineRasterizer) {
        this.filledLineRasterizer = lineRasterizer;
    }

    public void rasterize(int x, int y) {
        filledLineRasterizer.rasterize(
                polygon.getPoints().getLast().x,
                polygon.getPoints().getLast().y,
                x,
                y,
                polygon.getColor(),
                false
        );
    }

    public void start(Point point, int color) {
        this.polygon = new Polygon(color);
        addPoint(point);
    }

    public void addPoint(Point newPoint) {
        polygon.addPoints(newPoint);
    }

    public Polygon closePolygon() {
        System.out.printf("closePolygon() called, last point: (%d, %d), first point: (%d, %d)\n", polygon.getPoints().getLast().x, polygon.getPoints().getLast().y, polygon.getPoints().getFirst().x, polygon.getPoints().getFirst().y);
        filledLineRasterizer.rasterize(
                polygon.getPoints().getLast().x,
                polygon.getPoints().getLast().y,
                polygon.getPoints().getFirst().x,
                polygon.getPoints().getFirst().y,
                polygon.getColor(),
                false
        );

        return polygon;
    }
}
