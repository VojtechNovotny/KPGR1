package control;

import fill.ScanLine;
import fill.SeedFill;
import rasterize.*;
import view.Panel;
import model.Point;
import model.Polygon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Controller2D implements Controller {

    private final RasterBufferedImage raster;
    private RasterBufferedImage rasterCopy;
    private final Graphics rasterGraphics;
    private int x,y;
    private Polygon lastPolygon; // uložení posledního polygonu, potřebné pro scanline

    private LineRasterizer filledLineRasterizer;
    private PolygonRasterizer polygonRasterizer;
    private SeedFill seedFill;
    private ScanLine scanLineFiller;

    private enum DrawMode { LINE, POLYGON }
    private enum LineType { FULL, DASHED }
    private enum ActiveColor { WHITE, RED, BLUE, GREEN }
    private DrawMode drawMode = DrawMode.LINE;
    private LineType lineType = LineType.FULL;
    private ActiveColor activeColor = ActiveColor.WHITE;

    private boolean polygonDrawStarted;

    public Controller2D(Panel panel) {
        this.raster = (RasterBufferedImage) panel.getRaster();
        this.rasterGraphics = raster.getGraphics();

        initObjects(panel.getRaster());
        initListeners(panel);

        createToolbar(rasterGraphics);
    }

    private void initObjects(Raster raster) {
        filledLineRasterizer = new FilledLineRasterizer(raster);
        polygonRasterizer = new PolygonRasterizer(filledLineRasterizer);

        seedFill = new SeedFill(raster);
        scanLineFiller = new ScanLine(filledLineRasterizer);
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
                        if (!polygonDrawStarted) {
                            polygonDrawStarted = true;
                            polygonRasterizer.start(new Point(x, y), getActiveColorRGB(activeColor));
                        } else {
                            polygonRasterizer.addPoint(new Point(x, y));
                        }
                    }

                    copyRaster();
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    if (drawMode == DrawMode.POLYGON) {
                        raster.clear();
                        pasteRasterCopy();
                        lastPolygon = polygonRasterizer.closePolygon();
                        polygonDrawStarted = false;
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

                if (drawMode == DrawMode.POLYGON && polygonDrawStarted) {
                    raster.clear();
                    pasteRasterCopy();
                    polygonRasterizer.rasterize(e.getX(), e.getY());
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (e.isControlDown()) return;

                if (e.isShiftDown()) {
                    // TODO
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    if (drawMode == DrawMode.LINE) {
                        drawLine(
                                e.getX(),
                                e.getY(),
                                getActiveColorRGB(activeColor),
                                lineType == LineType.DASHED
                        );
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
                    polygonDrawStarted = false;
                }

                if (e.getKeyCode() == KeyEvent.VK_V) {
                    scanLineFiller.fillPolygon(lastPolygon.getPoints(), getActiveColorRGB(activeColor), lastPolygon.getColor());
                }

                // Stisk klávesy "P" přepne na mód vykreslování
                if (e.getKeyCode() == KeyEvent.VK_M) {
                    switch (drawMode) {
                        case DrawMode.LINE:
                            drawMode = DrawMode.POLYGON;
                            rasterGraphics.clearRect(48, 115, 400, 20);
                            rasterGraphics.drawString("Aktivní mód: Polygon", 48, 130);
                            break;
                        case DrawMode.POLYGON:
                            drawMode = DrawMode.LINE;
                            rasterGraphics.clearRect(48, 115, 400, 20);
                            rasterGraphics.drawString("Aktivní mód: Úsečka", 48, 130);
                            break;
                    }
                }

                // Stisk klávesy "T" přepne na typ úsečky
                if (e.getKeyCode() == KeyEvent.VK_T) {
                    switch (lineType) {
                        case LineType.FULL:
                            lineType = LineType.DASHED;
                            rasterGraphics.clearRect(48, 195, 400, 20);
                            rasterGraphics.drawString("Aktivní typ: Přerušovaná", 48, 210);
                            break;
                        case LineType.DASHED:
                            lineType = LineType.FULL;
                            rasterGraphics.clearRect(48, 195, 400, 20);
                            rasterGraphics.drawString("Aktivní typ: Plná", 48, 210);
                            break;
                    }
                }

                // Stisk klávesy "B" přepne aktivní barvu
                if (e.getKeyCode() == KeyEvent.VK_B) {
                    switch (activeColor) {
                        case ActiveColor.WHITE:
                            activeColor = ActiveColor.RED;
                            rasterGraphics.clearRect(48, 275, 400, 20);
                            rasterGraphics.drawString("Aktivní barva: Červená", 48, 290);
                            break;
                        case ActiveColor.RED:
                            activeColor = ActiveColor.BLUE;
                            rasterGraphics.clearRect(48, 275, 400, 20);
                            rasterGraphics.drawString("Aktivní barva: Modrá", 48, 290);
                            break;
                        case ActiveColor.BLUE:
                            activeColor = ActiveColor.GREEN;
                            rasterGraphics.clearRect(48, 275, 400, 20);
                            rasterGraphics.drawString("Aktivní barva: Zelená", 48, 290);
                            break;
                        case ActiveColor.GREEN:
                            activeColor = ActiveColor.WHITE;
                            rasterGraphics.clearRect(48, 275, 400, 20);
                            rasterGraphics.drawString("Aktivní barva: Bílá", 48, 290);
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

    private void drawLine(int endX, int endY, int color, Boolean dashed) {
        raster.clear();
        pasteRasterCopy();
        filledLineRasterizer.rasterize(x, y, endX, endY, color, dashed);
    }

    private int getActiveColorRGB(ActiveColor activeColor) {
        return switch (activeColor) {
            case ActiveColor.WHITE -> Color.WHITE.getRGB();
            case ActiveColor.RED -> Color.RED.getRGB();
            case ActiveColor.BLUE -> Color.BLUE.getRGB();
            case ActiveColor.GREEN -> Color.GREEN.getRGB();
        };
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

    private void createToolbar(Graphics rasterGraphics) {
        rasterGraphics.setColor(Color.WHITE);
        rasterGraphics.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        rasterGraphics.drawString("Mód kreslení (M): Úsečka - Polygon", 48, 40);
        rasterGraphics.drawString("Aktivní mód: Úsečka", 48, 130);

        rasterGraphics.drawString("Typ úsečky (T): Plná - Tečkovaná", 48, 180);
        rasterGraphics.drawString("Aktivní typ: Plná", 48, 210);

        rasterGraphics.drawString("Barva (B): Bílá - Červená - Modrá - Zelená", 48, 260);
        rasterGraphics.drawString("Aktivní barva: Bílá", 48, 290);

        rasterGraphics.drawString("Výpň posledního polygonu Scanline algoritmem (V)", 48, 340);
        rasterGraphics.drawString("Výpň seed fill algoritmem (S + LeftMouseButton)", 48, 370);
        rasterGraphics.drawString("Smazání plátna (C)", 48, 400);
    }
}
