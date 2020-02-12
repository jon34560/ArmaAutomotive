/* Copyright (C) 2019 by Jon Taylor

This program is free software; you can redistribute it and/or modify it under the
terms of the GNU General Public License as published by the Free Software
Foundation; either version 2 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE.  See the GNU General Public License for more details. */

package armadesignstudio;

import java.util.*;
import armadesignstudio.math.*;
import armadesignstudio.object.*;
import armadesignstudio.view.CanvasDrawer;
import java.io.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import buoy.widget.*;
import armadesignstudio.ui.*;
//import java.awt.Color;
//import java.awt.Dimension;
//import javax.swing.JOptionPane;
//import javax.swing.JPanel;
//import javax.swing.UIManager;
//import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import java.awt.BorderLayout;
import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.JFrame;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import armadesignstudio.texture.*;

public class JoinSplines {
    
    public JoinSplines(){
    }
    
    /**
     * joinSplines
     *
     * Description: given a selection of spline curves. join them into one continous new curve object
     */
    public void joinSplines(Scene scene, LayoutWindow layoutWindow, Vector<ObjectInfo> objects){
        Vector<Vec3[]> splines = new <Vec3[]>Vector();
        LayoutModeling layout = new LayoutModeling();
        int joinedSplineLength = 0;
        for (ObjectInfo obj : objects){
            if(obj.selected == true){
                //System.out.println("Object Info: ");
                Object co = (Object)obj.getObject();
                if((co instanceof Curve) == true){
                    //System.out.println("Curve");

                    Mesh mesh = (Mesh) obj.getObject(); // Object3D
                    Vec3 [] verts = mesh.getVertexPositions();

                    // translate local coords with obj location.
                    CoordinateSystem c;
                    c = layout.getCoords(obj);
                    Vec3 objOrigin = c.getOrigin();
                    for (Vec3 vert : verts){
                        Mat4 mat4 = c.duplicate().fromLocal();
                        mat4.transform(vert);
                        //System.out.println("    vert: " + vert.x + " " + vert.y + "  " + vert.z );
                    }
                    splines.addElement(verts);
                    joinedSplineLength += verts.length;
                }
            }
        }
        
        // Which ends connect
        
        // Generate new spline with combined
        Vec3[] joinedSpline = new Vec3[joinedSplineLength];
        for(int i = 0; i < splines.size(); i++){
            Vec3 [] spline = splines.elementAt(i);
            //joinedSpline[ ] =
        }
        
        /*
        Curve curve = getCurve(midCurvePoints);
        ObjectInfo curveObject = new ObjectInfo(curve, new CoordinateSystem(), "XXX ");
        
        scene.addObject(curveObject, null);
        
        layoutWindow.updateImage();
        layoutWindow.updateMenus();
        layoutWindow.rebuildItemList();
         */
    }
    
    public Curve getCurve(Vec3[] points){
        float smooths[] = new float[points.length];
        for(int i = 0; i < points.length; i++){
            smooths[i] = 1.0f;
        }
        Curve curve = new Curve(points, smooths, Mesh.APPROXIMATING, false);
        return curve;
    }
    
    
}
