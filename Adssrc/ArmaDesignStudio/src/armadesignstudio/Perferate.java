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

    Perferate(Scene scene, LayoutWindow window){
        System.out.println("Perferate ");
    }
    
    /**
     * perferateTriangles
     *
     * Description:
     *
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
                    double unit_distance = (bounds.maxz - bounds.minz) / 5; // testing for now
                    
                    for(int x_i = 0; x_i < ((bounds.maxx - bounds.minx) / unit_distance); x_i++){ // x
                        
                        for(int r_i = 0; r_i < (360 / unit_distance); r_i++){ // rotate
                            
                            // Create object
                            Vec3 point1 = new Vec3( );
                            //Vec3 point2 = new Vec3( );
                            //Vec3 point2 = new Vec3( );
                            
                            
                            //Curve theCurve = new Curve(vertex, s, smoothing, false);
                            
                        }
                    
                    }
                    
                    
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
            
            System.out.println("obj " + info);
            
            
            
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

