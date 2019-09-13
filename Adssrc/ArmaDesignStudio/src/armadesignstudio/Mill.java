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
import java.awt.BorderLayout;
import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.JFrame;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
    private double accuracy = 0.125; // 0.0393701; // 0.019685 = 0.5mm,  0.03125; // 1/32"   grid point length quanta_length
    private double drill_bit = 0.125;   // 0.125 1/8th 3.175mm
    private double drill_bit_angle = 135;
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
        widthLabel.setBounds(0, 0, 130, 40); // x, y, width, height
        panel.add(widthLabel);
        
        JTextField widthField = new JTextField(new String(width+""));
        widthField.setBounds(130, 0, 130, 40); // x, y, width, height
        panel.add(widthField);
        //widthField.getDocument().addDocumentListener(myListener);
        
        JLabel labelDepth = new JLabel("Bed Depth");
        //labelHeight.setForeground(new Color(255, 255, 0));
        labelDepth.setHorizontalAlignment(SwingConstants.CENTER);
        labelDepth.setFont(new Font("Arial", Font.BOLD, 11));
        labelDepth.setBounds(0, 40, 130, 40); // x, y, width, height
        panel.add(labelDepth);
        
        JTextField depthtField = new JTextField( new String(depth+""));
        depthtField.setBounds(130, 40, 130, 40); // x, y, width, height
        panel.add(depthtField);
        
        JLabel labelHeight = new JLabel("Material Height");
        //labelHeight.setForeground(new Color(255, 255, 0));
        labelHeight.setHorizontalAlignment(SwingConstants.CENTER);
        labelHeight.setFont(new Font("Arial", Font.BOLD, 11));
        labelHeight.setBounds(0, 80, 130, 40); // x, y, width, height
        panel.add(labelHeight);
        
        JTextField heightField = new JTextField(new String(material_height+""));
        heightField.setBounds(130, 80, 130, 40); // x, y, width, height
        panel.add(heightField);
        
        
        // quanta_length
        JLabel labelAccuracy = new JLabel("Accracy");
        //labelHeight.setForeground(new Color(255, 255, 0));
        labelAccuracy.setHorizontalAlignment(SwingConstants.CENTER);
        labelAccuracy.setFont(new Font("Arial", Font.BOLD, 11));
        labelAccuracy.setBounds(0, 120, 130, 40); // x, y, width, height
        panel.add(labelAccuracy);
        
        JTextField accuracyField = new JTextField(new String(accuracy + ""));
        accuracyField.setBounds(130, 120, 130, 40); // x, y, width, height
        panel.add(accuracyField);
        
    
        JLabel labelBit = new JLabel("Drill Bit Diameter");
        //labelHeight.setForeground(new Color(255, 255, 0));
        labelBit.setHorizontalAlignment(SwingConstants.CENTER);
        labelBit.setFont(new Font("Arial", Font.BOLD, 11));
        labelBit.setBounds(0, 160, 130, 40); // x, y, width, height
        panel.add(labelBit);
        
        JTextField bitField = new JTextField("0.125");
        bitField.setBounds(130, 160, 130, 40); // x, y, width, height
        panel.add(bitField);
        
        JLabel labelBitAngle = new JLabel("Drill Bit Angle");
        //labelHeight.setForeground(new Color(255, 255, 0));
        labelBitAngle.setHorizontalAlignment(SwingConstants.CENTER);
        labelBitAngle.setFont(new Font("Arial", Font.BOLD, 11));
        labelBitAngle.setBounds(0, 200, 130, 40); // x, y, width, height
        panel.add(labelBitAngle);
        
        JTextField bitAngleField = new JTextField( new String(drill_bit_angle+""));
        bitAngleField.setBounds(130, 200, 130, 40); // x, y, width, height
        panel.add(bitAngleField);
        
        
        // Debug feature.
        JLabel pathBit = new JLabel("Tool Path Markup");
        //labelHeight.setForeground(new Color(255, 255, 0));
        pathBit.setHorizontalAlignment(SwingConstants.CENTER);
        pathBit.setFont(new Font("Arial", Font.BOLD, 11));
        pathBit.setBounds(0, 240, 130, 40); // x, y, width, height
        panel.add(pathBit);
        
        JCheckBox toolpathCheck = new JCheckBox("");
        toolpathCheck.setBounds(130, 240, 130, 40); // x, y, width, height
        toolpathCheck.setSelected(false);
        panel.add(toolpathCheck);
        
        UIManager.put("OptionPane.minimumSize",new Dimension(350, 350));
        int result = JOptionPane.showConfirmDialog(null, panel, "CNC Mill Properties", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            //System.out.println("width value: " + widthField.getText());
            //System.out.println("depth value: " + depthtField.getText());
            //System.out.println("height value: " + heightField.getText());
            //System.out.println("bit value: " + bitField.getText());
            this.width = Integer.parseInt(widthField.getText());
            this.depth = Integer.parseInt(depthtField.getText());
            this.material_height = Double.parseDouble(heightField.getText());
            this.drill_bit = Double.parseDouble(bitField.getText());
            this.accuracy = Double.parseDouble(accuracyField.getText());
            this.drill_bit_angle = Double.parseDouble(bitAngleField.getText());
            this.toolpathMarkup = toolpathCheck.isSelected();
        }
    }
    
    public void progressDialog(){
        //JPanel panel = new JPanel();
        
        //UIManager.put("OptionPane.minimumSize",new Dimension(350, 100));
        
        //JProgressBar progressBar;
        
        //progressMonitor = new ProgressMonitor(ProgressMonitorDemo.this,
        //                                      "Creating Tool Path GCode",
        //                                      "", 0, task.getLengthOfTask());
        
    }
    
    
    public void run(){
        exportGCode();
    }
    
    /**
     * exportGCode
     *
     * Description: Process scene objects creating GCode CNC router cutting path.
     * TODO: slice into seperate files for layers of material. ie 2" foam blocks.
     */
    public void exportGCode(){
        System.out.println("Export 3D Mill GCode.");
        
        final JDialog progressDialog = new JDialog(); //  parentFrame , "Progress Dialog", true ); // parentFrame , "Progress Dialog", true); // Frame owner
        JProgressBar dpb = new JProgressBar(0, 100);
        progressDialog.add(BorderLayout.CENTER, dpb);
        progressDialog.add(BorderLayout.NORTH, new JLabel("Progress..."));
        progressDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        progressDialog.setSize(300, 75);
        progressDialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
       // progressDialog.setLocationRelativeTo(parentFrame);
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        progressDialog.setLocation((int)(screenSize.getWidth() / 2) - (300/2), (int) ((screenSize.getHeight()/(float)2) - ((float)75/(float)2.0)));
        
        progressDialog.addWindowListener(new WindowAdapter()
        {
            public void windowClosed(WindowEvent e)
            {
                System.out.println("jdialog window closed event received");
                running = false;
            }
            
            public void windowClosing(WindowEvent e)
            {
                System.out.println("jdialog window closing event received");
                running = false;
            }
        });
        
        progressDialog.setVisible(true);
        
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
        int mapWidth = (int)((this.maxx - this.minx) / accuracy) + 0; // drill_bit
        int mapDepth = (int)((this.maxz - this.minz) / accuracy) + 0; // drill_bit
        
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
        
        boolean tooWide = this.maxx - this.minx > width;
        boolean tooDeep = this.maxz - this.minz >  depth;
        if(tooWide || tooDeep){
            progressDialog.setVisible(false);
            
            String warning = "";
            if(tooWide && !tooDeep){
                warning = "The scene is too wide to fit in the cutting area. \n";
            } else if(!tooWide && tooDeep){
                warning = "The scene is too deep to fit in the cutting area. \n";
            } else if(tooWide && tooDeep) {
                warning = "The scene is too wide and too deep to fit in the cutting area. \n";
            }
            warning += "Do you wish to continue?";
            
            int n = JOptionPane.showConfirmDialog(
                                                  null,
                                                  warning,
                                                  "Warning",
                                                  JOptionPane.YES_NO_OPTION);
            System.out.println("" + n);
            if(n == 1){
                return;
            }
            
            progressDialog.setVisible(true);
        }
        
        // this.minx this.minz
        
        Double[][] cutHeights = new Double[mapWidth + 1][mapDepth + 1]; // state of machined material. Used to ensure not too deep a pass is made.
        Double[][] mapHeights = new Double[mapWidth + 1][mapDepth + 1]; // Object top surface
        
        
        for(int x = 0; x < mapWidth + 1; x++){
            for(int z = 0; z < mapDepth + 1; z++){
                mapHeights[x][z] = miny;
            }
        }
        
        //System.out.println(" map  x: " + mapWidth + " z: " + mapDepth);
        
        //
        // Calculate mapHeights[x][z] given scene mesh objects
        //
        for(int x = 0; x < mapWidth + 1; x++){
            for(int z = 0; z < mapDepth + 1; z++){
                double x_loc = this.minx + (x * accuracy);
                double z_loc = this.minz + (z * accuracy);
                Vec3 point_loc = new Vec3(x_loc, 0, z_loc);
                double height = this.miny;
                cutHeights[x][z] = material_height; // initalize material state.
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
                        // add obj location to bounds local coordinates.
                        bounds.minx += objOrigin.x;
                        bounds.maxx += objOrigin.x;
                        bounds.miny += objOrigin.y;
                        bounds.maxy += objOrigin.y;
                        bounds.minz += objOrigin.z;
                        bounds.maxz += objOrigin.z;
                        
                        //System.out.println(" x " + bounds.minx + "-" + bounds.maxx + "    loc " + objOrigin.x);
                        
                        if(
                           (x_loc >= bounds.minx && x_loc <= bounds.maxx && z_loc >= bounds.minz && z_loc <= bounds.maxz) // optimization, within x,z region space
                           && (bounds.maxy > height) // this object must have the possibility of raising/changing the mill height.
                           && obj.getObject().canConvertToTriangleMesh() != Object3D.CANT_CONVERT
                           ){
                            
                            //TriangleMesh.Edge[] edges = ((TriangleMesh)triangleMesh).getEdges();
                            
                            int progress = (int) ((((float)(x * mapDepth) + z) / (float)(mapWidth * mapDepth)) * (float)100);
                            //System.out.println(" % " + progress  );
                            dpb.setValue(progress);
                            
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
                                
                                // DEBUG
                                //if(height == 0 && inside_trion2(point_loc, vec1, vec2, vec3)){
                                
                                //    double currHeight = Math.max(Math.max(vec1.y, vec2.y), vec3.y);
                                //    if(currHeight > height){
                                //height = currHeight;
                                //    }
                                //}
                                
                                
                                // Edges of drill bit
                                double edgeHeightOffset = (drill_bit / 2) * Math.tan( Math.toRadians((90 - (drill_bit_angle / 2))) );
                                //System.out.println("edgeHeightOffset: "+ edgeHeightOffset);
                                // (90 - (drill_bit_angle / 2))     22.5
                                
                                Vec3 drill_side_l = new Vec3(x_loc - (drill_bit / 2), edgeHeightOffset, z_loc);
                                Vec3 drill_side_r = new Vec3(x_loc + (drill_bit / 2), edgeHeightOffset, z_loc);
                                Vec3 drill_side_f = new Vec3(x_loc, edgeHeightOffset, z_loc - (drill_bit / 2));
                                Vec3 drill_side_b = new Vec3(x_loc, edgeHeightOffset, z_loc + (drill_bit / 2));
                                
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
                                
                                //if(height > sectionTop){
                                //    height =
                                //}
                                
                                //System.out.println(" x: " + vec1.x + " y: "+ vec1.y + " z: " + vec1.z);
                            }
                        }
                    }
                }
                mapHeights[x][z] = height;
            }
        }
        
        //
        // Route drill cutting path
        //
        for(int s = 0; s < sections; s++){ // height sections
            double sectionBottom = this.miny + ((s) * material_height);
            double sectionTop = this.miny + ((s+1) * material_height);
            
            Vector toolpathMarkupPoints = new Vector();
            //BoundingBox sectionBounds = o3d.getBounds();
            
            //System.out.println("  bot: " +  sectionBottom + " top: " +  sectionTop + "  s: " + s);
            
            //
            // Write mapHeights to GCode file.
            //
            String gcode = "";
            gcode += "; Arma Automotive\n";
            gcode += "; CNC Top Down Mill\n";
            
            gcode += "G1\n";
            
            int prev_x = 0;
            int prev_z = 0;
            
            int adjacentDepth = 0;
            
            // Prescan X rows and record Z-depth of each row in order to skip cutting of adjacent rows
            int [] XDepth = new int[mapWidth + 1];
            for(int x = 0; x <= mapWidth; x++){
                int depth = 0;
                for(int z = mapDepth; z >= 0; z--){
                    double x_loc = this.minx + (x * accuracy);
                    double z_loc = this.minz + (z * accuracy);
                    double height = mapHeights[x][z];
                    if(height > sectionBottom){
                        depth = z;
                        z = -1; // Break
                    }
                }
                XDepth[x] = depth;
            }
            
            for(int x = 0; x <= mapWidth; x++){
                
                // Optimization, skip z line if no objects in path.
                // TODO: Still need adjacent
                int prevZLength = 0;
                int nextZLength = 0;
                boolean adjacentX = false;
                
                boolean skipZ = true;
                for(int z = 0; z <= mapDepth; z++){
                    double height = mapHeights[x][z];
                    if( height > sectionBottom ){
                        skipZ = false;
                    }
                }
                if(x > 0 && x < mapWidth && skipZ){ // check previous X
                    for(int z = 0; z <= mapDepth; z++){
                        double height = mapHeights[x - 1][z];
                        if( height > sectionBottom ){
                            skipZ = false;
                        }
                    }
                    for(int z = 0; z <= mapDepth; z++){
                        double height = mapHeights[x + 1][z];
                        if( height > sectionBottom ){
                            skipZ = false;
                        }
                    }
                }
                
                
                for(int z = 0; z <= mapDepth && skipZ == false; z++){
                    //adjacentDepth = z;
                    double prev_x_loc = this.minx + (prev_x * accuracy); // DEPRICATE
                    double prev_z_loc = this.minz + (prev_z * accuracy);
                    
                    //System.out.println(" x: " + x + "  z: " + z + " "  );
                    //System.out.println(" prev_x: " + prev_x + "  prev_z: " + prev_z + " "  );
                    double prev_height = miny; //  mapHeights[prev_x][prev_z];
                    
                    double x_loc = this.minx + (x * accuracy);
                    double z_loc = this.minz + (z * accuracy);
                    
                    double height = mapHeights[x][z];
                    
                    int next_x = x;
                    int next_z = z + 1;
                    if(next_z >= mapDepth){
                        next_x = next_x + 1;
                        next_z = 0;
                    }
                    double next_x_loc = this.minx + (next_x * accuracy);
                    double next_z_loc = this.minz + (next_z * accuracy);
                    double next_height = 0;
                    if( next_x < mapWidth + 1 && next_z < mapDepth + 1 ){
                        next_height = mapHeights[next_x][next_z];
                    }
                    
                    // Height section bounds.
                    if(height < sectionBottom){
                        height = sectionBottom;
                    }
                    if(height > sectionTop){
                        height = sectionTop;
                    }
                    if(next_height < sectionBottom){
                        next_height = sectionBottom;
                    }
                    if(next_height > sectionTop){
                        next_height = sectionTop;
                    }
                    
                    
                    //
                    // Toolpath Markup
                    //
                    if( z == 0 ){ // Start of row, drop from top pass
                        Vec3 markupPoint = new Vec3(x_loc, sectionTop, z_loc);
                        toolpathMarkupPoints.addElement(markupPoint);
                    }
                    
                    
                    Vec3 markupPoint = new Vec3(x_loc, height, z_loc);
                    toolpathMarkupPoints.addElement(markupPoint);
                    
                    
                    // Skip remaining Z path if no cuts. optimization.
                    boolean skip = false;
                    if(z > 6){
                        skip = true;
                    }
                    for(int zz = z - 6; zz > 0 && zz <= mapDepth; zz++){
                        double seek_height = mapHeights[x][zz];
                        if( seek_height > sectionBottom ){
                            skip = false;
                        }
                    }
                    int prevXDepth = 0;
                    int nextXDepth = 0;
                    if(x > 0){
                        prevXDepth = XDepth[x - 1];
                    }
                    if(x < mapWidth){
                        nextXDepth = XDepth[x + 1];
                    }
                    
                    if( prevXDepth + 6 > z || nextXDepth + 6 > z ){ // Force this Z row because it is adjacent
                        skip = false;
                    }
                    if(skip){
                        adjacentDepth = z; // Track how far this X row proceded down the Z axis.
                        prev_z = z;
                        z = mapDepth + 1; // Skip Z row
                        markupPoint = new Vec3(x_loc, sectionTop, z_loc); // Up ready for next X row.
                        toolpathMarkupPoints.addElement(markupPoint);
                        break;
                    }
                    
                    
                    // If next point is higher than current point, rise up in current location. Prevents cutting corners
                    if(next_height > height){
                        markupPoint = new Vec3(x_loc, next_height, z_loc);
                        toolpathMarkupPoints.addElement(markupPoint);
                        //System.out.println(" RISE " + x_loc + " " + z_loc + " next_height: " + next_height);
                    }
                    
                    
                    // If next point is lower than the current point, move over one bit width before moving down cutting the corner.
                    if(next_height < height && z > 0 && z < mapDepth ){
                        markupPoint = new Vec3(x_loc, height, next_z_loc);
                        toolpathMarkupPoints.addElement(markupPoint);
                    }
                    
                    
                    if(z == mapDepth){ // End of row, rise to pass back for next row.
                        //markupPoint = new Vec3(x_loc, height + material_height, z_loc);
                        markupPoint = new Vec3(x_loc, sectionTop, z_loc);
                        toolpathMarkupPoints.addElement(markupPoint);
                    }
                    
                    
                    
                    //System.out.println(" map   x: " + x_loc + " z: " +z_loc  + " h: "  +height );
                    
                    // Move up to height of next point before moving cutter or the corner will be cut.
                    if( z != 1 && !(z == 0 && z == 0) ){
                        
                        
                        gcode += "G1 X" + roundThree(prev_x_loc - this.minx) +
                        " Y" + roundThree(prev_z_loc - this.minz) +
                        " Z" + roundThree( (prev_height - material_height)  ); //  // - this.maxy
                        gcode += " F"+10+"";
                        gcode += ";  A pre rise  \n"; //
                        
                         markupPoint = new Vec3(prev_x_loc, prev_height , prev_z_loc);
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
                        
                         markupPoint = new Vec3(prev_x_loc, prev_height + material_height, prev_z_loc);
                        //toolpathMarkupPoints.addElement(markupPoint);
                        
                        markupPoint = new Vec3(x_loc, prev_height + material_height, z_loc);
                        //toolpathMarkupPoints.addElement(markupPoint);
                        
                    } else if(x == 0 && z == 0) { // Should only occur once.???
                     
                        gcode += "G1 X" + roundThree(prev_x_loc - this.minx) +
                        " Y" + roundThree(prev_z_loc - this.minz) +
                        " Z" + roundThree(0);
                        gcode += " F"+10+"";
                        gcode += "; D \n";
                        
                         markupPoint = new Vec3(prev_x_loc, prev_height  , prev_z_loc);
                        //toolpathMarkupPoints.addElement(markupPoint);
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
            
            
            // Debug toolpath markup
            if(window != null && toolpathMarkup){
                Vec3[] pathPoints = new Vec3[toolpathMarkupPoints.size()];
                float[] s_ = new float[toolpathMarkupPoints.size()];
                for(int p = 0; p < toolpathMarkupPoints.size(); p++){
                    pathPoints[p] = (Vec3)toolpathMarkupPoints.elementAt(p);
                    s_[p] = 0;
                }
                Curve toolPathMarkup = new Curve(pathPoints, s_, 0, false); // Vec3 v[], float smoothness[], int smoothingMethod, boolean isClosed
                CoordinateSystem coords = new CoordinateSystem(new Vec3(), Vec3.vz(), Vec3.vy());
                window.addObject(toolPathMarkup, coords, "Cut Tool Path " + s, null);
                window.setSelection(window.getScene().getNumObjects()-1);
                window.setUndoRecord(new UndoRecord(window, false, UndoRecord.DELETE_OBJECT, new Object [] {new Integer(window.getScene().getNumObjects()-1)}));
                window.updateImage();
            }
            
            
        } // sections
     
        progressDialog.setVisible(false);
    }
    
    
    /**
     * calcNormal
     *
     * Description: Calculate the normal vector for a three point face.
     */
    private Vec3 calcNormal(Vec3 v0, Vec3 v1, Vec3 v2) {
        Vec3 s1 = new Vec3( v1.x - v0.x, v1.y - v0.y, v1.z - v0.z ); // subtract
        Vec3 s2 = new Vec3( v2.x - v0.x, v2.y - v0.y, v2.z - v0.z ); // subtract
        Vec3 nv = new Vec3(s1.y * s2.z - s1.z*s2.y,
                           s1.z*s2.x - s1.x*s2.z,
                           s1.x*s2.y - s1.y*s2.x); // cross product
        float length = (float) Math.sqrt(nv.x * nv.x + nv.y * nv.y + nv.z * nv.z);
        nv.x /= length;
        nv.y /= length;
        nv.z /= length;
        return nv;
    }
    
    /**
     * trigon_height
     *
     * Description: calculate height on surface of polygon a,b,c given point x,z (s).
     *
     */
    double trigon_height(Vec3 s, Vec3 a, Vec3 b, Vec3 c){
        double height = 0;
        /*
        double aDistance = Math.sqrt(Math.pow(s.x - a.x, 2) + Math.pow(s.y - a.y, 2) + Math.pow(s.z - a.z, 2));
        double bDistance = Math.sqrt(Math.pow(s.x - b.x, 2) + Math.pow(s.y - b.y, 2) + Math.pow(s.z - b.z, 2));
        double cDistance = Math.sqrt(Math.pow(s.x - c.x, 2) + Math.pow(s.y - c.y, 2) + Math.pow(s.z - c.z, 2));
        double wv1 = 1.0/aDistance;
        double wv2 = 1.0/bDistance;
        double wv3 = 1.0/cDistance;
        height = (
                  ( wv1 * a.y)  +
                  ( wv2 * b.y)  +
                  ( wv3 * c.y)
                  )
        /
        (wv1 + wv2 + wv3);
        */
        Vec3 planeNormal = calcNormal(a, b, c);
        //System.out.println(" normal  x: " +  planeNormal.x + " y: " + planeNormal.y + " z: " + planeNormal.z  );
        Vec3 intersect = intersectPoint(new Vec3(0,1,0), s, planeNormal, a);
            //System.out.println(" intersect x " + intersect.x + " y " + intersect.y + " z" + intersect.z + " = " + height );
        height = intersect.y;
        return height;
    }
    
    
    /**
     * intersectPoint
     *
     * Description:
     */
    private static Vec3 intersectPoint(Vec3 rayVector, Vec3 rayPoint, Vec3 planeNormal, Vec3 planePoint) {
        //Vec3D diff = rayPoint.minus(planePoint);
        // new Vector3D(x - v.x, y - v.y, z - v.z);
        Vec3 diff = new Vec3(rayPoint.x - planePoint.x,  rayPoint.y - planePoint.y, rayPoint.z - planePoint.z);
        //double prod1 = diff.dot(planeNormal);
        double prod1 = diff.x * planeNormal.x + diff.y * planeNormal.y + diff.z * planeNormal.z;  //  x * v.x + y * v.y + z * v.z;
        //double prod2 = rayVector.dot(planeNormal);
        double prod2 = rayVector.x * planeNormal.x + rayVector.y * planeNormal.y + rayVector.z * planeNormal.z;
        double prod3 = prod1 / prod2;
        //return rayPoint.minus(rayVector.times(prod3));
        Vec3 t = new Vec3(rayVector.x * prod3, rayVector.y * prod3, rayVector.z * prod3);
        return new Vec3( rayPoint.x - t.x, rayPoint.y - t.y, rayPoint.z - t.z );
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
    
    // debug
    boolean inside_trion2(Vec3 s, Vec3 a, Vec3 b, Vec3 c){
        BoundingBox boundingBox = new BoundingBox(0,0,0,0,0,0);
        boundingBox.minx = 99999;
        boundingBox.maxx = -999999;
        boundingBox.miny = 99999;
        boundingBox.maxy = -999999;
        boundingBox.minz = 99999;
        boundingBox.maxz = -999999;
        
        if(a.x < boundingBox.minx){
            boundingBox.minx = a.x;
        }
        if(b.x < boundingBox.minx){
            boundingBox.minx = b.x;
        }
        if(c.x < boundingBox.minx){
            boundingBox.minx = c.x;
        }
        
        if(a.x > boundingBox.maxx){
            boundingBox.maxx = a.x;
        }
        if(b.x > boundingBox.maxx){
            boundingBox.maxx = b.x;
        }
        if(c.x > boundingBox.maxx){
            boundingBox.maxx = c.x;
        }
        
        
        if(a.y < boundingBox.miny){
            boundingBox.miny = a.y;
        }
        if(b.y < boundingBox.miny){
            boundingBox.miny = b.y;
        }
        if(c.y < boundingBox.miny){
            boundingBox.minx = c.y;
        }
        
        if(a.y > boundingBox.maxy){
            boundingBox.maxy = a.y;
        }
        if(b.y > boundingBox.maxy){
            boundingBox.maxy = b.y;
        }
        if(c.y > boundingBox.maxy){
            boundingBox.maxy = c.y;
        }
        
        if(a.z < boundingBox.minz){
            boundingBox.minz = a.z;
        }
        if(b.z < boundingBox.minz){
            boundingBox.minz = b.z;
        }
        if(c.z < boundingBox.minz){
            boundingBox.minz = c.z;
        }
        
        if(a.z > boundingBox.maxz){
            boundingBox.maxz = a.z;
        }
        if(b.z > boundingBox.maxz){
            boundingBox.maxz = b.z;
        }
        if(c.z > boundingBox.maxz){
            boundingBox.maxz = c.z;
        }
        
        if( s.x >= boundingBox.minx && s.x <= boundingBox.maxx
           && s.z >= boundingBox.minz && s.z <= boundingBox.maxz){
            
            return true;
        }
        
        return false;
    }
    
    /**
     * calculateBounds
     *
     * Description: calculate region to simulate flow to be a
     *  relative size larger than the bounds of scene objects.
     *
     * Object bounds doesn't work because of translations. Need translated geometry point boundary.
     */
    public void calculateBounds(Vector<ObjectInfo> objects){
        LayoutModeling layout = new LayoutModeling();
        // Calculate bounds
        for (ObjectInfo obj : objects){
            if(obj.getName().indexOf("Camera") < 0 &&
               obj.getName().indexOf("Light") < 0 &&
               obj.getName().equals("") == false &&
               obj.isVisible()
               ){ //obj.selected == true  || selection == false
                //System.out.println("Object Info: ");
                Object3D co = (Object3D)obj.getObject();
                //System.out.println("obj " + obj.getId() + "  " + obj.getName() );
                
                // obj.getObject(); // Object3D
                Object3D o3d = obj.getObject().duplicate();
                BoundingBox bounds = o3d.getBounds();           // THIS DOES NOT WORK
                
                bounds.minx = 999; bounds.maxx = -999;
                bounds.miny = 999; bounds.maxy = -999;
                bounds.minz = 999; bounds.maxz = -999;
                
                CoordinateSystem c;
                c = layout.getCoords(obj);
                Vec3 objOrigin = c.getOrigin();
                
                if(obj.getObject().canConvertToTriangleMesh() != Object3D.CANT_CONVERT){
                    TriangleMesh triangleMesh = null;
                    triangleMesh = obj.getObject().convertToTriangleMesh(0.0);
                    
                    MeshVertex[] points = triangleMesh.getVertices();
                    for(int i = 0; i < points.length; i++){
                        Vec3 point = points[i].r;
                        
                        Mat4 mat4 = c.duplicate().fromLocal();
                        mat4.transform(point);
                        
                        if(point.x < this.minx){
                            this.minx = point.x;
                        }
                        if(point.x > this.maxx){
                            this.maxx = point.x;
                        }
                        if(point.y < this.miny){
                            this.miny = point.y;
                        }
                        if(point.y > this.maxy){
                            this.maxy = point.y;
                        }
                        if(point.z < this.minz){
                            this.minz = point.z;
                        }
                        if(point.z > this.maxz){
                            this.maxz = point.z;
                        }
                        
                    }
                }
                
                
                // Include object location in bounds values.
                /*
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
                 */
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

