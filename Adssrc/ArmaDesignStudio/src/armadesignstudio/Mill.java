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

public class Mill extends Thread {
    boolean running = true;
    private Vector<ObjectInfo> objects;
    private Scene scene;
    private String name; // Depricate use scene getName instead
    private double minx = 99999;
    private double miny = 99999;
    private double minz = 99999;
    private double maxx = -999999;
    private double maxy = -999999;
    private double maxz = -999999;

    public void setObjects(Vector<ObjectInfo> objects){
        this.objects = objects;
    }
    
    public void setScene(Scene scene){
        this.scene = scene;
    }
    
    /**
     * exportGCode
     *
     * Description:
     */
    public void exportGCode(){
        System.out.println("Export 3D Mill.");
        LayoutModeling layout = new LayoutModeling();
        
        String dir = scene.getDirectory() + System.getProperty("file.separator") + scene.getName() + "_gCode3d";
        File d = new File(dir);
        if(d.exists() == false){
            d.mkdir();
        }
        
        // Find 
        
        //Vector cutPaths = new Vector(); // Lines to cut.
        //Vector<FluidPointObject> millPoint = new Vector<FluidPointObject>();
        
        
        calculateBounds(objects);
        for (ObjectInfo obj : objects){
            if(obj.getName().indexOf("Camera") < 0 &&
               obj.getName().indexOf("Light") < 0 &&
               //obj.getClass() != FluidPointObject.class
               obj.getName().equals("") == false &&
               obj.isVisible() &&
               running
               ){
                //System.out.println("Object Info: ");
                //Object3D co = (Object3D)obj.getObject();
                System.out.println("obj " + obj.getId() + "  " + obj.getName() );
                // obj.getObject(); // Object3D
                Object3D o3d = obj.getObject();
                
                CoordinateSystem c;
                c = layout.getCoords(obj);
                Vec3 objOrigin = c.getOrigin();
                //System.out.println(" obj origin " + objOrigin.x + " " + objOrigin.y + " " + objOrigin.z );
                
                BoundingBox bounds = o3d.getBounds(); // does not include location
                bounds = new BoundingBox(bounds); // clone bounds
                
                if(obj.getObject().canConvertToTriangleMesh() != Object3D.CANT_CONVERT){
                    
                    //TriangleMesh.Edge[] edges = ((TriangleMesh)triangleMesh).getEdges();
                    
                    TriangleMesh triangleMesh = null;
                    triangleMesh = obj.getObject().convertToTriangleMesh(0.0);
                    MeshVertex[] verts = triangleMesh.getVertices();
                    TriangleMesh.Edge[] edges = ((TriangleMesh)triangleMesh).getEdges();
                    
                    for(int e = 0; e < edges.length; e++){
                        TriangleMesh.Edge edge = edges[e];
                        
                        Vec3 vec1 = new Vec3(verts[edge.v1].r); // duplicate
                        Vec3 vec2 = new Vec3(verts[edge.v2].r);
                        
                        System.out.println(" x: " + vec1.x + " y: "+ vec1.y + " z: " + vec1.z  + " ->  " + " x: " + vec2.x + " y: "+ vec2.y + " z: " + vec2.z  );
                    }
                    
                    
                    TriangleMesh.Face[] faces = triangleMesh.getFaces();
                    
                    System.out.println("faces: " + faces.length);
                    for(int f = 0; f < faces.length; f++){ //  && running
                        TriangleMesh.Face face = faces[f];
                        Vec3 vec1 = new Vec3(verts[face.v1].r); // duplicate
                        Vec3 vec2 = new Vec3(verts[face.v2].r);
                        Vec3 vec3 = new Vec3(verts[face.v3].r);
                        
                        Mat4 mat4 = c.duplicate().fromLocal();
                        mat4.transform(vec1);
                        mat4.transform(vec2);
                        mat4.transform(vec3);
                        
                        
                        //System.out.println(" x: " + vec1.x + " y: "+ vec1.y + " z: " + vec1.z);
                        
                    }
                    
                    
                
                }
                
            }
        }
        
    }
    
    
    /**
     * calculateBounds
     *
     * Description: calculate region to simulate flow to be a
     *  relative size larger than the bounds of scene objects.
     */
    public void calculateBounds(Vector<ObjectInfo> objects){
        LayoutModeling layout = new LayoutModeling();
        // Calculate bounds
        for (ObjectInfo obj : objects){
            if(obj.getName().indexOf("Camera") < 0 &&
               obj.getName().indexOf("Light") < 0 &&
               obj.isVisible()
               ){ //obj.selected == true  || selection == false
                //System.out.println("Object Info: ");
                Object3D co = (Object3D)obj.getObject();
                //System.out.println("obj " + obj.getId() + "  " + obj.getName() );
                
                // obj.getObject(); // Object3D
                Object3D o3d = obj.getObject();
                BoundingBox bounds = o3d.getBounds();
                
                // Include object location in bounds values.
                CoordinateSystem c;
                c = layout.getCoords(obj);
                Vec3 objOrigin = c.getOrigin();
                bounds.minx += objOrigin.x; bounds.maxx += objOrigin.x;
                bounds.miny += objOrigin.y; bounds.maxy += objOrigin.y;
                bounds.minz += objOrigin.z; bounds.maxz += objOrigin.z;
                
                
                //System.out.println("  " + bounds.minx + " " + bounds.maxx );
                if(bounds.minx < this.minx){
                    this.minx = bounds.minx;
                }
                if(bounds.maxx > this.maxx){
                    this.maxx = bounds.maxx;
                }
                if(bounds.miny < this.miny){
                    this.miny = bounds.miny;
                }
                if(bounds.maxy > this.maxy){
                    this.maxy = bounds.maxy;
                }
                if(bounds.minz < this.minz){
                    this.minz = bounds.minz;
                }
                if(bounds.maxz > this.maxz){
                    this.maxz = bounds.maxz;
                }
            }
        }
        
        
        
    }
    
}

