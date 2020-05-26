/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package icewalk;

import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bb
 */

public class DarkBlendingCompositeContext implements CompositeContext{
     protected void checkRaster(Raster r) {
        if (r.getSampleModel().getDataType() != DataBuffer.TYPE_INT) {
            throw new IllegalStateException("Expected integer sample type");
        }
    }

    @Override
        public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
            int w = Math.min(src.getWidth(), dstIn.getWidth());
            int h = Math.min(src.getHeight(), dstIn.getHeight());

            int[] srcRgba = new int[4];
            int[] dstRgba = new int[4];

            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    src.getPixel(x, y, srcRgba);
                    dstIn.getPixel(x, y, dstRgba);
                    for (int i = 0; i < 3; i++) {
                        dstRgba[i] ^= srcRgba[i];
                    }
                    dstOut.setPixel(x, y, dstRgba);
                }
            }
        }

    private static int mixPixel(int x, int y) {
        int xb = (x) & 0xFF;
        int yb = (y) & 0xFF;
        int b = (xb * yb) / 255;

        int xg = (x >> 8) & 0xFF;
        int yg = (y >> 8) & 0xFF;
        int g = (xg * yg) / 255;

        int xr = (x >> 16) & 0xFF;
        int yr = (y >> 16) & 0xFF;
        int r = (xr * yr) / 255;

        int xa = (x >> 24) & 0xFF;
        int ya = (y >> 24) & 0xFF;
        int a = Math.min(255, xa + ya);

        return (b) | (g << 8) | (r << 16) | (a << 24);
    }


    @Override
    public void dispose() {
         try {
             super.finalize();
         } catch (Throwable ex) {
             Logger.getLogger(DarkBlendingCompositeContext.class.getName()).log(Level.SEVERE, null, ex);
         }
    }
    

//    @Override
//    public CompositeContext createContext(ColorModel cm, ColorModel cm1, RenderingHints rh) {
//        return this;
//    }
//
//    @Override
//    public void dispose() {
//    }
//
//    @Override
//    public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
//        int width = Math.min(src.getWidth(), dstIn.getWidth());
//        int height = Math.min(src.getHeight(), dstIn.getHeight());
//        int x, y;
//        int[] srcPixel = new int[3];
//        int[] dstPixel = new int[3];
//        int[] resultPixel = new int[3];
//        
//        for (y=0; y < height; y++) {
//            for (x=0; x < width; x++) {
//                src.getPixel(x,y,srcPixel);
//                dstIn.getPixel(x,y,dstPixel);
//                resultPixel[0]=(int)((float)dstPixel[0]*(float)(srcPixel[0]/255));
//                resultPixel[1]=(int)((float)dstPixel[1]*(float)(srcPixel[1]/255));
//                resultPixel[0]=(int)((float)dstPixel[2]*(float)(srcPixel[2]/255));
//                dstOut.setPixel(x,y,resultPixel);
//            }
//
//        }
//    }
    
}
