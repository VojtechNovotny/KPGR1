package fill;

import model.Edge;
import model.Point;
import rasterize.Raster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScanLine implements Filler {

    private final Raster raster;
    private final List<Point> points;
    private final int fillColor;
    private final int borderColor; // na finální obtažení

    public ScanLine(Raster raster, List<Point> points, int fillColor, int borderColor) {
        this.raster = raster;
        this.points = points;
        this.fillColor = fillColor;
        this.borderColor = borderColor;
    }

    @Override
    public void fill() {
        List<Edge> edges = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            // Poslední bod spojíme s prvním
            if (i == points.size()-1) {
                Edge edge = new Edge(points.get(i), points.getFirst());
                edges.add(edge);
            };

            Edge edge = new Edge(points.get(i+1), points.get(i+1));
            edges.add(edge);
        }

        // TODO projet body (list points) a vytvořit z nich hrany
        // 0. a 1. bod budou první hranou, 1. a 2. bod budou druhou hranou, ..., poslední a nultý DONE
        // ignorovat vodorovné hrany
        // vytvořené (nevodorovnorné) hrany zorientovat a přidat do seznamu

        // vysledek = seznam zorientovaných hran bez vodorovných úseků

        // najít min a max Y
        int minY = points.getFirst().y;
        int maxY = minY;
        // projet všechny body (list points) a najít min a max Y (optimalizační krok - není potřeba)

        for (int y = minY; y <= maxY; y++) {
             List<Integer> intersections = new ArrayList<>();
             // vnořený cyklus
            // projít všechny hrany (list edges)
            // pokud má hrana průsečík na daném Y tak vypočítat X hodnotu na hodnotu průsečíku a uložit ji do seznamu

            // nyní je naplněný seznam průsečíků

            // setřídit průsečíky
            Collections.sort(intersections);

            // vybarvení mezi průsečíky
            // spojení vždy sudého s lichým -> 0. a 1., 2. a 3., 4. a 5., ...
            // kreslení úseček (možná místo rasteru použít nějaký lineRasterizer ???)

        }

        // obtáhnout hranici tělesa definovaného pomocí listu points
    }
}
