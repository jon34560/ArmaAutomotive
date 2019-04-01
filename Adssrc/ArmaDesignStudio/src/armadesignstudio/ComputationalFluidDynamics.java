/* Copyright (C) 2018 by Jon Taylor
 
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

/**
 * ComputationalFluidDynamics
 * 
 */
public class ComputationalFluidDynamics extends Thread {
    boolean running = false;
    private Vector<ObjectInfo> objects;
    LayoutWindow window;
    private double minx = 0;
    private double miny = 0;
    private double minz = 0;
    private double maxx = 0;
    private double maxy = 0;
    private double maxz = 0;
    private int pointsPerLength = 10; // 13;
    
    Vector<FluidPointObject> pointObjects = new Vector<FluidPointObject>();
    
    public ComputationalFluidDynamics(){
        objects = null;
    }

    public void setObjects(Vector<ObjectInfo> objects){
        this.objects = objects;
    }
    
    public void setLayoutWindow(LayoutWindow window){
        this.window = window;
    }

    public boolean isRunning(){
        return running;
    }
    
    public void stopCFD(){
        running = false;
    }
    
    public void run(){
        //if(running){ // Stop existing process
        //    System.out.println("Exiting CFD");
        //    running = false;
        //    return;
        //}
        
        running = true;
        calculateBounds(objects);
        java.util.Random random = new java.util.Random();
        
        // Expand Scale
        minx -= ((maxx - minx)/1.7);
        maxx += ((maxx - minx)/1.7);
        miny -= ((maxy - miny)/1.7);
        maxy += ((maxy - miny)/1.7);
        minz -= ((maxz - minz)/1.7);
        maxz += ((maxz - minz)/1.7);
        
        System.out.println(" x " + this.minx + " " + this.maxx +
                           " y " +
                           this.miny + " " + this.maxy +
                           " z " +
                           this.minz + " " + this.maxz + " ");
        
        double xSegmentWidth = (maxx - minx) / pointsPerLength;
        double ySegmentWidth = (maxy - miny) / pointsPerLength;
        double zSegmentWidth = (maxz - minz) / pointsPerLength;
        
        // place fluid points
        for(int z = 0; z < pointsPerLength; z++){
            for(int x = 0; x < pointsPerLength; x++){
                for(int y = 0; y < pointsPerLength; y++){
                
                    Vec3[] vertex = new Vec3[3];
                    Vec3 vec = new Vec3(minx + (x * xSegmentWidth), miny + (y * ySegmentWidth), minz + (z * zSegmentWidth));
                    vertex[0] = vec;
                    vertex[1] = vec;
                    vertex[2] = vec;
                    float s[];
                    s = new float[3];
                    s[0] = 0;
                    s[1] = 0;
                    s[2] = 0;
                    int smoothing = Mesh.APPROXIMATING;
                    CoordinateSystem coords;
                    
                    Vec3[] resetLocation = new Vec3[3];
                    Vec3 resetVec = new Vec3(minx + (x * xSegmentWidth), miny + (y * ySegmentWidth), maxz);
                    resetLocation[0] = resetVec;
                    resetLocation[1] = resetVec;
                    resetLocation[2] = resetVec;
                    
                    FluidPointObject fluidPoint = new FluidPointObject(vertex, s, smoothing, false);
                    
                    fluidPoint.setResetLocation(resetLocation);
                    
                    pointObjects.addElement(fluidPoint);
                    
                    //
                    ObjectInfo info = new ObjectInfo(fluidPoint, new CoordinateSystem(), "" /*no name*/);
                    //UndoRecord undo = new UndoRecord(window, false);
                    int id = ((LayoutWindow)window).addObjectL(fluidPoint); // This is bad. don't add this way later.
                    fluidPoint.setId(id);
                    // This only reason we want it in the scene is to render we don't want it in the menu
                }
                window.repaint();
            }
            //System.out.print(".");
        }
        System.out.println("Points placed.");
        
        // Move fluid points through region around objects calculating diflections.
        while(running){
            
            for(int i = 0; i < pointObjects.size(); i++){
                FluidPointObject fluidPoint = pointObjects.elementAt(i);
                Vec3 location = fluidPoint.getLocation();
                // MeshVertex[] getVertices()
                // movePoint
                //fluidPoint.movePoint(  );
                Vec3 [] points = fluidPoint.getVertexPositions();
                int v = 0;
                //for(int v = 0; v < 1; v++ ){ //  points.length
                    
                    // Detect collisions.
                    boolean collide = false;
                    double collideZ = 0;
                    for (ObjectInfo obj : objects){
                        if(obj.getName().indexOf("Camera") < 0 &&
                           obj.getName().indexOf("Light") < 0 &&
                           //obj.getClass() != FluidPointObject.class
                           obj.getName().equals("") == false
                           ){
                            //System.out.println("Object Info: ");
                            //Object3D co = (Object3D)obj.getObject();
                            //System.out.println("obj " + obj.getId() + "  " + obj.getName() );
                            // obj.getObject(); // Object3D
                            Object3D o3d = obj.getObject();
                            BoundingBox bounds = o3d.getBounds();
                            
                            if(location.x > bounds.minx && location.x < bounds.maxx &&
                               location.y > bounds.miny && location.y < bounds.maxy &&
                               location.z > bounds.minz && location.z < bounds.maxz){
                                collide = true;
                           //     collideZ = bounds.maxz;
                                //System.out.println(" c obj " +obj.getName() );
                            }
                        }
                    }
                    
                    // Detect pressure from adjacent fluid points that are too close or too far.
                    // Too close psi > 1.0, too far psi < 1.0
                    
                    fluidPoint.setPSI(1.0);
                    double pressureAbove = 0.0;
                    double pressureBelow = 0.0;
                    double pressureLeft = 0.0;
                    double pressureRight = 0.0;
                    
                    double vacumeLeft = 1.0;
                    double vacumeRight =  1.0;
                    double vacumeAbove = 1.0;
                    double vacumeBelow = 1.0;
                    
                    // Compare this point (fluidPoint) with other points (compareFluidPoint) too see if they are too close or too far
                    for(int f = 0; f < pointObjects.size(); f++){ // optimise later with index data structures.
                        if(f != i){
                            FluidPointObject compareFluidPoint = pointObjects.elementAt(f);
                            double distance = fluidPoint.getLocation().distance(compareFluidPoint.getLocation());
                            
                            if(distance > zSegmentWidth * 3){
                                //break;
                            }
                            //System.out.println(" dist " + distance + " " + zSegmentWidth);
                            
                            double zDiff = Math.abs(fluidPoint.getLocation().z - compareFluidPoint.getLocation().z);
                            //if(fluidPoint.getLocation().z > compareFluidPoint.getLocation().z){
                            //    zDiff = fluidPoint.getLocation().z - compareFluidPoint.getLocation().z;
                            //} else {
                            //    zDiff = compareFluidPoint.getLocation().z - fluidPoint.getLocation().z;
                            //}
                            double xDiff = Math.abs(fluidPoint.getLocation().x - compareFluidPoint.getLocation().x);
                            double yDiff = Math.abs(fluidPoint.getLocation().y - compareFluidPoint.getLocation().y);
                            //System.out.println(" zDiff " + zDiff );
                            
                            //Depth
                            if(zDiff < zSegmentWidth && distance < zSegmentWidth){
                                fluidPoint.setPSI(fluidPoint.getPSI() + (( zSegmentWidth / distance  ) * 0.05)  );
                            }
                            
                            // Left
                            if( compareFluidPoint.getLocation().x < fluidPoint.getLocation().x &&   // compare is too close on the left
                               distance <= (xSegmentWidth * 1.5)
                               ){
                                if(compareFluidPoint.getPSI() > fluidPoint.getPSI() + 0.0){
                                    pressureLeft += compareFluidPoint.getPSI() - fluidPoint.getPSI();
                                    //System.out.print(" l ");
                                }
                                //fluidPoint.setPSI(fluidPoint.getPSI() + (distance / xSegmentWidth));
                            }
                            // Right
                            if( compareFluidPoint.getLocation().x > fluidPoint.getLocation().x &&
                               distance < (xSegmentWidth * 1.5)
                               ){
                                
                                if(compareFluidPoint.getPSI() > fluidPoint.getPSI() + 0.0){
                                    pressureRight += compareFluidPoint.getPSI() - fluidPoint.getPSI();
                                }
                                //fluidPoint.setPSI(fluidPoint.getPSI() + (distance / xSegmentWidth)); // compare is too close on right
                            }
                            // Down
                            if( compareFluidPoint.getLocation().y < fluidPoint.getLocation().y &&
                               distance < (ySegmentWidth * 1.5)
                               ){
                                
                                if(compareFluidPoint.getPSI() > fluidPoint.getPSI() + 0.0){
                                    pressureBelow += compareFluidPoint.getPSI() - fluidPoint.getPSI();
                                    //System.out.print("u");
                                }
                                //fluidPoint.setPSI(fluidPoint.getPSI() + (distance / ySegmentWidth));
                            }
                            // Up
                            if( compareFluidPoint.getLocation().y > fluidPoint.getLocation().y &&
                               distance < (ySegmentWidth * 1.5)
                               ){
                                if(compareFluidPoint.getPSI() > fluidPoint.getPSI() + 0.0){
                                    pressureAbove += compareFluidPoint.getPSI() - fluidPoint.getPSI();
                                }
                                //fluidPoint.setPSI(fluidPoint.getPSI() + (distance / ySegmentWidth)); // compare is too close above
                            }
                            
                            //
                            // Vacume
                            //
                            if( compareFluidPoint.getLocation().x < fluidPoint.getLocation().x &&
                               distance < (xSegmentWidth * 1.2)
                               //&& compareFluidPoint.getPSI() > fluidPoint.getPSI() + 0.01
                               ){
                                vacumeLeft = 0.0;
                            }
                            if( compareFluidPoint.getLocation().x > fluidPoint.getLocation().x &&
                               distance < (xSegmentWidth * 1.2)
                               //&& compareFluidPoint.getPSI() > fluidPoint.getPSI() + 0.01
                               ){
                                vacumeRight = 0.0;
                            }
                            if( compareFluidPoint.getLocation().y > fluidPoint.getLocation().y &&
                               distance < (ySegmentWidth * 1.2)
                            //   //&& compareFluidPoint.getPSI() > fluidPoint.getPSI() + 0.01
                               ){
                                vacumeAbove = 0.0;
                            }
                            if( compareFluidPoint.getLocation().y < fluidPoint.getLocation().y &&
                               distance < (ySegmentWidth * 1.2)
                               //   //&& compareFluidPoint.getPSI() > fluidPoint.getPSI() + 0.01
                               ){
                                vacumeBelow = 0.0;
                            }
                        }
                    }
                    
                    double zStep = 0.054;
                    double xStep = 0.0003;
                    double yStep = 0.0005;
                    
                    // High Pressure
                    if(collide == false){
                        //System.out.println(" X " + fluidPoint.getPSI() );
                        double psiScale = Math.log10(fluidPoint.getPSI() + 1);
                        if(psiScale <= 0){
                            psiScale = 0.001;
                            System.out.println("psi " + fluidPoint.getPSI() + " " + Math.log10(fluidPoint.getPSI() + 1) );
                        }
                        points[v].z -= (zStep * (1 - psiScale)); // ???? not all ar emoving
                        //System.out.println(" fluidPoint.getPSI(): " + psiScale );
                        
                        if(pressureLeft > pressureRight){
                            points[v].x += xStep * (pressureLeft-pressureRight); // Push right from pressure on left side
                        }
                        if(pressureRight > pressureLeft){
                            points[v].x -= xStep * (pressureRight-pressureLeft); // Push left from pressure on right side
                        }
                        if(pressureLeft == pressureRight){
                            //System.out.print(" e ");
                            if(Math.random() > 0.5){
                                points[v].x += xStep * (pressureLeft-pressureRight);
                            } else {
                                points[v].x -= xStep * (pressureLeft-pressureRight);
                            }
                        }
                        
                        
                        if(pressureAbove > pressureBelow){
                            points[v].y -= yStep * (pressureAbove-pressureBelow); // Push down from pressure on upper side
                        }
                        if(pressureBelow > pressureAbove){
                            points[v].y += yStep * (pressureBelow-pressureAbove); // Push left from pressure on right side
                        }
                        
                        if(pressureBelow == pressureAbove){
                            //System.out.print(" e ");
                            if(Math.random() > 0.5){
                                points[v].y -= yStep * (pressureAbove-pressureBelow);
                            } else {
                                points[v].y += yStep * (pressureBelow-pressureAbove);
                            }
                        }
                        
                        //points[v].z -= 0.005;
                        //points[v].y += 0.0005; // TEST
                        //points[v].x += 0.0015; // TEST
                    } else {
                        //points[v].z = collideZ;
                        //System.out.print("c");
                    }
                    
                /*
                    // Low Pressure
                    if(vacumeLeft > 0.0 || vacumeRight > 0.0 || vacumeAbove > 0.0 || vacumeBelow > 0.0){
                        
                        if(vacumeLeft > 0.0 && points[v].x > minx){ // move left
                            points[v].x -= 0.005; // * (pressureRight-pressureLeft); // Push left from pressure on right side
                        }
                        if(vacumeRight > 0.0 && points[v].x < maxx){
                            points[v].x += 0.005;
                        }
                        if(vacumeAbove > 0.0 && points[v].y < maxy){ // move up
                            points[v].y += 0.005;
                        }
                        if(vacumeBelow > 0.0 && points[v].y < miny){ // move down
                            points[v].y -= 0.005;
                        }
                        
                        fluidPoint.setPSI( 0.2 ); // render as blue
                    }
                */
                    
                    /*
                    // Bounds check
                    if(points[v].x < minx){
                        points[v].x = minx;
                    }
                    if(points[v].x > maxx){
                        points[v].x = maxx;
                    }
                    if(points[v].y < miny){
                        points[v].y = miny;
                    }
                    if(points[v].y > maxy){
                        points[v].y = maxy;
                    }
                     */
                    
                    // Reset fluid location
                    if(points[v].z < minz){
                        //fluidPoint.resetLocation();
                        //v = points.length;
                        
                        //points[v].x = fluidPoint.getResetLocation()[0].x;
                        //points[v].y = fluidPoint.getResetLocation()[0].y;
                        //points[v].z = maxz;
                        
                        ((LayoutWindow)window).removeObjectL(fluidPoint);
                        
                        for(int f = 0; f < pointObjects.size(); f++){
                            FluidPointObject fo = (FluidPointObject)pointObjects.elementAt(f);
                            if(fluidPoint == fo){
                                pointObjects.removeElementAt(f);
                                //f = pointObjects.size();
                            }
                        }
                        
                    }
                //}
                
                fluidPoint.setVertexPositions(points);
            }
            
            // Respawn fluid point at beginning
            for(int x = 0; x < pointsPerLength; x++){
                for(int y = 0; y < pointsPerLength; y++){
                    
                    boolean spawn = true;
                    for(int i = 0; i < pointObjects.size(); i++){
                        FluidPointObject fluidPoint = pointObjects.elementAt(i);
                        Vec3 location = fluidPoint.getLocation();
                        
                        if(
                           location.x > (minx + (x * xSegmentWidth)) - (xSegmentWidth/2) &&
                           location.x < (minx + (x * xSegmentWidth)) + (xSegmentWidth/2) &&
                           location.y > (miny + (y * ySegmentWidth)) - (ySegmentWidth/2) &&
                           location.y < (miny + (y * ySegmentWidth)) + (ySegmentWidth/2) &&
                           location.z > (maxz - (zSegmentWidth))
                        ){
                            spawn = false;
                        }
                    }
                    
                    if(spawn){
                        //System.out.println("SPAWN");
                        Vec3[] vertex = new Vec3[3];
                        Vec3 vec = new Vec3(minx + (x * xSegmentWidth), miny + (y * ySegmentWidth), maxz);
                        vertex[0] = vec;
                        vertex[1] = vec;
                        vertex[2] = vec;
                        float s[];
                        s = new float[3];
                        s[0] = 0;
                        s[1] = 0;
                        s[2] = 0;
                        int smoothing = Mesh.APPROXIMATING;
                        CoordinateSystem coords;
                        
                        //Vec3[] resetLocation = new Vec3[3];
                        //Vec3 resetVec = new Vec3(minx + (x * xSegmentWidth), miny + (y * ySegmentWidth), maxz);
                        //resetLocation[0] = resetVec;
                        //resetLocation[1] = resetVec;
                        //resetLocation[2] = resetVec;
                        
                        FluidPointObject fluidPoint = new FluidPointObject(vertex, s, smoothing, false);
                        
                        //fluidPoint.setResetLocation(resetLocation);
                        
                        pointObjects.addElement(fluidPoint);
                        
                        //
                        ObjectInfo info = new ObjectInfo(fluidPoint, new CoordinateSystem(), "" /*no name*/);
                        //UndoRecord undo = new UndoRecord(window, false);
                        int id = ((LayoutWindow)window).addObjectL(fluidPoint); // This is bad. don't add this way later.
                        //fluidPoint.setId(id);
                        // This only reason we want it in the scene is to render we don't want it in the menu
                    }
                }
                window.repaint();
            }
            
            
            // Refresh screen
            window.repaint();
            
            System.out.print(".");
            
            try {
                Thread.sleep(8);
            } catch (Exception e){  }
            
        }
        
    }
    
    /**
     * calculateBounds
     *
     * Description: calculate region to simulate flow to be a
     *  relative size larger than the bounds of scene objects.
     */
    public void calculateBounds(Vector<ObjectInfo> objects){
        // Calculate bounds
        for (ObjectInfo obj : objects){
            if(obj.getName().indexOf("Camera") < 0 && obj.getName().indexOf("Light") < 0 ){ //obj.selected == true  || selection == false
                //System.out.println("Object Info: ");
                Object3D co = (Object3D)obj.getObject();
                //System.out.println("obj " + obj.getId() + "  " + obj.getName() );
                
                // obj.getObject(); // Object3D
                Object3D o3d = obj.getObject();
                BoundingBox bounds = o3d.getBounds();
                
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