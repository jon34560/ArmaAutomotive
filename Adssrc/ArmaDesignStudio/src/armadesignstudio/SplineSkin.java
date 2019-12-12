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
     * splineGridSkin
     *
     * Description: Given selected splines, generate interpelated splines to fill in gaps and
     * generate a smoothed mesh based on the surface.
     */
    public void splineGridSkin(Scene scene, LayoutWindow layoutWindow, Vector<ObjectInfo> objects){
        System.out.println("splineGridSkin");
        LayoutModeling layout = new LayoutModeling();
        Vector curves = new Vector();
        Vector XyCurves = new Vector();
        Vector XzCurves = new Vector();
        Vector YzCurves = new Vector();
        Vector YxCurves = new Vector();
        Vector ZxCurves = new Vector();
        Vector ZyCurves = new Vector();
        
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

                    BoundingBox bounds = getTranslatedBounds(obj);
                    double xSize = bounds.maxx - bounds.minx;
                    double ySize = bounds.maxy - bounds.miny;
                    double zSize = bounds.maxz - bounds.minz;
                    //System.out.println( " bounds  x: " + bounds.minx + " " + bounds.maxx +
                    //                      " y: " + bounds.miny + " " + bounds.maxy +
                    //                      " z:  " + bounds.minz + " " + bounds.maxz);

                    curves.addElement(verts);

                    double x = bounds.maxx - bounds.minx;
                    double y = bounds.maxy - bounds.miny;
                    double z = bounds.maxz - bounds.minz;
                    if(xSize > ySize && xSize > zSize && ySize > zSize){ // X-y
                        //xCurves.addElement(verts);
                    }
                    if(xSize > ySize && xSize > zSize && zSize > ySize){ // X-z
                    
                    }
                    if(ySize > xSize && ySize > zSize && zSize > xSize){ //   Y-z orientation (major)-(minor)
                        // Insert in order
                        boolean inserted = false;
                        for(int i = 0; i < YzCurves.size(); i++){
                            Vec3 [] compareVerts = (Vec3 [])YzCurves.elementAt(i);
                            if(verts.length > 0 && compareVerts.length > 0 &&
                               verts[0].x < compareVerts[0].x){ // Sort by X axis
                                YzCurves.insertElementAt(verts, i);
                                inserted = true;
                                i = YzCurves.size() + 1; // break out of loop
                            }
                        }
                        if(!inserted){
                            YzCurves.addElement(verts);
                        }
                    }
                    // Y-x orientation (major)-(minor)
                    if(ySize > xSize && ySize > zSize && xSize > zSize){
                        
                    }
                    //
                    if(zSize > xSize && zSize > ySize && xSize > ySize){ // Z-x
                        // Insert in order
                        boolean inserted = false;
                        for(int i = 0; i < ZxCurves.size(); i++){
                            Vec3 [] compareVerts = (Vec3 [])ZxCurves.elementAt(i);
                            if(verts.length > 0 && compareVerts.length > 0 &&
                               verts[0].z < compareVerts[0].z){
                                ZxCurves.insertElementAt(verts, i);
                                inserted = true;
                                i = ZxCurves.size() + 1; // break out of loop
                            }
                        }
                        if(!inserted){
                            ZxCurves.addElement(verts);
                        }
                    }
                    if(zSize > xSize && zSize > ySize && ySize > xSize){ // Z-y
                    
                    }

                    //if(verts.length > maxPoints){
                    //maxPoints = verts.length;
                    //}

                }
            }
        }
        System.out.println("XXX: " );
        
        // yCurves -> order by position
        
        
        
        for(int i = 1; i < YzCurves.size(); i++){
            System.out.println("Yz spline pair " + i);
            Vec3 [] vertsA = (Vec3 [])YzCurves.elementAt(i-1);
            Vec3 [] vertsB = (Vec3 [])YzCurves.elementAt(i);
            
            int larger = Math.max(vertsA.length, vertsB.length);
            int smaller = Math.min(vertsA.length, vertsB.length);
            int pairing = (int)((float)Math.max(vertsA.length, vertsB.length) / (float)Math.min(vertsA.length, vertsB.length));
            System.out.println(" pairing " + pairing + " a " + vertsA[0].x + " b " + vertsB[0].x);
            
            double xNew = 0;
            // new spline curve
            //Curve midCurve = new Curve();
            Vec3[] midSpline = new Vec3[larger];
            float midSplineSmoothness[] = new float[larger];
            
            for(int j = 0; j < Math.max(vertsA.length, vertsB.length); j++){
                int aIndex = vertsA.length == larger ? j : j / (int)( (float)vertsB.length / (float)vertsA.length);
                int bIndex = vertsB.length == larger ? j : j / (int)( (float)vertsA.length / (float)vertsB.length);
                
                // Bounds check
                if(aIndex >= vertsA.length){
                    aIndex = vertsA.length - 1;
                }
                if(bIndex >= vertsB.length){
                    bIndex = vertsB.length - 1;
                }
                
                //System.out.println(" j  " + j +
                //                   " a " + vertsA.length + " " + (aIndex) +
                //                   " b " + vertsB.length + " " + (bIndex)  );
                
                // calculate misdpint between two paired points between two splines
                Vec3 v = new Vec3();
                v.x = (vertsA[aIndex].x + vertsB[bIndex].x) / 2;
                v.y = (vertsA[aIndex].y + vertsB[bIndex].y) / 2;
                v.z = (vertsA[aIndex].z + vertsB[bIndex].z) / 2;
                
                midSpline[j] = v;
                midSplineSmoothness[j] = 1;
            }
            
            Curve midCurve = new Curve(midSpline, midSplineSmoothness, 1, false);
            //CoordinateSystem coords = new CoordinateSystem(new Vec3(0.0, 0.0, Camera.DEFAULT_DISTANCE_TO_SCREEN), new Vec3(0.0, 0.0, -1.0), Vec3.vy());
            ObjectInfo midCurveInfo = new ObjectInfo(midCurve, new CoordinateSystem(), "midcurve");
            
            scene.addObject(midCurveInfo, null);
            layoutWindow.updateImage();
            layoutWindow.updateMenus();
            layoutWindow.rebuildItemList();
                
            
        }
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
