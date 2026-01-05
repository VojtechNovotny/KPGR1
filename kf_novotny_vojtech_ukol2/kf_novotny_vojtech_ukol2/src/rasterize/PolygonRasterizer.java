package rasterize;

import model.Polygon;
import model.Point;

import java.awt.Color;

public class PolygonRasterizer {

    private Polygon polygon;
    private FilledLineRasterizer filledLineRasterizer;

    public PolygonRasterizer(Raster raster) {
        this.polygon = new Polygon();
        this.filledLineRasterizer = new FilledLineRasterizer(raster);
    }

    public void rasterize(int x1, int y1, int x2, int y2) {
        Point lastPoint = polygon.getLastPoint();

        if (lastPoint == null) {
            filledLineRasterizer.rasterize(x1, y1, x2, y2, Color.WHITE.getRGB(), false);
        } else {
            filledLineRasterizer.rasterize(lastPoint.x, lastPoint.y, x2, y2, Color.WHITE.getRGB(), false);
        };
    }

    public void closePolygon(Point firstPolygonPoint) {
        Point lastPoint = polygon.getLastPoint();
        filledLineRasterizer.rasterize(lastPoint.x, lastPoint.y, firstPolygonPoint.x, firstPolygonPoint.y, Color.WHITE.getRGB(), false);
    }

    public void addPoint(Point newPoint) {
        polygon.addPoints(newPoint);
        polygon.setLastPoint(newPoint);
    }
}
