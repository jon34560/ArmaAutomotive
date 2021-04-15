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
    * exportGCode
    *
    * Description: Export layout object geometry to gcode files.
    *     Files are created for each group (Object children) with only curve structures one level nested.
    *   -- adding support for mesh structures.
    */
    public void exportGroupGCode(Scene scene){ // Vector<ObjectInfo> objects
        LayoutModeling layout = new LayoutModeling();

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
                    
                    
                    // Add boundary points (so you don't cut outside of the material or the clamps)
                    gcode2 += "G1 X" + roundThree(0) + " Y" + roundThree(0) + "\n"; // G90
                    gcode2 += "G1 X" + roundThree(maxX - minX) + " Y" + roundThree(0) + "\n"; // G90
                    gcode2 += "G1 X" + roundThree(maxX - minX) + " Y" + roundThree(maxZ - minZ) + "\n"; // G90
                    gcode2 += "G1 X" + roundThree(0) + " Y" + roundThree(maxZ - minZ) + "\n"; // G90
                    gcode2 += "G1 X" + roundThree(0) + " Y" + roundThree(0) + "\n"; // G90
                    gcode2 += "G1\n";

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
                            point.z = (point.z + -minZ); //
                            
                            //point.z = (point.z + -minZ);

                            gcode2 += "G1 X" +
                                roundThree(point.x) +
                                " Y" +
                                roundThree(point.z) +
                           //     " Z" +
                           //     roundThree(point.y) +
                                "\n"; // G90
                            if(!lowered){
                                gcode2 += "G00 Z-0.5\n"; // Lower router head for cutting.
                                lowered = true;
                                firstPoint = point;
                            }

                            polygon.setElementAt(point, pt);
                        }

                        // Connect last point to first point
                        if(firstPoint != null){
                            gcode2 += "G1 X" +
                                roundThree(firstPoint.x) +
                                " Y" +
                                roundThree(firstPoint.z) + "\n"; // G90
                        }

                        gcode2 += "G00 Z0.5\n"; // Raise router head
                    }

                    System.out.println("Width: " + (maxX - minX) + " Height: " + (maxZ - minZ));
                    System.out.println("Align: x: " + -minX + " y: " + -minZ);


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
                point.z = (point.z + -minZ); //
                
                //point.z = (point.z + -minZ);

                gcode2 += "G1 X" +
                    roundThree(point.x) +
                    " Y" +
                    roundThree(point.z) +
               //     " Z" +
               //     roundThree(point.y) +
                    "\n"; // G90
                if(!lowered){
                    gcode2 += "G00 Z-0.5\n"; // Lower router head for cutting.
                    lowered = true;
                    firstPoint = point;
                }

                polygon.setElementAt(point, pt);
            }

            // Connect last point to first point
            if(firstPoint != null){
                gcode2 += "G1 X" +
                    roundThree(firstPoint.x) +
                    " Y" +
                    roundThree(firstPoint.z) + "\n"; // G90
            }

            gcode2 += "G00 Z0.5\n"; // Raise router head
        }

        System.out.println("Width: " + (maxX - minX) + " Height: " + (maxZ - minZ));
        System.out.println("Align: x: " + -minX + " y: " + -minZ);
        
        
        // Write gcode to file
        if(writeFile){
            
            // Add boundary points (so you don't cut outside of the material or the clamps)
            gcode2 += "G1 X" + roundThree(0) + " Y" + roundThree(0) + "\n"; // G90
            gcode2 += "G1 X" + roundThree(maxX - minX) + " Y" + roundThree(0) + "\n"; // G90
            gcode2 += "G1 X" + roundThree(maxX - minX) + " Y" + roundThree(maxZ - minZ) + "\n"; // G90
            gcode2 += "G1 X" + roundThree(0) + " Y" + roundThree(maxZ - minZ) + "\n"; // G90
            gcode2 += "G1 X" + roundThree(0) + " Y" + roundThree(0) + "\n"; // G90
            gcode2 += "G1\n";
            
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

