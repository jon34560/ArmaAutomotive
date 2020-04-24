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

public class Perferate {
    private LayoutWindow window = null;

    Perferate(Scene scene, LayoutWindow window){
        System.out.println("Perferate ");
        this.window = window;
    }
    
    /**
     * perferateTriangles
     *
     * Description: Given an object selection, generate curve geometry representing cut lines to perferate the part with holes.
     * @param - Scene : object containing world objects.
     * @param - double percentage of material to remove.
     */
    public void perferateTriangles(Scene scene){
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
                
                
                if( (bounds.maxx - bounds.minx) > (bounds.maxy - bounds.miny) &&
                     (bounds.maxx - bounds.minx) > (bounds.maxz - bounds.minz)   ){ // X axis
                    System.out.println(" is on x ");
                    
                    // H
                    double rotate_angle_index = 0; // 360 degrees total
                    int x_index = 0; // bounds.minx;
                    double unit_distance = (bounds.maxz - bounds.minz) / 5.5; // testing for now
                    System.out.println("unit_distance " + unit_distance); // 0.36363636363636365
                    double partDiameter = (bounds.maxy - bounds.miny);
                    double partRadius = (partDiameter / 2);
                    partRadius = partRadius * 1.02; // expand for visibility.
                    //System.out.println(" xi : " + ((bounds.maxx - bounds.minx) / (unit_distance)));
                    //System.out.println(" r_i : " + (360 / (unit_distance * 4 )));
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
                        
                        for(int r_i = 0; r_i < rUnits; r_i++){ // rotate    // unit_distance
                            double angle = (360.0 / ((double)rUnits)) * (double)r_i;
                            angle = Math.toRadians(angle);
                            angle = angle + rShift; // shift every double pair
                            //System.out.println("  angle " + angle);
                            
                            // 1
                            double xPos = bounds.minx + xRow + xShift;
                            double yPos = (partRadius * Math.cos(angle)) - partRadius - 0.07;
                            double zPos = partRadius * Math.sin(angle);
                            Vec3 point1 = new Vec3(xPos, yPos, zPos);
                            
                            // 2
                            xPos = bounds.minx + xRow + xShift;
                            yPos = (partRadius * Math.cos(angle + 0.25)) - partRadius - 0.065;
                            zPos = partRadius * Math.sin(angle + 0.25);
                            Vec3 point2 = new Vec3(xPos, yPos, zPos);
                            
                            // 3
                            xPos = bounds.minx + xRow + (unit_distance / 1.68) + xShift;
                            yPos = (partRadius * Math.cos(angle + 0.125)) - partRadius - 0.065;
                            zPos = partRadius * Math.sin(angle + 0.125);
                            Vec3 point3 = new Vec3(xPos, yPos, zPos);
                            
                            if(x_i % 2 == 0){
                                xPos = bounds.minx + xRow + (unit_distance / 1.68) + xShift;
                                yPos = (partRadius * Math.cos(angle)) - partRadius - 0.065;
                                zPos = partRadius * Math.sin(angle);
                                point1 = new Vec3(xPos, yPos, zPos);
                                
                                xPos = bounds.minx + xRow + (unit_distance / 1.68) + xShift;
                                yPos = (partRadius * Math.cos(angle + 0.25)) - partRadius - 0.065;
                                zPos = partRadius * Math.sin(angle + 0.25);
                                point2 = new Vec3(xPos, yPos, zPos);
                                
                                xPos = bounds.minx + xRow + xShift;
                                yPos = (partRadius * Math.cos(angle + 0.125)) - partRadius - 0.065;
                                zPos = partRadius * Math.sin(angle + 0.125);
                                point3 = new Vec3(xPos, yPos, zPos);
                            }
                            
                            float[] s_ = new float[3]; s_[0] = 0; s_[1] = 0; s_[2] = 0;
                            Vec3[] vertex = new Vec3[3];
                            vertex[0] = point1;
                            vertex[1] = point2;
                            vertex[2] = point3;
                            Curve perferationCurve = new Curve(vertex, s_, 0, true); // false
                            
                            CoordinateSystem coords = new CoordinateSystem(new Vec3(), Vec3.vz(), Vec3.vy());
                            window.addObject(perferationCurve, coords, "Perferation " + ++p, null);
                            
                            
                            //window.setSelection(window.getScene().getNumObjects()-1); // Add to selected object as child
                            
                            //window.setUndoRecord(new UndoRecord(window, false, UndoRecord.DELETE_OBJECT, new Object [] {new Integer(window.getScene().getNumObjects()-1)}));
                            
                        }
                    
                    }
                    
                    // end of X axis
                    window.updateImage();
                }
                
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
                            double yPos = (partRadius * Math.cos(angle)) - partRadius - 0.07;
                            double zPos = partRadius * Math.sin(angle);
                            Vec3 point1 = new Vec3(xPos, yPos, zPos);
                            
                            // 2
                            xPos = bounds.minx + xRow + xShift;
                            yPos = (partRadius * Math.cos(angle + 0.22)) - partRadius - 0.065;
                            zPos = partRadius * Math.sin(angle + 0.22);
                            Vec3 point2 = new Vec3(xPos, yPos, zPos);
                            
                            // 3
                            xPos = bounds.minx + xRow + (unit_distance / 2) + xShift;
                            yPos = (partRadius * Math.cos(angle + 0.22)) - partRadius - 0.065;
                            zPos = partRadius * Math.sin(angle+ 0.22);
                            Vec3 point3 = new Vec3(xPos, yPos, zPos);
                            
                            // 4
                            xPos = bounds.minx + xRow + (unit_distance / 2) + xShift;
                            yPos = (partRadius * Math.cos(angle)) - partRadius - 0.065;
                            zPos = partRadius * Math.sin(angle);
                            Vec3 point4 = new Vec3(xPos, yPos, zPos);
                            
                           
                            
                            float[] s_ = new float[4]; s_[0] = 0; s_[1] = 0; s_[2] = 0; s_[3] = 0;
                            Vec3[] vertex = new Vec3[4];
                            vertex[0] = point1;
                            vertex[1] = point2;
                            vertex[2] = point3;
                            vertex[3] = point4;
                            Curve perferationCurve = new Curve(vertex, s_, 0, true); // false
                            
                            CoordinateSystem coords = new CoordinateSystem(new Vec3(), Vec3.vz(), Vec3.vy());
                            window.addObject(perferationCurve, coords, "Perferation " + ++p, null);
                            
                            
                            //window.setSelection(window.getScene().getNumObjects()-1); // Add to selected object as child
                            
                            //window.setUndoRecord(new UndoRecord(window, false, UndoRecord.DELETE_OBJECT, new Object [] {new Integer(window.getScene().getNumObjects()-1)}));
                            
                        }
                    
                    }
                    
                    // end of X axis
                    window.updateImage();
                }
                
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
                    partRadius = partRadius * 1.02; // expand for visibility.
                    //System.out.println(" xi : " + ((bounds.maxx - bounds.minx) / (unit_distance)));
                    //System.out.println(" r_i : " + (360 / (unit_distance * 4 )));
                    double yCentre = ((bounds.maxy - bounds.miny) / 2);
                    double zCentre = ((bounds.maxz - bounds.minz) / 2);
                    int p = 0;
                    
                    //for(int x_i = 0; x_i < ((bounds.maxx - bounds.minx) / unit_distance); x_i ++){ // x unit_distance
                    for(int x_i = 0; x_i < ((bounds.maxx - bounds.minx) / unit_distance); x_i++){
                        double xRow =  ( x_i * unit_distance); // (bounds.minx) +
                        int rUnits = 14; // (int)(unit_distance * 38.5); // 0.36363636363636365 = 14
                        
                        double rShift = 0; // rotate to evenly pack
                        if(x_i % 2 == 1){ //
                            rShift = Math.toRadians( (360.0 / ((double)rUnits)) / 2 );
                        }
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
                            double yPos = (partRadius * Math.cos(angle)) - partRadius - 0.07;
                            double zPos = partRadius * Math.sin(angle);
                            Vec3 point1 = new Vec3(xPos, yPos, zPos);
                            
                            // 2
                            xPos = bounds.minx + xRow + ((unit_distance / 1.68)/1.1) + xShift;
                            yPos = (partRadius * Math.cos(angle + 0.125)) - partRadius - 0.065;
                            zPos = partRadius * Math.sin(angle + 0.125);
                            Vec3 point2 = new Vec3(xPos, yPos, zPos);
                            
                            // 3
                            xPos = bounds.minx + xRow + ((unit_distance / 1.68) * 1.8 ) + xShift;
                            yPos = (partRadius * Math.cos(angle )) - partRadius - 0.065;
                            zPos = partRadius * Math.sin(angle);
                            Vec3 point3 = new Vec3(xPos, yPos, zPos);
                            
                            // 4
                            xPos = bounds.minx + xRow + ((unit_distance / 1.68)/1.1) + xShift;
                            yPos = (partRadius * Math.cos(angle - 0.125)) - partRadius - 0.065;
                            zPos = partRadius * Math.sin(angle - 0.125);
                            Vec3 point4 = new Vec3(xPos, yPos, zPos);
                            
                           
                            
                            float[] s_ = new float[4]; s_[0] = 0; s_[1] = 0; s_[2] = 0; s_[3] = 0;
                            Vec3[] vertex = new Vec3[4];
                            vertex[0] = point1;
                            vertex[1] = point2;
                            vertex[2] = point3;
                            vertex[3] = point4;
                            Curve perferationCurve = new Curve(vertex, s_, 0, true); // false
                            
                            CoordinateSystem coords = new CoordinateSystem(new Vec3(), Vec3.vz(), Vec3.vy());
                            window.addObject(perferationCurve, coords, "Perferation " + ++p, null);
                            
                            
                            //window.setSelection(window.getScene().getNumObjects()-1); // Add to selected object as child
                            
                            //window.setUndoRecord(new UndoRecord(window, false, UndoRecord.DELETE_OBJECT, new Object [] {new Integer(window.getScene().getNumObjects()-1)}));
                            
                        }
                    
                    }
                    
                    // end of X axis
                    window.updateImage();
                }
                
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

