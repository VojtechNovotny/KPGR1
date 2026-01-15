package rasterize;

public abstract class LineRasterizer {
    Raster raster;

    public LineRasterizer(Raster raster){
        this.raster = raster;
    }

    public abstract void rasterize(
            int x1,
            int y1,
            int x2,
            int y2,
            int color,
            boolean dashed
    );
}
