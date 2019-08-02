/* Copyright (C) 2018 by Jon Taylor
  
   This program is free software; you can redistribute it and/or modify it under the
   terms of the GNU General Public License as published by the Free Software
   Foundation; either version 2 of the License, or (at your option) any later version.

   This program is distributed in the hope that it will be useful, but WITHOUT ANY
   WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
   PARTICULAR PURPOSE.  See the GNU General Public License for more details. */

package armadesignstudio;

import armadesignstudio.ui.*;
import buoy.event.*;
import buoy.widget.*;
import java.awt.*;
import java.util.*;

import java.awt.Graphics;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import armadesignstudio.object.*;
import armadesignstudio.math.*;

/** This class implements the dialog box which is used for the "Object Layout" and
    "Transform Object" commands.  It allows the user enter values for the position,
    orientation, and size of an object.  The initial values are passed to the constructor
    in values[].  If this argument is omitted, all of the fields will initially be blank.
    If transformLabels is true, the rows will be labelled "Move", "Rotate", and "Scale".
    If it is false, they will be labelled "Position", "Orientation", and "Size".
 
 
  Scene.runCrashSimulation()
 
 */

public class CrashSimulation extends BDialog
{

    private double initialValues[], finalValues[];
    private ValueField fields[];
    private RadioButtonGroup centerGroup;
    private BCheckBox childrenBox;
    private BRadioButton objectCenterBox, selectionCenterBox;
    private boolean ok = true;

    private static boolean children = true;
    private static boolean selectionCenter = true;

    private double inerta = 1.0;
    
    LayoutWindow window;

    public CrashSimulation(BFrame parent)
    {
        super(parent, "FEA Crash Simulation", true);

        fields = new ValueField[9];
        layoutDialog();
        pack();
        setResizable(false);
        UIUtilities.centerDialog(this, parent);
        //fields[0].requestFocus();
        //if (show)
        setVisible(true);
        
        window = ((LayoutWindow)parent);
    }

    public boolean clickedOk()
    {
        return ok;
    }

    void layoutDialog()
    {
        BorderContainer content = new BorderContainer();
        setContent(BOutline.createEmptyBorder(content, UIUtilities.getStandardDialogInsets()));
        FormContainer center = new FormContainer(3, 6);
        content.add(center, BorderContainer.CENTER);
        LayoutInfo eastLayout = new LayoutInfo(LayoutInfo.EAST, LayoutInfo.NONE, new Insets(0, 0, 0, 5), null);
        LayoutInfo westLayout = new LayoutInfo(LayoutInfo.WEST, LayoutInfo.NONE, new Insets(0, 0, 0, 5), null);

        LayoutInfo centerLayout = new LayoutInfo(LayoutInfo.CENTER, LayoutInfo.NONE, null, null);
        center.add(new BLabel("X"), 1, 0, centerLayout);
        //center.add(new BLabel("Y"), 2, 0, centerLayout);
        //center.add(new BLabel("Z"), 3, 0, centerLayout);
        Object listener = new Object() {
          void processEvent()
          {
            for (int i = 0; i < finalValues.length; i++){
              finalValues[i] = fields[i].getValue();
            }
            //if (childrenBox != null){
            //  children = childrenBox.getState();
            //}
            if (centerGroup != null){
              selectionCenter = (centerGroup.getSelection() == selectionCenterBox);
            }
            dispatchEvent(new ValueChangedEvent(CrashSimulation.this));
          }
        };

        center.add(Translate.label("Mass"), 0, 1, eastLayout);
        center.add(fields[0] = new ValueField(3550, ValueField.NONE), 1, 1);
        center.add(Translate.label("kgs"), 2, 1, westLayout);

        center.add(Translate.label("Velocity"), 0, 2, eastLayout);
        center.add(fields[1] = new ValueField(56, ValueField.NONE), 1, 2);
        center.add(Translate.label("kph"), 2, 2, westLayout);

        center.add(Translate.label("Force"), 0, 3, eastLayout);
        center.add(fields[2] = new ValueField(-1, ValueField.NONE), 1, 3);
        center.add(Translate.label("kph"), 2, 3, westLayout);

        center.add(Translate.label("Material"), 0, 4, eastLayout);
        String[] materialStrings = { "Steel", "Aluminum" };
        JComboBox materialList = new JComboBox(materialStrings);
        materialList.setSelectedIndex(0);
        //materialList.addActionListener(this);
        //center.add(materialList, 1, 4);

        center.add(Translate.label("Material Strength"), 0, 5, eastLayout);
        center.add(fields[3] = new ValueField(-1, ValueField.NONE), 1, 5);
        center.add(Translate.label("psi"), 2, 5, westLayout);


        /*
        for (int i = 0; i < 9; i++)
        {

          center.add(
        fields[i] =
            new ValueField(
                0, ValueField.NONE), (i%3)+1, (i/3)+1);

          fields[i].addEventLink(ValueChangedEvent.class, listener);

        }
        */
        /*
        if (extraOptions)
        {
          center.add(childrenBox = new BCheckBox(Translate.text("applyToUnselectedChildren"), children), 0, 4, 4, 1);
          childrenBox.addEventLink(ValueChangedEvent.class, listener);
          FormContainer extra = new FormContainer(2, 2);
          center.add(extra, 0, 5, 4, 1);
          extra.add(new BLabel(Translate.text("rotateScaleAround")), 0, 0, 1, 2, eastLayout);
          centerGroup = new RadioButtonGroup();
          centerGroup.addEventLink(SelectionChangedEvent.class, listener);
          LayoutInfo westLayout = new LayoutInfo(LayoutInfo.WEST, LayoutInfo.NONE, null, null);
          extra.add(objectCenterBox = new BRadioButton(Translate.text("individualObjectCenters"), !selectionCenter, centerGroup), 1, 0, westLayout);
          extra.add(selectionCenterBox = new BRadioButton(Translate.text("centerOfSelection"), selectionCenter, centerGroup), 1, 1, westLayout);
        }
        */

        FormContainer extra = new FormContainer(1, 1);
        //RowContainer graphContainer = new RowContainer();
        JPanel graphPanel = new JPanel(){
            protected void paintComponent(Graphics g) {
              super.paintComponent(g);
              g.drawLine(0,0, 20, 35);
            };
        };

        //extra.add( graphPanel );
        //content.add(buttons, BorderContainer.SOUTH, new LayoutInfo());


        RowContainer buttons = new RowContainer();
        content.add(buttons, BorderContainer.SOUTH, new LayoutInfo());
        buttons.add(Translate.button("ok", this, "doOk"));
        buttons.add(Translate.button("cancel", this, "dispose"));
        addEventLink(WindowClosingEvent.class, this, "dispose");
        addAsListener(this);
    }


    private void doOk()
    {
        ok = true;
        dispose();
    }


    private void keyPressed(KeyPressedEvent ev)
    {
        int code = ev.getKeyCode();

        if (code == KeyPressedEvent.VK_ENTER)
          doOk();
        if (code == KeyPressedEvent.VK_ESCAPE)
          dispose();
    }

    private void addAsListener(Widget w)
    {
        w.addEventLink(KeyPressedEvent.class, this, "keyPressed");
        if (w instanceof WidgetContainer)
        {
          Iterator iter = ((WidgetContainer) w).getChildren().iterator();
          while (iter.hasNext())
            addAsListener((Widget) iter.next());
        }
    }

    public ImpactThread impactThread = new ImpactThread();
    public class ImpactThread  extends Thread {
        ObjectInfo obj;
        public ImpactThread(){}
        public void setObject(ObjectInfo obj){
            this.obj = obj;
        }
        public void run() {
            while(inerta > 0.001){
                impact(obj);
                try {
                    Thread.sleep(50);
                } catch (Exception e){
                    
                }
            }
        }
    }
    
    /**
     * process: DEPRICATE
    */
    public void runImpact(ObjectInfo obj){
        while(inerta > 0.001){
            impact(obj);
            try {
                Thread.sleep(50);
            } catch (Exception e){
                
            }
        }
    }
    
    
    /**
    * impact
    *
    * Description:
    */
    public void impact(ObjectInfo meshObj){
        System.out.println("Run Simulation." + "  mesh: " + meshObj);
        
        LayoutModeling layout = new LayoutModeling();

        //double inerta = 1.0;

        // We may need meshObj in TriangleMesh
        //
        Object3D triangleMesh = null; // && triangleMesh != null
        if(meshObj.getObject().canConvertToTriangleMesh() == Object3D.CANT_CONVERT){
            //System.out.println(" can't ");
        } else {
            //System.out.println(" can ");
            triangleMesh = meshObj.getObject().convertToTriangleMesh(0.0);
            //System.out.println(" tmesh " + triangleMesh.getClass().getName() );
            if( triangleMesh instanceof TriangleMesh ){     // selected object to impact is TriangleMesh
                TriangleMesh.Edge[] edges = ((TriangleMesh)triangleMesh).getEdges();
                MeshVertex[] verts = ((TriangleMesh)triangleMesh).getVertices();
                
                Vec3 vecPoints[] = new Vec3[verts.length];
                for(int i = 0; i < verts.length; i++){
                    vecPoints[i] = verts[i].r;
                }
                
                CoordinateSystem c;
                c = layout.getCoords(meshObj);
                Vec3 origin = c.getOrigin();
                
                // Find Left most vertex to start the impact calculation process.
                int firstVecId = -1;
                double firstVecX = 99999;
                for(int i = 0; i < verts.length; i++){
                    Vec3 vertex1 = verts[i].r;
                    
                    Mat4 mat4 = c.duplicate().fromLocal();
                    //mat4.transform(vertex1);
                    
                    if(vertex1.x < firstVecX){
                        firstVecX = vertex1.x;
                        firstVecId = i;
                    }
                }
                System.out.println("firstVecId: " + firstVecId);
                
                // Traverse mesh, left to right. (Impact is from Left axis.)
                HashMap traveredPoint = new HashMap(); // Vec
                HashMap traveredEdge = new HashMap();
                Vector orderedVec = new Vector();
                
                Vector processQueueVecs = new Vector();
                
                boolean done = false;
                int currVecId = -1;
                int counter = 0;
                
                while(!done ){ // && currVecId >= 0
                    if(currVecId == -1){
                        currVecId = firstVecId;
                        orderedVec.addElement( firstVecId );
                        traveredPoint.put(firstVecId, 1);
                    } else if(processQueueVecs.size() > 0) {
                        currVecId = ((Integer)processQueueVecs.remove(0)).intValue();
                    } else {
                        currVecId = -1;
                    }
                    
                    Vec3 vec = verts[currVecId].r;
                    
                    // Get list of all verts connected to the currVec by edges.
                    for(int i = 0; i < edges.length; i++){
                        TriangleMesh.Edge edge = edges[i];
                        
                        if(currVecId == edge.v1){
                            if(traveredPoint.containsKey(edge.v2) == false){
                                orderedVec.addElement(edge.v2);
                                processQueueVecs.addElement(edge.v2);
                            }
                            traveredPoint.put(edge.v2, 1);
                            traveredEdge.put(i, 1);
                        }
                        if(currVecId == edge.v2){
                            if(traveredPoint.containsKey(edge.v1) == false){
                                orderedVec.addElement(edge.v1);
                                processQueueVecs.addElement(edge.v1);
                            }
                            traveredPoint.put(edge.v1, 1);
                            traveredEdge.put(i, 1);
                        }
                    }
                    
                    //counter++;
                    if( processQueueVecs.size() == 0 ){
                        done = true;
                    }
                }
                
                // Print ordered object points
                for(int i = 0; i < orderedVec.size(); i++){
                    int vertId = ((Integer)orderedVec.get(i)).intValue();
                    Vec3 vec = verts[vertId].r;
                    
                    Mat4 mat4 = c.duplicate().fromLocal();
                    //mat4.transform(verts[i]);
                    //mat4.transform(vec);
                    
                    System.out.println(" vec " + i + " " + orderedVec.get(i) + " x: " + vec.x + " " + vec.y + " " + vec.z );
                    
                    // Calculate vert (and edge connected verts) angle delta from horizontal impact.
                    // TODO.
                    
                    // for each edge connected to this vec, process movement deformation, momentum dissipation.
                    double avgAngle = 0;
                    int avgAngleCount = 0;
                    double movement = 0;
                    for(int k = 0; k < edges.length; k++){
                        TriangleMesh.Edge edge = edges[k];
                        Vec3 vecCompare = null;
                        if(vertId == edge.v1){
                            vecCompare = verts[edge.v2].r;
                        }
                        if(vertId == edge.v2){
                            vecCompare = verts[edge.v1].r;
                        }
                        if(vecCompare != null && vecCompare.x > vec.x) {
                            double angle = getAngle3(vec, vecCompare);
                            System.out.println("     comp- x: " + vecCompare.x + " " + vecCompare.y + " " + vecCompare.z + "  a: " + angle);
                            
                            avgAngleCount++;
                            avgAngle += angle;
                        }
                    }
                    if(avgAngleCount > 0){
                        avgAngle = avgAngle / avgAngleCount;
                    }
                    // Move point
                    movement = 0.3 * (avgAngle) * inerta; // (avgAngle * avgAngle)  include momentum--
                    
                    inerta = inerta - (movement * 0.009); // absorb inerta from deformation of structure.
                    if(inerta < 0){
                        inerta = 0;
                    }
                    
                    // ObjectInfo meshObj
                    // triangleMesh = meshObj.getObject().convertToTriangleMesh(0.0);  object3D
                    // MeshVertex[] verts = ((TriangleMesh)triangleMesh).getVertices();
                    
                    // object3D set vec3 data
                    
                    //meshObj.getObject().
                    
                    Vec3 moved = vecPoints[ vertId ];
                    moved.x = moved.x + movement ;
                    vecPoints[ vertId ] = moved;
                    
                    // o3d.setVertexPositions(vr); // clears cache mesh
                    // obj.setObject((Object3D)o3d);
                    // obj.clearCachedMeshes();
                    // dispatchSceneChangedEvent()
                    
                    System.out.println("         Move: " + movement  + "   a: " + avgAngle + " inerta: " + inerta  );
                    
                } // for orderedVec
                
                
                ((Mesh)meshObj.getObject()).setVertexPositions(vecPoints); // todo: check object is instance of type.
                
                meshObj.clearCachedMeshes();
                
                ((LayoutWindow)window).setModified();
                ((LayoutWindow)window).updateImage();
                
                /*
                 for(int i = 0; i < edges.length; i++){
                 //System.out.println(" edge " + i );
                 Edge edge = edges[i];
                 
                 Vec3 vertex1 = verts[edge.v1].r;
                 Vec3 vertex2 = verts[edge.v2].r;
                 System.out.println(" edge v1: " + edge.v1 + " v2: " + edge.v2 +
                 " : " + vertex1.x + " " + vertex1.y + " " + vertex1.z  + " - " +
                 vertex2.x + " " + vertex2.y + " " + vertex2.z); // Vec3
                 
                 
                 }
                 */
            
        }
    }


    /*
    // calculate mesh boundary.
    // ...
    BoundingBox meshBound = meshObj.getBounds();
    Vec3 meshCenter = meshBound.getCenter();
    System.out.println(" mesh centre:  " + meshCenter.x + " " + meshCenter.y + " " + meshCenter.z);
    Vec3 meshSize = meshBound.getSize();
    System.out.println(" mesh size:  " + meshSize.x + " " + meshSize.y + " " + meshSize.z);


    //BoundingBox wallBound = wall.getBounds();
    //Vec3 wallCenter = wallBound.getCenter();
    //System.out.println(" wall:  " + wallCenter.x + " " + wallCenter.y + " " + wallCenter.z);
    //Vec3 wallSize = wallBound.getSize();
    //System.out.println(" wall size:  " + wallSize.x + " " + wallSize.y + " " + wallSize.z);

    //
    for( int i = 0; i < 1; i++ ){
        //Camera cam = view.getCamera();
        Mat4 transform;
        //transform = Mat4.translation(0, 0, -0.12);
        //meshObj.getCoords().transformCoordinates(transform);
        
        // triangleMesh
        
        // Get geometry
        //ObjectInfo meshClone = meshObj.duplicate();
        Object co = (Object)meshObj.getObject();
        if(co instanceof Mesh && meshObj.isVisible() == true){
            Mesh meshObj3d = (Mesh)meshObj.getObject();
            Vec3 [] verts = meshObj3d.getVertexPositions();
            Vec3 [] normals = meshObj3d.getNormals();
            
            //System.out.println(" verts: " + verts.length + " normals " + normals.length);
            
            //for (Vec3 vert : verts){
            //  System.out.println(" point " + vert.x + " " + vert.y + " " + vert.z);
            //  if( vert.z < 0 ){
            //    vert.z = 0;
            //  }
            //}
            
            CoordinateSystem c;
            c = layout.getCoords(meshObj);
            Vec3 origin = c.getOrigin();
            
            
            for(int j = 0; j < verts.length; j++){
                Vec3 vert = verts[j];
                
                Mat4 mat4 = c.duplicate().fromLocal();
                mat4.transform(verts[j]);
                
                mat4.transform(vert);
                
                //System.out.print("  x: " + vert.x + "   y: " + vert.y + "   z: " + vert.z + "    " +
                //                 " " +  origin.x + "   " +
                //               " n " + normals[j].x + " " + normals[j].y + " " + normals[j].z);
                
                //verts[j].z -= 0.12; // Move Object (if Z > 0)
                double z = verts[j].z;
                if( z < 0.0 ){
                    //System.out.print(" < ");
                    //verts[j].z = 0;
                } else {
                    //System.out.print(" > ");
                }
                
                //System.out.println("");
            }
            // update model
            //meshObj3d.setVertexPositions( verts ); // affects on its own
            //meshObj.setObject( (Object3D) meshObj3d);
            
        }
        // ViewerCanvas view
        ((LayoutWindow)frame).updateImage();
        
        //
        //try { Thread.sleep(250); } catch (Exception e) {}
        */
    }
      
      
    public double getAngle3(Vec3 a, Vec3 b){
      double result = 0;
      
      // Temp not accurate
      result += Math.max(a.y, b.y) - Math.min(a.y, b.y);
      result += Math.max(a.z, b.z) - Math.min(a.z, b.z);
      
      double distance = a.distance(b);
      
      result = result / distance;
      
      return result;
    }
}

