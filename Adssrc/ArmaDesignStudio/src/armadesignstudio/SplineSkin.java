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
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.JLabel;
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

public class SplineSkin extends Thread {

    HashMap<ObjectInfo, BoundingBox> objectBoundsCache = new HashMap<ObjectInfo, BoundingBox>();
    
    
    public SplineSkin(){
        
    }

    
    /**
     *
     *
     */
    public void splineGridSkin(Vector<ObjectInfo> objects){
        
        Vector curves = new Vector();
        Vector xCurves = new Vector();
        Vector yCurves = new Vector();
        Vector zCurves = new Vector();
        
        for (ObjectInfo obj : objects){
          if(obj.selected == true){
            System.out.println("Object Info: ");
            Object co = (Object)obj.getObject();
              if((co instanceof Curve) == true){
                System.out.println("Curve");

                Mesh mesh = (Mesh) obj.getObject(); // Object3D
                Vec3 [] verts = mesh.getVertexPositions();
                
                for (Vec3 vert : verts){
                    System.out.println("    vert: " + vert.x + " " + vert.y + "  " + vert.z );
                }
                  
                  BoundingBox bounds = getTranslatedBounds(obj);
                  double xSize = bounds.maxx - bounds.minx;
                  double ySize = bounds.maxy - bounds.miny;
                  double zSize = bounds.maxz - bounds.minz;
                  System.out.println( " bounds  x: " + bounds.minx + " " + bounds.maxx +
                                    " y: " + bounds.miny + " " + bounds.maxy +
                                     " z:  " + bounds.minz + " " + bounds.maxz);
                  
                  curves.addElement(verts);
                  
                  double x = bounds.maxx - bounds.minx;
                  double y = bounds.maxy - bounds.miny;
                  double z = bounds.maxz - bounds.minz;
                  //if( (bounds.maxx - bounds.minx)  ){
                      
                  //}

                //if(verts.length > maxPoints){
                    //maxPoints = verts.length;
                //}

              }
          }
        }
        System.out.println("XXX: " );
        
    }


    /**
    * getBounds
    *
    * Description: ObjectInfo.getBounds doesn't apply transfomations making its results inaccurate.
    */
    public BoundingBox getTranslatedBounds(ObjectInfo object){
        BoundingBox bounds = objectBoundsCache.get(object);
        if(bounds != null){
            return bounds;
        }
        LayoutModeling layout = new LayoutModeling();
        Object3D o3d = object.getObject().duplicate();
        bounds = o3d.getBounds();           // THIS DOES NOT WORK
        bounds.minx = 999; bounds.maxx = -999;
        bounds.miny = 999; bounds.maxy = -999;
        bounds.minz = 999; bounds.maxz = -999;
        CoordinateSystem c;
        c = layout.getCoords(object);
        Vec3 objOrigin = c.getOrigin();
        if((o3d instanceof Curve) == true){
            Mesh mesh = (Mesh) o3d; // obj.getObject(); // Object3D
            Vec3 [] verts = mesh.getVertexPositions();
            for(int i = 0; i < verts.length; i++){
                Vec3 point = verts[i];
                Mat4 mat4 = c.duplicate().fromLocal();
                mat4.transform(point);
                if(point.x < bounds.minx){
                    bounds.minx = point.x;
                }
                if(point.x > bounds.maxx){
                    bounds.maxx = point.x;
                }
                if(point.y < bounds.miny){
                    bounds.miny = point.y;
                }
                if(point.y > bounds.maxy){
                    bounds.maxy = point.y;
                }
                if(point.z < bounds.minz){
                    bounds.minz = point.z;
                }
                if(point.z > bounds.maxz){
                    bounds.maxz = point.z;
                }
            }
        }
        objectBoundsCache.put(object, bounds);
        return bounds;
    }
}
