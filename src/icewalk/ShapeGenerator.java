/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package icewalk;

import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

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
        /*n=System.nanoTime()-n;
        String nanoString = Long.toString(n%100000);
        String jumbledString="";
        for(int i:salt){
            try{
                jumbledString+=nanoString.charAt(i);
            }catch(StringIndexOutOfBoundsException e){
                System.out.println("string "+nanoString);
                throw e;
            }
        }
        random
        String nanoString = Long.toString(n);
        nanoString = nanoString.substring(0, 1);
        int lastTwoDigits = (int) (Long.parseInt(nanoString)%10000);
        int lastTwoDigits=(int) (Long.parseLong(jumbledString)%100000);
        System.out.println("val"+lastTwoDigits);
        int val=((int) ((higherBound-lowerBound)*(((double)lastTwoDigits)/100)));
        
        return val+lowerBound;*/
        //return ThreadLocalRandom.current().nextInt(lowerBound,higherBound);
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
