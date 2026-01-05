package control;

import rasterize.*;
import view.Panel;
import model.Point;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Controller2D implements Controller {

    private final Raster raster;
    private RasterBufferedImage rasterCopy;
    private int x,y;

    private LineRasterizer filledLineRasterizer;
    private PolygonRasterizer polygonRasterizer;

    private enum DrawMode { LINE, DASHED_LINE, POLYGON };
    private DrawMode drawMode = DrawMode.LINE;

    private Point firstPolygonPoint;

    public Controller2D(Panel panel) {
        this.raster = panel.getRaster();

        initObjects(panel.getRaster());
        initListeners(panel);
    }

    private void initObjects(Raster raster) {
        filledLineRasterizer = new FilledLineRasterizer(raster);
        polygonRasterizer = new PolygonRasterizer(raster);
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
                        case DASHED_LINE:
                            drawLine(e.getX(), e.getY(), true);
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

                // Stisk klávesy "O" přepne na vykreslování tečkované úsečky
                if (e.getKeyCode() == KeyEvent.VK_O) {
                    drawMode = DrawMode.DASHED_LINE;
                }

                // Stisk klávesy "P" přepne na vykreslování polygonu
                if (e.getKeyCode() == KeyEvent.VK_P) {
                    drawMode = DrawMode.POLYGON;
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
        copy.draw((RasterBufferedImage) raster);
        rasterCopy = copy;
    }

    private void pasteRasterCopy() {
        if (rasterCopy != null) {
            RasterBufferedImage castedRaster = (RasterBufferedImage) raster;
            castedRaster.draw(rasterCopy);
        }
    }
}
