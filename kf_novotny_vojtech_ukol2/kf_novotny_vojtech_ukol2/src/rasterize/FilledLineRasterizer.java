package rasterize;

/*
    Použil jsem triviální řešení (algoritmus) pro rasterizaci úsečky
*/

public class FilledLineRasterizer extends LineRasterizer {

    public FilledLineRasterizer(Raster raster) {
        super(raster);
    }

    @Override
    public void rasterize(int x1, int y1, int x2, int y2, int color, boolean dashed) {
        float k = (y2 -y1) / (float) (x2 - x1);
        float q = y1 - k * x1;

        int biggerX;
        int smallerX;
        int biggerY;
        int smallerY;

        if (x1 <= x2) {
            biggerX = x2;
            smallerX = x1;
        } else {
            biggerX = x1;
            smallerX = x2;
        }

        if (y1 <= y2) {
            biggerY = y2;
            smallerY = y1;
        } else {
            biggerY = y1;
            smallerY = y2;
        }

        if (Math.abs(k) <= 1) {
            for (int x = smallerX; x <= biggerX; x++) {
                float y = k * x + q;
                raster.setPixel(x, Math.round(y), color);
                if (dashed) {
                    x += 9;
                }
            }
        } else {
            for (int y = smallerY; y <= biggerY; y++) {
                float x = (y - q) / k;
                raster.setPixel(Math.round(x), y, color);
                if (dashed) {
                    y += 9;
                }
            }
        }
    }
}
