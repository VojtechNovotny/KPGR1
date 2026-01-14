package control;

import fill.ScanLine;
import fill.SeedFill;
import rasterize.*;
import view.Panel;
import model.Point;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class Controller2D implements Controller {

    private final RasterBufferedImage raster;
    private RasterBufferedImage rasterCopy;
    private Graphics rasterGraphics;
    private int x,y;

    private LineRasterizer filledLineRasterizer;
    private PolygonRasterizer polygonRasterizer;
    private SeedFill seedFill;
    private ScanLine scanLine;

    private enum DrawMode { LINE, POLYGON };
    private enum LineType { FULL, DASHED };
    private enum ActiveColor { WHITE, RED, BLUE, GREEN };
    private DrawMode drawMode = DrawMode.LINE;
    private LineType lineType = LineType.FULL;
    private ActiveColor activeColor = ActiveColor.WHITE;

    private Point firstPolygonPoint;

    public Controller2D(Panel panel) {
        this.raster = (RasterBufferedImage) panel.getRaster();
        this.rasterGraphics = raster.getGraphics();

        initObjects(panel.getRaster());
        initListeners(panel);

        createToolbar(raster);
    }

    private void initObjects(Raster raster) {
        filledLineRasterizer = new FilledLineRasterizer(raster);
        polygonRasterizer = new PolygonRasterizer(raster);

        seedFill = new SeedFill(raster);

        initTestScanLine(raster);
    }

    private void initTestScanLine(Raster raster) {
        List<Point> points = new ArrayList<Point>();
        points.add(new Point(200, 500));
        points.add(new Point(100, 300));
        points.add(new Point(250, 100));
        points.add(new Point(300, 300));
        points.add(new Point(500, 300));
        points.add(new Point(600, 100));
        points.add(new Point(650, 500));

        for (int i = 0; i < points.size(); i++) {
            Point p1 = points.get(i);
            Point p2;

            if (i == points.size()-1) {
                // Poslední bod spojí s prvním
                p2 = points.getFirst();
            } else {
                p2 = points.get(i+1);
            };

            //System.out.printf("drawing line (%d) from |%d, %d| to |%d, %d|\n", i+1, p1.x, p1.y, p2.x, p2.y);

            filledLineRasterizer.rasterize(p1.x, p1.y, p2.x, p2.y, Color.WHITE.getRGB(), false);
        }

        scanLine = new ScanLine(filledLineRasterizer, points, Color.BLUE.getRGB(), Color.WHITE.getRGB());
        scanLine.fill();
    }

    @Override
    public void initListeners(Panel panel) {
        panel.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isControlDown()) return;

                if (e.isShiftDown()) {
                    //TODO
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    x = e.getX();
                    y = e.getY();

                    if (drawMode == DrawMode.POLYGON) {
                        if (firstPolygonPoint == null) {
                            firstPolygonPoint = new Point(x, y);
                            polygonRasterizer.addPoint(firstPolygonPoint);
                        } else {
                            polygonRasterizer.addPoint(new Point(x, y));
                        }
                    }

                    copyRaster();
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    if (drawMode == DrawMode.POLYGON) {
                        raster.clear();
                        pasteRasterCopy();
                        polygonRasterizer.closePolygon(firstPolygonPoint);
                        firstPolygonPoint = null;
                    }
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.isControlDown()) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        seedFill.setSeed(new Point(e.getX(), e.getY()));
                        seedFill.setFillColor(Color.BLUE.getRGB());
                        seedFill.fill();
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        //TODO
                    }
                }
            }
        });

        panel.addMouseMotionListener(new MouseAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);

                if (drawMode == DrawMode.POLYGON && firstPolygonPoint != null) {
                    raster.clear();
                    pasteRasterCopy();
                    polygonRasterizer.rasterize(firstPolygonPoint.x, firstPolygonPoint.y, e.getX(), e.getY());
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (e.isControlDown()) return;

                if (e.isShiftDown()) {
                    // TODO
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    switch(drawMode) {
                        case LINE:
                            drawLine(e.getX(), e.getY(), false);
                            break;
                        default:
                            return;
                    }
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    //TODO
                }
            }
        });

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // Stisk klávesy "C" vymaže plátno
                if (e.getKeyCode() == KeyEvent.VK_C) {
                    raster.clear();
                    firstPolygonPoint = null;
                }

                // Stisk klávesy "U" přepne na vykreslování normální úsečky
                if (e.getKeyCode() == KeyEvent.VK_U) {
                    drawMode = DrawMode.LINE;
                }

                // Stisk klávesy "I" přepne na vykreslování barevné úsečky
                if (e.getKeyCode() == KeyEvent.VK_I) {
                    // TODO
                }

                // Stisk klávesy "P" přepne na mód vykreslování
                if (e.getKeyCode() == KeyEvent.VK_M) {
                    switch (drawMode) {
                        case DrawMode.LINE:
                            drawMode = DrawMode.POLYGON;
                            rasterGraphics.clearRect(800, 115, 400, 20);
                            rasterGraphics.drawString("Aktivní mód: Polygon", 800, 130);
                            break;
                        case DrawMode.POLYGON:
                            drawMode = DrawMode.LINE;
                            rasterGraphics.clearRect(800, 115, 400, 20);
                            rasterGraphics.drawString("Aktivní mód: Úsečka", 800, 130);
                            break;
                    }
                }

                // Stisk klávesy "T" přepne na typ úsečky
                if (e.getKeyCode() == KeyEvent.VK_T) {
                    switch (lineType) {
                        case LineType.FULL:
                            lineType = LineType.DASHED;
                            rasterGraphics.clearRect(800, 195, 400, 20);
                            rasterGraphics.drawString("Aktivní typ: Přerušovaná", 800, 210);
                            break;
                        case LineType.DASHED:
                            lineType = LineType.FULL;
                            rasterGraphics.clearRect(800, 195, 400, 20);
                            rasterGraphics.drawString("Aktivní typ: Plná", 800, 210);
                            break;
                    }
                }

                // Stisk klávesy "B" přepne aktivní barvu
                if (e.getKeyCode() == KeyEvent.VK_B) {
                    switch (activeColor) {
                        case ActiveColor.WHITE:
                            activeColor = ActiveColor.RED;
                            rasterGraphics.clearRect(800, 275, 400, 20);
                            rasterGraphics.drawString("Aktivní barva: Červená", 800, 290);
                            break;
                        case ActiveColor.RED:
                            activeColor = ActiveColor.BLUE;
                            rasterGraphics.clearRect(800, 275, 400, 20);
                            rasterGraphics.drawString("Aktivní barva: Modrá", 800, 290);
                            break;
                        case ActiveColor.BLUE:
                            activeColor = ActiveColor.GREEN;
                            rasterGraphics.clearRect(800, 275, 400, 20);
                            rasterGraphics.drawString("Aktivní barva: Zelená", 800, 290);
                            break;
                        case ActiveColor.GREEN:
                            activeColor = ActiveColor.WHITE;
                            rasterGraphics.clearRect(800, 275, 400, 20);
                            rasterGraphics.drawString("Aktivní barva: Bílá", 800, 290);
                            break;
                    }
                }
            }
        });

        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                panel.resize();
                initObjects(panel.getRaster());
            }
        });
    }

    private void drawLine(int endX, int endY, Boolean dashed) {
        raster.clear();
        pasteRasterCopy();
        filledLineRasterizer.rasterize(x, y, endX, endY, Color.WHITE.getRGB(), dashed);
    }

    private void copyRaster() {
        RasterBufferedImage copy = new RasterBufferedImage(raster.getWidth(), raster.getHeight());
        copy.draw(raster);
        rasterCopy = copy;
    }

    private void pasteRasterCopy() {
        if (rasterCopy != null) {
            raster.draw(rasterCopy);
        }
    }

    private void createToolbar(RasterBufferedImage raster) {
        rasterGraphics.setColor(Color.WHITE);
        rasterGraphics.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));

        rasterGraphics.drawString("Mód kreslení (M): Úsečka - Polygon", 800, 100);
        rasterGraphics.drawString("Aktivní mód: Úsečka", 800, 130);

        rasterGraphics.drawString("Typ úsečky (T): Plná - Tečkovaná", 800, 180);
        rasterGraphics.drawString("Aktivní typ: Plná", 800, 210);

        rasterGraphics.drawString("Barva (B): Bílá - Červená - Modrá - Zelená", 800, 260);
        rasterGraphics.drawString("Aktivní barva: Bílá", 800, 290);
    }
}
