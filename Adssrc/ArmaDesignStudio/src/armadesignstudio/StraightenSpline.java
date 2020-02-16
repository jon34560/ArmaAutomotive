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
                    System.out.println("Curve");
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
                        
                        
                        System.out.println("    vert: " +
                                           vertA.x + " " + vertA.y + "  " + vertA.z  + " - " +
                                           vertB.x + " " + vertB.y + "  " + vertB.z);
                        
                        if(i == 1){
                            //Vec3 moved = vecPoints[ vertId ];
                            Vec3 moved = vecPoints[ i ];
                            moved.x = moved.x + 0;
                            moved.y = moved.y + 1;
                            moved.z = moved.z + 0;
                            vecPoints[ i ] = moved;
                        }
                        
                    }
                    
                    
                    
                    ((Mesh)obj.getObject()).setVertexPositions(vecPoints); // todo: check object is instance of type.
                    obj.clearCachedMeshes();
                    ((LayoutWindow)layoutWindow).setModified();
                    ((LayoutWindow)layoutWindow).updateImage();
                    // Fix
                    
                }
            }
        }
    }
    
}


