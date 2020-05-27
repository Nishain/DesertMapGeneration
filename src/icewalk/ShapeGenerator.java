/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package icewalk;

import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bb
 */
public class ShapeGenerator {
     
    private double rotaionRate=10;
    private double maximumRotationDegree = 361;
    private boolean autoClosePath = true;
    private double maximumYOffsetTolerance=50;
    private double initalRotation=0;
    private boolean pendingTransformation=false;
    private AffineTransform transformer;
    private double upperMinimumY=0;
    private long n;
    public ShapeGenerator() {
        random = new Random();
        
    }
    
    
    private double maintainCenterY(double centerVariable, double centerStandard) {
        int randomDeviation;
        do{
           randomDeviation=rand(-10, 10); 
        }while(randomDeviation==0);
        return Math.abs(centerStandard - centerVariable) > maximumYOffsetTolerance ? centerStandard +  randomDeviation: centerVariable +randomDeviation;
    }//50
    
    private double rotatedDegree = 0;
    private double movedX;
    private Random random;
    private int[] salt=new int[]{2,1,3,4,0};
    public int rand(int lowerBound,int higherBound){
        return random.nextInt(higherBound-lowerBound)+lowerBound;
       
    }
    private boolean ranBool(){
        return random.nextBoolean();
    }
    private double pointToNextAngle(double lastMovedOffset) {
        rotatedDegree += rotaionRate;
        double offset;
        if (Double.isNaN(lastMovedOffset)) {
            offset = rand(5, 20);
            if (rotatedDegree > 180) {
                offset *= -1;
            }
            movedX += offset;
            return offset;
        } else {
            movedX += lastMovedOffset;
            return Double.NaN;
        }
    }
    
    public GeneralPath drawSinglePath(int startX,int startY,int noOfBends){
        
        GeneralPath p = new GeneralPath();
        double[] point;
        p.moveTo(startX, startY);
        int rotationDegree = rand(-5, 5);
        int distance=rand(5, 20);
        point = new double[]{startX,startY};
        double angle;
        for (int i = 1; i < noOfBends; i++) {
            rotationDegree += rand(-45,45);
                distance=10;
                angle=Math.toRadians(rotationDegree);
                point[0]=point[0] + Math.sin(angle)*distance;
                point[1]=point[1] + Math.cos(angle)*distance;
            p.lineTo(point[0], point[1]);
        }
        return p;
    }
    private void drawRootBranch(int startX,int startY,int startAngle,ArrayList<GeneralPath[]> cracks,boolean isRootBranching){
        int rotationDegree = startAngle;
        double[] point = new double[]{startX,startY,startX,startY};
        GeneralPath root= new GeneralPath();
        GeneralPath outlineShape = new GeneralPath();
        int distance,distanceToOuline;
        double angle,Xangle,Yangle;
        root.moveTo(startX, startY);
        int count=branchConfigurations.rootBranchNoOfBends;
        double[][] outlineCoordinates=new double[count*2][];
        outlineCoordinates[0]=outlineCoordinates[count]=new double[]{startX, startY};
        
        ArrayList<GeneralPath> branch = new ArrayList<>();
        for (int i = 1; i < count; i++) {
                distance=10;
                distanceToOuline=rand(10, 20);
                angle=Math.toRadians(rotationDegree);
                Xangle=Math.toRadians(rotationDegree-90);
                Yangle=Math.toRadians(rotationDegree+90);//Math.toRadians(angle-(angle<0?-90:90));
                
               
                point[0]=point[0] + Math.sin(angle)*distance;
                point[1]=point[1] + Math.cos(angle)*distance;
                
                 outlineCoordinates[i]=new double[]{
                    point[0] + Math.sin(Xangle)*distanceToOuline,
                    point[1] + Math.cos(Xangle)*distanceToOuline,
                };
                
                outlineCoordinates[count+i]=new double[]{                  
                    point[0] + Math.sin(Yangle)*distanceToOuline,
                    point[1] + Math.cos(Yangle)*distanceToOuline,
                }; 
                if(i==(count-1)){
                 outlineCoordinates[count]=new double[]{                  
                    point[0],point[1]
                };
                outlineCoordinates[outlineCoordinates.length-1]=new double[]{                  
                    point[0],point[1]
                }; 
                }
                root.lineTo(point[0], point[1]);
                
                  if(branchConfigurations.isRandom?ranBool():true)                       
                        drawBranch((int)point[0],
                            (int) point[1],
                            ((ranBool()?-1:1)*45)+rand(0, 45), 
                            1,
                            branch);
                    if(isRootBranching && rand(0, 100)>90){
                        branchConfigurations.isMainBranch = false;
                        drawRootBranch((int)point[0],
                            (int) point[1],
                            ((ranBool()?-1:1)*45)+rand(0, 45), 
                            cracks,false);
                    }
                
                rotationDegree += rand(-45,45);
        }
        
        outlineShape.moveTo(outlineCoordinates[0][0], outlineCoordinates[0][1]);
        for (int i = 1; i <= count; i++) {
            outlineShape.lineTo(
                    outlineCoordinates[i][0], 
                    outlineCoordinates[i][1]);
        }
        for (int i = outlineCoordinates.length-1; i > count; i--) {
            outlineShape.lineTo(
                    outlineCoordinates[i][0], 
                    outlineCoordinates[i][1]);
        }
        //outlineShape.closePath();
        
        branch.add(root);
        branch.add(outlineShape);
        cracks.add(branch.toArray(new GeneralPath[0]));
    }
    public GeneralPath drawLine(int startX,int startY,int count){
        int rotationDegree = rand(0,360);
        double[] point = new double[]{startX,startY,startX,startY};
        GeneralPath outlineShape = new GeneralPath();
        int distance,distanceToOuline;
        double angle,Xangle,Yangle;
        double[][] outlineCoordinates=new double[count*2][];
        outlineCoordinates[0]=outlineCoordinates[count]=new double[]{startX, startY};
        
        for (int i = 1; i < count; i++) {
                distance=rand(10,20);
                distanceToOuline=rand(12, 25);
                angle=Math.toRadians(rotationDegree);
                Xangle=Math.toRadians(rotationDegree-90);
                Yangle=Math.toRadians(rotationDegree+90);//Math.toRadians(angle-(angle<0?-90:90));
                
               
                point[0]=point[0] + Math.sin(angle)*distance;
                point[1]=point[1] + Math.cos(angle)*distance;
                
                 outlineCoordinates[i]=new double[]{
                    point[0] + Math.sin(Xangle)*distanceToOuline,
                    point[1] + Math.cos(Xangle)*distanceToOuline,
                };
                
                outlineCoordinates[count+i]=new double[]{                  
                    point[0] + Math.sin(Yangle)*distanceToOuline,
                    point[1] + Math.cos(Yangle)*distanceToOuline,
                }; 
                if(i==(count-1)){
                 outlineCoordinates[count]=new double[]{                  
                    point[0],point[1]
                };
                outlineCoordinates[outlineCoordinates.length-1]=new double[]{                  
                    point[0],point[1]
                }; 
                }
               
                rotationDegree += rand(-45,45);
        }
        
        outlineShape.moveTo(outlineCoordinates[0][0], outlineCoordinates[0][1]);
        for (int i = 1; i <= count; i++) {
            outlineShape.lineTo(
                    outlineCoordinates[i][0], 
                    outlineCoordinates[i][1]);
        }
        for (int i = outlineCoordinates.length-1; i > count; i--) {
            outlineShape.lineTo(
                    outlineCoordinates[i][0], 
                    outlineCoordinates[i][1]);
        }
        outlineShape.closePath();
        return outlineShape;
    }
    static class branchConfig{
        private static branchConfig defualtSettings=new branchConfig();
        private boolean isRandom=false;
        private boolean isMainBranch=true;
        public void resetToDefault(){
            Field[] fields=this.getClass().getFields();
            for (int i = 0; i < fields.length; i++) {
                try {
                    fields[i].set(this,fields[i].get(defualtSettings));
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    Logger.getLogger(ShapeGenerator.class.getName()).log(Level.SEVERE, null, ex);
                } 
                
            }
        }
        public int noOfBends=5,maximumBranchDepth=2,bendLength=3,rootBranchNoOfBends=50;

        public branchConfig setIsMainBranch(boolean isMainBranch) {
            this.isMainBranch = isMainBranch;
            return this;
        }

        
        
        public branchConfig setRootBranchNoOfBends(int rootBranchNoOfBends) {
            this.rootBranchNoOfBends = rootBranchNoOfBends;
            return this;
        }

        public branchConfig setIsRandom(boolean isRandom){
            this.isRandom=isRandom;
            return this;
        }
        
        public branchConfig setNoOfBends(int noOfBends) {
            this.noOfBends = noOfBends;
            return this;
        }

        public branchConfig setMaximumBranchDepth(int maximumBranchDepth) {
            this.maximumBranchDepth = maximumBranchDepth;
            return this;
        }

        public branchConfig setBendLength(int bendLength) {
            this.bendLength = bendLength;
            return this;
        }
        
    }
    public branchConfig branchConfigurations = new branchConfig();
    private void drawBranch(int startX,int startY,int startAngle,int depth,ArrayList<GeneralPath> cracks){
        int newDepth = depth;
        if(branchConfigurations.isRandom?depth >= rand(0,branchConfigurations.maximumBranchDepth)
                :depth==branchConfigurations.maximumBranchDepth)
            return;
        newDepth++;
        int rotationDegree = startAngle;
        double[] point = new double[]{startX,startY};
        GeneralPath root= new GeneralPath();
        int distance;
        double angle;
        root.moveTo(startX, startY);
        for (int i = 1; i < branchConfigurations.noOfBends; i++) {
                distance=branchConfigurations.bendLength;
                angle=Math.toRadians(rotationDegree);
                point[0]=point[0] + Math.sin(angle)*distance;
                point[1]=point[1] + Math.cos(angle)*distance;
                root.lineTo(point[0], point[1]);
                if(branchConfigurations.isRandom?ranBool():true){
                    drawBranch((int)point[0],
                            (int) point[1],
                            rotationDegree+((ranBool()?-1:1)*45)+rand(0, 45), 
                            newDepth,
                            cracks);
                }
                rotationDegree += rand(-45,45);
        }
        cracks.add(root);
    }
    public GeneralPath[][] drawCracks (int startX,int startY){
        ArrayList<GeneralPath[]> cracks = new ArrayList<>();
        drawRootBranch(startX, startY, 90,cracks,true);
        return cracks.toArray(new GeneralPath[0][]);
    }
    public GeneralPath[] drawRoadPath(int startX,int startY){
        ArrayList<GeneralPath[]> cracks = new ArrayList<>();
        drawRootBranch(startX, startY, 90,cracks,false);
        return cracks.get(0);
    }
    private double[] getArcPoint(double centerPositionY, double lastY) {
        double[] values = new double[3];
        double radius = rand(100, 140);
        double Yoffset = Double.NaN;
        double angle = rotatedDegree + rand(0, 4);
        double X = -(Math.cos(Math.toRadians(angle)) * radius) + movedX,
                Y = (Double.isNaN(lastY) ? Yoffset = (centerPositionY + rand(-100, -50)) : lastY) - ((double)(Math.sin(Math.toRadians(angle)) * radius));

        values[0] =(float) X + 200;
        values[1] =(float) Y;
        values[2] =(float) Yoffset;
        return values;
    }
     public GeneralPath[] generateShape( double startingX, double startingY, int rotate, double scale, double... scaleY) {
        GeneralPath p=new GeneralPath();
        GeneralPath p2 = new GeneralPath();
        rotatedDegree = initalRotation;
        double centerY = 500;
        movedX = 100;
        double[] coord=null;
        int count = 0;
        int partialLimit = rand(0, (int) maximumRotationDegree);
        double lastYoffset = Double.NaN, lastXoffset = Double.NaN;
        while (rotatedDegree < maximumRotationDegree) {
            
            centerY = maintainCenterY(centerY, 500);
            coord = getArcPoint(centerY, lastYoffset);
            if (rotatedDegree == initalRotation) {
                p2.moveTo( coord[0], coord[1]);
                p.moveTo( coord[0], coord[1]);
            }
            /*this condition only works when randomPartialLimit is
            divisible by rotaionRate or else this condition will
            get ignore*/
            /*if (rotatedDegree > 180 && maximumRotationDegree>180) {
                while (coord[1] < upperMinimumY) {
                    centerY = maintainCenterY(centerY, 500);
                    coord = getArcPoint(centerY, lastYoffset);
                    lastYoffset = coord[2];
                }
            }*/
            lastYoffset = coord[2];
            if (rotatedDegree <= 180 && coord[1] > upperMinimumY) {
                upperMinimumY = coord[1];
            }
            lastXoffset = pointToNextAngle(lastXoffset);
            if (rotatedDegree != 0) {
                if(rotatedDegree < partialLimit)
                    p2.lineTo(coord[0], coord[1]);
                p.lineTo(coord[0], coord[1]);
            }
        }
        
            p.closePath(); //attach the end to the start
       
        transformer = new AffineTransform();
       // transformer.tr
      
               
        
       p.transform(transformer);       
       transformer.setToScale(scale, (scaleY.length == 0 ? scale : scaleY[0]));
       p.transform(transformer);
       p2.transform(transformer);
       Rectangle2D bounds=p.getBounds2D();
       transformer.setToTranslation(startingX - bounds.getX(), startingY - bounds.getY());
       transformer.rotate(rotate, bounds.getCenterX(), bounds.getCenterY());
       p.transform(transformer);
       p2.transform(transformer);
       //transformer.setToTranslation(startingX - bounds.getX(), startingY - bounds.getY());
       // transformer.translate(startingX,startingY);
        
        return new GeneralPath[]{p,p2};    
        //if(!pendingTransformation)
        //    return transformer.createTransformedShape(p);
        //return p;
    }
}
