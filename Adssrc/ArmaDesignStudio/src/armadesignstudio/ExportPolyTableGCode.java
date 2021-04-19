/**
 * ExportGCode
 *
 * Used for polygon export for Router and plasma tables
 */
package armadesignstudio;

import armadesignstudio.animation.*;
import armadesignstudio.image.*;
import armadesignstudio.material.*;
import armadesignstudio.math.*;
import armadesignstudio.object.*;
import armadesignstudio.texture.*;
import armadesignstudio.ui.*;
import armadesignstudio.util.*;
import java.awt.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.List;
import java.util.zip.*;
import java.beans.*;

import armadesignstudio.object.TriangleMesh.*;
import javax.swing.*; // For JOptionPane

import buoy.event.*;
import buoy.widget.*;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import com.jsevy.jdxf.*;
//import eu.mihosoft.jcsg.*;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

// Rename ExportPolyTableGCode
public class ExportPolyTableGCode {
    
    private boolean isRouter = true;
    private boolean isPlasmaCutter = true;
    private boolean orderBySize = true;
    private boolean marginByNesting = true;
    private double drill_bit = 0.125;   // 0.125 1/8th 3.175mm
    private double material_height = 0.125;
    
    public ExportPolyTableGCode(){
        
    }
    
    
    /**
     * export
     *
     * Description:
     */
    public void export(Scene scene){
        System.out.println("Export GCode");
    }
    
    /**
     * getUserInput
     *
     * Description:
     */
    public boolean getUserInput(){
        JPanel panel = new JPanel();
        //panel.setBackground(new Color(0, 0, 0));
        panel.setSize(new Dimension(390, 32));
        panel.setLayout(null);
        
        int x = 0;
        int y = 0;
        
        /*
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
        */
        
        
        
         
        
        /*
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
         */
        
        // Checkbox Router
        JLabel routerLabel = new JLabel("Router"); //
        //labelHeight.setForeground(new Color(255, 255, 0));
        routerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        routerLabel.setFont(new Font("Arial", Font.BOLD, 11));
        routerLabel.setBounds(0, y, 130, 40); // x, y, width, height
        panel.add(routerLabel);
        
        JCheckBox routerCheck = new JCheckBox("");
        routerCheck.setBounds(130, y, 130, 40); // x, y, width, height
        routerCheck.setSelected( isRouter );
        panel.add(routerCheck);
        y += 40;
        
        
        
        // Checkbox Plasma Cutter
        JLabel plasmaCutterLabel = new JLabel("Plasma Cutter"); //
        //labelHeight.setForeground(new Color(255, 255, 0));
        plasmaCutterLabel.setHorizontalAlignment(SwingConstants.CENTER);
        plasmaCutterLabel.setFont(new Font("Arial", Font.BOLD, 11));
        plasmaCutterLabel.setBounds(0, y, 130, 40); // x, y, width, height
        panel.add(plasmaCutterLabel);
        
        JCheckBox plasmaCutterCheck = new JCheckBox("");
        plasmaCutterCheck.setBounds(130, y, 130, 40); // x, y, width, height
        plasmaCutterCheck.setSelected( isPlasmaCutter );
        panel.add(plasmaCutterCheck);
        y += 40;
        
        
        
        // Auto Nest Objects
        JLabel orderBySizeLabel = new JLabel("Order by object size"); //
        //labelHeight.setForeground(new Color(255, 255, 0));
        orderBySizeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        orderBySizeLabel.setFont(new Font("Arial", Font.BOLD, 11));
        orderBySizeLabel.setBounds(0, y, 130, 40); // x, y, width, height
        panel.add(orderBySizeLabel);
        
        JCheckBox orderBySizeCheck = new JCheckBox("");
        orderBySizeCheck.setBounds(130, y, 130, 40); // x, y, width, height
        orderBySizeCheck.setSelected( orderBySize );
        panel.add(orderBySizeCheck);
        y += 40;
        
        
        JLabel optimizationLabel = new JLabel("Margin Inset/Outset by nesting."); // inset / outset based on within another shape. Allowance Margin
        //labelHeight.setForeground(new Color(255, 255, 0));
        optimizationLabel.setHorizontalAlignment(SwingConstants.CENTER);
        optimizationLabel.setFont(new Font("Arial", Font.BOLD, 11));
        optimizationLabel.setBounds(0, y, 130, 40); // x, y, width, height
        panel.add(optimizationLabel);
        
        JCheckBox optimizationCheck = new JCheckBox("");
        optimizationCheck.setBounds(130, y, 130, 40); // x, y, width, height
        optimizationCheck.setSelected( marginByNesting );
        panel.add(optimizationCheck);
        y += 40;
        
    
        JLabel labelBit = new JLabel("Drill Bit/Plasma Kerf Diameter");
        //labelHeight.setForeground(new Color(255, 255, 0));
        labelBit.setHorizontalAlignment(SwingConstants.CENTER);
        labelBit.setFont(new Font("Arial", Font.BOLD, 11));
        labelBit.setBounds(0, y, 130, 40); // x, y, width, height
        panel.add(labelBit);
        
        JTextField bitField = new JTextField("0.0354331"); // 0.0354331 0.125
        bitField.setBounds(130, y, 130, 40); // x, y, width, height
        panel.add(bitField);
        y += 40;
        
        
        
        JLabel labelHeight = new JLabel("Material Thickness");
        //labelHeight.setForeground(new Color(255, 255, 0));
        labelHeight.setHorizontalAlignment(SwingConstants.CENTER);
        labelHeight.setFont(new Font("Arial", Font.BOLD, 11));
        labelHeight.setBounds(0, y, 130, 40); // x, y, width, height
        panel.add(labelHeight);
        
        JTextField heightField = new JTextField(new String(material_height+""));
        heightField.setBounds(130, y, 130, 40); // x, y, width, height
        panel.add(heightField);
        y += 40;
        
        /*
        JLabel labelBitAngle = new JLabel("Drill Bit Angle");
        //labelHeight.setForeground(new Color(255, 255, 0));
        labelBitAngle.setHorizontalAlignment(SwingConstants.CENTER);
        labelBitAngle.setFont(new Font("Arial", Font.BOLD, 11));
        labelBitAngle.setBounds(0, 200, 130, 40); // x, y, width, height
        panel.add(labelBitAngle);
        
        JTextField bitAngleField = new JTextField( new String(drill_bit_angle+""));
        bitAngleField.setBounds(130, 200, 130, 40); // x, y, width, height
        panel.add(bitAngleField);
        */
        
        /*
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
        */
        
        /*
        JLabel optimizationLabel = new JLabel("Cut Optimization ");
        //labelHeight.setForeground(new Color(255, 255, 0));
        optimizationLabel.setHorizontalAlignment(SwingConstants.CENTER);
        optimizationLabel.setFont(new Font("Arial", Font.BOLD, 11));
        optimizationLabel.setBounds(0, 280, 130, 40); // x, y, width, height
        panel.add(optimizationLabel);
        
        JCheckBox optimizationCheck = new JCheckBox("");
        optimizationCheck.setBounds(130, 280, 130, 40); // x, y, width, height
        optimizationCheck.setSelected( cutOptimization );
        panel.add(optimizationCheck);
        */
        
        /*
        JLabel minimizePassesLabel = new JLabel("Minimize Passes");
        //minimizePassesLabel.setForeground(new Color(255, 255, 0));
        minimizePassesLabel.setHorizontalAlignment(SwingConstants.CENTER);
        minimizePassesLabel.setFont(new Font("Arial", Font.BOLD, 11));
        minimizePassesLabel.setBounds(0, 320, 130, 40); // x, y, width, height
        panel.add(minimizePassesLabel);
        
        JCheckBox minimizePassesCheck = new JCheckBox("");
        minimizePassesCheck.setBounds(130, 320, 130, 40); // x, y, width, height
        minimizePassesCheck.setSelected( minimizePasses );
        panel.add(minimizePassesCheck);
        */
        
        UIManager.put("OptionPane.minimumSize",new Dimension(400, 350 + 80));
        int result = JOptionPane.showConfirmDialog(null, panel, "CNC Mill Properties", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            //System.out.println("width value: " + widthField.getText());
            //System.out.println("depth value: " + depthtField.getText());
            //System.out.println("height value: " + heightField.getText());
            //System.out.println("bit value: " + bitField.getText());
            //this.width = Integer.parseInt(widthField.getText());
            //this.depth = Integer.parseInt(depthtField.getText());
            this.material_height = Double.parseDouble(heightField.getText());
            this.drill_bit = Double.parseDouble(bitField.getText());
            //this.accuracy = Double.parseDouble(accuracyField.getText());
            //this.drill_bit_angle = Double.parseDouble(bitAngleField.getText());
            //this.toolpathMarkup = toolpathCheck.isSelected();
            //this.cutOptimization = optimizationCheck.isSelected();
            //this.minimizePasses = minimizePassesCheck.isSelected();
            return true;
        }
        
        return false;
    }
    
    
    
    
    /**
    * exportGCode
    *
    * Description: Export layout object geometry to gcode files.
    *     Files are created for each group (Object children) with only curve structures one level nested.
    *   -- adding support for mesh structures.
    */
    public void exportGroupGCode(Scene scene){ // Vector<ObjectInfo> objects
        LayoutModeling layout = new LayoutModeling();

        //
        // Detect orientation of curve geometry. (X/Y or X/Z)
        //
        double sceneDepth = 0;
        double sceneHeight = 0;
        double minY_ = 9999;
        double maxY_ = 0;
        double minZ_ = 9999;
        double maxZ_ = 0;
        for (ObjectInfo obj : scene.getObjects()){
            Object co = (Object)obj.getObject();
            boolean enabled = layout.isObjectEnabled(obj);
            if(enabled && co instanceof Curve){
                // Object co = (Object)childClone.getObject();
                BoundingBox bounds = obj.getTranslatedBounds();
                
                if(bounds.miny < minY_){
                    minY_ = bounds.miny;
                }
                if(bounds.maxy > maxY_){
                    maxY_ = bounds.maxy;
                }
                if(bounds.minz < minZ_){
                    minZ_ = bounds.minz;
                }
                if(bounds.maxz > maxZ_){
                    maxZ_ = bounds.maxz;
                }
            }
        }
        sceneDepth = maxZ_ - minZ_;
        sceneHeight = maxY_ - minY_;
        System.out.println(" sceneDepth: " + sceneDepth + " sceneHeight: " + sceneHeight);
        
        boolean frontView = false;
        if(sceneDepth > sceneHeight){ // Top
            
        }
        if(sceneDepth < sceneHeight){ // Front
            frontView = true;
        }
        
        
        
        //layout.setBaseDir(this.getDirectory() + System.getProperty("file.separator") + this.getName() + "_layout_data" );

        //String dir = System.getProperty("user.dir") + System.getProperty("file.separator") + "gcode";
        String dir = scene.getDirectory() + System.getProperty("file.separator") + scene.getName() + "_gCode";
        File d = new File(dir);
        if(d.exists() == false){
            d.mkdir();
        }

        double scale = 1.0;
        try {
            // Read current scale for this project.
            Properties prop = new Properties();
            InputStream input = null;
            OutputStream output = null;

            String dir2 = scene.getDirectory() + System.getProperty("file.separator") + scene.getName() + "_layout_data";
            File d2 = new File(dir2);
            if(d2.exists() == false){
                d2.mkdir();
            }
            dir2 = dir2 + System.getProperty("file.separator") + "scale.properties";
            d2 = new File(dir2);
            if(d2.exists() == false){
                //(works for both Windows and Linux)
                d2.getParentFile().mkdirs();
                d2.createNewFile();
            }

            input = new FileInputStream(dir2);
            // load a properties file
            prop.load(input);

            String v = prop.getProperty("export_scale");
            if(v != null){
                scale = Double.parseDouble(v);
            }
        } catch (Exception e){
            System.out.println("Error " + e);
            e.printStackTrace();
        }

        int groupsWritten = 0;

        for (ObjectInfo obj : scene.getObjects()){
            String name = obj.getName();
            boolean enabled = layout.isObjectEnabled(obj);
            //System.out.println("   - name: " + name);
            ObjectInfo[] children = obj.getChildren();
            if(children.length > 0 && enabled){
                System.out.println("   - Group: " + name + " count: " + children.length);
                try {
                    // Create gcode file for group.
                    String gCodeFile = dir + System.getProperty("file.separator") + name + ".gcode";
                    //PrintWriter writer = new PrintWriter(gCodeFile, "UTF-8");
                    System.out.println("      File: " + gCodeFile);
                    
                    Vector polygons = new Vector();
                    // gcode
                    String gcode = "G1\n";
                    String gcode2 = "G1\n";

                    double minX = 9999;
                    double minY = 9999;
                    double maxX = -9999;
                    double maxY = -9999;
                    double width = 0;
                    double depth = 0;
                    
                    double minZ = 9999;
                    double maxZ = -9999;

                    boolean writeFile = false;

                    HashMap<Vector, Integer> polygonOrder = new HashMap<Vector, Integer>();
                    
                    for (ObjectInfo child : children){
                        ObjectInfo childClone = child.duplicate();
                        childClone.setLayoutView(false);

                        //String child_name = child.getName();
                        //System.out.println("      - child name: " + child_name);
                        Object co = (Object)child.getObject();
                        boolean child_enabled = layout.isObjectEnabled(child);
                        
                        
                        //
                        // Polygon Flat (2d)
                        //
                        if(co instanceof Mesh &&
                                child.isVisible() == true &&
                                co instanceof Curve &&
                                child_enabled){  // Is mesh and visible.
                            
                            //System.out.println("      - Poly " + child_name);
                            writeFile = true;
                            //writer.println("# " + child_name );
                            Vector polygon = new Vector();

                            // Apply layout coordinates
                            CoordinateSystem c;
                            //c = child.getCoords();
                            c = layout.getCoords(childClone); // Read cutting coord from file
                            //System.out.println(" coords " + c.getOrigin().x);
                            childClone.setCoords(c);
                            //c.setOrigin( );

                            Vec3 origin = c.getOrigin(); // childClone.getCoords().getOrigin();
                            double angles[] = c.getRotationAngles(); //  childClone.getCoords().getRotationAngles();
                            //System.out.println(" " + origin.x + " " + c.getOrigin().x);
                            //System.out.println("         angles:  x " + angles[0] + " y " + angles[1] + " z " + angles[2]);

                            Mesh mesh = (Mesh) childClone.getObject(); // Object3D
                            Vec3 [] verts = mesh.getVertexPositions();
                            for (Vec3 vert : verts){
                                // Transform vertex points around object loc/rot.
                                Mat4 mat4 = c.duplicate().fromLocal();
                                mat4.transform(vert);

                                // Apply scale
                                vert.x = vert.x * scale;
                                vert.y = vert.y * scale;
                                vert.z = vert.z * scale;

                                double x = vert.x; // + origin.x;  // works
                                double y = vert.y; // + origin.y;
                                double z = vert.z; // + origin.z;

                                //System.out.println("         x " + x + " y: " + y + " z: " + z );

                                //writer.println(" x " + x + " y: " + y + " z: " + z);

                                polygon.addElement(vert);

                                if(x < minX){
                                    minX = x;
                                }
                                if(x > maxX){
                                    maxX = x;
                                }
                                
                                if(y < minY){
                                    minY = y;
                                }
                                if(y > maxY){
                                    maxY = y;
                                }
                                
                                if(z < minZ){
                                    minZ = z;
                                }
                                if(z > maxZ){
                                    maxZ = z;
                                }
                            }
                            
                            // Reverse order
                            if(layout.getReverseOrder(child.getId() + "") == 1){
                                Collections.reverse(polygon);
                            }

                            // Cycle polygon start.
                            // Allows cutting faccit deteail before long lines.
                            // polygons (Vector)
                            int start_offset = child.getCncPointOffset(); // layout.getPointOffset(child.getId() + "");
                            //System.out.println(" *** start_offset: " + start_offset);
                            //for(int pt = 0; pt < polygon.size(); pt++){
                            //}
                            while(start_offset > polygon.size() - 1){
                                start_offset = start_offset - polygon.size();
                            }
                            while(start_offset < 0){
                                start_offset = start_offset + polygon.size();
                            }
                            if(start_offset != 0 && polygon.size() > 0){
                                for(int i = 0; i < start_offset; i++){
                                    Vec3 vert = (Vec3)polygon.elementAt(0);
                                    polygon.remove(0);
                                    polygon.add(vert);
                                }
                            }
                            
                            
                            
                            // Insert this polygon into the correct sorted order.
                            
                            int polyOrder = child.getCncPolyOrder(); // layout.getPolyOrder( "" + child.getId() );
                            polygonOrder.put(polygon, polyOrder);
                            //System.out.println("  ****  " + child.getId() + "  polyOrder: " + polyOrder );
                            
                            int insertAtPos = 0;
                            boolean insertAtEnd = false;
                            //if(name.equals("Door")){
                            
                                
                            for(int pos = 0; pos < polygons.size(); pos++){
                                Vector poly = (Vector)polygons.elementAt(pos);
                                if(poly != null && polygonOrder != null){
                                    Object currPolyPos =  (Object)polygonOrder.get(poly);
                                    if(currPolyPos != null){
                                        int currPolyPosInt = ((Integer)currPolyPos).intValue();
                                        
                                        // If current poly order is less than the current in the existing list insert it before
                                        if( polyOrder < currPolyPosInt ){
                                            insertAtPos = pos - 0;
                                            pos = polygons.size() + 1; // break
                                            //break;
                                        }
                                    }
                                }
                            }
                            
                            if(polygons.size() > 0){
                                Vector lastPoly = (Vector)polygons.elementAt(polygons.size()-1);
                                Object lastPolyPos =  (Object)polygonOrder.get(lastPoly);
                                if(lastPolyPos != null){
                                    int lastPolyPosInt = ((Integer)lastPolyPos).intValue();
                                    if(polyOrder > lastPolyPosInt){
                                        insertAtEnd = true;
                                    }
                                }
                            }
                            
                            //System.out.println("  **** !!!!!!!!  " + child.getId() + "  insertAtPos: " + insertAtPos );
                            
                            if(insertAtEnd){
                                polygons.addElement(polygon);
                            } else {
                            //if(insertAtPos > 0){
                            //    System.out.println("insert at " + insertAtPos);
                                polygons.insertElementAt(polygon, insertAtPos);
                            //} else {
                            //    System.out.println("add element");
                            //    polygons.addElement(polygon);
                            //}
                            }
                        }
                        
                        
                        
                        
                        //
                        // Mesh 3d
                        //   (Raise on each row...) Don't want to cut through surface to get to next row...
                        //
                        /*
                        if(co instanceof Mesh &&
                           child.isVisible() == true &&
                           (co instanceof Curve) == false &&
                           child_enabled){  // Is mesh and visible.
                            
                            System.out.println("      - Mesh " + child_name);
                            writeFile = true;
                            //writer.println("# " + child_name );
                            Vector polygon = new Vector();
                            
                            // Apply layout coordinates
                            CoordinateSystem c;
                            //c = child.getCoords();
                            c = layout.getCoords(childClone); // Read cutting coord from file
                            //System.out.println(" coords " + c.getOrigin().x);
                            childClone.setCoords(c);
                            //c.setOrigin( );
                            
                            Vec3 origin = c.getOrigin(); // childClone.getCoords().getOrigin();
                            double angles[] = c.getRotationAngles(); //  childClone.getCoords().getRotationAngles();
                            //System.out.println(" " + origin.x + " " + c.getOrigin().x);
                            //System.out.println("         angles:  x " + angles[0] + " y " + angles[1] + " z " + angles[2]);
                            
                            Mesh mesh = (Mesh) childClone.getObject(); // Object3D
                            Vec3 [] verts = mesh.getVertexPositions();
                            
                            int i = 0;
                            
                            for (Vec3 vert : verts){
                                // Transform vertex points around object loc/rot.
                                Mat4 mat4 = c.duplicate().fromLocal();
                                mat4.transform(vert);
                                
                                // Apply scale
                                vert.x = vert.x * scale;
                                vert.y = vert.y * scale;
                                vert.z = vert.z * scale;
                                
                                double x = vert.x; // + origin.x;  // works
                                double y = vert.y; // + origin.y;
                                double z = vert.z; // + origin.z;
                                
                                //System.out.println("         x " + x + " y: " + y + " z: " + z );
                                
                                //writer.println(" x " + x + " y: " + y + " z: " + z);
                                
                                polygon.addElement(vert);
                                
                                if(x < minX){
                                    minX = x;
                                }
                                if(z < minY){
                                    minY = z;
                                }
                                if(x > maxX){
                                    maxX = x;
                                }
                                if(z > maxY){
                                    maxY = z;
                                }
                                
                                if(y < minZ){
                                    minZ = y;
                                }
                                if(y > maxZ){
                                    maxZ = y;
                                }
                            }
                            
                            // Cycle polygon start.
                            // Allows cutting faccit deteail before long lines.
                            // polygons (Vector)
                            int start_offset = layout.getPointOffset(child.getId() + "");
                            System.out.println(" *** start_offset: " + start_offset);
                            //for(int pt = 0; pt < polygon.size(); pt++){
                            //}
                            while(start_offset > polygon.size() - 1){
                                start_offset = start_offset - polygon.size();
                            }
                            while(start_offset < 0){
                                start_offset = start_offset + polygon.size();
                            }
                            if(start_offset != 0 && polygon.size() > 0){
                                for(int i = 0; i < start_offset; i++){
                                    Vec3 vert = (Vec3)polygon.elementAt(0);
                                    polygon.remove(0);
                                    polygon.add(vert);
                                }
                            }
                            
                            polygons.addElement(polygon);
                        }
                        */
                        
                        
                    }
                    // Close file
                    //writer.close();

                    
                    /*
                    if(name.equals("Door")){
                        
                        for(int x = 0; x < polygons.size(); x++){
                            Vector poly = (Vector)polygons.elementAt(x);
                            Object currPolyPos =  (Object)polygonOrder.get(poly);
                            if(currPolyPos != null){
                                int currPolyPosInt = ((Integer)currPolyPos).intValue();
                                
                                
                                System.out.println( "    poly " + x + "     order: " +  currPolyPosInt );
                            }
                        }
                        
                    }
                    */
                    
                    if(frontView){ // if frontView swap Z with Y
                        
                        // Add boundary points (so you don't cut outside of the material or the clamps)
                        gcode2 += "G1 X" + roundThree(0) + " Y" + roundThree(0) + "\n"; // G90
                        gcode2 += "G1 X" + roundThree(maxX - minX) + " Y" + roundThree(0) + "\n"; // G90
                        gcode2 += "G1 X" + roundThree(maxX - minX) + " Y" + roundThree(maxY - minY) + "\n"; // G90
                        gcode2 += "G1 X" + roundThree(0) + " Y" + roundThree(maxY - minY) + "\n"; // G90
                        gcode2 += "G1 X" + roundThree(0) + " Y" + roundThree(0) + "\n"; // G90
                        gcode2 += "G1\n";
                        
                    } else {
                    
                        // Add boundary points (so you don't cut outside of the material or the clamps)
                        gcode2 += "G1 X" + roundThree(0) + " Y" + roundThree(0) + "\n"; // G90
                        gcode2 += "G1 X" + roundThree(maxX - minX) + " Y" + roundThree(0) + "\n"; // G90
                        gcode2 += "G1 X" + roundThree(maxX - minX) + " Y" + roundThree(maxZ - minZ) + "\n"; // G90
                        gcode2 += "G1 X" + roundThree(0) + " Y" + roundThree(maxZ - minZ) + "\n"; // G90
                        gcode2 += "G1 X" + roundThree(0) + " Y" + roundThree(0) + "\n"; // G90
                        gcode2 += "G1\n";
                            
                    }

                    // Sort polygons by order attribute



                    //
                    // Write gcode file
                    // Iterate data
                    for(int p = 0; p < polygons.size(); p++){
                        //System.out.println(" POLYGON ***");
                        
                        Vector polygon = (Vector)polygons.elementAt(p);
                        boolean lowered = false;
                        Vec3 firstPoint = null;
                        for(int pt = 0; pt < polygon.size(); pt++){
                            Vec3 point = (Vec3)polygon.elementAt(pt);
                            //System.out.println("  Point *** " + point.getX() + " " + point.getY());

                            point.x = (point.x + -minX); // shift to align all geometry to 0,0
                            point.y = (point.y + -minY);
                            point.z = (point.z + -minZ); //
                            
                            //point.z = (point.z + -minZ);

                            if(frontView){ // if frontView swap Z with Y
                                gcode2 += "G1 X" +
                                    roundThree(point.x) +
                                    " Y" +
                                    roundThree(point.y) +
                                    "\n"; // G90
                            } else {        // top
                                gcode2 += "G1 X" +
                                    roundThree(point.x) +
                                    " Y" +
                                    roundThree(point.z) +
                                    "\n"; // G90
                            }
                                
                            if(!lowered){
                                gcode2 += "G00 Z-0.5\n"; // Lower router head for cutting.
                                lowered = true;
                                firstPoint = point;
                            }

                            polygon.setElementAt(point, pt);
                        }

                        // Connect last point to first point
                        if(firstPoint != null){
                            
                            if(frontView){ // if frontView swap Z with Y
                                gcode2 += "G1 X" +
                                    roundThree(firstPoint.x) +
                                    " Y" +
                                    roundThree(firstPoint.y) + "\n"; // G90
                            } else {                                    // top view
                                gcode2 += "G1 X" +
                                    roundThree(firstPoint.x) +
                                    " Y" +
                                    roundThree(firstPoint.z) + "\n"; // G90
                            }
                        }

                        gcode2 += "G00 Z0.5\n"; // Raise router head
                    }

                    if(frontView){ // if frontView swap Z with Y
                        System.out.println("Width: " + (maxX - minX) + " Height: " + (maxY - minY));
                        System.out.println("Align: x: " + -minX + " y: " + -minY);
                    } else {
                        System.out.println("Width: " + (maxX - minX) + " Height: " + (maxZ - minZ));
                        System.out.println("Align: x: " + -minX + " y: " + -minZ);
                    }


                    // Write gcode to file
                    if(writeFile){
                        try {
                            groupsWritten++;
                            
                            String gcodeFile = dir + System.getProperty("file.separator") + name + "";
                            gcodeFile += ".gcode";
                            System.out.println("Writing g code file: " + gcodeFile);
                            PrintWriter writer2 = new PrintWriter(gcodeFile, "UTF-8");
                            writer2.println(gcode2);
                            writer2.close();
                        } catch (Exception e){
                            System.out.println("Error: " + e.toString());
                        }
                        
                        // Multi part file
                        String gcode3 = gcode2;
                        int lines = 0; // StringUtils.countMatches(gcode2, "\n");
                        for(int i = 0; i < gcode3.length(); i++){
                            if(gcode3.charAt(i) == '\n'){
                                lines++;
                            }
                        }
                        if(lines > 499){
                            int lineNumber = 0;
                            int fileNumber = 1;
                            lines = 0;
                            for(int i = 0; i < gcode3.length(); i++){
                                if(gcode3.charAt(i) == '\n'){
                                    lines++;
                                    if(lines > 480){
                                        String gCodeSection = gcode3.substring(0, i);
                                        
                                        String gcodeFile = dir + System.getProperty("file.separator") + name + "_" + fileNumber;
                                        gcodeFile += ".gcode";
                                        System.out.println("Writing g code file: " + gcodeFile);
                                        PrintWriter writer2 = new PrintWriter(gcodeFile, "UTF-8");
                                        writer2.println(gCodeSection);
                                        writer2.close();
                                        
                                        fileNumber++;
                                        gcode3 = gcode3.substring(i+1, gcode3.length());
                                    }
                                }
                            }
                            String gcodeFile = dir + System.getProperty("file.separator") + name + "_" + fileNumber;
                            gcodeFile += ".gcode";
                            System.out.println("Writing g code file: " + gcodeFile);
                            PrintWriter writer2 = new PrintWriter(gcodeFile, "UTF-8");
                            writer2.println(gcode3);
                            writer2.close();
                            System.out.println(" Lines *** " + lines);
                        }
                        
                    }

                } catch (Exception e){
                    System.out.println("Error: " + e);
                    e.printStackTrace();
                }
            } // Obj has children and is enabled.
        } // Loop objets
        System.out.println("Export done. Groups: " + groupsWritten);
        
        // Notify dialog.
        if(groupsWritten == 0){
            JOptionPane.showMessageDialog(null, "GCode export, No groups found", "No Groups Found", JOptionPane.ERROR_MESSAGE );
        } else {
            JOptionPane.showMessageDialog(null, "GCode export complete.", "Complete", JOptionPane.ERROR_MESSAGE );
            
        }
    }
    
    
    
    
    
    
    
    /**
     * exportAllGCode
     *
     * Description: Export layout object geometry to gcode files.
     *     Files are created for each group (Object children) with only curve structures one level nested.
     *   -- adding support for mesh structures.
     *
     * @param Scene
     */
    public void exportAllGCode(Scene scene){
        LayoutModeling layout = new LayoutModeling();

        //
        // Detect orientation of curve geometry. (X/Y or X/Z)
        //
        double sceneDepth = 0;
        double sceneHeight = 0;
        double minY_ = 9999;
        double maxY_ = 0;
        double minZ_ = 9999;
        double maxZ_ = 0;
        for (ObjectInfo obj : scene.getObjects()){
            Object co = (Object)obj.getObject();
            boolean enabled = layout.isObjectEnabled(obj);
            if(enabled && co instanceof Curve){
                // Object co = (Object)childClone.getObject();
                BoundingBox bounds = obj.getTranslatedBounds();
                
                if(bounds.miny < minY_){
                    minY_ = bounds.miny;
                }
                if(bounds.maxy > maxY_){
                    maxY_ = bounds.maxy;
                }
                if(bounds.minz < minZ_){
                    minZ_ = bounds.minz;
                }
                if(bounds.maxz > maxZ_){
                    maxZ_ = bounds.maxz;
                }
            }
        }
        sceneDepth = maxZ_ - minZ_;
        sceneHeight = maxY_ - minY_;
        System.out.println(" sceneDepth: " + sceneDepth + " sceneHeight: " + sceneHeight);
        
        boolean frontView = false;
        if(sceneDepth > sceneHeight){ // Top
            
        }
        if(sceneDepth < sceneHeight){ // Front
            frontView = true;
        }
        
        getUserInput();
        
        //layout.setBaseDir(this.getDirectory() + System.getProperty("file.separator") + this.getName() + "_layout_data" );

        //String dir = System.getProperty("user.dir") + System.getProperty("file.separator") + "gcode";
        String dir = scene.getDirectory() + System.getProperty("file.separator") + scene.getName() + "_gCode";
        File d = new File(dir);
        if(d.exists() == false){
            d.mkdir();
        }

        double scale = 1.0;
        try {
            // Read current scale for this project.
            Properties prop = new Properties();
            InputStream input = null;
            OutputStream output = null;

            String dir2 = scene.getDirectory() + System.getProperty("file.separator") + scene.getName() + "_layout_data";
            File d2 = new File(dir2);
            if(d2.exists() == false){
                d2.mkdir();
            }
            dir2 = dir2 + System.getProperty("file.separator") + "scale.properties";
            d2 = new File(dir2);
            if(d2.exists() == false){
                //(works for both Windows and Linux)
                d2.getParentFile().mkdirs();
                d2.createNewFile();
            }

            input = new FileInputStream(dir2);
            // load a properties file
            prop.load(input);

            String v = prop.getProperty("export_scale");
            if(v != null){
                scale = Double.parseDouble(v);
            }
        } catch (Exception e){
            System.out.println("Error " + e);
            e.printStackTrace();
        }

        int groupsWritten = 0;
        Vector polygons = new Vector();
        
        double minX = 9999;
        double minY = 9999;
        double maxX = -9999;
        double maxY = -9999;
        double width = 0;
        double depth = 0;
        
        double minZ = 9999;
        double maxZ = -9999;
        
        boolean writeFile = false;
        
        // Create gcode file.
        String gCodeFile = dir + System.getProperty("file.separator") + scene.getName() + ".gcode";
        //PrintWriter writer = new PrintWriter(gCodeFile, "UTF-8");
        System.out.println("      File: " + gCodeFile);
        
        HashMap<Vector, Integer> polygonOrder = new HashMap<Vector, Integer>();
        
        // gcode
        String gcode = "G1\n";
        String gcode2 = "G1\n";
        
        for (ObjectInfo obj : scene.getObjects()){
            String name = obj.getName();
            boolean enabled = layout.isObjectEnabled(obj);
            //System.out.println("   - name: " + name);
            //ObjectInfo[] children = obj.getChildren();
            if(enabled){ // children.length > 0 &&
                //System.out.println("   - Group: " + name + " count: " + children.length);
                try {
                    
                        ObjectInfo childClone = obj.duplicate(); // child.duplicate();
                        childClone.setLayoutView(false);

                        String child_name = childClone.getName();
                        //System.out.println("      - child name: " + child_name);
                        Object co = (Object)childClone.getObject();
                        boolean child_enabled = layout.isObjectEnabled(childClone);
                        
                        
                        //
                        // Polygon Flat (2d)
                        //
                        if(co instanceof Mesh &&
                           childClone.isVisible() == true &&
                                co instanceof Curve &&
                                child_enabled){  // Is mesh and visible.
                            
                            //System.out.println("      - Poly " + child_name);
                            writeFile = true;
                            //writer.println("# " + child_name );
                            Vector polygon = new Vector();

                            // Apply layout coordinates
                            CoordinateSystem c;
                            //c = child.getCoords();
                            c = layout.getCoords(childClone); // Read cutting coord from file
                            //System.out.println(" coords " + c.getOrigin().x);
                            childClone.setCoords(c);
                            //c.setOrigin( );

                            //Vec3 origin = c.getOrigin(); // childClone.getCoords().getOrigin();
                            //double angles[] = c.getRotationAngles(); //  childClone.getCoords().getRotationAngles();
                            //System.out.println(" " + origin.x + " " + c.getOrigin().x);
                            //System.out.println("         angles:  x " + angles[0] + " y " + angles[1] + " z " + angles[2]);

                            Mesh mesh = (Mesh) childClone.getObject().duplicate(); // Object3D
                            Vec3 [] verts = mesh.getVertexPositions();
                            for (Vec3 vert : verts){
                                // Transform vertex points around object loc/rot.
                                Mat4 mat4 = c.duplicate().fromLocal();
                                mat4.transform(vert);

                                // Apply scale
                                vert.x = vert.x * scale;
                                vert.y = vert.y * scale;
                                vert.z = vert.z * scale;

                                double x = vert.x; // + origin.x;  // works
                                double y = vert.y; // + origin.y;
                                double z = vert.z; // + origin.z;

                                //System.out.println("         x " + x + " y: " + y + " z: " + z );

                                //writer.println(" x " + x + " y: " + y + " z: " + z);

                                polygon.addElement(vert);

                                if(x < minX){
                                    minX = x;
                                }
                                if(x > maxX){
                                    maxX = x;
                                }
                                
                                if(y < minY){
                                    minY = y;
                                }
                                if(y > maxY){
                                    maxY = y;
                                }
                                
                                if(z < minZ){
                                    minZ = z;
                                }
                                if(z > maxZ){
                                    maxZ = z;
                                }
                            }
                            
                            // Reverse order
                            if(layout.getReverseOrder(childClone.getId() + "") == 1){
                                Collections.reverse(polygon);
                            }

                            // Cycle polygon start.
                            // Allows cutting faccit deteail before long lines.
                            // polygons (Vector)
                            int start_offset = childClone.getCncPointOffset(); // layout.getPointOffset(child.getId() + "");
                            //System.out.println(" *** start_offset: " + start_offset);
                            //for(int pt = 0; pt < polygon.size(); pt++){
                            //}
                            while(start_offset > polygon.size() - 1){
                                start_offset = start_offset - polygon.size();
                            }
                            while(start_offset < 0){
                                start_offset = start_offset + polygon.size();
                            }
                            if(start_offset != 0 && polygon.size() > 0){
                                for(int i = 0; i < start_offset; i++){
                                    Vec3 vert = (Vec3)polygon.elementAt(0);
                                    polygon.remove(0);
                                    polygon.add(vert);
                                }
                            }
                            
                            
                            
                            // Insert this polygon into the correct sorted order.
                            
                            int polyOrder = childClone.getCncPolyOrder(); // layout.getPolyOrder( "" + child.getId() );
                            polygonOrder.put(polygon, polyOrder);
                            //System.out.println("  ****  " + child.getId() + "  polyOrder: " + polyOrder );
                            
                            int insertAtPos = 0;
                            boolean insertAtEnd = false;
                            //if(name.equals("Door")){
                            
                                
                            for(int pos = 0; pos < polygons.size(); pos++){
                                Vector poly = (Vector)polygons.elementAt(pos);
                                if(poly != null && polygonOrder != null){
                                    Object currPolyPos =  (Object)polygonOrder.get(poly);
                                    if(currPolyPos != null){
                                        int currPolyPosInt = ((Integer)currPolyPos).intValue();
                                        
                                        // If current poly order is less than the current in the existing list insert it before
                                        if( polyOrder < currPolyPosInt ){
                                            insertAtPos = pos - 0;
                                            pos = polygons.size() + 1; // break
                                            //break;
                                        }
                                    }
                                }
                            }
                            
                            if(polygons.size() > 0){
                                Vector lastPoly = (Vector)polygons.elementAt(polygons.size()-1);
                                Object lastPolyPos =  (Object)polygonOrder.get(lastPoly);
                                if(lastPolyPos != null){
                                    int lastPolyPosInt = ((Integer)lastPolyPos).intValue();
                                    if(polyOrder > lastPolyPosInt){
                                        insertAtEnd = true;
                                    }
                                }
                            }
                            
                            //System.out.println("  **** !!!!!!!!  " + child.getId() + "  insertAtPos: " + insertAtPos );
                            
                            if(insertAtEnd){
                                polygons.addElement(polygon);
                            } else {
                            //if(insertAtPos > 0){
                            //    System.out.println("insert at " + insertAtPos);
                                polygons.insertElementAt(polygon, insertAtPos);
                            //} else {
                            //    System.out.println("add element");
                            //    polygons.addElement(polygon);
                            //}
                            }
                        }
                        
                        
                        
                        
                        //
                        // Mesh 3d
                        //   (Raise on each row...) Don't want to cut through surface to get to next row...
                        //
                        /*
                        if(co instanceof Mesh &&
                           child.isVisible() == true &&
                           (co instanceof Curve) == false &&
                           child_enabled){  // Is mesh and visible.
                            
                            System.out.println("      - Mesh " + child_name);
                            writeFile = true;
                            //writer.println("# " + child_name );
                            Vector polygon = new Vector();
                            
                            // Apply layout coordinates
                            CoordinateSystem c;
                            //c = child.getCoords();
                            c = layout.getCoords(childClone); // Read cutting coord from file
                            //System.out.println(" coords " + c.getOrigin().x);
                            childClone.setCoords(c);
                            //c.setOrigin( );
                            
                            Vec3 origin = c.getOrigin(); // childClone.getCoords().getOrigin();
                            double angles[] = c.getRotationAngles(); //  childClone.getCoords().getRotationAngles();
                            //System.out.println(" " + origin.x + " " + c.getOrigin().x);
                            //System.out.println("         angles:  x " + angles[0] + " y " + angles[1] + " z " + angles[2]);
                            
                            Mesh mesh = (Mesh) childClone.getObject(); // Object3D
                            Vec3 [] verts = mesh.getVertexPositions();
                            
                            int i = 0;
                            
                            for (Vec3 vert : verts){
                                // Transform vertex points around object loc/rot.
                                Mat4 mat4 = c.duplicate().fromLocal();
                                mat4.transform(vert);
                                
                                // Apply scale
                                vert.x = vert.x * scale;
                                vert.y = vert.y * scale;
                                vert.z = vert.z * scale;
                                
                                double x = vert.x; // + origin.x;  // works
                                double y = vert.y; // + origin.y;
                                double z = vert.z; // + origin.z;
                                
                                //System.out.println("         x " + x + " y: " + y + " z: " + z );
                                
                                //writer.println(" x " + x + " y: " + y + " z: " + z);
                                
                                polygon.addElement(vert);
                                
                                if(x < minX){
                                    minX = x;
                                }
                                if(z < minY){
                                    minY = z;
                                }
                                if(x > maxX){
                                    maxX = x;
                                }
                                if(z > maxY){
                                    maxY = z;
                                }
                                
                                if(y < minZ){
                                    minZ = y;
                                }
                                if(y > maxZ){
                                    maxZ = y;
                                }
                            }
                            
                            // Cycle polygon start.
                            // Allows cutting faccit deteail before long lines.
                            // polygons (Vector)
                            int start_offset = layout.getPointOffset(child.getId() + "");
                            System.out.println(" *** start_offset: " + start_offset);
                            //for(int pt = 0; pt < polygon.size(); pt++){
                            //}
                            while(start_offset > polygon.size() - 1){
                                start_offset = start_offset - polygon.size();
                            }
                            while(start_offset < 0){
                                start_offset = start_offset + polygon.size();
                            }
                            if(start_offset != 0 && polygon.size() > 0){
                                for(int i = 0; i < start_offset; i++){
                                    Vec3 vert = (Vec3)polygon.elementAt(0);
                                    polygon.remove(0);
                                    polygon.add(vert);
                                }
                            }
                            
                            polygons.addElement(polygon);
                        }
                        */
                        
                        
                    //}
                    // Close file
                    //writer.close();

                    
                    /*
                    if(name.equals("Door")){
                        
                        for(int x = 0; x < polygons.size(); x++){
                            Vector poly = (Vector)polygons.elementAt(x);
                            Object currPolyPos =  (Object)polygonOrder.get(poly);
                            if(currPolyPos != null){
                                int currPolyPosInt = ((Integer)currPolyPos).intValue();
                                
                                
                                System.out.println( "    poly " + x + "     order: " +  currPolyPosInt );
                            }
                        }
                        
                    }
                    */
                    
                    // Sort polygons by order attribute


                } catch (Exception e){
                    System.out.println("Error: " + e);
                    e.printStackTrace();
                }
            } // Obj has children and is enabled.
        } // Loop objects
        
        
        
        //
        // Write gcode file
        // Iterate data
        for(int p = 0; p < polygons.size(); p++){
            //System.out.println(" POLYGON ***");
            
            Vector polygon = (Vector)polygons.elementAt(p);
            boolean lowered = false;
            Vec3 firstPoint = null;
            for(int pt = 0; pt < polygon.size(); pt++){
                Vec3 point = (Vec3)polygon.elementAt(pt);
                //System.out.println("  Point *** " + point.getX() + " " + point.getY());


                point.x = (point.x + -minX); // shift to align all geometry to 0,0
                point.y = (point.y + -minY);
                point.z = (point.z + -minZ); //
                
                
                if(frontView){ // if frontView swap Z with Y *** TODO
                    
                    gcode2 += "G1 X" +
                        roundThree(point.x) +
                        " Y" +
                        roundThree(point.y) +
                        "\n"; // G90

                    
                } else {
                    gcode2 += "G1 X" +
                        roundThree(point.x) +
                        " Y" +
                        roundThree(point.z) +
                   //     " Z" +
                   //     roundThree(point.y) +
                        "\n"; // G90
                    
                }
                if(!lowered){
                    gcode2 += "G00 Z-0.5\n"; // Lower router head for cutting.
                    lowered = true;
                    firstPoint = point;
                }

                polygon.setElementAt(point, pt);
            }

            // Connect last point to first point
            if(firstPoint != null){
                
                if(frontView){ // if frontView swap Z with Y *** TODO
                
                    gcode2 += "G1 X" +
                        roundThree(firstPoint.x) +
                        " Y" +
                        roundThree(firstPoint.y) + "\n"; // G90
                    
                } else {    // Top view
                    gcode2 += "G1 X" +
                        roundThree(firstPoint.x) +
                        " Y" +
                        roundThree(firstPoint.z) + "\n"; // G90
                
                }
            }

            gcode2 += "G00 Z0.5\n"; // Raise router head
        }

        if(frontView){
            System.out.println("Width: " + (maxX - minX) + " Height: " + (maxY - minY));
            System.out.println("Align: x: " + -minX + " y: " + -minY);
        } else {
            System.out.println("Width: " + (maxX - minX) + " Height: " + (maxZ - minZ));
            System.out.println("Align: x: " + -minX + " y: " + -minZ);
        }
        
        
        // Write gcode to file
        if(writeFile){
            
            if(frontView){ // if frontView swap Z with Y *** TODO
                // Add boundary points (so you don't cut outside of the material or the clamps)
                gcode2 += "G1 X" + roundThree(0) + " Y" + roundThree(0) + "\n"; // G90
                gcode2 += "G1 X" + roundThree(maxX - minX) + " Y" + roundThree(0) + "\n"; // G90
                gcode2 += "G1 X" + roundThree(maxX - minX) + " Y" + roundThree(maxY - minY) + "\n"; // G90
                gcode2 += "G1 X" + roundThree(0) + " Y" + roundThree(maxY - minY) + "\n"; // G90
                gcode2 += "G1 X" + roundThree(0) + " Y" + roundThree(0) + "\n"; // G90
                gcode2 += "G1\n";
            } else {
                // Add boundary points (so you don't cut outside of the material or the clamps)
                gcode2 += "G1 X" + roundThree(0) + " Y" + roundThree(0) + "\n"; // G90
                gcode2 += "G1 X" + roundThree(maxX - minX) + " Y" + roundThree(0) + "\n"; // G90
                gcode2 += "G1 X" + roundThree(maxX - minX) + " Y" + roundThree(maxZ - minZ) + "\n"; // G90
                gcode2 += "G1 X" + roundThree(0) + " Y" + roundThree(maxZ - minZ) + "\n"; // G90
                gcode2 += "G1 X" + roundThree(0) + " Y" + roundThree(0) + "\n"; // G90
                gcode2 += "G1\n";
            }
            
            
            try {
                groupsWritten++;
                
                String gcodeFile = dir + System.getProperty("file.separator") + scene.getName() + "";
                gcodeFile += ".gcode";
                System.out.println("Writing g code file: " + gcodeFile);
                PrintWriter writer2 = new PrintWriter(gcodeFile, "UTF-8");
                writer2.println(gcode2);
                writer2.close();
            
            
                // Multi part file
                String gcode3 = gcode2;
                int lines = 0; // StringUtils.countMatches(gcode2, "\n");
                for(int i = 0; i < gcode3.length(); i++){
                    if(gcode3.charAt(i) == '\n'){
                        lines++;
                    }
                }
                if(lines > 499){
                    int lineNumber = 0;
                    int fileNumber = 1;
                    lines = 0;
                    for(int i = 0; i < gcode3.length(); i++){
                        if(gcode3.charAt(i) == '\n'){
                            lines++;
                            if(lines > 480){
                                String gCodeSection = gcode3.substring(0, i);
                                
                                gcodeFile = dir + System.getProperty("file.separator") + scene.getName() + "_" + fileNumber;
                                gcodeFile += ".gcode";
                                System.out.println("Writing g code file: " + gcodeFile);
                                writer2 = new PrintWriter(gcodeFile, "UTF-8");
                                writer2.println(gCodeSection);
                                writer2.close();
                                
                                fileNumber++;
                                gcode3 = gcode3.substring(i+1, gcode3.length());
                            }
                        }
                    }
                    gcodeFile = dir + System.getProperty("file.separator") + scene.getName() + "_" + fileNumber;
                    gcodeFile += ".gcode";
                    System.out.println("Writing g code file: " + gcodeFile);
                    writer2 = new PrintWriter(gcodeFile, "UTF-8");
                    writer2.println(gcode3);
                    writer2.close();
                    System.out.println(" Lines *** " + lines);
                }
            } catch (Exception e){
                System.out.println("Error: " + e.toString());
            }
        }
        
        System.out.println("Export done. Groups: " + groupsWritten);
        
        // Notify dialog.
        if(groupsWritten == 0){
            JOptionPane.showMessageDialog(null, "GCode export, No groups found", "No Groups Found", JOptionPane.ERROR_MESSAGE );
        } else {
            JOptionPane.showMessageDialog(null, "GCode export complete.", "Complete", JOptionPane.ERROR_MESSAGE );
            
        }
    }
    
    
    
    String roundThree(double x){
        //double rounded = ((double)Math.round(x * 100000) / 100000);
        
        DecimalFormat df = new DecimalFormat("#");
            df.setMaximumFractionDigits(3);

        return df.format(x);
    }
}

