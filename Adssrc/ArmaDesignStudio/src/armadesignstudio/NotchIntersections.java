/* Copyright (C) 2020 by Jon Taylor

This program is free software; you can redistribute it and/or modify it under the
terms of the GNU General Public License as published by the Free Software
Foundation; either version 2 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE.  See the GNU General Public License for more details. */

package armadesignstudio;

import armadesignstudio.object.*;
import armadesignstudio.math.*;
import java.util.Vector;

public class NotchIntersections {
    private LayoutWindow window = null;

    // Constructor requires the Scene object and layout window in order to add generated objects.
    NotchIntersections(Scene scene, LayoutWindow window){
        this.window = window;
    }
    
    /**
     * EdgeStruct
     *
     * Description: Class used as a struc to store edge geometry for processing. The Object mesh objects store geometry
     *  in a list of vectors and indexes to vectors and I need just quick access to edge points in an easy way to pass around.
     */
    class EdgeStruct {
        public EdgeStruct(int id, Vec3 vec1, Vec3 vec2){
            objectID = id; this.vec1 = vec1; this.vec2 = vec2;
        }
        public int objectID = -1;
        public Vec3 vec1 = null;
        public Vec3 vec2 = null;
        public Vector<Vec3> intermediates;
    }
    
    /**
     * notchIntersections
     *
     * Description: Create a notch spline around a tube object where it intersects with other objects.
     *
     * @param Scene  used to add notch curve to the project and update the view and menu.
     */
    public void notchIntersections(Scene scene){
        LayoutModeling layout = new LayoutModeling();
        int selection[] = scene.getSelection();
        if(selection.length > 0){
            // Get scene edges to use when detecting intersection of selection.
            ObjectInfo info = scene.getObject(selection[0]); // .duplicate();
            //System.out.println("obj " + info);
            Object co = (Object)info.getObject();
            //Object3D o3d = object.getObject().duplicate();
            if(info.getObject().canConvertToTriangleMesh() != Object3D.CANT_CONVERT){ // If selected is a suitable object
                
                //
                // Load scene object edges vectors into data structures.
                //
                Vector<EdgeStruct> edgeStructs = new Vector<EdgeStruct>();
                Vector<ObjectInfo> sceneObjectInfos = scene.getObjects();
                for(int i = 0; i < sceneObjectInfos.size(); i++){
                    //if(i != selection[0]){ // doesn't support multiple selection.
                    ObjectInfo objectInfo = sceneObjectInfos.elementAt(i);
                    Object obj = objectInfo.getObject();
                    System.out.println("class: " + obj.getClass().getName());
                    if((obj instanceof armadesignstudio.object.SceneCamera) == false &&
                       (obj instanceof armadesignstudio.object.DirectionalLight) == false &&
                       (obj instanceof armadesignstudio.object.Curve) == false){
                    
                        //objectInfo = objectInfo.duplicate();
                        CoordinateSystem objectCS;
                        objectCS = layout.getCoords(objectInfo);
                        //BoundingBox compareBounds = compareOI.getTranslatedBounds();
                        //boolean intersects = bounds.intersects(compareBounds);
                        //if(intersects){
                            //System.out.println(" intersect: " + compareOI.getName());
                            TriangleMesh triangleMesh = null;
                            triangleMesh = objectInfo.getObject().convertToTriangleMesh(0.05);
                            if(triangleMesh != null){
                                MeshVertex[] objectVerts = triangleMesh.getVertices();
                                TriangleMesh.Edge[] objectEdges = ((TriangleMesh)triangleMesh).getEdges();
                                for(int ce = 0; ce < objectEdges.length; ce++){ //  && running
                                    TriangleMesh.Edge objectEdge = objectEdges[ce];
                                    Vec3 vec1 = new Vec3(objectVerts[objectEdge.v1].r);
                                    Vec3 vec2 = new Vec3(objectVerts[objectEdge.v2].r);
                                    Mat4 mat4 = objectCS.duplicate().fromLocal();
                                    mat4.transform(vec1);
                                    mat4.transform(vec2);
                                    EdgeStruct edgeStruct = new EdgeStruct(i, vec1, vec2);
                                    edgeStruct.intermediates = intermediatePoints(vec1, vec2);
                                    
                                    edgeStructs.addElement(edgeStruct);
                                    
                                    //System.out.println("Edge " + i + "  x: " + vec1.x + "  y: " + vec1.y +   "  z: " +
                                    //                   vec2.z + "-  x: " + vec2.x + "  y: " + vec2.y +   "  z: " + vec2.z);
                                }
                            }
                        //}
                    }
                }
                //
                //
                //
                System.out.println("Captured ");
                
                double shortestEdge = 999;
                
                // get edges of selected object
                Vector<EdgeStruct> selectedObjectEdges = new Vector<EdgeStruct>();
                for(int e = 0; e < edgeStructs.size(); e++){
                    EdgeStruct edgeStruct = edgeStructs.elementAt(e);
                    if(edgeStruct.objectID == selection[0]){
                        selectedObjectEdges.addElement(edgeStruct);
                        
                        double edgeDistance = edgeStruct.vec1.distance(edgeStruct.vec2);
                        if(edgeDistance < shortestEdge){
                            shortestEdge = edgeDistance;
                        }
                    }
                }
                   
                BoundingBox bounds = info.getTranslatedBounds();
                
                // Get edges of other objects that could intersect with the selected object.
                sceneObjectInfos = scene.getObjects();
                for(int i = 0; i < sceneObjectInfos.size(); i++){
                    ObjectInfo compareOI = sceneObjectInfos.elementAt(i);
                    Object obj = compareOI.getObject();
                    //System.out.println("class: " + obj.getClass().getName());
                    if((obj instanceof armadesignstudio.object.SceneCamera) == false &&
                       (obj instanceof armadesignstudio.object.DirectionalLight) == false){
                        
                        if(i != selection[0]){ // doesn't support multiple selection.
                            
                            //compareOI = compareOI.duplicate();
                            BoundingBox compareBounds = compareOI.getTranslatedBounds();
                            boolean intersects = bounds.intersects(compareBounds);
                            if(intersects){                                                     // Intersection of object bounds
                                System.out.println(" intersect: " + compareOI.getName());
                                
                                Vector<Vec3> notchPoints = new Vector<Vec3>();
                                Vector<Vec3> notchPoints2 = new Vector<Vec3>();
                                
                                for(int f = 0; f < selectedObjectEdges.size(); f++){            // sel edges
                                    EdgeStruct selectedEdgeStruct = selectedObjectEdges.elementAt(f);
                                    
                                    Vector<Vec3> selectedIntermediates = selectedEdgeStruct.intermediates;
                                    // intermediatePoints(selectedEdgeStruct.vec1, selectedEdgeStruct.vec2);
                                
                                    double maxDistance = shortestEdge; //  selectedEdgeStruct.vec1.distance(selectedEdgeStruct.vec2) / 15; // 12 threshold distance
                                    //System.out.println("  shortestEdge: " + shortestEdge + " maxDistance: " + maxDistance);
                                    
                                    // Closest point - (There can be multiple closest points)
                                    Vec3 closestPoint = null;
                                    double closestPointDistance = 999;
                                    Vec3 secondClosestPoint = null;
                                    double secondClosestPointDistance = 999;
                                    
                                    for(int e = 0; e < edgeStructs.size(); e++){
                                        EdgeStruct edgeStruct = edgeStructs.elementAt(e);
                                        if(edgeStruct.objectID == i){                           // edges of collided object
                                            //
                                            boolean edgesCollide = edgesCollide(selectedEdgeStruct, edgeStruct); // performance optimization
                                            if(edgesCollide){
                                            
                                                Vector<Vec3> compareIntermediates = edgeStruct.intermediates;
                                                // intermediatePoints(edgeStruct.vec1, edgeStruct.vec2);
                                                for(int a = 0; a < selectedIntermediates.size(); a++){
                                                    for(int b = 0; b < compareIntermediates.size(); b++){
                                                        Vec3 av = selectedIntermediates.elementAt(a);
                                                        Vec3 bv = compareIntermediates.elementAt(b);
                                                        double distance = av.distance(bv);
                                                        if(distance < closestPointDistance &&
                                                           distance < (maxDistance * 0.45) ) {
                                                            closestPoint = av; // closest point along the selected object edges.
                                                        }
                                                        if(distance < secondClosestPointDistance &&
                                                           distance > closestPointDistance &&
                                                           distance < maxDistance){
                                                            secondClosestPoint = av;
                                                            
                                                            System.out.print(".");
                                                        }
                                                    }
                                                }
                                            }
                                            
                                        }
                                    }
                                    if(closestPoint != null){
                                        notchPoints.addElement(closestPoint);
                                        
                                    }
                                    if(secondClosestPoint != null){
                                        notchPoints2.addElement(secondClosestPoint);
                                    }
                                }
                                // Create curve from notchPoints
                                if(notchPoints.size() > 0){
                                    System.out.println(" notchPoints.size() "+ notchPoints.size());
                                    float[] s_ = new float[notchPoints.size()]; // s_[0] = 0; s_[1] = 0; s_[2] = 0;
                                    for(int ii = 0; ii < notchPoints.size(); ii++){
                                        s_[ii] = 0;
                                    }
                                    Vec3[] vertex = new Vec3[notchPoints.size()];
                                    for(int ii = 0; ii < notchPoints.size(); ii++){
                                        Vec3 point = notchPoints.elementAt(ii);
                                        vertex[ii] = point;
                                    }
                                    Curve notchCurve = new Curve(vertex, s_, 0, true); // false
                                    CoordinateSystem coords = new CoordinateSystem(new Vec3(), Vec3.vz(), Vec3.vy());
                                    ObjectInfo notchInfo = new ObjectInfo(notchCurve, coords, "Notch " ); // + ++p
                                    notchInfo.setParent(info); // Add perferation object to selection.
                                    info.addChild(notchInfo, info.getChildren().length); // info.getChildren().length+1
                                    
                                    window.addObject(notchInfo, null); // Add ObjectInfo
                                    
                                    System.out.println("Add notch");
                                    
                                    // tab and slot this notched joint.
                                    notchTabAndSlot(info, compareOI, notchPoints); // pass in objects and notch geometry
                                }
                                
                                if(notchPoints2.size() > 1){
                                    System.out.println(" notchPoints2.size() "+ notchPoints2.size());
                                    float[] s_ = new float[notchPoints2.size()]; // s_[0] = 0; s_[1] = 0; s_[2] = 0;
                                    for(int ii = 0; ii < notchPoints2.size(); ii++){
                                        s_[ii] = 0;
                                    }
                                    Vec3[] vertex = new Vec3[notchPoints2.size()];
                                    for(int ii = 0; ii < notchPoints2.size(); ii++){
                                        Vec3 point = notchPoints2.elementAt(ii);
                                        vertex[ii] = point;
                                        
                                        System.out.println("poimt " + point.x + " " + point.y + " " + point.z);
                                    }
                                    Curve notchCurve2 = new Curve(vertex, s_, 0, true); // false
                                    CoordinateSystem coords2 = new CoordinateSystem(new Vec3(), Vec3.vz(), Vec3.vy());
                                    ObjectInfo notchInfo2 = new ObjectInfo(notchCurve2, coords2, "Notch 2" ); // + ++p
                                    notchInfo2.setParent(info); // Add perferation object to selection.
                                    info.addChild(notchInfo2, info.getChildren().length); // info.getChildren().length+1
                                    
                                    window.addObject(notchInfo2, null); // Add ObjectInfo
                                    
                                    System.out.println("Add notch 2");
                                
                                }
                                
                            }
                        }
                    }
                }
                            
                
                //System.out.println("bounds: x: " + bounds.minx + " " + bounds.maxx +
                //                   " y: " + bounds.miny + " " + bounds.maxy +
                //                   " z: " + bounds.minz + " " + bounds.maxz);
               
                
                window.updateImage();
                window.updateTree(); // Tell the tree it has changed.
                
                
                System.out.println("Notch done");
            }
            
            
            
            /*
            Vec3 a = new Vec3(1, 0, 0);
            Vec3 b = new Vec3(100, 1, -1);
            Vector<Vec3> intermediates = intermediatePoints(a, b);
            for(int i = 0; i < intermediates.size(); i++){
                Vec3 intermediate = intermediates.elementAt(i);
                System.out.println(" _ " + intermediate.x + "  y " + intermediate.y + " z " + intermediate.z);
            }
            */
        }
    }
    
    /**
     * notchTabAndSlot
     *
     * Description: A selected object, add a tab or slot with a colliding object notched with.
     */
    public void notchTabAndSlot(ObjectInfo selectedObject, ObjectInfo collidedObject, Vector<Vec3> notchPoints){
        
        // determine if object is tab or slot.
        
        // Add geometry to object.
        
        
    }
    
    /**
     * edgesCollide
     *
     * Description: return true if bounds of two edges collide.
     */
    public boolean edgesCollide(EdgeStruct edgeStructA, EdgeStruct edgeStructB){
        boolean result = true;
        BoundingBox boundsA = new BoundingBox(edgeStructA.vec1, edgeStructA.vec2);
        BoundingBox boundsB = new BoundingBox(edgeStructB.vec1, edgeStructB.vec2);
        result = boundsA.intersects(boundsB);
        return result;
    }
    
    /**
     * intermediatePoints
     *
     * Description: generate a list of equally spaced  intermediate points in a line between two points.
     *  Started out hard coded, then rolled up into a loop. Will clean up later.
     */
    public Vector<Vec3> intermediatePoints(Vec3 vec1, Vec3 vec2){
        Vector<Vec3> points = new Vector<Vec3>();
        
        Vec3 midPoint = vec1.midPoint(vec2);
        
        
        Vec3 midPoint2 = vec1.midPoint(midPoint);
        Vec3 midPoint3 = midPoint.midPoint(vec2);
        
        Vec3 midPoint4 = vec1.midPoint(midPoint2);
        Vec3 midPoint5 = midPoint2.midPoint(midPoint);
        Vec3 midPoint6 = midPoint.midPoint(midPoint3);
        Vec3 midPoint7 = midPoint3.midPoint(vec2);
        
        Vec3 midPoint8 = vec1.midPoint(midPoint4);
        Vec3 midPoint9 = midPoint4.midPoint(midPoint2);
        Vec3 midPoint10 = midPoint2.midPoint(midPoint5);
        Vec3 midPoint11 = midPoint5.midPoint(midPoint);
        Vec3 midPoint12 = midPoint.midPoint(midPoint6);
        Vec3 midPoint13 = midPoint6.midPoint(midPoint3);
        Vec3 midPoint14 = midPoint3.midPoint(midPoint7);
        Vec3 midPoint15 = midPoint7.midPoint(vec2);
        
        Vec3 midPoint16 = vec1.midPoint(midPoint8);
        Vec3 midPoint17 = midPoint8.midPoint(midPoint4);
        Vec3 midPoint18 = midPoint4.midPoint(midPoint9);
        Vec3 midPoint19 = midPoint9.midPoint(midPoint2);
        Vec3 midPoint20 = midPoint2.midPoint(midPoint10);
        Vec3 midPoint21 = midPoint10.midPoint(midPoint5);
        Vec3 midPoint22 = midPoint5.midPoint(midPoint11);
        Vec3 midPoint23 = midPoint11.midPoint(midPoint);
        Vec3 midPoint24 = midPoint.midPoint(midPoint12);
        Vec3 midPoint25 = midPoint12.midPoint(midPoint6);
        Vec3 midPoint26 = midPoint6.midPoint(midPoint13);
        Vec3 midPoint27 = midPoint13.midPoint(midPoint3);
        Vec3 midPoint28 = midPoint3.midPoint(midPoint14);
        Vec3 midPoint29 = midPoint14.midPoint(midPoint7);
        Vec3 midPoint30 = midPoint7.midPoint(midPoint15);
        Vec3 midPoint31 = midPoint15.midPoint(vec2);
        
        
        
        points.addElement(vec1);
        points.addElement(midPoint16 );
        points.addElement(midPoint8);
        points.addElement(midPoint17 );
        points.addElement(midPoint4);
        points.addElement(midPoint18 );
        points.addElement(midPoint9);
        points.addElement(midPoint19 );
        points.addElement(midPoint2);
        points.addElement(midPoint20 );
        points.addElement(midPoint10);
        points.addElement(midPoint21);
        points.addElement(midPoint5);
        points.addElement(midPoint22);
        points.addElement(midPoint11);
        points.addElement(midPoint23);
        points.addElement(midPoint);
        points.addElement(midPoint24);
        points.addElement(midPoint12);
        points.addElement(midPoint25);
        points.addElement(midPoint6);
        points.addElement(midPoint26);
        points.addElement(midPoint13);
        points.addElement(midPoint27);
        points.addElement(midPoint3);
        points.addElement(midPoint28);
        points.addElement(midPoint14);
        points.addElement(midPoint29);
        points.addElement(midPoint7);
        points.addElement(midPoint30);
        points.addElement(midPoint15);
        points.addElement(midPoint31);
        points.addElement(vec2);
        
        
        for(int subdivs = 0; subdivs < 5; subdivs++){
            Vector<Vec3> expandedPoints = new Vector<Vec3>();
            for(int i = 1; i < points.size(); i++){
                Vec3 a = points.elementAt(i-1);
                Vec3 b = points.elementAt(i);
                Vec3 midPointX = a.midPoint(b);
                expandedPoints.addElement(midPointX);
            }
            for(int i = 0; i < expandedPoints.size(); i++){
                points.insertElementAt( expandedPoints.elementAt( i ), (i*2) + 1 );
            }
        }
        
        return points;
    }
    
}

