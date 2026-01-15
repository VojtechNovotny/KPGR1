package fill;

import model.Edge;
import model.Point;
import rasterize.LineRasterizer;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScanLine implements Filler {

    private final LineRasterizer filledLineRasterizer;
    private List<Point> points;
    private int fillColor;
    private int borderColor; // na finální obtažení

    public ScanLine(LineRasterizer filledLineRasterizer) {
        this.filledLineRasterizer = filledLineRasterizer;
    }

    public void fillPolygon(List<Point> points, int fillColor, int borderColor) {
        this.points = points;
        this.fillColor = fillColor;
        this.borderColor = borderColor;
        fill();
    }

    @Override
    public void fill() {
        List<Edge> edges = new ArrayList<>();
        Edge edge;
        int minY = points.getFirst().y;
        int maxY = 0;

        // Naplnění seznamu hranami
        for (int i = 0; i < points.size(); i++) {
            // Poslední bod spojíme s prvním
            if (i == points.size()-1) {
                edge = new Edge(points.get(i), points.getFirst());
            } else {
                edge = new Edge(points.get(i), points.get(i+1));
            };

            if (!edge.isHorizontal()) {
                // Zorientování hrany
                edge.orientate();

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

                // Zkrácení hrany o jeden pixel
                edge.shorten();
                // Uložení do seznamu hran
                edges.add(edge);
            }
        }

        // Hledání průsečíků a jejich vyplnění
        for (int y = minY; y <= maxY; y++) {
             List<Integer> intersections = new ArrayList<>();

             for (int j = 0; j < edges.size(); j++) {
                 // Pokud má scan line průsečík s hranou získáme daný průsečík a vložíme ho do seznamu
                 if (edges.get(j).hasIntersection(y)) {
                     intersections.add(edges.get(j).getIntersection(y));
                 }
             }

            // Seřazení průsečíků
            Collections.sort(intersections);

             // Spojení průsečíků barevnou úsečkou (vyplnění řádku na aktuálním y)
            for (int m = 0; m < intersections.size(); m += 2) {
                filledLineRasterizer.rasterize(intersections.get(m), y, intersections.get(m+1), y, fillColor, false);
            }
        }

        // Obtažení hrany polygonu
        Point p1;
        Point p2;
        for (int i = 0; i < points.size(); i++) {
            p1 = points.get(i);

            if (i == points.size()-1) { // Spojení posledního bodu s prvním
                p2 = points.getFirst();
            } else {
                p2 = points.get(i+1);
            }

            filledLineRasterizer.rasterize(p1.x, p1.y, p2.x, p2.y, borderColor, false);
        }
    }
}
