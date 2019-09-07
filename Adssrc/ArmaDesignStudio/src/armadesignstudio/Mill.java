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
import java.math.BigDecimal;
import java.text.DecimalFormat;


import buoy.widget.*;
//import java.awt.*;
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
import javax.swing.JCheckBox;

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
    
    private int width = 48;
    private int depth = 96;
    
    private double drill_bit = 0.125;   // 0.125 1/8th 3.175mm
    private double pass_height = 0.5;   // drill cuts this much material per pass
    private double material_height = 2; // cut scene into layers this thick for seperate parts/files.

    private boolean toolpathMarkup = false;
    
    private LayoutWindow window = null;
    
    public void setObjects(Vector<ObjectInfo> objects){
        this.objects = objects;
    }
    
    public void setScene(Scene scene){
        this.scene = scene;
    }
    
    public void setLayoutWindow(LayoutWindow window){
        this.window = window;
    }
    
    /**
     * getUserInput
     *
     * Description:
     */
    public void getUserInput(){
        JPanel panel = new JPanel();
        //panel.setBackground(new Color(0, 0, 0));
        panel.setSize(new Dimension(350, 32));
        panel.setLayout(null);
        
        JLabel widthLabel = new JLabel("Bed Width");
        //widthLabel.setForeground(new Color(255, 255, 0));
        widthLabel.setHorizontalAlignment(SwingConstants.CENTER);
        widthLabel.setFont(new Font("Arial", Font.BOLD, 11));
        widthLabel.setBounds(0, 0, 100, 40); // x, y, width, height
        panel.add(widthLabel);
        
        JTextField widthField = new JTextField("48");
        widthField.setBounds(100, 0, 100, 40); // x, y, width, height
        panel.add(widthField);
        //widthField.getDocument().addDocumentListener(myListener);
        
        JLabel labelDepth = new JLabel("Bed Depth");
        //labelHeight.setForeground(new Color(255, 255, 0));
        labelDepth.setHorizontalAlignment(SwingConstants.CENTER);
        labelDepth.setFont(new Font("Arial", Font.BOLD, 11));
        labelDepth.setBounds(0, 40, 120, 40); // x, y, width, height
        panel.add(labelDepth);
        
        JTextField depthtField = new JTextField("96");
        depthtField.setBounds(100, 40, 120, 40); // x, y, width, height
        panel.add(depthtField);
        
        JLabel labelHeight = new JLabel("Material Height");
        //labelHeight.setForeground(new Color(255, 255, 0));
        labelHeight.setHorizontalAlignment(SwingConstants.CENTER);
        labelHeight.setFont(new Font("Arial", Font.BOLD, 11));
        labelHeight.setBounds(0, 80, 120, 40); // x, y, width, height
        panel.add(labelHeight);
        
        JTextField heightField = new JTextField("2");
        heightField.setBounds(100, 80, 120, 40); // x, y, width, height
        panel.add(heightField);
        
        JLabel labelBit = new JLabel("Drill Bit Diameter");
        //labelHeight.setForeground(new Color(255, 255, 0));
        labelBit.setHorizontalAlignment(SwingConstants.CENTER);
        labelBit.setFont(new Font("Arial", Font.BOLD, 11));
        labelBit.setBounds(0, 120, 120, 40); // x, y, width, height
        panel.add(labelBit);
        
        JTextField bitField = new JTextField("0.125");
        bitField.setBounds(100, 120, 120, 40); // x, y, width, height
        panel.add(bitField);
        
    
        JLabel pathBit = new JLabel("Tool Path Markup");
        //labelHeight.setForeground(new Color(255, 255, 0));
        pathBit.setHorizontalAlignment(SwingConstants.CENTER);
        pathBit.setFont(new Font("Arial", Font.BOLD, 11));
        pathBit.setBounds(0, 160, 120, 40); // x, y, width, height
        panel.add(pathBit);
        
        JCheckBox toolpathCheck = new JCheckBox("");
        toolpathCheck.setBounds(100, 160, 120, 40); // x, y, width, height
        toolpathCheck.setSelected(false);
        panel.add(toolpathCheck);
        
        
        UIManager.put("OptionPane.minimumSize",new Dimension(350, 350));
        
        int result = JOptionPane.showConfirmDialog(null, panel, "CNC Mill Properties", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            System.out.println("width value: " + widthField.getText());
            System.out.println("depth value: " + depthtField.getText());
            System.out.println("height value: " + heightField.getText());
            System.out.println("bit value: " + bitField.getText());
            
            this.width = Integer.parseInt(widthField.getText());
            this.depth = Integer.parseInt(depthtField.getText());
            this.material_height = Double.parseDouble(heightField.getText());
            this.drill_bit = Double.parseDouble(bitField.getText());
            
            this.toolpathMarkup = toolpathCheck.isSelected();
        }
    }
    
    
    /**
     * exportGCode
     *
     * Description: Process scene objects creating GCode CNC router cutting path.
     * TODO: slice into seperate files for layers of material. ie 2" foam blocks.
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
        
        // Create grid across bounds, with border, using the width of the drill bit.
        // The cut path can scan the grid height raised for point and faces contained within.
        int mapWidth = (int)((this.maxx - this.minx) / drill_bit) + 0;
        int mapDepth = (int)((this.maxz - this.minz) / drill_bit) + 0;
        
        int sections = 1;
        if(this.maxy - this.miny > material_height){
            double sd = ((this.maxy - this.miny) / material_height);
            int si = (int)sd;
            if(sd > (double)si){
                si++;
            }
            sections = si;
        }
        System.out.println("sec " + sections);
        
        // this.minx this.minz
        
        Double[][] cutHeights = new Double[mapWidth + 1][mapDepth + 1]; // state of machined material. Used to ensure not too deep a pass is made.
        Double[][] mapHeights = new Double[mapWidth + 1][mapDepth + 1]; // Object top surface
        
        System.out.println(" map  x: " + mapWidth + " z: " + mapDepth);
        
        Vector toolpathMarkupPoints = new Vector();
        
        for(int s = 1; s < sections; s++){
            double top = this.maxy - ((s-1) * material_height);
            double bot = this.maxy - ((s-2) * material_height);
            
            //BoundingBox sectionBounds = o3d.getBounds();
            
            
            
            //
            for(int x = 0; x < mapWidth + 1; x++){
                for(int z = 0; z < mapDepth + 1; z++){
                    double x_loc = this.minx + (x * drill_bit);
                    double z_loc = this.minz + (z * drill_bit);
                    Vec3 point_loc = new Vec3(x_loc, 0, z_loc);
                    double height = 0;
                    cutHeights[x][z] = material_height; // initalize material state.
                    //System.out.print(".");
            
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
                            //System.out.println("obj " + obj.getId() + "  " + obj.getName() );
                            Object3D o3d = obj.getObject();
                            
                            CoordinateSystem c;
                            c = layout.getCoords(obj);
                            Vec3 objOrigin = c.getOrigin();
                            //System.out.println(" obj origin " + objOrigin.x + " " + objOrigin.y + " " + objOrigin.z );
                            
                            BoundingBox bounds = o3d.getBounds(); // does not include location
                            bounds = new BoundingBox(bounds); // clone bounds
                            
                            if(
                               ( x_loc >= bounds.minx && x_loc <= bounds.maxx && z_loc >= bounds.minz && z_loc <= bounds.maxz) // optimization, within x,z region space
                               && (bounds.maxy > height) // this object must have the possibility of raising/changing the mill height.
                               && obj.getObject().canConvertToTriangleMesh() != Object3D.CANT_CONVERT
                               ){
                                
                                //TriangleMesh.Edge[] edges = ((TriangleMesh)triangleMesh).getEdges();
                                
                                TriangleMesh triangleMesh = null;
                                triangleMesh = obj.getObject().convertToTriangleMesh(0.0);
                                //triangleMesh = ((TriangleMesh)obj.getObject()).duplicate()  .convertToTriangleMesh(0.0);
                                MeshVertex[] verts = triangleMesh.getVertices();
                                TriangleMesh.Edge[] edges = ((TriangleMesh)triangleMesh).getEdges();
                                
                                //for(int e = 0; e < edges.length; e++){
                                //    TriangleMesh.Edge edge = edges[e];
                                //    Vec3 vec1 = new Vec3(verts[edge.v1].r); // duplicate
                                //    Vec3 vec2 = new Vec3(verts[edge.v2].r);
                                //    System.out.println(" x: " + vec1.x + " y: "+ vec1.y + " z: " + vec1.z  + " ->  " + " x: " + vec2.x + " y: "+ vec2.y + " z: " + vec2.z  );
                                //}
                                
                                TriangleMesh.Face[] faces = triangleMesh.getFaces();
                                
                                //System.out.println("faces: " + faces.length);
                                for(int f = 0; f < faces.length; f++){ //  && running
                                    TriangleMesh.Face face = faces[f];
                                    Vec3 vec1 = new Vec3(verts[face.v1].r); // duplicate
                                    Vec3 vec2 = new Vec3(verts[face.v2].r);
                                    Vec3 vec3 = new Vec3(verts[face.v3].r);
                                    
                                    Mat4 mat4 = c.duplicate().fromLocal();
                                    mat4.transform(vec1);
                                    mat4.transform(vec2);
                                    mat4.transform(vec3);
                                    
                                    //  first row of polygons isn't detecting.
                                    
                                    if(inside_trigon(point_loc, vec1, vec2, vec3)){
                                        //double currHeight = Math.max(Math.max(vec1.y, vec2.y), vec3.y);  // TODO get actual height
                                        double currHeight = trigon_height(point_loc, vec1, vec2, vec3);
                                        if(currHeight > height){
                                            height = currHeight;
                                        }
                                        //if(currHeight == 0){
                                        //    System.out.println(" height 0 ");
                                        //}
                                    }
                                    
                                    // Edges of drill bit
                                    Vec3 drill_side_l = new Vec3(x_loc - (drill_bit / 2), 0, z_loc);
                                    Vec3 drill_side_r = new Vec3(x_loc + (drill_bit / 2), 0, z_loc);
                                    Vec3 drill_side_f = new Vec3(x_loc, 0, z_loc - (drill_bit / 2));
                                    Vec3 drill_side_b = new Vec3(x_loc, 0, z_loc + (drill_bit / 2));
                                    
                                    if(inside_trigon(drill_side_l, vec1, vec2, vec3)){
                                        double currHeight = trigon_height(drill_side_l, vec1, vec2, vec3);
                                        if(currHeight > height){
                                            height = currHeight;
                                        }
                                    }
                                    if(inside_trigon(drill_side_r, vec1, vec2, vec3)){
                                        double currHeight = trigon_height(drill_side_r, vec1, vec2, vec3);
                                        if(currHeight > height){
                                            height = currHeight;
                                        }
                                    }
                                    if(inside_trigon(drill_side_f, vec1, vec2, vec3)){
                                        double currHeight = trigon_height(drill_side_f, vec1, vec2, vec3);
                                        if(currHeight > height){
                                            height = currHeight;
                                        }
                                    }
                                    if(inside_trigon(drill_side_b, vec1, vec2, vec3)){
                                        double currHeight = trigon_height(drill_side_b, vec1, vec2, vec3);
                                        if(currHeight > height){
                                            height = currHeight;
                                        }
                                    }
                                    
                                    //System.out.println(" x: " + vec1.x + " y: "+ vec1.y + " z: " + vec1.z);
                                }
                            }
                        }
                    }
                    mapHeights[x][z] = height;
                }
            }
            
            // Write mapHeights to GCode file.
            // todo...
            String gcode = "";
            gcode += "; Arma Automotive\n";
            gcode += "; CNC Top Down Mill\n";
            
            gcode += "G1\n";
            
            int prev_x = 0;
            int prev_z = 0;
            
            for(int x = 0; x <= mapWidth; x++){
                for(int z = 0; z <= mapDepth; z++){
                    double prev_x_loc = this.minx + (prev_x * drill_bit);
                    double prev_z_loc = this.minz + (prev_z * drill_bit);
                    double prev_height = mapHeights[prev_x][prev_z];
                    
                    double x_loc = this.minx + (x * drill_bit);
                    double z_loc = this.minz + (z * drill_bit);
                    double height = mapHeights[x][z];
                    //System.out.println(" map   x: " + x_loc + " z: " +z_loc  + " h: "  +height );
                    
                    // Move up to height of next point before moving cutter or the corner will be cut.
                    if( z != 1 && !(z == 0 && z == 0) ){
                        /*
                        gcode += "G1 X" + roundThree(prev_x_loc) +
                        " Y" + roundThree(prev_z_loc) +
                        " Z" + roundThree(height - material_height);
                        gcode += " F"+10+"";
                        gcode += ";  A pre rise  \n"; //
                        */
                        
                        gcode += "G1 X" + roundThree(prev_x_loc - this.minx) +
                        " Y" + roundThree(prev_z_loc - this.minz) +
                        " Z" + roundThree( (prev_height - material_height)  ); //  // - this.maxy
                        gcode += " F"+10+"";
                        gcode += ";  A pre rise  \n"; //
                        
                        Vec3 markupPoint = new Vec3(prev_x_loc, prev_height , prev_z_loc);
                        //toolpathMarkupPoints.addElement(markupPoint);
                        
                    } else if(z == 1) {    // end on z line
                        
                        // Raise
                        gcode += "G1 " +
                        " Z" + roundThree(0.0);
                        gcode += " F"+10+"";
                        gcode += "; end line  \n"; // End line
                        
                        gcode += "G1 X" + roundThree(prev_x_loc - this.minx) +
                        " Y" + roundThree(prev_z_loc - this.minz) +
                        " Z" + roundThree(0);
                        gcode += " F"+10+"";
                        gcode += "; C   \n"; // End line
                        
                        Vec3 markupPoint = new Vec3(prev_x_loc, prev_height + material_height, prev_z_loc);
                        //toolpathMarkupPoints.addElement(markupPoint);
                        
                        markupPoint = new Vec3(x_loc, prev_height + material_height, z_loc);
                        //toolpathMarkupPoints.addElement(markupPoint);
                        
                    } else if(x == 0 && z == 0) { // Should only occur once.???
                     
                        gcode += "G1 X" + roundThree(prev_x_loc - this.minx) +
                        " Y" + roundThree(prev_z_loc - this.minz) +
                        " Z" + roundThree(0);
                        gcode += " F"+10+"";
                        gcode += "; D \n";
                        
                        Vec3 markupPoint = new Vec3(prev_x_loc, prev_height  , prev_z_loc);
                        //toolpathMarkupPoints.addElement(markupPoint);
                    }
                    
                    
                    if( z == 0 ){
                        Vec3 markupPoint = new Vec3(x_loc, prev_height + material_height, z_loc);
                        toolpathMarkupPoints.addElement(markupPoint);
                    }
                    
                    Vec3 markupPoint = new Vec3(x_loc, prev_height , z_loc);
                    toolpathMarkupPoints.addElement(markupPoint);
                    
                    if( z == mapDepth ){
                        
                        markupPoint = new Vec3(x_loc, prev_height + material_height, z_loc);
                        toolpathMarkupPoints.addElement(markupPoint);
                    }
                    
                    
                    // todo: if x < mapWidth && height == next X height skip. Compression of gcode
                    //if( x < mapWidth &&  ){
                    // GCode coordinates are different.
                    /*
                    if( (prev_height - material_height) != (height - material_height) ){
                        gcode += "G1 X" + roundThree(prev_x_loc - this.minx) +
                        " Y" + roundThree(prev_z_loc - this.minz) +
                        " Z" + roundThree(prev_height - material_height);
                        gcode += " F"+10+"";
                        gcode += ";\n"; // End line
                    }
                     */
                    //}
                    prev_x = x;
                    prev_z = z;
                }
                
                // Raise
                //gcode += "G1 " +
                //" Z" + roundThree(0.0);
                //gcode += " F"+10+"";
                //gcode += ";\n"; // End line
            }
            
            // String dir = scene.getDirectory() + System.getProperty("file.separator") + scene.getName() + "_gCode3d";
            try {
                String gcodeFile = dir + System.getProperty("file.separator") + "mill_" + s + ".gcode";
                //gcodeFile += ".gcode";
                System.out.println("Writing g code file: " + gcodeFile);
                PrintWriter writer2 = new PrintWriter(gcodeFile, "UTF-8");
                writer2.println(gcode);
                writer2.close();
            } catch (Exception e){
                System.out.println("Error: " + e.toString());
            }
            
        }
        
        
        System.out.println(" window " + window);
        if(window != null && toolpathMarkup){
            
            //toolpathMarkupPoints
            Vec3[] pathPoints = new Vec3[toolpathMarkupPoints.size()];
            float[] s = new float[toolpathMarkupPoints.size()];
            for(int p = 0; p < toolpathMarkupPoints.size(); p++){
                pathPoints[p] = (Vec3)toolpathMarkupPoints.elementAt(p);
                s[p] = 0;
            }
            
            Curve toolPathMarkup = new Curve(pathPoints, s, 0, false); // Vec3 v[], float smoothness[], int smoothingMethod, boolean isClosed
            
            CoordinateSystem coords = new CoordinateSystem(new Vec3(), Vec3.vz(), Vec3.vy());
            window.addObject(toolPathMarkup, coords, "Path Object ", null);
            window.setSelection(window.getScene().getNumObjects()-1);
            window.setUndoRecord(new UndoRecord(window, false, UndoRecord.DELETE_OBJECT, new Object [] {new Integer(window.getScene().getNumObjects()-1)}));
            window.updateImage();
            
        }
        
    }
    
    
    /**
     * trigon_height
     *
     * Description:
     */
    double trigon_height(Vec3 s, Vec3 a, Vec3 b, Vec3 c){
        double height = 0;
        double aDistance = Math.sqrt(Math.pow(s.x - a.x, 2) + Math.pow(s.y - a.y, 2) + Math.pow(s.z - a.z, 2));
        double bDistance = Math.sqrt(Math.pow(s.x - b.x, 2) + Math.pow(s.y - b.y, 2) + Math.pow(s.z - b.z, 2));
        double cDistance = Math.sqrt(Math.pow(s.x - c.x, 2) + Math.pow(s.y - c.y, 2) + Math.pow(s.z - c.z, 2));
        
        //System.out.println(" aDistance " + aDistance + " " + bDistance + " " + cDistance );
        
        double wv1 = 1.0/aDistance;
        double wv2 = 1.0/bDistance;
        double wv3 = 1.0/cDistance;
        
        //double wv1_ = ()
        
        height = (
                  ( wv1 * a.y)  +
                  ( wv2 * b.y)  +
                  ( wv3 * c.y)
                 )
                 /
                 (wv1 + wv2 + wv3);
        
        return height;
    }
    
    /**
     * inside_trigon
     *
     * Description: determine if a point lays with the bounds of a triangle horizontally.
     */
    boolean inside_trigon(Vec3 s, Vec3 a, Vec3 b, Vec3 c)
    {
        double as_x = s.x-a.x;
        double as_z = s.z-a.z;
        boolean s_ab = (b.x-a.x)*as_z-(b.z-a.z)*as_x > 0;
        if((c.x-a.x)*as_z-(c.z-a.z)*as_x > 0 == s_ab) return false;
        if((c.x-b.x)*(s.z-b.z)-(c.z-b.z)*(s.x-b.x) > 0 != s_ab) return false;
        return true;
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
                Object3D o3d = obj.getObject().duplicate();
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
    
    /**
     * calculateSectionBounds
     *
     * Description:
     */
    public BoundingBox calculateSectionBounds(Vector<ObjectInfo> objects, double top, double bot){
        LayoutModeling layout = new LayoutModeling();
        
        BoundingBox bounds = new BoundingBox(0,0,0,0,0,0);
        bounds.minx = 99999;
        bounds.miny = 99999;
        bounds.minz = 99999;
        bounds.maxx = -999999;
        bounds.maxy = -999999;
        bounds.maxz = -999999;
        
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
                Object3D o3d = obj.getObject().duplicate();
                BoundingBox b = o3d.getBounds();
                
                // Include object location in bounds values.
                CoordinateSystem c;
                c = layout.getCoords(obj);
                Vec3 objOrigin = c.getOrigin();
                b.minx += objOrigin.x; b.maxx += objOrigin.x;
                b.miny += objOrigin.y; b.maxy += objOrigin.y;
                b.minz += objOrigin.z; b.maxz += objOrigin.z;
                
                //System.out.println("  " + bounds.minx + " " + bounds.maxx );
                if(bounds.minx < bounds.minx){
                    bounds.minx = bounds.minx;
                }
                if(bounds.maxx > bounds.maxx){
                    bounds.maxx = bounds.maxx;
                }
                if(bounds.miny < bounds.miny){
                    bounds.miny = bounds.miny;
                }
                if(bounds.maxy > bounds.maxy){
                    bounds.maxy = bounds.maxy;
                }
                if(bounds.minz < bounds.minz){
                    bounds.minz = bounds.minz;
                }
                if(bounds.maxz > bounds.maxz){
                    bounds.maxz = bounds.maxz;
                }
            }
        }
        return bounds;
    }
    
    String roundThree(double x){
        //double rounded = ((double)Math.round(x * 100000) / 100000);
        
        DecimalFormat df = new DecimalFormat("#");
        df.setMaximumFractionDigits(3);
        
        return df.format(x);
    }
    
}

