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

    NotchIntersections(Scene scene, LayoutWindow window){
        System.out.println("NotchIntersections ");
        this.window = window;
    }
    
    class EdgeStruct {
        public EdgeStruct(int id, Vec3 vec1, Vec3 vec2){
            objectID = id; this.vec1 = vec1; this.vec2 = vec2;
        }
        public int objectID = -1;
        public Vec3 vec1 = null;
        public Vec3 vec2 = null;
    }
    
    /**
     * notchIntersections
     *
     * Description: Create a notch spline around a tube object where it intersects with other objects.
     */
    public void notchIntersections(Scene scene){
        LayoutModeling layout = new LayoutModeling();
        int selection[] = scene.getSelection();
        if(selection.length > 0){
            // Get scene edges to use when detecting intersection of selection.
            
            ObjectInfo info = scene.getObject(selection[0]);
            
            System.out.println("obj " + info);
            Object co = (Object)info.getObject();
            /*
            if((co instanceof Mesh) == true){
                // Find object orientation
                BoundingBox bounds = getTranslatedBounds(info);
                System.out.println("bounds " + bounds.minx + " " + bounds.maxx + " " +
                                   bounds.miny + " " + bounds.maxy + " " +
                                   bounds.minz + " " + bounds.maxz + " " );
                
                // Iterate through each edge segment.
                
                // Iterate through other objects to find intersections.
                
            } else {
                System.out.println("Selected object is not a mesh. " );
            }
             */
            
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
                                    edgeStructs.addElement(edgeStruct);
                                    
                                    //System.out.println("Edge " + i + "  x: " + vec1.x + "  y: " + vec1.y +   "  z: " +
                                    //                   vec2.z + "-  x: " + vec2.x + "  y: " + vec2.y +   "  z: " + vec2.z);
                                }
                            }
                        //}
                    //}
                }
                
                
                // get edges of selected object
                Vector<EdgeStruct> selectedObjectEdges = new Vector<EdgeStruct>();
                for(int e = 0; e < edgeStructs.size(); e++){
                    EdgeStruct edgeStruct = edgeStructs.elementAt(e);
                    if(edgeStruct.objectID == selection[0]){
                        selectedObjectEdges.addElement(edgeStruct);
                    }
                }
                        
             
                BoundingBox bounds = info.getTranslatedBounds();
                
                
                // Get edges of other objects that could intersect with the selected object.
                sceneObjectInfos = scene.getObjects();
                for(int i = 0; i < sceneObjectInfos.size(); i++){
                    if(i != selection[0]){ // doesn't support multiple selection.
                        ObjectInfo compareOI = sceneObjectInfos.elementAt(i);
                        BoundingBox compareBounds = compareOI.getTranslatedBounds();
                        boolean intersects = bounds.intersects(compareBounds);
                        if(intersects){
                            System.out.println(" intersect: " + compareOI.getName());
                            
                            Vector<Vec3> notchPoints = new Vector<Vec3>();
                            
                            for(int f = 0; f < selectedObjectEdges.size(); f++){
                                EdgeStruct selectedEdgeStruct = selectedObjectEdges.elementAt(f);
                                
                                Vector<Vec3> selectedIntermediates = intermediatePoints(selectedEdgeStruct.vec1, selectedEdgeStruct.vec2);
                            
                                // Closest point
                                Vec3 closestPoint = null;
                                double closestPointDistance = 999;
                                
                                for(int e = 0; e < edgeStructs.size(); e++){
                                    EdgeStruct edgeStruct = edgeStructs.elementAt(e);
                                    if(edgeStruct.objectID == i){ // edges of collided object
                                        Vector<Vec3> compareIntermediates = intermediatePoints(edgeStruct.vec1, edgeStruct.vec2);
                                        for(int a = 0; a < selectedIntermediates.size(); a++){
                                            for(int b = 0; b < compareIntermediates.size(); b++){
                                                Vec3 av = selectedIntermediates.elementAt(a);
                                                Vec3 bv = compareIntermediates.elementAt(b);
                                                double distance = av.distance(bv);
                                                if(distance < closestPointDistance){
                                                    closestPoint = av; // closest point along the selected object edges.
                                                }
                                            }
                                        }
                                        
                                    }
                                }
                                if(closestPoint != null){
                                    notchPoints.addElement(closestPoint);
                                }
                            
                            }
                            // Create curve from notchPoints
                            // TODO
                            if(notchPoints.size() > 0){
                                float[] s_ = new float[notchPoints.size()]; // s_[0] = 0; s_[1] = 0; s_[2] = 0;
                                for(int ii = 0; ii < notchPoints.size(); ii++){
                                    s_[ii] = 0;
                                }
                                Vec3[] vertex = new Vec3[notchPoints.size()];
                                for(int ii = 0; ii < notchPoints.size(); ii++){
                                    Vec3 point = notchPoints.elementAt(ii);
                                    vertex[ii] = point;
                                }
                                Curve perferationCurve = new Curve(vertex, s_, 0, true); // false
                                CoordinateSystem coords = new CoordinateSystem(new Vec3(), Vec3.vz(), Vec3.vy());
                                ObjectInfo perferationInfo = new ObjectInfo(perferationCurve, coords, "Notch " ); // + ++p
                                perferationInfo.setParent(info); // Add perferation object to selection.
                                info.addChild(perferationInfo, info.getChildren().length); // info.getChildren().length+1
                                
                                window.addObject(perferationInfo, null); // Add ObjectInfo
                            }
                            
                        }
                    }
                }
                            
                
                //TriangleMesh triangleMesh = null;
                //triangleMesh = info.getObject().convertToTriangleMesh(0.05);
                
                //System.out.println("bounds: x: " + bounds.minx + " " + bounds.maxx +
                //                   " y: " + bounds.miny + " " + bounds.maxy +
                //                   " z: " + bounds.minz + " " + bounds.maxz);
                /*
                CoordinateSystem c;
                c = layout.getCoords(info);
                MeshVertex[] verts = triangleMesh.getVertices();
                TriangleMesh.Edge[] edges = ((TriangleMesh)triangleMesh).getEdges();
                for(int e = 0; e < edges.length; e++){
                    TriangleMesh.Edge edge = edges[e];
                    
                    Vec3 vec1 = new Vec3(verts[edge.v1].r);
                    Vec3 vec2 = new Vec3(verts[edge.v2].r);
                    Mat4 mat4 = c.duplicate().fromLocal();
                    mat4.transform(vec1);
                    mat4.transform(vec2);
                    
                    Vector midPoints = intermediatePoints(vec1, vec2);
                                                
                    //System.out.println("Edge x: " + vec1.x + "  y: " + vec1.y +   "  z: " +
                    //                   vec2.z + "-  x: " + vec2.x + "  y: " + vec2.y +   "  z: " + vec2.z + "   " + e);
                    
                    double edgeLength = vec1.distance(vec2);
                    //System.out.println("edgeLength: " + edgeLength);
                    
                    // Generate list of intermediate points along edge.
                    Vector intermediatePoints = intermediatePoints(vec1, vec2);
                    
                    
                                //TriangleMesh compareTriangleMesh = null;
                                //compareTriangleMesh = compareOI.getObject().convertToTriangleMesh(0.05);
                                //MeshVertex[] compareVerts = compareTriangleMesh.getVertices();
                                //TriangleMesh.Edge[] compareEdges = ((TriangleMesh)compareTriangleMesh).getEdges();
                                //for(int ce = 0; ce < compareEdges.length; ce++){ //  && running
                                    //TriangleMesh.Edge compareEdge = compareEdges[ce];
                                    //Vec3 vec1 = new Vec3(verts[edge.v1].r);
                                    //Vec3 vec2 = new Vec3(verts[edge.v2].r);
                                //}
                                
                            //if(info.getObject().canConvertToTriangleMesh() != Object3D.CANT_CONVERT){
                            
                            //    System.out.println("Check for intersection with: " + compareOI.getName() + " " +  compareOI.getClass().getName());
                            
                            //}
                        }
                    }
                    
                }
                
                */
                
            }
            
            // Generate curve
            
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
     * intermediatePoints
     *
     * Description: generate a list of equally spaced  intermediate points in a line between two points.
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
        return points;
    }
    
    
    
    
    
    /**
     * isCloseToPoints
     *
     * Description: check if a point is within a given distance to a list of points.
     */
    public boolean isCloseToPoints(Vec3 point, Vector <Vec3>points, double distance){
        for(int i = 0; i < points.size(); i++){
            Vec3 comparePoint = points.elementAt(i);
            if(point.distance(comparePoint) < distance){
                return true;
            }
        }
        return false;
    }
    
    /**
     * removeExistingPerferations
     *
     *
     */
    public void removeExistingPerferations(ObjectInfo info){
        
    }
    
    /**
     * perferateTriangles
     *
     * Description: Given an object selection, generate curve geometry representing cut lines to perferate the part with holes.
     * @param - Scene : object containing world objects.
     * @param - double percentage of material to remove.
     */
    public void perferateTriangles(Scene scene){
        LayoutModeling layout = new LayoutModeling();
        int selection[] = scene.getSelection();
        if(selection.length > 0){
            ObjectInfo info = scene.getObject(selection[0]);
            
            System.out.println("obj " + info);
            Object co = (Object)info.getObject();
            if((co instanceof Mesh) == true){
            
                // Find object orientation
                
                BoundingBox bounds = getTranslatedBounds(info);
                System.out.println("bounds " + bounds.minx + " " + bounds.maxx + " " +
                                   bounds.miny + " " + bounds.maxy + " " +
                                   bounds.minz + " " + bounds.maxz + " " );
                
                // What if part is not aligned to primary axis???
                // todo sort out
                
                // Collect child verticies and avoid pererating near them.
                Vector childVerts = new Vector();
                ObjectInfo[] children = info.getChildren();
                for(int i = 0; i < children.length; i++){
                    ObjectInfo child = children[i];
                    Object childCo = (Object)child.getObject();
                    if((childCo instanceof Curve) == true){
                        CoordinateSystem c;
                        c = layout.getCoords(child);
                        //Vec3 objOrigin = c.getOrigin();
                        
                        //System.out.println("Curve");
                        Mesh mesh = (Mesh) child.getObject(); // Object3D
                        Vec3 [] verts = mesh.getVertexPositions();
                        for (Vec3 vert : verts){
                            
                            Mat4 mat4 = c.duplicate().fromLocal();
                            mat4.transform(vert);
                            
                            //System.out.println("    vert: " + vert.x + " " + vert.y + "  " + vert.z );
                            childVerts.addElement(vert);
                        }
                    }
                }
                
                
                // X Axis
                if( (bounds.maxx - bounds.minx) > (bounds.maxy - bounds.miny) &&
                     (bounds.maxx - bounds.minx) > (bounds.maxz - bounds.minz)   ){ // X axis
                    System.out.println(" is on x ");
                    
                    // H
                    double rotate_angle_index = 0; // 360 degrees total
                    int x_index = 0; // bounds.minx;
                    double unit_distance = (bounds.maxz - bounds.minz) / 5.5; // testing for now
                    //System.out.println("unit_distance " + unit_distance); // 0.36363636363636365
                    double partDiameter = (bounds.maxy - bounds.miny);
                    double partRadius = (partDiameter / 2);
                    double partRadiusExpand = partRadius * 1.01; // expand for visibility.
                    double yCentre = ((bounds.maxy - bounds.miny) / 2);
                    double zCentre = ((bounds.maxz - bounds.minz) / 2);
                    int p = 0;
                    
                    //for(int x_i = 0; x_i < ((bounds.maxx - bounds.minx) / unit_distance); x_i ++){ // x unit_distance
                    for(int x_i = 0; x_i < ((bounds.maxx - bounds.minx) / unit_distance); x_i++){
                        double xRow =  ( x_i * unit_distance); // (bounds.minx) +
                        int rUnits = 14; // (int)(unit_distance * 38.5); // 0.36363636363636365 = 14
                        
                        double rShift = 0; // rotate to evenly pack
                        if(x_i % 4 == 2 || x_i % 4 == 3){ //
                            rShift = Math.toRadians( (360.0 / ((double)rUnits)) / 2 );
                        }
                        double xShift = 0;  // Shift to evenly pack
                        if(x_i % 4 == 1){
                            xShift = (unit_distance / 7.0); // move right
                        } else if (x_i % 4 == 2){
                            xShift = -(unit_distance / 7.0); // move left
                        }
                        if(x_i % 4 == 1){
                            xShift += (unit_distance / 24.0);
                        }
                        if(x_i % 4 == 2){
                            xShift += ((unit_distance / 24.0) * 2);
                            xShift += ((unit_distance / 24.0) );
                        }
                        if(x_i % 4 == 3){
                            xShift += ((unit_distance / 24.0) * 3);
                            xShift += ((unit_distance / 24.0) );
                        }
                        
                        for(int r_i = 0; r_i < rUnits; r_i++){ // rotate    // unit_distance
                            double angle = (360.0 / ((double)rUnits)) * (double)r_i;
                            angle = Math.toRadians(angle);
                            angle = angle + rShift; // shift every double pair
                            
                            // 1
                            double xPos = bounds.minx + xRow + xShift;
                            double yPos = bounds.miny + (partRadiusExpand * Math.cos(angle)) + yCentre;
                            double zPos = bounds.minz + partRadiusExpand * Math.sin(angle) + zCentre;
                            Vec3 point1 = new Vec3(xPos, yPos, zPos);
                            
                            // 2
                            xPos = bounds.minx + xRow + xShift;
                            yPos = bounds.miny + (partRadiusExpand * Math.cos(angle + 0.25)) + yCentre;
                            zPos = bounds.minz + partRadiusExpand * Math.sin(angle + 0.25) + zCentre;
                            Vec3 point2 = new Vec3(xPos, yPos, zPos);
                            
                            // 3
                            xPos = bounds.minx + xRow + (unit_distance / 1.65) + xShift;
                            yPos = bounds.miny + (partRadiusExpand * Math.cos(angle + 0.125)) + yCentre;
                            zPos = bounds.minz + partRadiusExpand * Math.sin(angle + 0.125) + zCentre;
                            Vec3 point3 = new Vec3(xPos, yPos, zPos);
                            
                            if(x_i % 2 == 0){
                                xPos = bounds.minx + xRow + (unit_distance / 1.65) + xShift;
                                yPos = bounds.miny + (partRadiusExpand * Math.cos(angle)) + yCentre;
                                zPos = bounds.minz + partRadiusExpand * Math.sin(angle) + zCentre;
                                point1 = new Vec3(xPos, yPos, zPos);
                                
                                xPos = bounds.minx + xRow + (unit_distance / 1.65) + xShift;
                                yPos = bounds.miny + (partRadiusExpand * Math.cos(angle + 0.25)) + yCentre;
                                zPos = bounds.minz + partRadiusExpand * Math.sin(angle + 0.25) + zCentre;
                                point2 = new Vec3(xPos, yPos, zPos);
                                
                                xPos = bounds.minx + xRow + xShift;
                                yPos = bounds.miny + (partRadiusExpand * Math.cos(angle + 0.125)) + yCentre;
                                zPos = bounds.minz + partRadiusExpand * Math.sin(angle + 0.125) + zCentre;
                                point3 = new Vec3(xPos, yPos, zPos);
                            }
                            
                            if(isCloseToPoints(point1, childVerts, unit_distance * 2) == false){
                            
                                float[] s_ = new float[3]; s_[0] = 0; s_[1] = 0; s_[2] = 0;
                                Vec3[] vertex = new Vec3[3];
                                vertex[0] = point1;
                                vertex[1] = point2;
                                vertex[2] = point3;
                                Curve perferationCurve = new Curve(vertex, s_, 0, true); // false
                                CoordinateSystem coords = new CoordinateSystem(new Vec3(), Vec3.vz(), Vec3.vy());
                                ObjectInfo perferationInfo = new ObjectInfo(perferationCurve, coords, "Perferation " + ++p);
                                perferationInfo.setParent(info); // Add perferation object to selection.
                                info.addChild(perferationInfo, info.getChildren().length); // info.getChildren().length+1
                                window.addObject(perferationInfo, null); // Add ObjectInfo
                                //window.setSelection(window.getScene().getNumObjects()-1); // Add to selected object as child
                                //window.setUndoRecord(new UndoRecord(window, false, UndoRecord.DELETE_OBJECT, new Object [] {new Integer(window.getScene().getNumObjects()-1)}));
                            }
                        }
                        if(x_i % 6 == 0){   // Show intermittant progress
                            window.updateImage();
                        }
                    }
                    window.updateImage();
                    window.updateTree(); // Tell the tree it has changed.
                } // end of X axis
                
                
                // Y axis
                if( (bounds.maxx - bounds.minx) < (bounds.maxy - bounds.miny) &&
                     (bounds.maxy - bounds.miny) > (bounds.maxz - bounds.minz)   ){ // Y axis
                    System.out.println(" is on y ");
                    
                    // H
                    double rotate_angle_index = 0; // 360 degrees total
                    int x_index = 0; // bounds.minx;
                    double unit_distance = (bounds.maxz - bounds.minz) / 5.5; // testing for now
                    //System.out.println("unit_distance " + unit_distance); // 0.36363636363636365
                    double partDiameter = (bounds.maxx - bounds.minx);
                    double partRadius = (partDiameter / 2);
                    double partRadiusExpand = partRadius * 1.01; // expand for visibility.
                    double xCentre = ((bounds.maxx - bounds.minx) / 2);
                    double yCentre = ((bounds.maxy - bounds.miny) / 2);
                    double zCentre = ((bounds.maxz - bounds.minz) / 2);
                    int p = 0;
                    
                    for(int y_i = 0; y_i < ((bounds.maxy - bounds.miny) / unit_distance); y_i++){
                        double yRow =  (y_i * unit_distance); // (bounds.minx) +
                        int rUnits = 14; // (int)(unit_distance * 38.5); // 0.36363636363636365 = 14
                        
                        double rShift = 0; // rotate to evenly pack
                        if(y_i % 4 == 2 || y_i % 4 == 3){ //
                            rShift = Math.toRadians( (360.0 / ((double)rUnits)) / 2 );
                        }
                        double yShift = 0;  // Shift to evenly pack
                        if(y_i % 4 == 1){
                            yShift = (unit_distance / 7.0); // move right
                        } else if (y_i % 4 == 2){
                            yShift = -(unit_distance / 7.0); // move left
                        }
                        if(y_i % 4 == 1){
                            yShift += (unit_distance / 24.0);
                        }
                        if(y_i % 4 == 2){
                            yShift += ((unit_distance / 24.0) * 2);
                            yShift += ((unit_distance / 24.0) );
                        }
                        if(y_i % 4 == 3){
                            yShift += ((unit_distance / 24.0) * 3);
                            yShift += ((unit_distance / 24.0) );
                        }
                        
                        for(int r_i = 0; r_i < rUnits; r_i++){ // rotate    // unit_distance
                            double angle = (360.0 / ((double)rUnits)) * (double)r_i;
                            angle = Math.toRadians(angle);
                            angle = angle + rShift; // shift every double pair
                            
                            // 1
                            double xPos = bounds.minx + (partRadiusExpand * Math.cos(angle)) + xCentre;
                            double yPos = bounds.miny + yRow + yShift;
                            double zPos = bounds.minz + partRadiusExpand * Math.sin(angle) + zCentre;
                            Vec3 point1 = new Vec3(xPos, yPos, zPos);
                            
                            // 2
                            xPos = bounds.minx + (partRadiusExpand * Math.cos(angle + 0.25)) + xCentre;
                            yPos = bounds.miny + yRow + yShift;
                            zPos = bounds.minz + partRadiusExpand * Math.sin(angle + 0.25) + zCentre;
                            Vec3 point2 = new Vec3(xPos, yPos, zPos);
                            
                            // 3
                            xPos = bounds.minx + (partRadiusExpand * Math.cos(angle + 0.125)) + xCentre;
                            yPos = bounds.miny + yRow + (unit_distance / 1.65) + yShift;
                            zPos = bounds.minz + partRadiusExpand * Math.sin(angle + 0.125) + zCentre;
                            Vec3 point3 = new Vec3(xPos, yPos, zPos);
                            
                            if(y_i % 2 == 0){
                                xPos = bounds.minx + (partRadiusExpand * Math.cos(angle)) + xCentre;
                                yPos = bounds.miny + yRow + (unit_distance / 1.65) + yShift;
                                zPos = bounds.minz + partRadiusExpand * Math.sin(angle) + zCentre;
                                point1 = new Vec3(xPos, yPos, zPos);
                                
                                xPos = bounds.minx + (partRadiusExpand * Math.cos(angle + 0.25)) + xCentre;
                                yPos = bounds.miny + yRow + (unit_distance / 1.65) + yShift;
                                zPos = bounds.minz + partRadiusExpand * Math.sin(angle + 0.25) + zCentre;
                                point2 = new Vec3(xPos, yPos, zPos);
                                
                                xPos = bounds.minx + (partRadiusExpand * Math.cos(angle + 0.125)) + xCentre;
                                yPos = bounds.miny + yRow + yShift;
                                zPos = bounds.minz + partRadiusExpand * Math.sin(angle + 0.125) + zCentre;
                                point3 = new Vec3(xPos, yPos, zPos);
                            }
                            
                            if(isCloseToPoints(point1, childVerts, unit_distance * 2) == false){
                                float[] s_ = new float[3]; s_[0] = 0; s_[1] = 0; s_[2] = 0;
                                Vec3[] vertex = new Vec3[3];
                                vertex[0] = point1;
                                vertex[1] = point2;
                                vertex[2] = point3;
                                Curve perferationCurve = new Curve(vertex, s_, 0, true); // false
                                CoordinateSystem coords = new CoordinateSystem(new Vec3(), Vec3.vz(), Vec3.vy());
                                ObjectInfo perferationInfo = new ObjectInfo(perferationCurve, coords, "Perferation " + ++p);
                                perferationInfo.setParent(info); // Add perferation object to selection.
                                info.addChild(perferationInfo, info.getChildren().length); // info.getChildren().length+1
                                window.addObject(perferationInfo, null); // Add ObjectInfo
                                //window.setSelection(window.getScene().getNumObjects()-1); // Add to selected object as child
                                //window.setUndoRecord(new UndoRecord(window, false, UndoRecord.DELETE_OBJECT, new Object [] {new Integer(window.getScene().getNumObjects()-1)}));
                            }
                        }
                        if(y_i % 6 == 0){   // Show intermittant progress
                            window.updateImage();
                        }
                    }
                    window.updateImage();
                    window.updateTree(); // Tell the tree it has changed.
                } // end of Y axis
                
                
                // Z axis
                if( (bounds.maxz - bounds.minz) > (bounds.maxy - bounds.miny) &&
                 (bounds.maxx - bounds.minx) < (bounds.maxz - bounds.minz)   ){ // X axis
                    System.out.println(" is on z ");
                    
                    // H
                    double rotate_angle_index = 0; // 360 degrees total
                    int z_index = 0;
                    double unit_distance = (bounds.maxx - bounds.minx) / 5.5; // testing for now
                    //System.out.println("unit_distance " + unit_distance); // 0.36363636363636365
                    double partDiameter = (bounds.maxy - bounds.miny);
                    double partRadius = (partDiameter / 2);
                    double partRadiusExpand = partRadius * 1.01; // expand for visibility.
                    double xCentre = ((bounds.maxx - bounds.minx) / 2);
                    double yCentre = ((bounds.maxy - bounds.miny) / 2);
                    double zCentre = ((bounds.maxz - bounds.minz) / 2);
                    int p = 0;
                    for(int z_i = 0; z_i < ((bounds.maxz - bounds.minz) / unit_distance); z_i++){
                        double zRow =  ( z_i * unit_distance); // (bounds.minx) +
                        int rUnits = 14; // (int)(unit_distance * 38.5); // 0.36363636363636365 = 14
                        
                        double rShift = 0; // rotate to evenly pack
                        if(z_i % 4 == 2 || z_i % 4 == 3){ //
                            rShift = Math.toRadians( (360.0 / ((double)rUnits)) / 2 );
                        }
                        double zShift = 0;  // Shift to evenly pack
                        if(z_i % 4 == 1){
                            zShift = (unit_distance / 7.0); // move right
                        } else if (z_i % 4 == 2){
                            zShift = -(unit_distance / 7.0); // move left
                        }
                        if(z_i % 4 == 1){
                            zShift += (unit_distance / 24.0);
                        }
                        if(z_i % 4 == 2){
                            zShift += ((unit_distance / 24.0) * 2);
                            zShift += ((unit_distance / 24.0) );
                        }
                        if(z_i % 4 == 3){
                            zShift += ((unit_distance / 24.0) * 3);
                            zShift += ((unit_distance / 24.0) );
                        }
                        
                        for(int r_i = 0; r_i < rUnits; r_i++){ // rotate    // unit_distance
                            double angle = (360.0 / ((double)rUnits)) * (double)r_i;
                            angle = Math.toRadians(angle);
                            angle = angle + rShift; // shift every double pair
                            
                            // 1
                            double xPos = bounds.minx + partRadiusExpand * Math.sin(angle) + xCentre;
                            double yPos = bounds.miny + (partRadiusExpand * Math.cos(angle)) + yCentre;
                            double zPos = bounds.minz + zRow + zShift;
                            Vec3 point1 = new Vec3(xPos, yPos, zPos);
                            
                            // 2
                            xPos = bounds.minx + partRadiusExpand * Math.sin(angle + 0.25) + xCentre;
                            yPos = bounds.miny + (partRadiusExpand * Math.cos(angle + 0.25)) + yCentre;
                            zPos = bounds.minz + zRow + zShift;
                            Vec3 point2 = new Vec3(xPos, yPos, zPos);
                            
                            // 3
                            xPos = bounds.minx + partRadius * Math.sin(angle + 0.125) + xCentre;
                            yPos = bounds.miny + (partRadiusExpand * Math.cos(angle + 0.125)) + yCentre;
                            zPos = bounds.minz + zRow + (unit_distance / 1.65) + zShift;
                            Vec3 point3 = new Vec3(xPos, yPos, zPos);
                            
                            if(z_i % 2 == 0){
                                xPos = bounds.minx + partRadiusExpand * Math.sin(angle) + xCentre;
                                yPos = bounds.miny + (partRadiusExpand * Math.cos(angle)) + yCentre;
                                zPos = bounds.minz + zRow + (unit_distance / 1.65) + zShift;
                                point1 = new Vec3(xPos, yPos, zPos);
                                
                                xPos = bounds.minx + partRadiusExpand * Math.sin(angle + 0.25) + xCentre;
                                yPos = bounds.miny + (partRadiusExpand * Math.cos(angle + 0.25)) + yCentre;
                                zPos = bounds.minz + zRow + (unit_distance / 1.65) + zShift;
                                point2 = new Vec3(xPos, yPos, zPos);
                                
                                xPos = bounds.minx + partRadiusExpand * Math.sin(angle + 0.125) + xCentre;
                                yPos = bounds.miny + (partRadiusExpand * Math.cos(angle + 0.125)) + yCentre;
                                zPos = bounds.minz + zRow + zShift;
                                point3 = new Vec3(xPos, yPos, zPos);
                            }
                            
                            if(isCloseToPoints(point1, childVerts, unit_distance * 2) == false){
                                float[] s_ = new float[3]; s_[0] = 0; s_[1] = 0; s_[2] = 0;
                                Vec3[] vertex = new Vec3[3];
                                vertex[0] = point1;
                                vertex[1] = point2;
                                vertex[2] = point3;
                                Curve perferationCurve = new Curve(vertex, s_, 0, true); // false
                                CoordinateSystem coords = new CoordinateSystem(new Vec3(), Vec3.vz(), Vec3.vy());
                                ObjectInfo perferationInfo = new ObjectInfo(perferationCurve, coords, "Perferation " + ++p);
                                perferationInfo.setParent(info); // Add perferation object to selection.
                                info.addChild(perferationInfo, info.getChildren().length); // info.getChildren().length+1
                                window.addObject(perferationInfo, null); // Add ObjectInfo
                                //window.setSelection(window.getScene().getNumObjects()-1); // Add to selected object as child
                                //window.setUndoRecord(new UndoRecord(window, false, UndoRecord.DELETE_OBJECT, new Object [] {new Integer(window.getScene().getNumObjects()-1)}));
                            }
                        }
                        if(z_i % 6 == 0){   // Show intermittant progress
                            window.updateImage();
                        }
                    }
                    window.updateImage();
                    window.updateTree(); // Tell the tree it has changed.
                } // end Z axis
                
            } else {
                System.out.println("Error: Object is not a mesh. ");
            }
        }
    }
    
    
    /**
     * perferateSquares
     *
     * Description:
     *
     */
    public void perferateSquares(Scene scene){
        LayoutModeling layout = new LayoutModeling();
        int selection[] = scene.getSelection();
        if(selection.length > 0){
            ObjectInfo info = scene.getObject(selection[0]);
            //System.out.println("obj " + info);
            Object co = (Object)info.getObject();
            if((co instanceof Mesh) == true){
        
                // Find object orientation
                
                BoundingBox bounds = getTranslatedBounds(info);
                System.out.println("bounds " + bounds.minx + " " + bounds.maxx + " " +
                                   bounds.miny + " " + bounds.maxy + " " +
                                   bounds.minz + " " + bounds.maxz + " " );
                
                // What if part is not aligned to primary axis???
                // todo sort out
                
                // Collect child verticies and avoid pererating near them.
                Vector childVerts = new Vector();
                ObjectInfo[] children = info.getChildren();
                for(int i = 0; i < children.length; i++){
                    ObjectInfo child = children[i];
                    Object childCo = (Object)child.getObject();
                    if((childCo instanceof Curve) == true){
                        CoordinateSystem c;
                        c = layout.getCoords(child);
                        //Vec3 objOrigin = c.getOrigin();
                        
                        //System.out.println("Curve");
                        Mesh mesh = (Mesh) child.getObject(); // Object3D
                        Vec3 [] verts = mesh.getVertexPositions();
                        for (Vec3 vert : verts){
                            
                            Mat4 mat4 = c.duplicate().fromLocal();
                            mat4.transform(vert);
                            
                            //System.out.println("    vert: " + vert.x + " " + vert.y + "  " + vert.z );
                            childVerts.addElement(vert);
                        }
                    }
                }
                
                if( (bounds.maxx - bounds.minx) > (bounds.maxy - bounds.miny) &&
                     (bounds.maxx - bounds.minx) > (bounds.maxz - bounds.minz)   ){ // X axis
                    System.out.println(" is on x ");
                    
                    // H
                    double rotate_angle_index = 0; // 360 degrees total
                    int x_index = 0; // bounds.minx;
                    double unit_distance = (bounds.maxz - bounds.minz) / (5.5 - 1); // testing for now
                    //System.out.println("unit_distance " + unit_distance); // 0.36363636363636365
                    double partDiameter = (bounds.maxy - bounds.miny);
                    double partRadius = (partDiameter / 2);
                    partRadius = partRadius * 1.02; // expand for visibility.
                    //System.out.println(" xi : " + ((bounds.maxx - bounds.minx) / (unit_distance)));
                    //System.out.println(" r_i : " + (360 / (unit_distance * 4 )));
                    double xCentre = ((bounds.maxx - bounds.minx) / 2);
                    double yCentre = ((bounds.maxy - bounds.miny) / 2);
                    double zCentre = ((bounds.maxz - bounds.minz) / 2);
                    int p = 0;
                    
                    //for(int x_i = 0; x_i < ((bounds.maxx - bounds.minx) / unit_distance); x_i ++){ // x unit_distance
                    for(int x_i = 0; x_i < ((bounds.maxx - bounds.minx) / unit_distance); x_i++){
                        double xRow =  ( x_i * unit_distance); // (bounds.minx) +
                        int rUnits = 14; // (int)(unit_distance * 38.5); // 0.36363636363636365 = 14
                        
                        double rShift = 0; // rotate to evenly pack
                        //if(x_i % 2 == 1){ //
                        //    rShift = Math.toRadians( (360.0 / ((double)rUnits)) / 2 );
                        //}
                        double xShift = 0;  // Shift to evenly pack
                        //if(x_i % 4 == 1){
                            //xShift = (unit_distance / 7.0); // move right
                        //} else if (x_i % 4 == 2){
                            //xShift = -(unit_distance / 7.0); // move left
                        //}
                        
                        for(int r_i = 0; r_i < rUnits; r_i++){ // rotate    // unit_distance
                            double angle = (360.0 / ((double)rUnits)) * (double)r_i;
                            angle = Math.toRadians(angle);
                            angle = angle + rShift; // shift every double pair
                            //System.out.println("  angle " + angle);
                            
                            // 1
                            double xPos = bounds.minx + xRow + xShift;
                            double yPos = bounds.miny + (partRadius * Math.cos(angle)) + yCentre;
                            double zPos = bounds.minz + partRadius * Math.sin(angle) + zCentre;
                            Vec3 point1 = new Vec3(xPos, yPos, zPos);
                            
                            // 2
                            xPos = bounds.minx + xRow + xShift;
                            yPos = bounds.maxy + (partRadius * Math.cos(angle + 0.22)) - yCentre;
                            zPos = bounds.minz + partRadius * Math.sin(angle + 0.22) + zCentre;
                            Vec3 point2 = new Vec3(xPos, yPos, zPos);
                            
                            // 3
                            xPos = bounds.minx + xRow + (unit_distance / 2) + xShift;
                            yPos = bounds.maxy + (partRadius * Math.cos(angle + 0.22)) - yCentre;
                            zPos = bounds.minz + partRadius * Math.sin(angle+ 0.22) + zCentre;
                            Vec3 point3 = new Vec3(xPos, yPos, zPos);
                            
                            // 4
                            xPos = bounds.minx + xRow + (unit_distance / 2) + xShift;
                            yPos = bounds.maxy + (partRadius * Math.cos(angle)) - yCentre;
                            zPos = bounds.minz + partRadius * Math.sin(angle) + zCentre;
                            Vec3 point4 = new Vec3(xPos, yPos, zPos);
                            
                            if(isCloseToPoints(point1, childVerts, unit_distance * 2) == false){
                                float[] s_ = new float[4]; s_[0] = 0; s_[1] = 0; s_[2] = 0; s_[3] = 0;
                                Vec3[] vertex = new Vec3[4];
                                vertex[0] = point1;
                                vertex[1] = point2;
                                vertex[2] = point3;
                                vertex[3] = point4;
                                Curve perferationCurve = new Curve(vertex, s_, 0, true); // false
                                
                                CoordinateSystem coords = new CoordinateSystem(new Vec3(), Vec3.vz(), Vec3.vy());
                                //window.addObject(perferationCurve, coords, "Perferation " + ++p, null);
                                ObjectInfo perferationInfo = new ObjectInfo(perferationCurve, coords, "Perferation " + ++p);
                                perferationInfo.setParent(info); // Add perferation object to selection.
                                info.addChild(perferationInfo, info.getChildren().length); // info.getChildren().length+1
                                window.addObject(perferationInfo, null); // Add ObjectInfo
                            }
                            //window.setSelection(window.getScene().getNumObjects()-1); // Add to selected object as child
                            //window.setUndoRecord(new UndoRecord(window, false, UndoRecord.DELETE_OBJECT, new Object [] {new Integer(window.getScene().getNumObjects()-1)}));
                        }
                    }
                    
                    // end of X axis
                    window.updateImage();
                    window.updateTree(); // Tell the tree it has changed.
                }
                
                
                // Y axis
                if( (bounds.maxx - bounds.minx) < (bounds.maxy - bounds.miny) &&
                     (bounds.maxy - bounds.miny) > (bounds.maxz - bounds.minz)   ){ // Y axis
                    System.out.println(" is on y ");
                    
                    // H
                    double rotate_angle_index = 0; // 360 degrees total
                    int x_index = 0; // bounds.minx;
                    double unit_distance = (bounds.maxz - bounds.minz) / 5.5; // testing for now
                    //System.out.println("unit_distance " + unit_distance); // 0.36363636363636365
                    double partDiameter = (bounds.maxx - bounds.minx);
                    double partRadius = (partDiameter / 2);
                    double partRadiusExpand = partRadius * 1.01; // expand for visibility.
                    double xCentre = ((bounds.maxx - bounds.minx) / 2);
                    double yCentre = ((bounds.maxy - bounds.miny) / 2);
                    double zCentre = ((bounds.maxz - bounds.minz) / 2);
                    int p = 0;
                    
                    for(int y_i = 0; y_i < ((bounds.maxy - bounds.miny) / unit_distance); y_i++){
                        double yRow =  (y_i * unit_distance); // (bounds.minx) +
                        int rUnits = 14; // (int)(unit_distance * 38.5); // 0.36363636363636365 = 14
                        double rShift = 0; // rotate to evenly pack
                        double yShift = 0;  // Shift to evenly pack
                       
                        for(int r_i = 0; r_i < rUnits; r_i++){ // rotate    // unit_distance
                            double angle = (360.0 / ((double)rUnits)) * (double)r_i;
                            angle = Math.toRadians(angle);
                            angle = angle + rShift; // shift every double pair
                            
                            // 1
                            double xPos = bounds.minx + (partRadiusExpand * Math.cos(angle)) + xCentre;
                            double yPos = bounds.miny + yRow + yShift;
                            double zPos = bounds.minz + partRadiusExpand * Math.sin(angle) + zCentre;
                            Vec3 point1 = new Vec3(xPos, yPos, zPos);
                            
                            // 2
                            xPos = bounds.maxx + (partRadiusExpand * Math.cos(angle + 0.22)) - xCentre;
                            yPos = bounds.miny + yRow + yShift;
                            zPos = bounds.minz + partRadiusExpand * Math.sin(angle + 0.22) + zCentre;
                            Vec3 point2 = new Vec3(xPos, yPos, zPos);
                            
                            // 3
                            xPos = bounds.maxx + (partRadiusExpand * Math.cos(angle + 0.22)) - xCentre;
                            yPos = bounds.miny + yRow + (unit_distance / 2) + yShift;
                            zPos = bounds.minz + partRadiusExpand * Math.sin(angle+ 0.22) + zCentre;
                            Vec3 point3 = new Vec3(xPos, yPos, zPos);
                            
                            // 4
                            xPos = bounds.maxx + (partRadiusExpand * Math.cos(angle)) - xCentre;
                            yPos = bounds.miny + yRow + (unit_distance / 2) + yShift;
                            zPos = bounds.minz + partRadiusExpand * Math.sin(angle) + zCentre;
                            Vec3 point4 = new Vec3(xPos, yPos, zPos);
                            
                            if(isCloseToPoints(point1, childVerts, unit_distance * 2) == false){
                                float[] s_ = new float[4]; s_[0] = 0; s_[1] = 0; s_[2] = 0;  s_[3] = 0;
                                Vec3[] vertex = new Vec3[4];
                                vertex[0] = point1;
                                vertex[1] = point2;
                                vertex[2] = point3;
                                vertex[3] = point4;
                                Curve perferationCurve = new Curve(vertex, s_, 0, true); // false
                                CoordinateSystem coords = new CoordinateSystem(new Vec3(), Vec3.vz(), Vec3.vy());
                                ObjectInfo perferationInfo = new ObjectInfo(perferationCurve, coords, "Perferation " + ++p);
                                perferationInfo.setParent(info); // Add perferation object to selection.
                                info.addChild(perferationInfo, info.getChildren().length); // info.getChildren().length+1
                                window.addObject(perferationInfo, null); // Add ObjectInfo
                                //window.setSelection(window.getScene().getNumObjects()-1); // Add to selected object as child
                                //window.setUndoRecord(new UndoRecord(window, false, UndoRecord.DELETE_OBJECT, new Object [] {new Integer(window.getScene().getNumObjects()-1)}));
                            }
                        }
                    }
                    window.updateImage();
                    window.updateTree(); // Tell the tree it has changed.
                } // end of Y axis
                
                
                // Z
                if( (bounds.maxx - bounds.minx) < (bounds.maxz - bounds.minz) &&
                     (bounds.maxy - bounds.miny) < (bounds.maxz - bounds.minz)   ){ // Y axis
                    System.out.println(" is on z ");
                    
                    // H
                    double rotate_angle_index = 0; // 360 degrees total
                    int x_index = 0; // bounds.minx;
                    double unit_distance = (bounds.maxx - bounds.minx) / 5.5; // testing for now
                    //System.out.println("unit_distance " + unit_distance); // 0.36363636363636365
                    double partDiameter = (bounds.maxx - bounds.minx);
                    double partRadius = (partDiameter / 2);
                    double partRadiusExpand = partRadius * 1.01; // expand for visibility.
                    double xCentre = ((bounds.maxx - bounds.minx) / 2);
                    double yCentre = ((bounds.maxy - bounds.miny) / 2);
                    double zCentre = ((bounds.maxz - bounds.minz) / 2);
                    int p = 0;
                    
                    for(int z_i = 0; z_i < ((bounds.maxz - bounds.minz) / unit_distance); z_i++){
                        double zRow =  (z_i * unit_distance); // (bounds.minx) +
                        int rUnits = 14; // (int)(unit_distance * 38.5); // 0.36363636363636365 = 14
                        double rShift = 0; // rotate to evenly pack
                        double zShift = 0;  // Shift to evenly pack
                       
                        for(int r_i = 0; r_i < rUnits; r_i++){ // rotate    // unit_distance
                            double angle = (360.0 / ((double)rUnits)) * (double)r_i;
                            angle = Math.toRadians(angle);
                            angle = angle + rShift; // shift every double pair
                            
                            // 1
                            double xPos = bounds.minx + (partRadiusExpand * Math.cos(angle)) + xCentre;
                            double yPos = bounds.miny + partRadiusExpand * Math.sin(angle) + yCentre;
                            double zPos = bounds.minz + zRow + zShift;
                            Vec3 point1 = new Vec3(xPos, yPos, zPos);
                            
                            // 2
                            xPos = bounds.maxx + (partRadiusExpand * Math.cos(angle + 0.22)) - xCentre;
                            yPos = bounds.miny + partRadiusExpand * Math.sin(angle + 0.22) + yCentre;
                            zPos = bounds.minz + zRow + zShift;
                            Vec3 point2 = new Vec3(xPos, yPos, zPos);
                            
                            // 3
                            xPos = bounds.maxx + (partRadiusExpand * Math.cos(angle + 0.22)) - xCentre;
                            yPos = bounds.miny + partRadiusExpand * Math.sin(angle+ 0.22) + yCentre;
                            zPos = bounds.minz + zRow + (unit_distance / 2) + zShift;
                            Vec3 point3 = new Vec3(xPos, yPos, zPos);
                            
                            // 4
                            xPos = bounds.maxx + (partRadiusExpand * Math.cos(angle)) - xCentre;
                            yPos = bounds.miny + partRadiusExpand * Math.sin(angle) + yCentre;
                            zPos = bounds.minz + zRow + (unit_distance / 2) + zShift;
                            Vec3 point4 = new Vec3(xPos, yPos, zPos);
                            
                            if(isCloseToPoints(point1, childVerts, unit_distance * 2) == false){
                                float[] s_ = new float[4]; s_[0] = 0; s_[1] = 0; s_[2] = 0;  s_[3] = 0;
                                Vec3[] vertex = new Vec3[4];
                                vertex[0] = point1;
                                vertex[1] = point2;
                                vertex[2] = point3;
                                vertex[3] = point4;
                                Curve perferationCurve = new Curve(vertex, s_, 0, true); // false
                                CoordinateSystem coords = new CoordinateSystem(new Vec3(), Vec3.vz(), Vec3.vy());
                                ObjectInfo perferationInfo = new ObjectInfo(perferationCurve, coords, "Perferation " + ++p);
                                perferationInfo.setParent(info); // Add perferation object to selection.
                                info.addChild(perferationInfo, info.getChildren().length); // info.getChildren().length+1
                                window.addObject(perferationInfo, null); // Add ObjectInfo
                                //window.setSelection(window.getScene().getNumObjects()-1); // Add to selected object as child
                                //window.setUndoRecord(new UndoRecord(window, false, UndoRecord.DELETE_OBJECT, new Object [] {new Integer(window.getScene().getNumObjects()-1)}));
                            }
                        }
                    }
                    window.updateImage();
                    window.updateTree(); // Tell the tree it has changed.
                } // end of Y axis
                
            } else {
                System.out.println("Error: Object is not a mesh. ");
            }
        }
    }
    
    /**
     * perferateDiamonds
     *
     * Description:
     */
    public void perferateDiamonds(Scene scene){
        LayoutModeling layout = new LayoutModeling();
        int selection[] = scene.getSelection();
        if(selection.length > 0){
            ObjectInfo info = scene.getObject(selection[0]);
            //System.out.println("obj " + info);
            Object co = (Object)info.getObject();
            if((co instanceof Mesh) == true){
        
                // Find object orientation
                
                BoundingBox bounds = getTranslatedBounds(info);
                System.out.println("bounds " + bounds.minx + " " + bounds.maxx + " " +
                                   bounds.miny + " " + bounds.maxy + " " +
                                   bounds.minz + " " + bounds.maxz + " " );
                
                // What if part is not aligned to primary axis???
                // todo sort out
                
                // Collect child verticies and avoid pererating near them.
                Vector childVerts = new Vector();
                ObjectInfo[] children = info.getChildren();
                for(int i = 0; i < children.length; i++){
                    ObjectInfo child = children[i];
                    Object childCo = (Object)child.getObject();
                    if((childCo instanceof Curve) == true){
                        CoordinateSystem c;
                        c = layout.getCoords(child);
                        //Vec3 objOrigin = c.getOrigin();
                        
                        //System.out.println("Curve");
                        Mesh mesh = (Mesh) child.getObject(); // Object3D
                        Vec3 [] verts = mesh.getVertexPositions();
                        for (Vec3 vert : verts){
                            
                            Mat4 mat4 = c.duplicate().fromLocal();
                            mat4.transform(vert);
                            
                            //System.out.println("    vert: " + vert.x + " " + vert.y + "  " + vert.z );
                            childVerts.addElement(vert);
                        }
                    }
                }
                
                if( (bounds.maxx - bounds.minx) > (bounds.maxy - bounds.miny) &&
                     (bounds.maxx - bounds.minx) > (bounds.maxz - bounds.minz)   ){ // X axis
                    System.out.println(" is on x ");
                    
                    // H
                    double rotate_angle_index = 0; // 360 degrees total
                    int x_index = 0; // bounds.minx;
                    double unit_distance = (bounds.maxz - bounds.minz) / 5.5; // testing for now
                    double partDiameter = (bounds.maxy - bounds.miny);
                    double partRadius = (partDiameter / 2);
                    partRadius = partRadius * 1.02; // expand for visibility.
                    double yCentre = ((bounds.maxy - bounds.miny) / 2);
                    double zCentre = ((bounds.maxz - bounds.minz) / 2);
                    int p = 0;
                    
                    for(int x_i = 0; x_i < ((bounds.maxx - bounds.minx) / unit_distance); x_i++){
                        double xRow =  ( x_i * unit_distance); // (bounds.minx) +
                        int rUnits = 14; // (int)(unit_distance * 38.5); // 0.36363636363636365 = 14
                        
                        double rShift = 0; // rotate to evenly pack
                        if(x_i % 2 == 1){ //
                            rShift = Math.toRadians( (360.0 / ((double)rUnits)) / 2 );
                        }
                        double xShift = 0;  // Shift to evenly pack
                        
                        for(int r_i = 0; r_i < rUnits; r_i++){ // rotate    // unit_distance
                            double angle = (360.0 / ((double)rUnits)) * (double)r_i;
                            angle = Math.toRadians(angle);
                            angle = angle + rShift; // shift every double pair
                            //System.out.println("  angle " + angle);
                            
                            // 1
                            double xPos = bounds.minx + xRow + xShift;
                            double yPos = bounds.miny + (partRadius * Math.cos(angle)) + yCentre;
                            double zPos = bounds.minz + partRadius * Math.sin(angle) + zCentre;
                            Vec3 point1 = new Vec3(xPos, yPos, zPos);
                            
                            // 2
                            xPos = bounds.minx + xRow + ((unit_distance / 1.68)/1.1) + xShift;
                            yPos = bounds.miny + (partRadius * Math.cos(angle + 0.125)) + yCentre;
                            zPos = bounds.minz + partRadius * Math.sin(angle + 0.125) + zCentre;
                            Vec3 point2 = new Vec3(xPos, yPos, zPos);
                            
                            // 3
                            xPos = bounds.minx + xRow + ((unit_distance / 1.68) * 1.8 ) + xShift;
                            yPos = bounds.miny + (partRadius * Math.cos(angle )) + yCentre;
                            zPos = bounds.minz + partRadius * Math.sin(angle) + zCentre;
                            Vec3 point3 = new Vec3(xPos, yPos, zPos);
                            
                            // 4
                            xPos = bounds.minx + xRow + ((unit_distance / 1.68)/1.1) + xShift;
                            yPos = bounds.miny + (partRadius * Math.cos(angle - 0.125)) + yCentre;
                            zPos = bounds.minz + partRadius * Math.sin(angle - 0.125) + zCentre;
                            Vec3 point4 = new Vec3(xPos, yPos, zPos);
                            
                            if(isCloseToPoints(point1, childVerts, unit_distance * 2) == false){
                                float[] s_ = new float[4]; s_[0] = 0; s_[1] = 0; s_[2] = 0; s_[3] = 0;
                                Vec3[] vertex = new Vec3[4];
                                vertex[0] = point1;
                                vertex[1] = point2;
                                vertex[2] = point3;
                                vertex[3] = point4;
                                Curve perferationCurve = new Curve(vertex, s_, 0, true); // false
                                CoordinateSystem coords = new CoordinateSystem(new Vec3(), Vec3.vz(), Vec3.vy());
                                ObjectInfo perferationInfo = new ObjectInfo(perferationCurve, coords, "Perferation " + ++p);
                                perferationInfo.setParent(info); // Add perferation object to selection.
                                info.addChild(perferationInfo, info.getChildren().length); // info.getChildren().length+1
                                window.addObject(perferationInfo, null); // Add ObjectInfo
                                //window.setSelection(window.getScene().getNumObjects()-1); // Add to selected object as child
                                //window.setUndoRecord(new UndoRecord(window, false, UndoRecord.DELETE_OBJECT, new Object [] {new Integer(window.getScene().getNumObjects()-1)}));
                            }
                        }
                    }
                    window.updateImage();
                    window.updateTree(); // Tell the tree it has changed.
                } // end of X axis
                
                
                // Y
                if( (bounds.maxy - bounds.miny) > (bounds.maxx - bounds.minx) &&
                 (bounds.maxy - bounds.miny) > (bounds.maxz - bounds.minz)   ){ // X axis
                    System.out.println(" is on y ");
                    
                    // H
                    double rotate_angle_index = 0; // 360 degrees total
                    int x_index = 0; // bounds.minx;
                    double unit_distance = (bounds.maxz - bounds.minz) / 5.5; // testing for now
                    //System.out.println("unit_distance " + unit_distance); // 0.36363636363636365
                    double partDiameter = (bounds.maxx - bounds.minx);
                    double partRadius = (partDiameter / 2);
                    partRadius = partRadius * 1.02; // expand for visibility.
                    //System.out.println(" xi : " + ((bounds.maxx - bounds.minx) / (unit_distance)));
                    //System.out.println(" r_i : " + (360 / (unit_distance * 4 )));
                    double xCentre = ((bounds.maxx - bounds.minx) / 2);
                    double yCentre = ((bounds.maxy - bounds.miny) / 2);
                    double zCentre = ((bounds.maxz - bounds.minz) / 2);
                    int p = 0;
                
                    for(int y_i = 0; y_i < ((bounds.maxy - bounds.miny) / unit_distance); y_i++){
                        double yRow =  ( y_i * unit_distance); // (bounds.minx) +
                        int rUnits = 14; // (int)(unit_distance * 38.5); // 0.36363636363636365 = 14
                        
                        double rShift = 0; // rotate to evenly pack
                        if(y_i % 2 == 1){ //
                            rShift = Math.toRadians( (360.0 / ((double)rUnits)) / 2 );
                        }
                        double yShift = 0;  // Shift to evenly pack
                        
                        for(int r_i = 0; r_i < rUnits; r_i++){ // rotate    // unit_distance
                            double angle = (360.0 / ((double)rUnits)) * (double)r_i;
                            angle = Math.toRadians(angle);
                            angle = angle + rShift; // shift every double pair
                            //System.out.println("  angle " + angle);
                            
                            // 1
                            double xPos = bounds.minx + (partRadius * Math.cos(angle)) + xCentre;
                            double yPos = bounds.miny + yRow + yShift;
                            double zPos = bounds.minz + partRadius * Math.sin(angle) + zCentre;
                            Vec3 point1 = new Vec3(xPos, yPos, zPos);
                            
                            // 2
                            xPos = bounds.minx + (partRadius * Math.cos(angle + 0.125)) + xCentre;
                            yPos = bounds.miny + yRow + ((unit_distance / 1.68)/1.1) + yShift;
                            zPos = bounds.minz + partRadius * Math.sin(angle + 0.125) + zCentre;
                            Vec3 point2 = new Vec3(xPos, yPos, zPos);
                            
                            // 3
                            xPos = bounds.minx + (partRadius * Math.cos(angle )) + xCentre;
                            yPos = bounds.miny + yRow + ((unit_distance / 1.68) * 1.8 ) + yShift;
                            zPos = bounds.minz + partRadius * Math.sin(angle) + zCentre;
                            Vec3 point3 = new Vec3(xPos, yPos, zPos);
                            
                            // 4
                            xPos = bounds.minx + (partRadius * Math.cos(angle - 0.125)) + xCentre;
                            yPos = bounds.miny + yRow + ((unit_distance / 1.68)/1.1) + yShift;
                            zPos = bounds.minz + partRadius * Math.sin(angle - 0.125) + zCentre;
                            Vec3 point4 = new Vec3(xPos, yPos, zPos);
                            
                            if(isCloseToPoints(point1, childVerts, unit_distance * 2) == false){
                                float[] s_ = new float[4]; s_[0] = 0; s_[1] = 0; s_[2] = 0; s_[3] = 0;
                                Vec3[] vertex = new Vec3[4];
                                vertex[0] = point1;
                                vertex[1] = point2;
                                vertex[2] = point3;
                                vertex[3] = point4;
                                Curve perferationCurve = new Curve(vertex, s_, 0, true); // false
                                CoordinateSystem coords = new CoordinateSystem(new Vec3(), Vec3.vz(), Vec3.vy());
                                ObjectInfo perferationInfo = new ObjectInfo(perferationCurve, coords, "Perferation " + ++p);
                                perferationInfo.setParent(info); // Add perferation object to selection.
                                info.addChild(perferationInfo, info.getChildren().length); // info.getChildren().length+1
                                window.addObject(perferationInfo, null); // Add ObjectInfo
                                //window.setSelection(window.getScene().getNumObjects()-1); // Add to selected object as child
                                //window.setUndoRecord(new UndoRecord(window, false, UndoRecord.DELETE_OBJECT, new Object [] {new Integer(window.getScene().getNumObjects()-1)}));
                            }
                        }
                    }
                
                    window.updateImage();
                    window.updateTree(); // Tell the tree it has changed.
                } // end of Y axis
                
                
                // Z
                if( (bounds.maxz - bounds.minz) > (bounds.maxx - bounds.minx) &&
                 (bounds.maxz - bounds.minz) > (bounds.maxy - bounds.miny)   ){ // X axis
                    System.out.println(" is on z ");
                    
                    // H
                    double rotate_angle_index = 0; // 360 degrees total
                    int x_index = 0; // bounds.minx;
                    double unit_distance = (bounds.maxx - bounds.minx) / 5.5; // testing for now
                    //System.out.println("unit_distance " + unit_distance); // 0.36363636363636365
                    double partDiameter = (bounds.maxx - bounds.minx);
                    double partRadius = (partDiameter / 2);
                    partRadius = partRadius * 1.02; // expand for visibility.
                    //System.out.println(" xi : " + ((bounds.maxx - bounds.minx) / (unit_distance)));
                    //System.out.println(" r_i : " + (360 / (unit_distance * 4 )));
                    double xCentre = ((bounds.maxx - bounds.minx) / 2);
                    double yCentre = ((bounds.maxy - bounds.miny) / 2);
                    double zCentre = ((bounds.maxz - bounds.minz) / 2);
                    int p = 0;
                
                    for(int z_i = 0; z_i < ((bounds.maxz - bounds.minz) / unit_distance); z_i++){
                        double zRow =  ( z_i * unit_distance); // (bounds.minx) +
                        int rUnits = 14; // (int)(unit_distance * 38.5); // 0.36363636363636365 = 14
                        
                        double rShift = 0; // rotate to evenly pack
                        if(z_i % 2 == 1){ //
                            rShift = Math.toRadians( (360.0 / ((double)rUnits)) / 2 );
                        }
                        double zShift = 0;  // Shift to evenly pack
                        
                        for(int r_i = 0; r_i < rUnits; r_i++){ // rotate    // unit_distance
                            double angle = (360.0 / ((double)rUnits)) * (double)r_i;
                            angle = Math.toRadians(angle);
                            angle = angle + rShift; // shift every double pair
                            //System.out.println("  angle " + angle);
                            
                            // 1
                            double xPos = bounds.minx + (partRadius * Math.cos(angle)) + xCentre;
                            double yPos = bounds.miny + partRadius * Math.sin(angle) + yCentre;
                            double zPos = bounds.minz + zRow + zShift;
                            Vec3 point1 = new Vec3(xPos, yPos, zPos);
                            
                            // 2
                            xPos = bounds.minx + (partRadius * Math.cos(angle + 0.125)) + xCentre;
                            yPos = bounds.miny + partRadius * Math.sin(angle + 0.125) + yCentre;
                            zPos = bounds.minz + zRow + ((unit_distance / 1.68)/1.1) + zShift;
                            Vec3 point2 = new Vec3(xPos, yPos, zPos);
                            
                            // 3
                            xPos = bounds.minx + (partRadius * Math.cos(angle )) + xCentre;
                            yPos = bounds.miny + partRadius * Math.sin(angle) + yCentre;
                            zPos = bounds.minz + zRow + ((unit_distance / 1.68) * 1.8 ) + zShift;
                            Vec3 point3 = new Vec3(xPos, yPos, zPos);
                            
                            // 4
                            xPos = bounds.minx + (partRadius * Math.cos(angle - 0.125)) + xCentre;
                            yPos = bounds.miny + partRadius * Math.sin(angle - 0.125) + yCentre;
                            zPos = bounds.minz + zRow + ((unit_distance / 1.68)/1.1) + zShift;
                            Vec3 point4 = new Vec3(xPos, yPos, zPos);
                            
                            if(isCloseToPoints(point1, childVerts, unit_distance * 2) == false){
                                float[] s_ = new float[4]; s_[0] = 0; s_[1] = 0; s_[2] = 0; s_[3] = 0;
                                Vec3[] vertex = new Vec3[4];
                                vertex[0] = point1;
                                vertex[1] = point2;
                                vertex[2] = point3;
                                vertex[3] = point4;
                                Curve perferationCurve = new Curve(vertex, s_, 0, true); // false
                                CoordinateSystem coords = new CoordinateSystem(new Vec3(), Vec3.vz(), Vec3.vy());
                                ObjectInfo perferationInfo = new ObjectInfo(perferationCurve, coords, "Perferation " + ++p);
                                perferationInfo.setParent(info); // Add perferation object to selection.
                                info.addChild(perferationInfo, info.getChildren().length); // info.getChildren().length+1
                                window.addObject(perferationInfo, null); // Add ObjectInfo
                                //window.setSelection(window.getScene().getNumObjects()-1); // Add to selected object as child
                                //window.setUndoRecord(new UndoRecord(window, false, UndoRecord.DELETE_OBJECT, new Object [] {new Integer(window.getScene().getNumObjects()-1)}));
                            }
                        }
                    }
                
                    window.updateImage();
                    window.updateTree(); // Tell the tree it has changed.
                } // end of Z axis
                    
                
            } else {
                System.out.println("Error: Object is not a mesh. ");
            }
        }
    }
    
    public BoundingBox getTranslatedBounds(ObjectInfo object){
        BoundingBox bounds = null;
        
        LayoutModeling layout = new LayoutModeling();
        Object3D o3d = object.getObject().duplicate();
        bounds = o3d.getBounds();           // THIS DOES NOT WORK
        
        bounds.minx = 999; bounds.maxx = -999;
        bounds.miny = 999; bounds.maxy = -999;
        bounds.minz = 999; bounds.maxz = -999;
        
        CoordinateSystem c;
        c = layout.getCoords(object);
        Vec3 objOrigin = c.getOrigin();
        
        //System.out.println("getTranslatedBounds: " + object.getName());
        
        Mesh mesh = (Mesh) object.getObject(); // Object3D
        Vec3 [] verts = mesh.getVertexPositions();
        
        //if(object.getObject().canConvertToTriangleMesh() != Object3D.CANT_CONVERT){
            //TriangleMesh triangleMesh = null;
            //triangleMesh = object.getObject().convertToTriangleMesh(0.05);
            
            //MeshVertex[] points = triangleMesh.getVertices();
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
        //}
        //System.out.println("getTranslatedBounds: " + object.getName());
        return bounds;
    }
}

