/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package icewalk;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 *
 * @author bb
 */
public class MapPanel extends JComponent {
    
    Random random;
    
    public int rand(int lowerBound, int higherBound) {
        return random.nextInt(higherBound - lowerBound) + lowerBound;
        //return ThreadLocalRandom.current().nextInt(lowerBound,higherBound);
    }
    
    private boolean ranBool() {
        return random.nextBoolean();
    }
    
    private void showMarkerCity(Graphics2D g) {
        
        g.setStroke(new BasicStroke(1f));
        for (GeneralPath[] segment : markerCity) {
            g.setColor(Color.yellow);
            g.fill(segment[0]);
            g.setColor(Color.BLACK);
            g.draw(segment[1]);
        }
        markerCity.clear();
    }
    private ArrayList<GeneralPath[]> markerCity = new ArrayList<>();
    
    private void drawGround(Graphics2D g) {
        
        GeneralPath[] shapes;
        ShapeGenerator sg = new ShapeGenerator();
        int dimensionSizeX = this.getSize().width, dimensionSizeY = getSize().height;
        
        int crowdDistanceX = 0, crowdDistanceY = 0;

        //g.fillRect(0, 0, dimesionSize, dimesionSize);
        boolean direction;
        Color groundColor = new Color(239, 157, 84),
                groundDarker = new Color(224, 133, 53),
                waterColor = new Color(139, 157, 244);//(67,211,244);

        g.setColor(groundColor);
        g.fillRect(0, 0, dimensionSizeX, dimensionSizeY);
        int colonyPopulationCount;
        boolean val;
        Color editedColor = groundColor;
        for (int i = 0; i < 40; i++) {
            crowdDistanceX = rand(0, dimensionSizeX);
            crowdDistanceY = rand(0, dimensionSizeY);
            int lowScale = 4, highScale = 7;
            boolean isDakerRegion;
            colonyPopulationCount = rand(120, 200);
            // groundColor=(val=ranBool())?Color.WHITE:new Color(239,157,84);
            //groundDarker=val?Color.gray:new Color(224, 133, 53);
            float brightnessRatio = ((float) rand(9, 10)) / 10f;
            //if(ranBool())

            editedColor
                    = new Color(
                            (int) (rand(190, 255) * brightnessRatio),
                            (int) (rand(groundColor.getGreen(), 200) * brightnessRatio),
                            (int) (groundColor.getBlue() * brightnessRatio));
            for (int j = 0; j < colonyPopulationCount; j++) {
                if (j == 70) {
                    lowScale = 1;
                    highScale = 3;
                    
                }
                direction = ranBool();
                if (direction) {
                    shapes = sg.generateShape(rand(crowdDistanceX, crowdDistanceX + 200), rand(crowdDistanceY, crowdDistanceY + 200), rand(0, 180), ((double) (rand(lowScale, highScale))) / rand(20, 40));
                } else {
                    shapes = sg.generateShape(dimensionSizeX - rand(crowdDistanceX, crowdDistanceX + 200), dimensionSizeY - rand(crowdDistanceY, crowdDistanceY + 200), rand(0, 180), ((double) (rand(lowScale, highScale))) / 40);
                }
                
                g.setColor((isDakerRegion = rand(0, 100) > 75) ? groundDarker : editedColor);
                g.fill(shapes[0]);
                if (!isDakerRegion) {
                    drawShapeOutline(shapes[1], g, editedColor);
                }
            }
        }
        drawRoad(sg, groundColor, dimensionSizeX, dimensionSizeY, g);
        GeneralPath riverPath;
        riverPath = sg.drawSinglePath(0, 0, 700);
        AffineTransform transform = new AffineTransform();
        Rectangle bounds = riverPath.getBounds();
        transform.translate(-bounds.x + rand(0, dimensionSizeX - 200), -bounds.y + rand(0, dimensionSizeY - 200));
        riverPath.transform(transform);
        g.setColor(waterColor.darker());
        g.setStroke((new BasicStroke(7, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND)));
        g.draw(riverPath);
        g.setStroke((new BasicStroke(5, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND)));
        g.setColor(waterColor);
        g.draw(riverPath);
        
        sg.branchConfigurations = new ShapeGenerator.branchConfig();
        
        drawMountainEdges(groundColor, sg, dimensionSizeX, dimensionSizeY, g);

        //drawing a river
        //drawing a mountain
//        g.setColor(waterColor);            
//        g.setStroke((new BasicStroke(1,BasicStroke.CAP_ROUND,
//                                    BasicStroke.JOIN_ROUND)));
//        for (GeneralPath crack : sg.drawCracks(500, 500)) {
//            g.draw(crack);
//        }
        // drawGrid(g, dimensionSizeX, dimensionSizeY);
    }
    
    private void drawMountainEdges(Color groundColor, ShapeGenerator sg, int dimensionSizeX, int dimensionSizeY, Graphics2D g) {
        sg.branchConfigurations.
                setRootBranchNoOfBends(30).setMaximumBranchDepth(3).setNoOfBends(3);
        for (int i = 0; i < rand(1,4); i++) {
//            float brightnessRatio = ((float) rand(9, 10)) / 10f;
//            editedColor = new Color(
//                    (int) (rand(190, 255) * brightnessRatio),
//                    (int) (rand(groundColor.getGreen(), 200) * brightnessRatio),
//                    (int) (groundColor.getBlue() * brightnessRatio));

            GeneralPath[][] cracks = sg.drawCracks(rand(0, dimensionSizeX), rand(0, dimensionSizeY));
            g.setColor(groundColor);
            for (int j = 0; j < cracks.length; j++) {
                g.fill(cracks[j][cracks[j].length - 1]);
            }
            
            for (int j = 1; j < 16; j++) {
                
                g.setColor(DarkerColor(groundColor, 1f - (0.025f * j)));
                
                for (int k = 0; k < cracks.length; k++) {
                    g.setStroke((new BasicStroke(17 - j, BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_ROUND)));
                    for (int l = 0; l < cracks[k].length - 2; l++) {
                        g.draw(cracks[k][l]);
                    }
                    g.setStroke((new BasicStroke(21 - j, BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_ROUND)));
                    g.draw(cracks[k][cracks[k].length - 2]);
                }
            }
            
            for (int j = 0; j < cracks.length; j++) {
                g.setColor(groundColor);
                g.setStroke((new BasicStroke(1, BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND)));
                for (int k = 0; k < cracks[j].length - 2; k++) {
                    g.draw(cracks[j][k]);
                }
                g.setStroke((new BasicStroke(3, BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND)));
                g.draw(cracks[j][cracks[j].length - 2]);
            }
            
        }
    }
    
    private void drawRoad(ShapeGenerator sg, Color groundColor, int dimensionSizeX, int dimensionSizeY, Graphics2D g) {
        sg.branchConfigurations.setIsRandom(true).setIsMainBranch(false).
                setBendLength(15).setMaximumBranchDepth(5).setNoOfBends(4).setRootBranchNoOfBends(400);
        GeneralPath[] roads = sg.drawRoadPath(rand(0, dimensionSizeX), rand(0, dimensionSizeY));
        g.setStroke((new BasicStroke(5, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND)));
        g.setColor(Color.BLACK);
        for (int i = 0; i < roads.length - 1; i++) {
            g.draw(roads[i]);
        }
        g.setStroke((new BasicStroke(3, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND)));
        g.setColor(groundColor);
        for (int i = 0; i < roads.length - 1; i++) {
            g.draw(roads[i]);
        }
    }
    
    private Color DarkerColor(Color sample, float ratio) {
        return new Color(
                (int) (((float) sample.getRed()) * ratio),
                (int) (((float) sample.getGreen()) * ratio),
                (int) (((float) sample.getBlue()) * ratio));
    }
    
    private void drawShapeOutline(GeneralPath path, Graphics2D g, Color color) {
        int minChannel = color.getGreen();
        
        if (color.getRed() < minChannel) {
            minChannel = color.getRed();
        }
        if (color.getBlue() < minChannel) {
            minChannel = color.getBlue();
        }
        int channnel = rand(-1 * minChannel, 0);
        //when channel becomer more negative line becomes thinner
        float maxSize = (float) rand(2, 6);
        float computatesStrokeSize = (float) ((maxSize + 0.5) - ((((double) -channnel) / ((double) minChannel)) * maxSize));
        g.setStroke(new BasicStroke(computatesStrokeSize, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND));
        g.setColor(new Color(color.getRed() + channnel, color.getGreen() + channnel, color.getBlue() + channnel));
        g.draw(path);
    }
    
    private void drawGrid(Graphics2D g, int dimensionX, int dimensionY) {
        g.setColor(new Color(255, 255, 255, 100));
        
        for (int j = 0; j < dimensionX; j += 50) {
            g.drawLine(0, j, dimensionX, j);
        }
        for (int j = 0; j < dimensionX; j += 50) {
            g.drawLine(j, 0, j, dimensionY);
        }
        
    }
    
    public void nextMap() {
        random = new Random(System.nanoTime());
        Graphics2D g = (Graphics2D) getGraphics();
        drawGround(g);
        // showMarkerCity(g);
        paint(g);
    }
}
