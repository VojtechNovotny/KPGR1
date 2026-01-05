package rasterize;

import model.Line;

import java.awt.*;

public abstract class LineRasterizer {
    Raster raster;
    Color color;

    public LineRasterizer(Raster raster){
        this.raster = raster;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setColor(int color) {
        this.color = new Color(color);
    }

    public void rasterize(Line line, Boolean dashed) {
        rasterize(line.getX1(), line.getY1(), line.getX2(), line.getY2(), line.getColor(), dashed);
    }

    public abstract void rasterize(int x1, int y1, int x2, int y2, int color, Boolean dashed);
}
