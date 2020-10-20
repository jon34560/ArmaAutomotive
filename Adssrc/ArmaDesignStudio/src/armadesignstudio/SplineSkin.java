/* Copyright (C) 2019, 2020 by Jon Taylor

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
//import javax.swing.JCheckBox;
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
     * connectedCurvesToMeshCommand
     *
     * Description: Create mesh from connected curves
     *  TODO: order of points along curve may need to be reversed in pairs.
     */
    public void connectedCurvesToMesh(Scene scene, LayoutWindow layoutWindow, Vector<ObjectInfo> objects){
        System.out.println("connectedCurvesToMeshCommand " );
        LayoutModeling layout = new LayoutModeling();
        Vector meshPoints = new Vector();
        
        //Vector<Vec3[]> insertedCurves = new Vector<Vec3[]>();
        Vector< PointJoinObject > connections = new Vector<>();
        Vector< Curve > curves = new Vector<>();
        Vector< ObjectInfo > curveOIs = new Vector<>();
        
        //Vector< Vector < > > connections = new Vector<  >();
        //HashMap <Integer, >
        
        // 1 Find relevent curves and connections.
        for (ObjectInfo obj : objects){
            Object co = (Object)obj.getObject();
            // PointJoinObject
            if((co instanceof Curve) == true){
                //curves.addElement(co);
                curveOIs.addElement(obj);
            }
            if(co instanceof PointJoinObject){
                PointJoinObject pjo = (PointJoinObject)co;
                // pjo.objectA
                // pjo.objectB
                connections.addElement(pjo);
            }
        }
        
        // 2 Subdivide curves.
        
        //
        for(int i = 0; i < curveOIs.size(); i++){
            ObjectInfo curveOI = (ObjectInfo)curveOIs.elementAt(i);
            int curveId = curveOI.getId();  // objectInfo.getId();
            
            // what other curves (and points) is this connected to.
            
            for(int x = 0; x < connections.size(); x++){
                PointJoinObject pjo = (PointJoinObject)connections.elementAt(x);
                if(pjo.objectA == curveId){
                    System.out.println(" * " );
                }
                if(pjo.objectB == curveId){
                    System.out.println(" & " );
                }
            }
            
            
            // create duplicate of curve
            
        }
        
        
    }
    
    /**
     * autoSkinBySpace
     *
     * Description: create smoothed mesh from a selection of curves. Use evenly spaced voids to calculate where midpoints should be.
     */
    public void autoSkinByVoids(Scene scene, LayoutWindow layoutWindow, Vector<ObjectInfo> objects){
        LayoutModeling layout = new LayoutModeling();
        Vector curves = new Vector();
        HashMap completed = new HashMap();
        Vector meshPoints = new Vector();
        ObjectInfo firstMidCurveInfo = null; // debug curves for visualization of process.
        Vector<Vec3[]> insertedCurves = new Vector<Vec3[]>();
        System.out.println("autoSkinByVoids " );
        
        // 1 Get scene selection
        for (ObjectInfo obj : objects){
            if(obj.selected == true){
                Object co = (Object)obj.getObject();
                if((co instanceof Curve) == true){
                    Mesh mesh = (Mesh) obj.getObject(); // Object3D
                    Vec3 [] curveVerts = mesh.getVertexPositions();
                    //Vector<Vec3> verts = new Vector<Vec3>();

                    // translate local coords with obj location.
                    CoordinateSystem c;
                    c = layout.getCoords(obj);
                    Vec3 objOrigin = c.getOrigin();
                    for (Vec3 vert : curveVerts){
                        Mat4 mat4 = c.duplicate().fromLocal();
                        mat4.transform(vert);
                        //System.out.println("    vert: " + vert.x + " " + vert.y + "  " + vert.z );
                        //verts.addElement(vert);
                    }
                    // public Vec3 [] subdivideCurve(Vec3 [] curveVerts, int subdivisions){
                    //curveVerts = subdivideCurve(curveVerts, 2); // 3 or 4
                    curves.addElement(curveVerts);
                }
            }
        }
        
        
        // 2 Subdivide selected splines to generate more points.
        for(int c = 0; c < curves.size(); c++){
            Vec3[] curveVerts = (Vec3[])curves.elementAt(c);
            curveVerts = subdivideCurve(curveVerts, 2); // 3 or 4
            curves.setElementAt(curveVerts, c);
            for(int i = 0; i < curveVerts.length; i++){
                meshPoints.addElement(curveVerts[i]);
            }
        }
        
        // 3 Calculate spline vert point curve on 3 axies. (Midpoint - actual) / (edge distance).
        HashMap vertCurveMap = new HashMap();
        for(int c = 0; c < curves.size(); c++){
            Vec3[] curve = (Vec3[])curves.elementAt(c);
            for(int v = 1; v < curve.length - 1; v++){ // Ignore first and last as they don't have a curvature.
                Vec3 beforeVec = (Vec3)curve[(v - 1)];
                Vec3 afterVec = (Vec3)curve[(v + 1)];
                Vec3 vec = (Vec3)curve[v];
                Vec3 midpoint = beforeVec.midPoint(afterVec);
                double endsDistance = beforeVec.distance(afterVec);
                // Difference between midpoint and actual vec.
                Vec3 vecCurve = new Vec3(
                                         /*
                                         (Math.max(midpoint.x, vec.x) - Math.min(midpoint.x, vec.x)) / endsDistance,
                                         (Math.max(midpoint.y, vec.y) - Math.min(midpoint.y, vec.y)) / endsDistance,
                                         (Math.max(midpoint.z, vec.z) - Math.min(midpoint.z, vec.z)) / endsDistance
                                          */
                                         (midpoint.x - vec.x) / endsDistance,
                                         (midpoint.y - vec.y) / endsDistance,
                                         (midpoint.z - vec.z) / endsDistance
                                         );
                String vecKey = c + "_" + v;
                vertCurveMap.put(vecKey, vecCurve);
                
                //System.out.println("AutoMesh point curve "  + vecKey + " x: " + vecCurve.x +  " y: " + vecCurve.y + " z: "+ vecCurve.z + "  d: " + endsDistance);
                //System.out.println("  encode  z "  + (Math.max(midpoint.z, vec.z) - Math.min(midpoint.z, vec.z)) + "  d: " + endsDistance + " = zcurve:" + vecCurve.z);
            }
        }
        
        
        // 2 Calculate points in empty space within the bounds of at least three curves.
        // for each combination of three or two curves calculate the mid point.
        for(int c = 0; c < curves.size(); c++){
            Vec3[] curveVerts = (Vec3[])curves.elementAt(c);
            for(int c2 = 0; c2 < curves.size(); c2++){
                if(c != c2){
                    Vec3[] curveVerts2 = (Vec3[])curves.elementAt(c2);
                    
                    int cMidIndex = (int)(curveVerts.length / 2);
                    int c2MidIndex = (int)(curveVerts2.length / 2);
                    
                    Vec3 aMidVec = curveVerts[cMidIndex];
                    Vec3 bMidVec = curveVerts2[cMidIndex];
                    
                    Vec3 midPoint = aMidVec.midPoint(bMidVec);
                    
                    // Hash these
                    // duplicates
                    
                    Vec3[] newCurvePoints = new Vec3[3];
                    newCurvePoints[0] = aMidVec;
                    newCurvePoints[1] = midPoint;
                    newCurvePoints[2] = bMidVec;
                    Curve curve = getCurve(newCurvePoints);
                    ObjectInfo midCurveInfo = new ObjectInfo(curve, new CoordinateSystem(), "TEST ");
                    scene.addObject(midCurveInfo, null);
                    
                }
            }
        }
        
        
        // 3 create curve
        
        // 4 add mesh
        
        
        layoutWindow.updateImage();
        //layoutWindow.updateMenus();
        layoutWindow.rebuildItemList();
    }
    
    /**
     * smartSubdivideCurves
     *
     * Description: subdivide to segments that line up with perpendicular intersecting curves for better mesh generaation.
     *
     */
    public Vector<Vec3[]> smartSubdivideCurves(Vector<Vec3[]> curves){
        Vector<Vec3[]> result = new Vector<Vec3[]>();
        Vector<Vec3[]> tempCurves = new Vector<Vec3[]>();
        for(int c = 0; c < curves.size(); c++){
            Vec3[] curveVerts = (Vec3[])curves.elementAt(c);
            Vec3[] copy = new Vec3[curveVerts.length];
            for(int v = 0; v < curveVerts.length; v++){
                copy[v] = new Vec3( curveVerts[v] );
            }
            tempCurves.addElement(copy);
        }
        
        for(int c = 0; c < tempCurves.size(); c++){
            Vec3[] curveVerts = (Vec3[])tempCurves.elementAt(c);
            curveVerts = subdivideCurve(curveVerts, 5); // 3 or 4
            tempCurves.setElementAt(curveVerts, c);
        }
        
        // How many other curves intersect with each curve.
        HashMap<Integer, HashMap<Integer, Boolean>> curveIntersections = new HashMap<Integer, HashMap<Integer, Boolean>>();
        for(int c = 0; c < tempCurves.size(); c++){
            Vec3[] curveVerts = (Vec3[])tempCurves.elementAt(c);
            double intersectRange = 999999; if(curveVerts.length > 1){intersectRange = curveVerts[0].distance(curveVerts[1]);}
            for( int cv = 0; cv < curveVerts.length; cv++ ){
                Vec3 vec = curveVerts[cv];
                for(int c2 = 0; c2 < tempCurves.size(); c2++){
                    if(c != c2){
                        Vec3[] curveVerts2 = (Vec3[])tempCurves.elementAt(c2);
                        for( int c2v = 0; c2v < curveVerts2.length; c2v++ ){
                            Vec3 vec2 = curveVerts2[c2v];
                            
                            double vecDistance = vec.distance(vec2);
                            if( vecDistance < intersectRange * 2){
                                
                                // Curve c has an intersecting curve of c2.
                                HashMap<Integer, Boolean> intersections = curveIntersections.get(c);
                                if(intersections == null){
                                    intersections = new HashMap<Integer, Boolean>();
                                }
                                intersections.put(c2, true);
                                curveIntersections.put(c, intersections);
                            }
                            
                        }
                    }
                }
            }
        }
        
        // Create data structure representing desired number of points for each curve.
        HashMap<Integer, Integer> curveIntersectionCounts = new HashMap<Integer, Integer>();
        Iterator hmIterator = curveIntersections.entrySet().iterator();
        while (hmIterator.hasNext()) {
            Map.Entry mapElement = (Map.Entry)hmIterator.next();
            int curveIndex = (int)mapElement.getKey();
            //System.out.println("Curve " + curveIndex );
            
            HashMap<Integer, Boolean> intersections = (HashMap<Integer, Boolean>)mapElement.getValue();
            Iterator hmIntersectIterator = intersections.entrySet().iterator();
            int count = 0;
            while (hmIntersectIterator.hasNext()) {
                Map.Entry intersectMapElement = (Map.Entry)hmIntersectIterator.next();
                int intersectIndex = (int)intersectMapElement.getKey();
                //System.out.println("    -> " + intersectIndex);
                count++;
            }
            curveIntersectionCounts.put(curveIndex, count);
        }
        
        for(int c = 0; c < curves.size(); c++){
            Vec3[] curveVerts = (Vec3[])curves.elementAt(c);
            int intersections = curveIntersectionCounts.get(c);
            int targetPoints = (intersections * 2) - 1;
            int existingPoints = curveVerts.length;
            
            while(curveVerts.length < targetPoints){
                curveVerts = subdivideCurve(curveVerts, 1);
                curves.setElementAt(curveVerts, c);
            }
            
            //curveVerts = subdivideCurve(curveVerts, 1);
            //curves.setElementAt(curveVerts, c);
            
            System.out.println("Curve: " + c + " points: " + existingPoints + " target:  " + targetPoints + " result " + curveVerts.length );
        }
        
        return curves;
    }
    
    /**
     * autoSkin
     *
     * Description: Generate curved mesh from multiple curved splines. The mesh is to be smooth across all curve geometry.
     *
     * @param Scene, used to get object data
     * @param LayoutWindow  used to add objects generated.
     * @param Scene objects.
     */
    public void autoSkin(Scene scene, LayoutWindow layoutWindow, Vector<ObjectInfo> objects){
        LayoutModeling layout = new LayoutModeling();
        Vector curves = new Vector();
        HashMap completed = new HashMap();
        Vector meshPoints = new Vector();
        ObjectInfo firstMidCurveInfo = null; // debug curves for visualization of process.
        Vector<Vec3[]> insertedCurves = new Vector<Vec3[]>();
        
        // 1 Get scene selection
        for (ObjectInfo obj : objects){
            if(obj.selected == true){
                Object co = (Object)obj.getObject();
                if((co instanceof Curve) == true){
                    Mesh mesh = (Mesh) obj.getObject(); // Object3D
                    Vec3 [] curveVerts = mesh.getVertexPositions();
                    //Vector<Vec3> verts = new Vector<Vec3>();

                    // translate local coords with obj location.
                    CoordinateSystem c;
                    c = layout.getCoords(obj);
                    Vec3 objOrigin = c.getOrigin();
                    for (Vec3 vert : curveVerts){
                        Mat4 mat4 = c.duplicate().fromLocal();
                        mat4.transform(vert);
                        //System.out.println("    vert: " + vert.x + " " + vert.y + "  " + vert.z );
                        //verts.addElement(vert);
                    }
                    // public Vec3 [] subdivideCurve(Vec3 [] curveVerts, int subdivisions){
                    //curveVerts = subdivideCurve(curveVerts, 2); // 3 or 4
                    curves.addElement(curveVerts);
                }
            }
        }
        
        
        // 2 Subdivide selected splines to generate more points.
        
        smartSubdivideCurves(curves);
        // curves to meshPoints
        
        for(int c = 0; c < curves.size(); c++){
            Vec3[] curveVerts = (Vec3[])curves.elementAt(c);
            //curveVerts = subdivideCurve(curveVerts, 2); // 3 or 4
            //curves.setElementAt(curveVerts, c);
            for(int i = 0; i < curveVerts.length; i++){
                meshPoints.addElement(curveVerts[i]);
            }
        }
    
        // 3 Calculate spline vert point curve on 3 axies. (Midpoint - actual) / (edge distance).
        HashMap vertCurveMap = new HashMap();
        for(int c = 0; c < curves.size(); c++){
            Vec3[] curve = (Vec3[])curves.elementAt(c);
            for(int v = 1; v < curve.length - 1; v++){ // Ignore first and last as they don't have a curvature.
                Vec3 beforeVec = (Vec3)curve[(v - 1)];
                Vec3 afterVec = (Vec3)curve[(v + 1)];
                Vec3 vec = (Vec3)curve[v];
                Vec3 midpoint = beforeVec.midPoint(afterVec);
                double endsDistance = beforeVec.distance(afterVec);
                // Difference between midpoint and actual vec.
                Vec3 vecCurve = new Vec3(
                                         /*
                                         (Math.max(midpoint.x, vec.x) - Math.min(midpoint.x, vec.x)) / endsDistance,
                                         (Math.max(midpoint.y, vec.y) - Math.min(midpoint.y, vec.y)) / endsDistance,
                                         (Math.max(midpoint.z, vec.z) - Math.min(midpoint.z, vec.z)) / endsDistance
                                          */
                                         (midpoint.x - vec.x) / endsDistance,
                                         (midpoint.y - vec.y) / endsDistance,
                                         (midpoint.z - vec.z) / endsDistance
                                         );
                String vecKey = c + "_" + v;
                vertCurveMap.put(vecKey, vecCurve);
                
                //System.out.println("AutoMesh point curve "  + vecKey + " x: " + vecCurve.x +  " y: " + vecCurve.y + " z: "+ vecCurve.z + "  d: " + endsDistance);
                //System.out.println("  encode  z "  + (Math.max(midpoint.z, vec.z) - Math.min(midpoint.z, vec.z)) + "  d: " + endsDistance + " = zcurve:" + vecCurve.z);
            }
        }
        
        // 4 For each point, with a vert angle (angle between neighbours), iterate all points in other splines.
        int addedCurves = 0;
        for(int c = 0; c < curves.size(); c++){
            Vec3[] curve = (Vec3[])curves.elementAt(c);
            for(int v = 1; v < curve.length - 1; v++){
                Vec3 vec = (Vec3)curve[v];
                // Compare
                for(int cx = 0; cx < curves.size(); cx++){              // each comparison curve
                    if(c != cx){
                        Vec3[] curvex = (Vec3[])curves.elementAt(cx);
                        for(int vx = 1; vx < curvex.length - 1; vx++){  // eaach point in comparison curve
                            String completedKey = Math.min(c, cx) + "-" + Math.min(v, vx) + "_" + Math.max(c, cx) + "-" + Math.max(v, vx);
                            Vec3 vecx = (Vec3)curvex[vx];
                            
                            // 4 calculate mid point and offset by average of two edge point bends. skew average by distance.
                            Vec3 midPoint = vec.midPoint(vecx);
                            double distance = vec.distance(midPoint);
                            
                            // If midpoint is closer to any other spline point ignore.
                            // 5 if midpoint is not closer to another (non spline from endpoints) then add new 3 point spline from
                            // ends and new midpoint.
                            if(
                                isClosest(curves, midPoint, distance, c, cx)
                               //&&
                                //isEvenPoints(curves, c, cx , v, vx)
                               ){
                                // Generate test curve for validation
                                
                                // Apply curvature to midpoint.
                                // TODO
                                String aCurvatureKey = c + "_" + v;
                                String bCurvatureKey = cx + "_" + vx;
                                Vec3 aCurvature = (Vec3)vertCurveMap.get(aCurvatureKey);
                                Vec3 bCurvature = (Vec3)vertCurveMap.get(bCurvatureKey);
                                if(aCurvature != null && bCurvature != null){
                                    /*
                                    double curveX = (aCurvature.x + bCurvature.x) / 2;
                                    midPoint.x *= 1+ (curveX * distance * 2);
                                    double curveY = (aCurvature.y + bCurvature.y) / 2;
                                    midPoint.y *= 1+ (curveY * distance * 2);
                                    double curveZ = (aCurvature.z + bCurvature.z) / 2;
                                    midPoint.z *= 1+ (curveZ * distance * 2);
                                    */
                                }
                                
                                Vec3 regionCurvature = getRegionCurvature(curves, vertCurveMap, midPoint, distance); //
                                /*
                                midPoint.x -= (regionCurvature.x * 1) * distance;
                                midPoint.y -= (regionCurvature.y * 1) * distance;
                                midPoint.z -= (regionCurvature.z * 1) * distance;
                                */
                                midPoint.x -= ((regionCurvature.x * 1) * distance) * 2;
                                midPoint.y -= ((regionCurvature.y * 1) * distance) * 2;
                                midPoint.z -= ((regionCurvature.z * 1) * distance) * 2;
                                
                                //System.out.println(" decode regionCurvature.z: " + regionCurvature.z + " distance: " + distance + " offset " + (regionCurvature.z  * distance) );
                                
                                Vec3[] newCurvePoints = new Vec3[3];
                                newCurvePoints[0] = vec;
                                newCurvePoints[1] = midPoint;
                                newCurvePoints[2] = vecx;
                                
                                // 6 subdivide new splines.
                                newCurvePoints = subdivideCurve(newCurvePoints, 1); // subdivide mid splines (OPTIONAL)
                                /*
                                Curve newCurve = getCurve(newCurvePoints);
                                
                                ObjectInfo midCurveInfo = new ObjectInfo(newCurve, new CoordinateSystem(), "TEST " + regionCurvature.z + " " + distance);
                                
                                if(firstMidCurveInfo == null){
                                    firstMidCurveInfo = midCurveInfo;
                                } else {
                                    midCurveInfo.setParent(firstMidCurveInfo);
                                    firstMidCurveInfo.addChild(midCurveInfo, firstMidCurveInfo.getChildren().length);
                                }
                                */
                                
                                if(completed.containsKey(completedKey) == false ){
                                    insertedCurves.addElement(newCurvePoints);
                                    
                                    //scene.addObject(midCurveInfo, null);
                                    
                                    //meshPoints.addElement(midPoint); // point for mesh
                                    
                                    addedCurves++;
                                }
                                
                                completed.put(completedKey, true);
                            }
                        }
                    }
                }
            }
        }
        System.out.println("Added curves: " + addedCurves );
        
        // 5
        // Optimization; If multiple infill points occupy the same region, remove the one with the longest span
        // as it is likely to be inacurate?
        // Determine region size. Iterate each point and find the next closest point, The region is the shortest span
        double regionScale = 1.5; // higher means fewer deletions.
        int deletedCurves = 0;
        for(int i = insertedCurves.size() - 1; i >= 0; i--){
            Vec3[] curve = (Vec3[])insertedCurves.elementAt(i);
            int curveMidIndex = (int)(curve.length / 2);
            double closestDistance = 999999;
            Vec3[] closestCurve = null;
            int closestIndex = -1;
            for(int j = insertedCurves.size() - 1; j >= 0; j--){
                Vec3[] compareCurve = (Vec3[])insertedCurves.elementAt(j);
                int compareCurveMidIndex = (int)(compareCurve.length / 2);
                if(i != j){
                    double distance = curve[curveMidIndex].distance(compareCurve[compareCurveMidIndex]);
                    if(distance < closestDistance){
                        closestDistance = distance;
                        closestCurve = compareCurve;
                        closestIndex = j;
                    }
                }
            }
            if(closestCurve != null){
                int closestCurveMidIndex = (int)(closestCurve.length / 2);
                double aLength = curve[0].distance(curve[curveMidIndex]);
                double bLength = closestCurve[0].distance(closestCurve[closestCurveMidIndex]);
                double distance = curve[curveMidIndex].distance(closestCurve[closestCurveMidIndex]);
                double shortestLength = Math.min(aLength, bLength);
                if(aLength < bLength && (distance * regionScale) < (aLength)){
                    insertedCurves.removeElementAt(closestIndex); // remove b
                    deletedCurves++;
                } else if (bLength < aLength && (distance * regionScale) < (bLength)){
                    insertedCurves.removeElementAt(i); // remove a
                    deletedCurves++;
                }
            }
        }
        System.out.println("Deleted curves: " + deletedCurves );
        
        
        
        //insertedCurves.addAll(autoMeshIteration(curves));
        //insertedCurves.addAll(autoMeshIteration(curves));
    
        
        // Add curves to the scene as a debug visualization tool.
        for(int i = 0; i < insertedCurves.size(); i++){
            Vec3[] newCurvePoints = (Vec3[])insertedCurves.elementAt(i);
            Curve newCurve = getCurve(newCurvePoints);
            ObjectInfo midCurveInfo = new ObjectInfo(newCurve, new CoordinateSystem(), "TEST ");
            if(firstMidCurveInfo == null){
                firstMidCurveInfo = midCurveInfo;
            } else {
                midCurveInfo.setParent(firstMidCurveInfo);
                firstMidCurveInfo.addChild(midCurveInfo, firstMidCurveInfo.getChildren().length);
            }
            scene.addObject(midCurveInfo, null);
        }
        
        
        // 7 Generate triaangle mesh from all points selected and generated.
        for(int i = 0; i < insertedCurves.size(); i++){
            Vec3[] newCurvePoints = (Vec3[])insertedCurves.elementAt(i);
            // calculate meshPoints as middle point in insertedCurves vector.
            int midIndex = (int)(newCurvePoints.length / 2);
            Vec3 midPoint = newCurvePoints[midIndex]; // 1 or middle
            meshPoints.addElement(midPoint); // point for mesh
        }
        TriangleMesh tm = pointsToMesh(meshPoints);
        ObjectInfo triangleMeshInfo = new ObjectInfo(tm, new CoordinateSystem(), "mesh ");
        scene.addObject(triangleMeshInfo, null);
        
        layoutWindow.updateImage();
        //layoutWindow.updateMenus();
        layoutWindow.rebuildItemList();
    }
    
    /**
     * In progress experiment
     *
     */
    public Vector<Vec3[]> autoMeshIteration(Vector curves ){
        Vector<Vec3[]> insertedCurves = new Vector<Vec3[]>();
        HashMap completed = new HashMap();
        
        // 2 Subdivide selected splines to generate more points.
            for(int c = 0; c < curves.size(); c++){
                Vec3[] curveVerts = (Vec3[])curves.elementAt(c);
                curveVerts = subdivideCurve(curveVerts, 2); // 3 or 4
                curves.setElementAt(curveVerts, c);
                //for(int i = 0; i < curveVerts.length; i++){
                //    meshPoints.addElement(curveVerts[i]);
                //}
            }
        
            // 3 Calculate spline vert point curve on 3 axies. (Midpoint - actual) / (edge distance).
            HashMap vertCurveMap = new HashMap();
            for(int c = 0; c < curves.size(); c++){
                Vec3[] curve = (Vec3[])curves.elementAt(c);
                for(int v = 1; v < curve.length - 1; v++){ // Ignore first and last as they don't have a curvature.
                    Vec3 beforeVec = (Vec3)curve[(v - 1)];
                    Vec3 afterVec = (Vec3)curve[(v + 1)];
                    Vec3 vec = (Vec3)curve[v];
                    Vec3 midpoint = beforeVec.midPoint(afterVec);
                    double endsDistance = beforeVec.distance(afterVec);
                    // Difference between midpoint and actual vec.
                    Vec3 vecCurve = new Vec3(
                                             /*
                                             (Math.max(midpoint.x, vec.x) - Math.min(midpoint.x, vec.x)) / endsDistance,
                                             (Math.max(midpoint.y, vec.y) - Math.min(midpoint.y, vec.y)) / endsDistance,
                                             (Math.max(midpoint.z, vec.z) - Math.min(midpoint.z, vec.z)) / endsDistance
                                              */
                                             (midpoint.x - vec.x) / endsDistance,
                                             (midpoint.y - vec.y) / endsDistance,
                                             (midpoint.z - vec.z) / endsDistance
                                             );
                    String vecKey = c + "_" + v;
                    vertCurveMap.put(vecKey, vecCurve);
                    
                    //System.out.println("AutoMesh point curve "  + vecKey + " x: " + vecCurve.x +  " y: " + vecCurve.y + " z: "+ vecCurve.z + "  d: " + endsDistance);
                    //System.out.println("  encode  z "  + (Math.max(midpoint.z, vec.z) - Math.min(midpoint.z, vec.z)) + "  d: " + endsDistance + " = zcurve:" + vecCurve.z);
                }
            }
            
            // 4 For each point, with a vert angle (angle between neighbours), iterate all points in other splines.
            int addedCurves = 0;
            for(int c = 0; c < curves.size(); c++){
                Vec3[] curve = (Vec3[])curves.elementAt(c);
                for(int v = 1; v < curve.length - 1; v++){
                    Vec3 vec = (Vec3)curve[v];
                    // Compare
                    for(int cx = 0; cx < curves.size(); cx++){              // each comparison curve
                        if(c != cx){
                            Vec3[] curvex = (Vec3[])curves.elementAt(cx);
                            for(int vx = 1; vx < curvex.length - 1; vx++){  // eaach point in comparison curve
                                String completedKey = Math.min(c, cx) + "-" + Math.min(v, vx) + "_" + Math.max(c, cx) + "-" + Math.max(v, vx);
                                Vec3 vecx = (Vec3)curvex[vx];
                                
                                // 4 calculate mid point and offset by average of two edge point bends. skew average by distance.
                                Vec3 midPoint = vec.midPoint(vecx);
                                double distance = vec.distance(midPoint);
                                
                                // If midpoint is closer to any other spline point ignore.
                                // 5 if midpoint is not closer to another (non spline from endpoints) then add new 3 point spline from
                                // ends and new midpoint.
                                if(
                                    isClosest(curves, midPoint, distance, c, cx)
                                   //&&
                                    //isEvenPoints(curves, c, cx , v, vx)
                                   ){
                                    // Generate test curve for validation
                                    
                                    // Apply curvature to midpoint.
                                    // TODO
                                    String aCurvatureKey = c + "_" + v;
                                    String bCurvatureKey = cx + "_" + vx;
                                    Vec3 aCurvature = (Vec3)vertCurveMap.get(aCurvatureKey);
                                    Vec3 bCurvature = (Vec3)vertCurveMap.get(bCurvatureKey);
                                    if(aCurvature != null && bCurvature != null){
                                        /*
                                        double curveX = (aCurvature.x + bCurvature.x) / 2;
                                        midPoint.x *= 1+ (curveX * distance * 2);
                                        double curveY = (aCurvature.y + bCurvature.y) / 2;
                                        midPoint.y *= 1+ (curveY * distance * 2);
                                        double curveZ = (aCurvature.z + bCurvature.z) / 2;
                                        midPoint.z *= 1+ (curveZ * distance * 2);
                                        */
                                    }
                                    
                                    Vec3 regionCurvature = getRegionCurvature(curves, vertCurveMap, midPoint, distance); //
                                    /*
                                    midPoint.x -= (regionCurvature.x * 1) * distance;
                                    midPoint.y -= (regionCurvature.y * 1) * distance;
                                    midPoint.z -= (regionCurvature.z * 1) * distance;
                                    */
                                    midPoint.x -= ((regionCurvature.x * 1) * distance) * 2;
                                    midPoint.y -= ((regionCurvature.y * 1) * distance) * 2;
                                    midPoint.z -= ((regionCurvature.z * 1) * distance) * 2;
                                    
                                    //System.out.println(" decode regionCurvature.z: " + regionCurvature.z + " distance: " + distance + " offset " + (regionCurvature.z  * distance) );
                                    
                                    Vec3[] newCurvePoints = new Vec3[3];
                                    newCurvePoints[0] = vec;
                                    newCurvePoints[1] = midPoint;
                                    newCurvePoints[2] = vecx;
                                    
                                    // 6 subdivide new splines.
                                    newCurvePoints = subdivideCurve(newCurvePoints, 1); // subdivide mid splines (OPTIONAL)
                                    /*
                                    Curve newCurve = getCurve(newCurvePoints);
                                    
                                    ObjectInfo midCurveInfo = new ObjectInfo(newCurve, new CoordinateSystem(), "TEST " + regionCurvature.z + " " + distance);
                                    
                                    if(firstMidCurveInfo == null){
                                        firstMidCurveInfo = midCurveInfo;
                                    } else {
                                        midCurveInfo.setParent(firstMidCurveInfo);
                                        firstMidCurveInfo.addChild(midCurveInfo, firstMidCurveInfo.getChildren().length);
                                    }
                                    */
                                    
                                    if(completed.containsKey(completedKey) == false ){
                                        
                                        
                                        insertedCurves.addElement(newCurvePoints);
                                        
                                        //scene.addObject(midCurveInfo, null);
                                        
                                        //meshPoints.addElement(midPoint); // point for mesh
                                        
                                        addedCurves++;
                                    }
                                    
                                    completed.put(completedKey, true);
                                }
                            }
                        }
                    }
                }
            }
            System.out.println("Added curves: " + addedCurves );
            
            // 5
            // Optimization; If multiple infill points occupy the same region, remove the one with the longest span
            // as it is likely to be inacurate?
            // Determine region size. Iterate each point and find the next closest point, The region is the shortest span
            double regionScale = 1.5; // higher means fewer deletions.
            int deletedCurves = 0;
            for(int i = insertedCurves.size() - 1; i >= 0; i--){
                Vec3[] curve = (Vec3[])insertedCurves.elementAt(i);
                int curveMidIndex = (int)(curve.length / 2);
                double closestDistance = 999999;
                Vec3[] closestCurve = null;
                int closestIndex = -1;
                for(int j = insertedCurves.size() - 1; j >= 0; j--){
                    Vec3[] compareCurve = (Vec3[])insertedCurves.elementAt(j);
                    int compareCurveMidIndex = (int)(compareCurve.length / 2);
                    if(i != j){
                        double distance = curve[curveMidIndex].distance(compareCurve[compareCurveMidIndex]);
                        if(distance < closestDistance){
                            closestDistance = distance;
                            closestCurve = compareCurve;
                            closestIndex = j;
                        }
                    }
                }
                if(closestCurve != null){
                    int closestCurveMidIndex = (int)(closestCurve.length / 2);
                    double aLength = curve[0].distance(curve[curveMidIndex]);
                    double bLength = closestCurve[0].distance(closestCurve[closestCurveMidIndex]);
                    double distance = curve[curveMidIndex].distance(closestCurve[closestCurveMidIndex]);
                    double shortestLength = Math.min(aLength, bLength);
                    if(aLength < bLength && (distance * regionScale) < (aLength)){
                        insertedCurves.removeElementAt(closestIndex); // remove b
                        deletedCurves++;
                    } else if (bLength < aLength && (distance * regionScale) < (bLength)){
                        insertedCurves.removeElementAt(i); // remove a
                        deletedCurves++;
                    }
                }
            }
            System.out.println("Deleted curves: " + deletedCurves );
        
        return insertedCurves;
    }
    
    /**
     * pointsToMesh
     *
     * Description: Given a point cloud, caalculate edges and faces to generate a surface mesh.
     */
    public TriangleMesh pointsToMesh(Vector<Vec3> meshPoints){
        Vec3 [] meshVerts = new Vec3[meshPoints.size()];
        Vector face = new Vector();                             // Vector of int[3] vec indexes
        for(int i = 0; i < meshPoints.size(); i++){
            meshVerts[i] = (Vec3)meshPoints.elementAt(i);
        }
        
        Vector facePoints = new Vector(meshPoints);
        HashMap pointsUsed = new HashMap();
        HashMap edges = new HashMap();
        HashMap edgeIndexes = new HashMap();
        
        for(int p = 0; p < facePoints.size(); p++){
            Vec3 point = (Vec3)facePoints.elementAt(p);
            Vec3 closestPoint = new Vec3(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
            Vec3 closestPoint2 = new Vec3(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
            Vec3 closestPoint3 = new Vec3(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
            Vec3 closestPoint4 = new Vec3(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
            Vec3 closestPoint5 = new Vec3(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
            Vec3 closestPoint6 = new Vec3(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
            Vec3 closestPoint7 = new Vec3(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
            
            int closestPointIndex = -1;
            int secondClosestPointIndex = -1;
            int thirdClosestPointIndex = -1;
            int fourthClosestPointIndex = -1;
            int fifthClosestPointIndex = -1;
            int sixthClosestPointIndex = -1;
            int seventhClosestPointIndex = -1;
            int eighthClosestPointIndex = -1;
            
            Vector<Vec3> comparePoints = new Vector<Vec3>();
            // distance
            Vec3 point2Angle = new Vec3();
            // normalize();
            
            
            double distance = Double.MAX_VALUE;
            for(int i = 0; i < facePoints.size(); i++){
                Vec3 px = (Vec3)facePoints.elementAt(i);
                double d = point.distance(px);
                if(d < distance && i != p){
                    distance = d;
                    closestPoint = px;
                    closestPointIndex = i;
                }
            }
            double secondDistance = Double.MAX_VALUE;
            for(int i = 0; i < facePoints.size(); i++){
                Vec3 px = (Vec3)facePoints.elementAt(i);
                double d = point.distance(px);
                double d_1_2 = px.distance(closestPoint);
                if(d < secondDistance &&
                   i != closestPointIndex &&
                   distance < (d_1_2/2) &&                  // point1 and point2 must be farther away from each other than p-point1.
                   i != p){
                    secondDistance = d;
                    closestPoint2 = px;
                    secondClosestPointIndex = i;
                }
            }
            double thirdDistance = Double.MAX_VALUE;
            for(int i = 0; i < facePoints.size(); i++){
                Vec3 px = (Vec3)facePoints.elementAt(i);
                double d = point.distance(px);
                double d_con = Math.min(px.distance(closestPoint), px.distance(closestPoint2));
                if(d < thirdDistance &&
                   i != closestPointIndex &&
                   i != p &&
                   i != secondClosestPointIndex &&
                   secondDistance > (d_con/2)
                   ){
                    thirdDistance = d;
                    thirdClosestPointIndex = i;
                    closestPoint3 = px;
                }
            }
            double fourthDistance = Double.MAX_VALUE;
            for(int i = 0; i < facePoints.size(); i++){
                Vec3 px = (Vec3)facePoints.elementAt(i);
                double d = point.distance(px);
                double d_con = Math.min(Math.min(px.distance(closestPoint), px.distance(closestPoint2)), px.distance(closestPoint3));
                if(d < fourthDistance &&
                   i != closestPointIndex &&
                   i != p &&
                   i != secondClosestPointIndex &&
                   i != thirdClosestPointIndex &&
                   thirdDistance > (d_con/2)
                   ){
                    fourthDistance = d;
                    fourthClosestPointIndex = i;
                    closestPoint4 = px;
                }
            }
            double fifthDistance = Double.MAX_VALUE;
            for(int i = 0; i < facePoints.size(); i++){
                Vec3 px = (Vec3)facePoints.elementAt(i);
                double d = point.distance(px);
                double d_con = Math.min(Math.min(Math.min(px.distance(closestPoint), px.distance(closestPoint2)), px.distance(closestPoint3)), px.distance(closestPoint4));
                if(d < fifthDistance && i != closestPointIndex && i != p &&
                   i != secondClosestPointIndex &&
                   i != thirdClosestPointIndex &&
                   i != fourthClosestPointIndex &&
                   fourthDistance > (d_con/2)){
                    fifthDistance = d;
                    fifthClosestPointIndex = i;
                    closestPoint5 = px;
                }
            }
            double sixthDistance = Double.MAX_VALUE;
            for(int i = 0; i < facePoints.size(); i++){
                Vec3 px = (Vec3)facePoints.elementAt(i);
                double d = point.distance(px);
                double d_con = Math.min(Math.min(Math.min(Math.min(px.distance(closestPoint), px.distance(closestPoint2)), px.distance(closestPoint3)), px.distance(closestPoint4)), px.distance(closestPoint5));
                if(d < sixthDistance && i != closestPointIndex && i != p &&
                   i != secondClosestPointIndex &&
                   i != thirdClosestPointIndex &&
                   i != fourthClosestPointIndex &&
                   i != fifthClosestPointIndex &&
                   fifthDistance > (d_con/2)){
                    sixthDistance = d;
                    sixthClosestPointIndex = i;
                    closestPoint6 = px;
                }
            }
            double seventhDistance = Double.MAX_VALUE;
            for(int i = 0; i < facePoints.size(); i++){
                Vec3 px = (Vec3)facePoints.elementAt(i);
                double d = point.distance(px);
                double d_con = Math.min(Math.min(Math.min(Math.min(Math.min(px.distance(closestPoint), px.distance(closestPoint2)), px.distance(closestPoint3)), px.distance(closestPoint4)), px.distance(closestPoint5)), px.distance(closestPoint6));
                if(d < seventhDistance && i != closestPointIndex && i != p &&
                   i != secondClosestPointIndex &&
                   i != thirdClosestPointIndex &&
                   i != fourthClosestPointIndex &&
                   i != fifthClosestPointIndex &&
                   i != sixthClosestPointIndex &&
                   sixthDistance > (d_con/2)){
                    seventhDistance = d;
                    seventhClosestPointIndex = i;
                    closestPoint7 = px;
                }
            }
            double eighthDistance = Double.MAX_VALUE;
            for(int i = 0; i < facePoints.size(); i++){
                Vec3 px = (Vec3)facePoints.elementAt(i);
                double d = point.distance(px);
                
                if(d < eighthDistance && i != closestPointIndex && i != p &&
                   i != secondClosestPointIndex &&
                   i != thirdClosestPointIndex &&
                   i != fourthClosestPointIndex &&
                   i != fifthClosestPointIndex &&
                   i != sixthClosestPointIndex
                   ){
                    eighthDistance = d;
                    eighthClosestPointIndex = i;
                    //closestPoint8 = px;
                }
            }
            
            //if(  pointsUsed.containsKey(p) == false &&
            //   pointsUsed.containsKey(closestPointIndex) == false &&
            //   pointsUsed.containsKey(secondClosestPointIndex) == false  ){
                
                //int[] indexes  = new int[3];
                //indexes[0] = p; indexes[1] = closestPointIndex;  indexes[2] = secondClosestPointIndex;
                //face.addElement( indexes ); // TEMP prototype only
                //System.out.println(" mesh face " + p + " " + closestPointIndex + " "  + secondClosestPointIndex);
            //}
            //pointsUsed.put( p, true );
            //pointsUsed.put( closestPointIndex, true );
            //pointsUsed.put( secondClosestPointIndex, true );
            
            
            String edgeKey = Math.min(p, closestPointIndex) + "_" + Math.max(p, closestPointIndex);
            edges.put(edgeKey, true);
            String edgeKey2 = Math.min(p, secondClosestPointIndex) + "_" + Math.max(p, secondClosestPointIndex);
            edges.put(edgeKey2, true);
            String edgeKey3 = Math.min(p, thirdClosestPointIndex) + "_" + Math.max(p, thirdClosestPointIndex);
            edges.put(edgeKey3, true);
            String edgeKey4 = Math.min(p, fourthClosestPointIndex) + "_" + Math.max(p, fourthClosestPointIndex);
            edges.put(edgeKey4, true);
            String edgeKey5 = Math.min(p, fifthClosestPointIndex) + "_" + Math.max(p, fifthClosestPointIndex);
            edges.put(edgeKey5, true);
            String edgeKey6 = Math.min(p, sixthClosestPointIndex) + "_" + Math.max(p, sixthClosestPointIndex);
            edges.put(edgeKey6, true);
            String edgeKey7 = Math.min(p, seventhClosestPointIndex) + "_" + Math.max(p, seventhClosestPointIndex);
            edges.put(edgeKey7, true);
            String edgeKey8 = Math.min(p, eighthClosestPointIndex) + "_" + Math.max(p, eighthClosestPointIndex);
            edges.put(edgeKey8, true);
        }
        
        // Find faces from edges
        Iterator edgeIterator = edges.entrySet().iterator();
        Vector edgeKeys = new Vector();
        while (edgeIterator.hasNext()) {
            Map.Entry mapElement = (Map.Entry)edgeIterator.next();
            String key = (String)mapElement.getKey();
            edgeKeys.addElement(key);
        }
        
        for(int i = 0; i < edgeKeys.size(); i++){
            String key = (String)edgeKeys.elementAt(i);
            int a = firstInt(key);
            int b = secondInt(key);
            if(a != -1 && b != -1){
                //System.out.println("  ---- a: " + a + "  b: " + b);
                // Find another edge that match with a or b
                for(int j = 0; j < edgeKeys.size(); j++){   // Second edge that matches.
                    String key2 = (String)edgeKeys.elementAt(j);
                    int a2 = firstInt(key2);
                    int b2 = secondInt(key2);
                    if(i != j && a2 != -1 && b2 != -1){ // valid data and different from i
                        if(a == a2){
                            // Find third matching edge
                            for(int k = 0; k < edgeKeys.size(); k++){
                                String key3 = (String)edgeKeys.elementAt(k);
                                int a3 = firstInt(key3);
                                int b3 = secondInt(key3);
                                if(i != j && i != k && a3 != -1 && b3 != -1){
                                    if((b == a3 || b == b3) && (b2 == a3 || b2 == b3)){
                                        // Triangle: a, b, b2
                                        int[] ints = new int[3]; //{a, b, b2};
                                        ints[0] = a; ints[1] = b; ints[2] = b2;
                                        Arrays.sort(ints);
                                        //System.out.println( " test 1 " + ints[0] + "  2 " + ints[1] + "  3 " + ints[2]);
                                        String faceKey = ints[0] + "_" + ints[1] + "_" + ints[2];
                                        edgeIndexes.put(faceKey, true);
                                    }
                                }
                            }
                        }
                        if(a == b2){
                            // Find third matching edge
                            for(int k = 0; k < edgeKeys.size(); k++){
                                String key3 = (String)edgeKeys.elementAt(k);
                                int a3 = firstInt(key3);
                                int b3 = secondInt(key3);
                                if(i != j && i != k && a3 != -1 && b3 != -1){
                                    if((b == a3 || b == b3) && (a2 == a3 || a2 == b3)){
                                        // Triangle: a, b, a2
                                        int[] ints = new int[3]; //{a, b, a2};
                                        ints[0] = a; ints[1] = b; ints[2] = a2;
                                        Arrays.sort(ints);
                                        //System.out.println( " test 1 " + ints[0] + "  2 " + ints[1] + "  3 " + ints[2]);
                                        String faceKey = ints[0] + "_" + ints[1] + "_" + ints[2];
                                        edgeIndexes.put(faceKey, true);
                                    }
                                }
                            }
                        }
                        if(b == a2){
                            // Find third matching edge
                            for(int k = 0; k < edgeKeys.size(); k++){
                                String key3 = (String)edgeKeys.elementAt(k);
                                int a3 = firstInt(key3);
                                int b3 = secondInt(key3);
                                if(i != j && i != k && a3 != -1 && b3 != -1){
                                    if((a == a3 || a == b3) && (b2 == a3 || b2 == b3)){
                                        // Triangle: a, b, b2
                                        int[] ints = new int[3]; //{a, b, a2};
                                        ints[0] = a; ints[1] = b; ints[2] = b2;
                                        Arrays.sort(ints);
                                        //System.out.println( " test 1 " + ints[0] + "  2 " + ints[1] + "  3 " + ints[2]);
                                        String faceKey = ints[0] + "_" + ints[1] + "_" + ints[2];
                                        edgeIndexes.put(faceKey, true);
                                    }
                                }
                            }
                        }
                        if(b == b2){
                            // Find third matching edge
                            for(int k = 0; k < edgeKeys.size(); k++){
                                String key3 = (String)edgeKeys.elementAt(k);
                                int a3 = firstInt(key3);
                                int b3 = secondInt(key3);
                                if(i != j && i != k && a3 != -1 && b3 != -1){
                                    if((a == a3 || a == b3) && (a2 == a3 || a2 == b3)){
                                        // Triangle: a, b, a2
                                        int[] ints = new int[3]; //{a, b, a2};
                                        ints[0] = a; ints[1] = b; ints[2] = a2;
                                        Arrays.sort(ints);
                                        //System.out.println( " test 1 " + ints[0] + "  2 " + ints[1] + "  3 " + ints[2]);
                                        String faceKey = ints[0] + "_" + ints[1] + "_" + ints[2];
                                        edgeIndexes.put(faceKey, true);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            //System.out.println(mapElement.getKey() + " : " + marks);
        }
        
        // Print edge indexed
        Iterator faceIndexesIterator = edgeIndexes.entrySet().iterator();
        while (faceIndexesIterator.hasNext()) {
            Map.Entry mapElement = (Map.Entry)faceIndexesIterator.next();
            String key = (String)mapElement.getKey();
            int a = firstInt(key);
            int b = secondInt(key);
            int c = thirdInt(key);
            //System.out.println("Face  " + key + " : " + a + " : " + b + " : " + c);
            
            int[] indexes  = new int[3];
            indexes[0] = a; indexes[1] = b;  indexes[2] = c;
            face.addElement( indexes ); // TEMP prototype only
            //System.out.println(" mesh face " + p + " " + closestPointIndex + " "  + secondClosestPointIndex);
            
        }
        
        System.out.println("Faces:  " + face.size());
        
        int faces[][] = new int [face.size()][3];
        for(int i = 0; i < face.size(); i++){
            int[] faceIndexes = (int[])face.elementAt(i);
            faces[i][0] = faceIndexes[0];
            faces[i][1] = faceIndexes[1];
            faces[i][2] = faceIndexes[2];
        }
        //faces = new int[10][10];
        //int f[][] = new int [face.size()][3];
        
        TriangleMesh tm = new TriangleMesh(meshVerts, faces );
        
        return tm;
    }
    
    public int firstInt(String key){
        int a = -1;
        int sep = key.indexOf("_");
        if(sep != -1){
            String as = key.substring(0, sep);
            String bs = key.substring(sep+1, key.length());
            
            sep = bs.indexOf("_");
            if(sep != -1){
                bs = bs.substring(0, sep);
            }
            
            a = Integer.parseInt(as);
            int b = Integer.parseInt(bs);
        }
        return a;
    }
    public int secondInt(String key){
        int b = -1;
        int sep = key.indexOf("_");
        if(sep != -1){
            String as = key.substring(0, sep);
            String bs = key.substring(sep+1, key.length());
            
            sep = bs.indexOf("_");
            if(sep != -1){
                bs = bs.substring(0, sep);
            }
            
            int a = Integer.parseInt(as);
            b = Integer.parseInt(bs);
        }
        return b;
    }
    public int thirdInt(String key){
        int c = -1;
        int sep = key.indexOf("_");
        if(sep != -1){
            String as = key.substring(0, sep);
            String bs = key.substring(sep+1, key.length());
            sep = bs.indexOf("_");
            if(sep != -1){
                String cs = bs.substring(sep + 1, bs.length());
                c = Integer.parseInt(cs);
            }
        }
        return c;
    }
    
    /**
     * isClosest
     *
     * Description: Used to determine if mesh smoothing should apply from current curves. If not closest curve points then other points would be better.
     */
    public boolean isClosest(Vector curves, Vec3 midpoint, double distance, int ignoreC, int ignoreCx){
        for(int c = 0; c < curves.size(); c++){
            if(c != ignoreC && c != ignoreCx){
                Vec3[] curve = (Vec3[])curves.elementAt(c);
                for(int v = 1; v < curve.length - 1; v++){
                    Vec3 vec = (Vec3)curve[v];
                    double currDistance = vec.distance(midpoint);
                    if(currDistance < distance){
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    /**
     * getRegionCurvature
     *
     * Description:
     */
    public Vec3 getRegionCurvature(Vector curves, HashMap vertCurveMap, Vec3 midPoint, double distance){
        Vec3 curvature = new Vec3();
        int count = 0;
        for(int c = 0; c < curves.size(); c++){
            Vec3[] curve = (Vec3[])curves.elementAt(c);
            for(int v = 1; v < curve.length - 1; v++){
                Vec3 vec = (Vec3)curve[v];
                double currDistance = midPoint.distance(vec);
                if(currDistance <= distance * 2.1){
                    String curveKey = c + "_" + v;
                    Vec3 currCurve = (Vec3)vertCurveMap.get(curveKey);
                    
                    // reduce curvature by distance
                    
                    double scale = distance / currDistance;
                    if(scale < 1.0){
                        //currCurve.x *= scale;
                        //currCurve.y *= scale;
                        //currCurve.z *= scale;
                    }
                    
                    curvature.add(currCurve);
                    count++;
                }
            }
        }
        // Divide
        if(count > 0){
            curvature.x = curvature.x / ((double)count);
            curvature.y = curvature.y / ((double)count);
            curvature.z = curvature.z / ((double)count);
        }
        return curvature;
    }
    
    
    /**
     * isEvenPoints
     *
     * Description: Only connect   equal points to prevent cross lines.
     */
    public boolean isEvenPoints(Vector curves, int c, int cx , int v, int vx  ){
        boolean result = false;
        Vec3[] cCurve = (Vec3[])curves.elementAt(c);
        Vec3[] cxCurve = (Vec3[])curves.elementAt(cx);
        int cLength = cCurve.length;
        int cxLength = cxCurve.length;
        if( v == vx ){ // && v <= (cLength / 2) && vx <= (cxLength / 2)
            result = true;
        }
        if( v ==  cxLength - vx ){ // && v <= (cLength / 2) && vx <= (cxLength / 2)
            result = true;
        }
        if( cLength - v == vx ){ // && v <= (cLength / 2) && vx <= (cxLength / 2)
            result = true;
        }
        if( cLength - v == cxLength - vx ){
            result = true;
        }
        double aDistance = cCurve[v].distance(cxCurve[vx]);
        double bDistance = cCurve[cLength-v].distance(cxCurve[vx]);
        double cDistance = cCurve[v].distance(cxCurve[cxLength-vx]);
        double dDistance = cCurve[cLength-v].distance(cxCurve[cxLength-vx]);
        if(result && aDistance <= bDistance && aDistance <= cDistance && aDistance <= dDistance){
            result = true;
        } else {
            result = false;
        }
        return result;
    }
    
    /**
     * splineGridSkin
     *
     * Description: Given selected splines, generate interpelated splines to fill in gaps and
     * generate a smoothed mesh based on the surface.
     */
    public void splineGridSkin(Scene scene, LayoutWindow layoutWindow, Vector<ObjectInfo> objects){
        System.out.println("SplineSkin.splineGridSkin");
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
        orderPointsInCurves(XzCurves);
        
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
            //System.out.println("Yz spline pair " + i);
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
            //System.out.println("XzCurves "+  XzCurves.size() + "  XyCurves "  + XyCurves.size() );
            
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
            
            Vec3[] midCurve2 = generateMidCurve(midPoints, scanCurves, scene, layoutWindow);
            
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
        /*
        for(int i = 0; i < YzCurves.size(); i++){
            Vec3[] spline = (Vec3[])YzCurves.elementAt(i);
            
            Curve midCurve = getCurve(spline); //  new Curve(spline, midSplineSmoothness, Mesh.APPROXIMATING, false);
            ObjectInfo midCurveInfo = new ObjectInfo(midCurve, new CoordinateSystem(), "midcurve Yz " + i);
            scene.addObject(midCurveInfo, null);
            
            layoutWindow.updateImage();
            layoutWindow.updateMenus();
            layoutWindow.rebuildItemList();
        }
        */
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
            Vec3[] midCurve2 = generateMidCurve(midPoints, scanCurves, scene, layoutWindow);
            
            addedCurves.addElement(midCurve2);
            
            //addedCurves.addElement(midSpline);
            //insertOrdered(XzCurves, midSpline, SplineSkin.Y); // recursive
        }
        for(int i = 0; i < addedCurves.size(); i++){
            Vec3[] spline = (Vec3[])addedCurves.elementAt(i);
            insertOrdered(XzCurves, spline, SplineSkin.Z); // Add new curve
        }
        // Equalize point count in each group of curves. Equal points is required if skin to mesh is used.
        equalizeCurvePointCounts(XzCurves, scene);
        skinMesh(XzCurves, scene, layoutWindow, "Xz");
        
        
        
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
    
    /*
     // DEPRICATE
    public Vec3 [] subdivideCurve(Vec3 [] curveVerts, int subdivisions){
        Vec3[] subdivided = curveVerts;
        for(int subs = 0; subs < subdivisions; subs++){
            Curve curve = getCurve(curveVerts);
            curve = curve.subdivideCurve();
            curveVerts = curve.getVertexPositions();
            subdivided = curveVerts;
        }
        return subdivided;
    }
     */
    
    /**
     * subdivideCurves
     *
     * Description: given a list of curve points, subdivide the mesh.
     *
     * @paraam Vector curves
     * @param int subdivisions
     * @return Vector
     */
    public Vector<Vec3> subdivideCurves(Vector<Vec3> curves, int subdivisions){
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
    
    
    /**
     * orderPointsInCurves
     *
     * Description:
     */
    public void orderPointsInCurves(Vector curves){
        for(int i = 1; i < curves.size(); i++){
            Vec3 [] curve = (Vec3 [])curves.elementAt(i);
            if(curve.length > 1){
                if(curve[0].x < curve[curve.length-1].x){
                    //System.out.println(" Reverse curve points: " + i);
                    Vec3[] reversed = new Vec3[curve.length];
                    for(int j = 0; j < reversed.length; j++){
                        reversed[j] = curve[ (curve.length - 1) - j  ];
                    }
                    curves.setElementAt(reversed, i);
                }
            }
            
            //for(int p = 0; p < curve.length; p++){
            //    Vec3 point = (Vec3)curve[p];
            
            //    System.out.println(" i " + p + "    x " + point.x   );
            //}
            
        }
    }
}
