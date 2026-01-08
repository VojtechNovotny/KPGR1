package model;

public class Edge {

    private int x1, y1, x2, y2;

    // TODO nahradit v celé code base Boolean na boolean

    public Edge(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public Edge(Point p1, Point p2) {
        this(p1.x, p1.y, p2.x, p2.y);
    }

    /**
     * Zjistí, zda je hrana vodorovná
     * @return true pokud je vodorovná, jinak false
     */
    public boolean isHorizontal() {
        // TODO test na rovnost mezi y1 a y2
        return false;
    }

    /**
     * Zorientuje hranu odshora dolů
     */
    public void orientate() {
        // TODO prohození hodnot, pokud y1 je větší než y2
    }

    /**
     * Zjistí, zda existuje průsečík scan-line s touto hranou
     * @param y souřadnice y vodorovné přímky (scan-line)
     * @return true pokud průsečík existuje, jinak false
     */
    public boolean hasIntersection(int y) {
        // TODO y, y1, y2 - porovnat zda je y v rozsahu
        return false;
    }

    /**
     *
     * @param y souřadnice y vodorovné přímky (scan-line)
     * @return x souřadnici průsečíku
     */
    public int getIntersection(int y) {
        // TODO vypočítat průsečík pomocí y, k, q (osa Y)
        return 0;
    }


}
