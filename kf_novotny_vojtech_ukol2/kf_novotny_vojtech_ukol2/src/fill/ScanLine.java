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
        Edge edge;
        int minY = points.getFirst().y;
        int maxY = 0;

        for (int i = 0; i < points.size(); i++) {
            // Poslední bod spojíme s prvním
            if (i == points.size()-1) {
                edge = new Edge(points.get(i), points.getFirst());
            } else {
                edge = new Edge(points.get(i), points.get(i+1));
            };

            if (!edge.isHorizontal()) {
                // Zorientování hrany a přidání do seznamu
                edge.orientate();
                edges.add(edge);

                // Hledání minY a maxY
                if (edge.getY1() < minY) {
                    minY = edge.getY1();
                }
                if (edge.getY2() < minY) {
                    minY = edge.getY2();
                }
                if (edge.getY1() > maxY) {
                    maxY = edge.getY1();
                }
                if (edge.getY2() > maxY) {
                    maxY = edge.getY2();
                }
            }
        }

        for (int j = 0; j < edges.size(); j++) {
            Edge currentEdge = edges.get(j);
            System.out.printf("edge %d from (%d, %d) to (%d, %d)\n", j+1, currentEdge.getX1(), currentEdge.getY1(), currentEdge.getX2(), currentEdge.getY2());
        }

        System.out.printf("minY: %d, maxY: %d\n", minY, maxY);

        for (int y = minY; y <= maxY; y++) {
            System.out.printf("finding intersections on y=%d\n", y);
             List<Integer> intersections = new ArrayList<>();

             for (int k = 0; k < edges.size(); k++) {
                 // Pokud má scan line průsečík s hranou získáme daný průsečík a vložíme ho do seznamu
                 if (edges.get(k).hasIntersection(y)) {
                     intersections.add(edges.get(k).getIntersection(y));
                 }
             }
             // vnořený cyklus
            // projít všechny hrany (list edges)
            // pokud má hrana průsečík na daném Y tak vypočítat X hodnotu na hodnotu průsečíku a uložit ji do seznamu

            // nyní je naplněný seznam průsečíků

            // Seřazení průsečíků
            for (int l = 0; l < intersections.size(); l++) {
                System.out.printf("intersection %d: %d\n", l+1, intersections.get(l));
            }

            //System.out.println("sorting intersetions ...");
            Collections.sort(intersections);

            for (int l = 0; l < intersections.size(); l++) {
                System.out.printf("intersection %d: %d\n", l+1, intersections.get(l));
            }

            // vybarvení mezi průsečíky
            // spojení vždy sudého s lichým -> 0. a 1., 2. a 3., 4. a 5., ...
            // kreslení úseček (možná místo rasteru použít nějaký lineRasterizer ???)

        }

        // obtáhnout hranici tělesa definovaného pomocí listu points
    }
}
