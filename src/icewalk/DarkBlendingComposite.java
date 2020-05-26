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

/**
 *
 * @author bb
 */
public class DarkBlendingComposite implements Composite{

    @Override
    public CompositeContext createContext(ColorModel cm, ColorModel cm1, RenderingHints rh) {
        return new DarkBlendingCompositeContext();
    }
    
}
