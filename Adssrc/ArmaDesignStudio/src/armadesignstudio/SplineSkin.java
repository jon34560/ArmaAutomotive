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
import armadesignstudio.texture.*;

public class SplineSkin extends Thread {
    public static int X = 1;
    public static int Y = 2;
    public static int Z = 3;
    HashMap<ObjectInfo, BoundingBox> objectBoundsCache = new HashMap<ObjectInfo, BoundingBox>();
    
    public SplineSkin(){
        // Curve.subdivideCurve(int times)
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
                        insertOrdered(XyCurves, verts, SplineSkin.Z);
                    }
                    if(xSize > ySize && xSize > zSize && zSize > ySize){ // X-z
                        insertOrdered(XzCurves, verts, SplineSkin.Y);
                    }
                    if(ySize > xSize && ySize > zSize && zSize > xSize){ //   Y-z orientation (major)-(minor)
                        insertOrdered(YzCurves, verts, SplineSkin.X);
                    }
                    if(ySize > xSize && ySize > zSize && xSize > zSize){ // Y-x orientation (major)-(minor)
                        insertOrdered(YxCurves, verts, SplineSkin.Z);
                    }
                    if(zSize > xSize && zSize > ySize && xSize > ySize){ // Z-x
                        insertOrdered(ZxCurves, verts, SplineSkin.Z);
                    }
                    if(zSize > xSize && zSize > ySize && ySize > xSize){ // Z-y
                        insertOrdered(ZyCurves, verts, SplineSkin.X);
                    }
                    //if(verts.length > maxPoints){
                    //  maxPoints = verts.length;
                    //}
                }
            }
        }
        
        // subdividing the curves increases accuracy when equalizing point counts and possibly mid curves
        
         XyCurves = subdivideCurves(XyCurves, 4);
         XzCurves = subdivideCurves(XzCurves, 4);
         YxCurves = subdivideCurves(YxCurves, 4);
         YzCurves = subdivideCurves(YzCurves, 4);
         ZxCurves = subdivideCurves(ZxCurves, 4);
         ZyCurves = subdivideCurves(ZyCurves, 4);
        
        
        // XyCurves
        
        
        // Yz generate curve between parallel pairs (Works to some degree)
        Vector addedCurves = new Vector();
        for(int i = 1; i < YzCurves.size(); i++){
            System.out.println("Yz spline pair " + i);
            Vec3 [] vertsA = (Vec3 [])YzCurves.elementAt(i-1);
            Vec3 [] vertsB = (Vec3 [])YzCurves.elementAt(i);
            int larger = Math.max(vertsA.length, vertsB.length);
            int smaller = Math.min(vertsA.length, vertsB.length);
            int pairing = (int)((float)Math.max(vertsA.length, vertsB.length) / (float)Math.min(vertsA.length, vertsB.length));
            System.out.println(" pairing " + pairing + " a " + vertsA[0].x + " b " + vertsB[0].x + "  YzCurves.size() " + YzCurves.size());
            //double xNew = 0;
            // new spline curve
            
            Vector midPoints = new Vector();
            Vector scanCurves = new Vector();
            scanCurves.addAll(XzCurves);
            scanCurves.addAll(XyCurves);
            System.out.println("XzCurves "+  XzCurves.size() + "  XyCurves "  + XyCurves.size() );
            
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
                midPoints.addElement(v);
                // Calculate Z offset from X-z or X-y curves in closest region
                
                double zOffset = getZOffset(vertsA[aIndex], vertsB[bIndex], v, scanCurves);
                v.z += zOffset;
                midSpline[j] = v;
                midSplineSmoothness[j] = 1.0f;
            }
            
            // Experimental
            // mid curve by closest perpendicular curves matching mid points (v)
            
            Vec3[] midCurve2 = generateMidCurve( midPoints, scanCurves, scene, layoutWindow);
            
            addedCurves.addElement(midCurve2);
            
            
            //addedCurves.addElement(midSpline);
        }
        for(int i = 0; i < addedCurves.size(); i++){
            Vec3[] spline = (Vec3[])addedCurves.elementAt(i);
            insertOrdered(YzCurves, spline, SplineSkin.X); // Add new curve
        }
        // Equalize point count in each group of curves. Equal points is required if skin to mesh is used.
        equalizeCurvePointCounts(YzCurves, scene);
        // Add curves to scene (Optional)
        
        for(int i = 0; i < YzCurves.size(); i++){
            Vec3[] spline = (Vec3[])YzCurves.elementAt(i);
            
            Curve midCurve = getCurve(spline); //  new Curve(spline, midSplineSmoothness, Mesh.APPROXIMATING, false);
            ObjectInfo midCurveInfo = new ObjectInfo(midCurve, new CoordinateSystem(), "midcurve Yz " + i);
            scene.addObject(midCurveInfo, null);
            
            layoutWindow.updateImage();
            layoutWindow.updateMenus();
            layoutWindow.rebuildItemList();
        }
        
        // Skin to mesh
        skinMesh(YzCurves, scene, layoutWindow, "Yz");
        
        
        //
        // Xz
        //
        addedCurves.clear();
        for(int i = 1; i < XzCurves.size(); i++){
            //System.out.println("Yz spline pair " + i);
            Vec3 [] vertsA = (Vec3 [])XzCurves.elementAt(i-1);
            Vec3 [] vertsB = (Vec3 [])XzCurves.elementAt(i);
            int larger = Math.max(vertsA.length, vertsB.length);
            int smaller = Math.min(vertsA.length, vertsB.length);
            int pairing = (int)((float)Math.max(vertsA.length, vertsB.length) / (float)Math.min(vertsA.length, vertsB.length));
            //System.out.println(" pairing " + pairing + " a " + vertsA[0].x + " b " + vertsB[0].x);
            //double xNew = 0;
            
            Vector scanCurves = new Vector(); // rename -> perpendicular curves
            scanCurves.addAll(YzCurves);
            scanCurves.addAll(YxCurves);
            
            // test midCurve
            Vector midPoints = new Vector();
            
            // new spline curve
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
                midPoints.addElement(v);
                // Calculate Z offset from X-z or X-y curves in closest region
                
                double zOffset = getZOffset(vertsA[aIndex], vertsB[bIndex], v, scanCurves);
                v.z += zOffset;
                midSpline[j] = v;
                midSplineSmoothness[j] = 1.0f;
            }
            
            // Experimental
            // mid curve by closest perpendicular curves matching mid points (v)
            //generateMidCurve( midPoints, scanCurves, scene, layoutWindow);
            
            addedCurves.addElement(midSpline);
            //insertOrdered(XzCurves, midSpline, SplineSkin.Y); // recursive
        }
        for(int i = 0; i < addedCurves.size(); i++){
            Vec3[] spline = (Vec3[])addedCurves.elementAt(i);
            insertOrdered(XzCurves, spline, SplineSkin.Z); // Add new curve
        }
        // Equalize point count in each group of curves. Equal points is required if skin to mesh is used.
        equalizeCurvePointCounts(XzCurves, scene);
        //skinMesh(XzCurves, scene, layoutWindow, "Xz");
        
        
        
        // YxCurves
        
        
        // ZxCurves
        
        
        // ZxCurves
        
    }

    /**
     * insertOrdered
     *
     * Description:
     *
     * @param
     */
    public void insertOrdered(Vector curves, Vec3 [] verts, int compareAxis){
        // Insert in order
        boolean inserted = false;
        for(int i = 0; i < curves.size(); i++){
            Vec3 [] compareVerts = (Vec3 [])curves.elementAt(i);
            if(verts.length > 0 && compareVerts.length > 0){
                if(compareAxis == SplineSkin.X && verts[0].x < compareVerts[0].x){ // Sort by X axis
                    curves.insertElementAt(verts, i);
                    inserted = true;
                    i = curves.size() + 1; // break out of loop
                }
                if(compareAxis == SplineSkin.Y && verts[0].y < compareVerts[0].y){ // Sort by X axis
                    curves.insertElementAt(verts, i);
                    inserted = true;
                    i = curves.size() + 1; // break out of loop
                }
                if(compareAxis == SplineSkin.Z && verts[0].z < compareVerts[0].z){ // Sort by X axis
                    curves.insertElementAt(verts, i);
                    inserted = true;
                    i = curves.size() + 1; // break out of loop
                }
            }
        }
        if(!inserted){
            curves.addElement(verts);
        }
    }
    
    /**
     * getZOffset
     *
     * Description: Scan given a pair of points to find curvature data from a vector of curves.
     *
     * @param Vec3 a - The reference point of a segment to scan curves for diagonal curvature data.
     * @param Vec3 b - second reference point.
     * @param Vec3 mid - mid point between a and b.
     * @param Vector curves - list of perpendicular curves. Find segments between a and b.
     */
    public double getZOffset(Vec3 a, Vec3 b, Vec3 mid, Vector curves){
        double result = 0;
        //System.out.println("  getZOffset   a: " + a.x + " " + a.y + " " + a.z +
        //                   "     b: " + b.x + " " + b.y + " " + b.z +
        //                   "     mid " +  mid.x + " " + mid.y + " " + mid.z);
        Vec3 [] matchingCurve = null;
        Vec3 closestAVec = null;
        Vec3 closestBVec = null;
        Vec3 closestMidVec = null;
        int closestAPointIndex = -1;
        int closestBPointIndex = -1;
        int closestMidPointIndex = -1;
        int matchingCurveIndex = -1;
        int matchingCurveIndexA = -1;
        int matchingCurveIndexB = -1;
        int matchingCurveIndexM = -1;
        double closestADist = Double.MAX_VALUE;
        double closestBDist = Double.MAX_VALUE;
        double closestMidDist = Double.MAX_VALUE;
        // NOTE: This can incorrectly match points for a and b on different curves which wont work well...
        
        for(int i = 0; i < curves.size(); i++){
            Vec3 [] compareVerts = (Vec3 [])curves.elementAt(i);
            
            //closestADist = Double.MAX_VALUE;
            //closestBDist = Double.MAX_VALUE;
            //closestMidDist = Double.MAX_VALUE;
            for(int v = 0; v < compareVerts.length; v++){
                Vec3 vec = compareVerts[v];
                
                //
                
                double d = vec.distance(a);
                if(d < closestADist){
                    closestADist = d;
                    closestAVec = vec;
                    closestAPointIndex = v;
                    matchingCurve = compareVerts;
                    //matchingCurveIndex = i;
                    matchingCurveIndexA = i;
                }
                d = vec.distance(b);
                if(d < closestBDist){
                    closestBDist = d;
                    closestBVec = vec;
                    closestBPointIndex = v;
                    matchingCurve = compareVerts;
                    //matchingCurveIndex = i;
                    matchingCurveIndexB = i;
                }
                d = vec.distance(mid);
                //System.out.println(" mid " + d);
                if(d < closestMidDist){
                    //System.out.println(". " + closestMidPointIndex);
                    closestMidDist = d;
                    closestMidVec = vec;
                    closestMidPointIndex = v;
                    
                    matchingCurveIndexM = i;
                }
            }
        }
        
        TreeMap<Double, Double> matches = new TreeMap<Double, Double>();
        //Vector matchVector = new Vector();
        double bestMatchDistance = Double.MAX_VALUE;
        double bestMatchOffset = 0;
        for(int i = 0; i < curves.size(); i++){
            Vec3 [] curveVerts = (Vec3 [])curves.elementAt(i);
            closestADist = Double.MAX_VALUE;
            closestBDist = Double.MAX_VALUE;
            closestMidDist = Double.MAX_VALUE;
            closestAPointIndex = -1;
            closestBPointIndex = -1;
            closestMidPointIndex = -1;
            for(int v = 0; v < curveVerts.length; v++){
                Vec3 vec = curveVerts[v];
                double d = vec.distance(a);
                if(d < closestADist){
                    closestADist = d;
                    closestAVec = vec;
                    closestAPointIndex = v;
                }
                d = vec.distance(b);
                if(d < closestBDist){
                    closestBDist = d;
                    closestBVec = vec;
                    closestBPointIndex = v;
                }
                d = vec.distance(mid);
                if(d < closestMidDist){
                    closestMidDist = d;
                    closestMidVec = vec;
                    closestMidPointIndex = v;
                }
            }
            
            double patternMatchDistance = closestADist + closestBDist + closestMidDist;
            
            //System.out.println(" a " + closestAPointIndex + " " + closestBPointIndex + " " + closestMidPointIndex + " " );
            Vector match = new Vector();
            match.addElement(closestAVec);
            match.addElement(closestMidVec);
            match.addElement(closestBVec);
            //matches.put();
            if(closestAPointIndex != closestBPointIndex &&
               closestAPointIndex != closestMidPointIndex &&
               closestBPointIndex != closestMidPointIndex){
                //System.out.println(".");
                Vec3 closestMidV = new Vec3();
                closestMidV.x = (closestAVec.x + closestBVec.x) / 2;
                closestMidV.y = (closestAVec.y + closestBVec.y) / 2;
                closestMidV.z = (closestAVec.z + closestBVec.z) / 2;
                
                double r = closestMidVec.z - closestMidV.z;
                
                if(patternMatchDistance < bestMatchDistance){
                    bestMatchDistance = patternMatchDistance;
                    bestMatchOffset = r;
                    result = bestMatchOffset;
                    //System.out.println(" r ");
                }
                //matchVector.addElement( ); //  (patternMatchDistance, r)
                matches.put( (patternMatchDistance),  (r));
            }
        }
        
        /*
        System.out.println("  ");
        Vector distances = new Vector();
        Vector offsets = new Vector();
        for(Map.Entry<Double,Double> entry : matches.entrySet()) {
          Double key = entry.getKey();
          Double value = entry.getValue();
            distances.addElement(key);
            offsets.addElement(value);
          System.out.println(" d " + key + " =>   r " + value);
        }
        
        double largestDistance = (double)distances.elementAt(distances.size() - 1);
        for(int i = 0; i < distances.size(); i++){
            double distance = (double)distances.elementAt(i);
            double offset = (double)offsets.elementAt(i);
            System.out.println("   distance " + distance + " " + offset + " " + (1-( distance/largestDistance)) );
        }
        // if two closest distances are within 5% of each other compared to the farthest match distance, average the offsets.
        if(distances.size() > 1){
            double closestDist = ((double)distances.elementAt(0));
            double secondClosestDist = ((double)distances.elementAt(1));
            if(   (1-( secondClosestDist/largestDistance))  -   (1-( closestDist/largestDistance))  < 0.05 ){
                //System.out.println("****");
                
                double closestOffset = ((double)offsets.elementAt(0));
                double secondClosestOffet = ((double)offsets.elementAt(1));
                double avg = (closestOffset + secondClosestOffet) / 2;
                //result = avg
                
                System.out.println("**** " + avg);
            }
        }
        */
        
        if(matchingCurve != null && closestAVec != null && closestBVec != null && closestAPointIndex != closestBPointIndex &&
           matchingCurveIndexA == matchingCurveIndexB && matchingCurveIndexB == matchingCurveIndexM){
            //System.out.println("     found a: " + closestAPointIndex + "  b: " + closestBPointIndex +
            //                   " curve_i " + matchingCurveIndex+
            //                   " mid  "  + closestMidPointIndex +
            //                   "   l: " + matchingCurve.length );
            //System.out.println(" matchingCurveIndexA "+ matchingCurveIndexA + " " + matchingCurveIndexB + " " +  matchingCurveIndexM);
            // calculate
            Vec3 closestMidV = new Vec3();
            closestMidV.x = (closestAVec.x + closestBVec.x) / 2;
            closestMidV.y = (closestAVec.y + closestBVec.y) / 2;
            closestMidV.z = (closestAVec.z + closestBVec.z) / 2;
        //    result = closestMidVec.z - closestMidV.z; // Math.abs(); // and divide difference by distance between mid_closest and actual
            //double midDiff = closestMidVec.distance(closestMidV);
            //System.out.println(" offset  " + closestMidVec.z + " c.z  "  + closestMidV.z + " md " + midDiff + "  result " + result);
        }
        return result;
    }
    
    public double getXOffset(Vec3 a, Vec3 b, Vec3 mid, Vector curves){
        double result = 0;
        return result;
    }
    
    public double getYOffset(Vec3 a, Vec3 b, Vec3 mid, Vector curves){
        double result = 0;
        return result;
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
        //bounds.minx = Float.MAX_VALUE; bounds.maxx = Float.MIN_VALUE;
        //bounds.miny = Float.MAX_VALUE; bounds.maxy = Float.MIN_VALUE;
        //bounds.minz = Float.MAX_VALUE; bounds.maxz = Float.MIN_VALUE;
        
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
    
    public Curve getCurve(Vec3[] points){
        float smooths[] = new float[points.length];
        for(int i = 0; i < points.length; i++){
            smooths[i] = 1.0f;
        }
        Curve curve = new Curve(points, smooths, Mesh.APPROXIMATING, false);
        return curve;
    }
    
    
    /**
     * equalizeCurvePointCounts
     *
     * Description:
     */
    public Curve equalizeCurvePointCounts(Vector curves, Scene scene){
        Curve result = null;
        int maxPointCurve = 0;
        for(int i = 0; i < curves.size(); i++){
            Vec3 [] verts = (Vec3 [])curves.elementAt(i);
            if(verts.length > maxPointCurve){
                maxPointCurve = verts.length;
            }
        }
        //System.out.println(" maxPointCurve " + maxPointCurve);
        for(int i = 0; i < curves.size(); i++){
            Vec3 [] verts = (Vec3 [])curves.elementAt(i);
            int inserts = 0;
            while(verts.length < maxPointCurve && inserts < 1000){
                inserts++;
                //System.out.println( " cirve " + i + " is too small " );
                
                // find largest spacing vertex pair to insert a new point between.
                double largestDistance = 0;
                int largestDistanceIndex = -1;
                for(int j = 1; j < verts.length; j++){
                    Vec3 a = verts[j-1];
                    Vec3 b = verts[j];
                    double distance = a.distance(b);
                    if(distance > largestDistance){
                        largestDistance = distance;
                        largestDistanceIndex = j-1;
                    }
                }
                if(largestDistanceIndex > -1){
                    Curve tempCurve = getCurve(verts);
                    Curve biggerCurve = tempCurve.addPointToCurve(largestDistanceIndex);
                    verts = biggerCurve.getVertexPositions();
                    
                    curves.setElementAt(verts, i);
                    
                    if(verts.length == maxPointCurve){
                        //ObjectInfo midCurveInfoTest = new ObjectInfo(biggerCurve, new CoordinateSystem(), "expanded Yz TEST" + i);
                        //scene.addObject(midCurveInfoTest, null);
                        result = biggerCurve;
                    }
                }
            }
        }
        return result;
    }
    
    
    /**
     * subdivideCurves
     *
     * Description: given a list of curve points, subdivide the mesh.
     */
    public Vector subdivideCurves(Vector curves, int subdivisions){
        Vector subdivided = new Vector(curves);
        for(int subs = 0; subs < subdivisions; subs++){
            for(int i = 0; i < subdivided.size(); i++){
                Vec3 [] verts = (Vec3 [])subdivided.elementAt(i);
                Curve curve = getCurve(verts);
                curve = curve.subdivideCurve();
                verts = curve.getVertexPositions();
                subdivided.setElementAt(verts, i);
            }
        }
        return subdivided;
    }
    
    public Vec3[] subdivideCurve(Vec3[] curveVerts, int subdivisions){
        for(int subs = 0; subs < subdivisions; subs++){
            Curve curve = getCurve(curveVerts);
            curve = curve.subdivideCurve();
            curveVerts = curve.getVertexPositions();
        }
        return curveVerts;
    }
    
    /**
     * makeObject  Rename create mesh
     *
     * From SkinDialog()
     */
    private void skinMesh(Vector curves, Scene scene, LayoutWindow layoutWindow, String name){
        if(curves.size() == 0){
            return;
        }
        ObjectInfo curve[] = new ObjectInfo[curves.size()];
        boolean reverse[] = new boolean [curves.size()];
        Vec3 centerOffset;
        for(int i = 0; i < curves.size(); i++){
            Vec3 [] verts = (Vec3 [])curves.elementAt(i);
            //System.out.println(" curve "+ i + " length " + verts.length );
            Curve c = getCurve(verts);
            ObjectInfo curveObject = new ObjectInfo(c, new CoordinateSystem(), "name " + i);
            curve[i] = curveObject;
            reverse[i] = false;
        }
        
        Vec3 v[][] = new Vec3 [curve.length][], center = new Vec3();
        float us[] = new float [curve.length], vs[] = new float [((Curve) curve[0].getObject()).getVertices().length];
        int smoothMethod = Mesh.INTERPOLATING;
        boolean closed = false;

        for (int i = 0; i < curve.length; i++)
        {
            Curve cv = (Curve) curve[i].getObject();
            MeshVertex vert[] = cv.getVertices();
            v[i] = new Vec3 [vert.length];
            float smooth[] = cv.getSmoothness();
            if (cv.getSmoothingMethod() > smoothMethod)
                smoothMethod = cv.getSmoothingMethod();
            closed |= cv.isClosed();
            for (int j = 0; j < vert.length; j++){
                int k = (reverse[i] ? vert.length-j-1 : j);
                v[i][j] = curve[i].getCoords().fromLocal().times(vert[k].r);
                center.add(v[i][j]);
                if (cv.getSmoothingMethod() != Mesh.NO_SMOOTHING && k < vs.length){
                    //System.out.println("vs.length: " + vs.length + "  k: " + k);
                    vs[j] += smooth[k];                                             // error
                }
            }
            us[i] = 1.0f;
        }
        for (int i = 0; i < vs.length; i++)
            vs[i] /= curve.length;

        // Center it.

        center.scale(1.0/(v.length*v[0].length));
        for (int i = 0; i < v.length; i++){
            for (int j = 0; j < v[i].length; j++){
                //v[i][j].subtract(center);
            }
        }

        //centerOffset = center;

        SplineMesh mesh = new SplineMesh(v, us, vs, smoothMethod, false, closed);
        Texture tex = layoutWindow.getScene().getDefaultTexture();
        mesh.setTexture(tex, tex.getDefaultMapping(mesh));
        mesh.makeRightSideOut();
        
        ObjectInfo meshObjectInfo = new ObjectInfo(mesh, new CoordinateSystem(), "mesh " + name);
        scene.addObject(meshObjectInfo, null); // SplineMesh(Object3D) -> ObjectInfo
        
        layoutWindow.updateImage();
        layoutWindow.updateMenus();
        layoutWindow.rebuildItemList();
    }
    
    /**
     * generateMidCurve
     *
     * Description:
     */
    public Vec3[] generateMidCurve(Vector midPoints, Vector perpendicularCurves, Scene scene, LayoutWindow layoutWindow){
        Vector<Vec3> generatedMidCurve = new Vector<Vec3>();
        
        //perpendicularCurves = subdivideCurves(perpendicularCurves, 8);
        HashMap hash = new HashMap();
        
        // Find closest pairs of points for each
        //System.out.println(" - " + perpendicularCurves.size());
        
            
        //for(int i = 0; i < perpendicularCurves.size(); i++){            // perpendicular curves
        for(int i = perpendicularCurves.size()-1; i >= 0;  i-- ){
            Vec3 [] verts = (Vec3 [])perpendicularCurves.elementAt(i);
            
            double closestPointDistance = Double.MAX_VALUE;
            Vec3 closestPoint = null;
            
            for(int p = 0; p < verts.length; p++){
                Vec3 perpVert = (Vec3)verts[p];
                
                for(int j = 0; j < midPoints.size(); j++){
                    Vec3 midPoint = (Vec3)midPoints.elementAt(j);
                
                    double distance = perpVert.distance(midPoint);
                    if(distance < closestPointDistance){
                        closestPointDistance = distance;
                        closestPoint = perpVert;
                    }
                    
                }
            }
            
            
            if(closestPoint != null){
                generatedMidCurve.addElement(closestPoint);
                //System.out.println(" point " + closestPoint);
            }
        }
            
        
        
        // Blend, subtract difference between closest perp point and midpoint edges (a, b)
        // This way perpendicular curves won't pull mesh faces out if not aligned to the rib curves perfectly.
        // TODO
        Vec3[] midCurvePoints = new Vec3[generatedMidCurve.size()];
        if(generatedMidCurve.size() > 1 ){
            //Vec3[] points = new Vec3[generatedMidCurve.size()];
            for(int i = 0; i < generatedMidCurve.size(); i++){
                midCurvePoints[i] = (Vec3)generatedMidCurve.elementAt(i);
            }
            
            midCurvePoints = subdivideCurve(midCurvePoints, 4);
            
            Curve curve = getCurve(midCurvePoints);
            ObjectInfo curveObject = new ObjectInfo(curve, new CoordinateSystem(), "XXX ");
            
            scene.addObject(curveObject, null);
            
            layoutWindow.updateImage();
            layoutWindow.updateMenus();
            layoutWindow.rebuildItemList();
        }
        return midCurvePoints;
    }
}
