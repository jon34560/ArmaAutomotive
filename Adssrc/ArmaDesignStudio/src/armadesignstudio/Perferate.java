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

