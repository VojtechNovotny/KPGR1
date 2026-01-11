package model;

public class Edge {

    private int x1, y1, x2, y2;
    private float k, q; // směrnice a posunutí

    public Edge(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;

        // TODO vypočítat k a q
    }

    public Edge(Point p1, Point p2) {
        this(p1.x, p1.y, p2.x, p2.y);
    }

    /**
     * Zjistí, zda je hrana vodorovná
     * @return true pokud je vodorovná, jinak false
     */
    public boolean isHorizontal() {
        return y1 == y2;
    }

    /**
     * Zorientuje hranu odspoda nahoru (od ymax do ymin)
     */
    public void orientate() {
        // Prohození hodnot, pokud je y2 větší než y1
        if (y2 > y1) {
            int y1Copy = y1;
            y1 = y2;
            y2 = y1Copy;

            int x1Copy = x1;
            x1 = x2;
            x2 = x1Copy;
        }
    }

    /**
     * Zjistí, zda existuje průsečík scan-line s touto hranou
     * @param y souřadnice y vodorovné přímky (scan-line)
     * @return true pokud průsečík existuje, jinak false
     */
    public boolean hasIntersection(int y) {
        // Porovnání zda je y v rozsahu
        return y >= y1 && y <= y2;
    }

    /**
     *
     * @param y souřadnice y vodorovné přímky (scan-line)
     * @return x souřadnici průsečíku
     */
    public int getIntersection(int y) {
        // TODO vypočítat průsečík pomocí y, k, q (osa Y)
        return Math.round((y - q) / k);
    }

}
