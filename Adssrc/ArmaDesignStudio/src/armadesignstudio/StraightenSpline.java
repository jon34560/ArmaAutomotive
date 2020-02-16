/*
 
 Copyright (C) 2020 by Jon Taylor

 This program is free software; you can redistribute it and/or modify it under the
 terms of the GNU General Public License as published by the Free Software
 Foundation; either version 2 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful, but WITHOUT ANY
 WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 
*/


package armadesignstudio;

import buoy.event.*;
import buoy.widget.*;
import java.util.*;
import armadesignstudio.object.*;
import armadesignstudio.math.*;
import java.lang.Math;

public class StraightenSpline {
    LayoutWindow layoutWindow;
    
    public StraightenSpline(LayoutWindow layoutWindow){
        this.layoutWindow = layoutWindow;
    }
    
    /**
     *
     *
     */
    public void straightenSpline(Vector<ObjectInfo> objects){
        System.out.println("Straighten Spline ");
        
        for (ObjectInfo obj : objects){
            if(obj.selected == true){
                //System.out.println("Object Info: ");
                Object co = (Object)obj.getObject();
                if((co instanceof Curve) == true){
                    //System.out.println("Curve");
                    CoordinateSystem cs = ((ObjectInfo)obj).getCoords();
                    Vec3 origin = cs.getOrigin();

                    Mesh mesh = (Mesh) obj.getObject(); // Object3D
                    Vec3 [] verts = mesh.getVertexPositions();
                    
                    Vec3 vecPoints[] = new Vec3[verts.length];
                    for(int i = 0; i < verts.length; i++){
                        vecPoints[i] = verts[i]; // .r;
                    }

                    //for (Vec3 vert : verts){
                        //Mat4 mat4 = cs.duplicate().fromLocal();
                        //mat4.transform(vert);
                        //System.out.println("    vert: " + vert.x + " " + vert.y + "  " + vert.z );
                    //}
                    
                    Vector vecAnglesX = new Vector();
                    Vector vecAnglesY = new Vector();
                    
                    for(int i = 1; i < verts.length; i++){
                        Vec3 vertA = verts[i - 1];
                        Vec3 vertB = verts[i];
                        
                        double distance = Math.sqrt(Math.pow(vertA.x - vertB.x, 2) + Math.pow(vertA.y - vertB.y, 2) + Math.pow(vertA.z - vertB.z, 2));
                        double xOffset = Math.max(vertA.x, vertB.x) - Math.min(vertA.x, vertB.x);
                        double yOffset = Math.max(vertA.y, vertB.y) - Math.min(vertA.y, vertB.y);
                        double zOffset = Math.max(vertA.z, vertB.z) - Math.min(vertA.z, vertB.z);
                        double yAngle = 0;
                        //System.out.println("xOffset: " + xOffset);
                        if(xOffset != 0){
                            yAngle = Math.atan(yOffset / xOffset); // yOffset = opposite, xOffet = adjacent
                        }
                        double zAngle = 0;
                        if(xOffset != 0){
                            zAngle = Math.atan(zOffset / xOffset);
                        }
                        
                        //System.out.println("    vert: " +
                        //                   vertA.x + " " + vertA.y + "  " + vertA.z  + " - " +
                        //                   vertB.x + " " + vertB.y + "  " + vertB.z);
                        //System.out.println( "distance " + distance);
                        if(true){ // i == 1 || i == 2
                            
                            // Rotate all remaining points around vertA to angle required to become straight.
                            double rotateXRequired = 0;
                            double rotateZRequired = 0;
                            
                            Vec3 vertBNew = new Vec3(vertB);
                            vertBNew.x = vertA.x - distance;
                            vertBNew.y = vertA.y;
                            vertBNew.z = vertA.z;
                            vecPoints[ i ] = vertBNew;
                            verts[i] = vertBNew;
                            
                            rotateXRequired = getAngleX(vertA, vertB, vertBNew);
                            System.out.println("rotateXRequired " + rotateXRequired);
                            
                            for(int j = i + 1; j < verts.length; j++){
                                Vec3 next = verts[j];
                                Vec3 rotated = rotatePointX(next, vertA, rotateXRequired);
                                verts[j] = rotated;
                                vecPoints[ j ] = rotated;
                            }
                             
                        }
                        
                    } // verts pairs
                    
                    
                    // Update scene
                    ((Mesh)obj.getObject()).setVertexPositions(vecPoints); // todo: check object is instance of type.
                    obj.clearCachedMeshes();
                    ((LayoutWindow)layoutWindow).setModified();
                    ((LayoutWindow)layoutWindow).updateImage();
                    
                    
                } else {
                    //
                    System.out.println("No curve selected.");
                }
            }
        }
    }
    
    /**
     * getAngle
     *
     * Description:
     */
    double getAngleX(Vec3 a, Vec3 b, Vec3 b2){
        double angle = 0;
        
        // Scale to a.
        double x1 = b.x - a.x;     // 1 = b
        double x2 = b2.x - a.x;     // 2 = b2
        double y1 = b.y - a.y;
        double y2 = b2.y - a.y;
        System.out.println("     - x1: " + x1 + " y1: " + y1 + "   x2: " + x2 + " y2: " + y2 );
        
        //double f = arccos((x1*x2 + y1*y2) / ( sqrt(x1*x1 + y1*y1) * sqrt(x2*x2 + y2*y2) );
        angle = Math.acos((x1*x2 + y1*y2) / ( Math.sqrt(x1*x1 + y1*y1) * Math.sqrt(x2*x2 + y2*y2)));
        
        return angle;
    }
    
    /**
     * rotatePoint
     *
     * Description:
     */
    Vec3 rotatePointX(Vec3 point, Vec3 origin, double angle){
        Vec3 rotatedPoint = new Vec3();
        rotatedPoint.x = origin.x + (point.x-origin.x)*Math.cos(angle) - (point.y - origin.y)*Math.sin(angle);
        rotatedPoint.y = origin.y + (point.x-origin.x)*Math.sin(angle) + (point.y - origin.y)*Math.cos(angle);
        rotatedPoint.z = point.z;
        return rotatedPoint;
    }
}


