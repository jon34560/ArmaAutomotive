/* Copyright (C) 1999-2009 by Peter Eastman

   This program is free software; you can redistribute it and/or modify it under the
   terms of the GNU General Public License as published by the Free Software
   Foundation; either version 2 of the License, or (at your option) any later version.

   This program is distributed in the hope that it will be useful, but WITHOUT ANY
   WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
   PARTICULAR PURPOSE.  See the GNU General Public License for more details. */

package armadesignstudio;

import armadesignstudio.math.*;
import armadesignstudio.object.*;
import armadesignstudio.ui.*;
import buoy.event.*;
import buoy.widget.*;
import java.awt.*;
import java.util.Vector;

import armadesignstudio.LayoutModeling; // JDT

/** MoveObjectTool is an EditingTool used for moving objects in a scene. */

public class MoveObjectTool extends EditingTool
{
  Point clickPoint;
  Vec3 objectPos[];
  Vector<ObjectInfo> toMove;
  ObjectInfo clickedObject;
  boolean dragged, applyToChildren = true;

  public MoveObjectTool(EditingWindow fr)
  {
    super(fr);
    initButton("move");
  }

  public void activate()
  {
    super.activate();
    theWindow.setHelpText(Translate.text("moveObjectTool.helpText"));
  }

  public int whichClicks()
  {
    return OBJECT_CLICKS;
  }

  public boolean allowSelectionChanges()
  {
    return true;
  }

  public String getToolTipText()
  {
    return Translate.text("moveObjectTool.tipText");
  }

  public void mousePressedOnObject(WidgetMouseEvent e, ViewerCanvas view, int obj)
  {
	  //System.out.println("MoveObjectTool.mousePressedOnObject()"); // JDT

    Scene theScene = theWindow.getScene();
    int i, sel[];

    toMove = new Vector<ObjectInfo>();
    clickedObject = theScene.getObject(obj);
    if (applyToChildren)
      sel = theScene.getSelectionWithChildren();
    else
      sel = theScene.getSelection();
    for (i = 0; i < sel.length; i++)
      toMove.addElement(theScene.getObject(sel[i]));
    objectPos = new Vec3 [toMove.size()];
    for (i = 0; i < objectPos.length; i++)
      {
        ObjectInfo info = toMove.elementAt(i);
        objectPos[i] = info.getCoords().getOrigin();
      }
    clickPoint = e.getPoint();
    dragged = false;
  }

/**
* mouseDragged
*
*
*/
  public void mouseDragged(final WidgetMouseEvent e, final ViewerCanvas view)
  {
	  //System.out.println("MoveObjectTool.mouseDragged()"); // JDT
	LayoutModeling layout = new LayoutModeling();
	Scene theScene = theWindow.getScene();
	//layout.setBaseDir(theScene.getDirectory() + "\\" + theScene.getName() + "_layout_data" );


    Camera cam = view.getCamera();
    Point dragPoint = e.getPoint();
    CoordinateSystem c;
    int i, dx, dy;
    Vec3 v;

    if (!dragged)
      {
        UndoRecord undo;
        theWindow.setUndoRecord(undo = new UndoRecord(theWindow, false));
        for (i = 0; i < toMove.size(); i++)
          {
            ObjectInfo info = toMove.elementAt(i);
            c = info.getCoords();
            undo.addCommand(UndoRecord.COPY_COORDS, new Object [] {c, c.duplicate()});
          }
        dragged = true;
      }
    dx = dragPoint.x - clickPoint.x;
    dy = dragPoint.y - clickPoint.y;
    if (e.isShiftDown() && !e.isControlDown())
      {
        if (Math.abs(dx) > Math.abs(dy))
          dy = 0;
        else
          dx = 0;
      }
    if (e.isControlDown())
      v = cam.getCameraCoordinates().getZDirection().times(-dy*0.01);
    else
      v = cam.findDragVector(clickedObject.getCoords().getOrigin(), dx, dy);

      //System.out.println(" To move size: " + toMove.size());
    for (i = 0; i < toMove.size(); i++)
      {
        ObjectInfo info = toMove.elementAt(i);

        // If LayoutView == cutting use seperate coordinates.
        c = info.getCoords();
        // JDT
        if(info.getLayoutView() == false){
		c = layout.getCoords(info); // Read cutting coord from file
	}
	if(info.getTubeLayoutView() == true){
		c = layout.getCoords(info); // Read cutting coord from file 	
	}

        c.setOrigin(objectPos[i].plus(v));

		//System.out.println("   -- " + info.name  );

		// JDT
		if(info.getLayoutView() == false){
			layout.saveLayout(info, c);
			info.resetLayoutCoords(c);
		}
		if(info.getTubeLayoutView() == true){
			layout.saveLayout(info, c);
                        info.resetLayoutCoords(c);	
		}
      }
    theWindow.setModified();
    theWindow.updateImage();
    theWindow.setHelpText(Translate.text("moveObjectTool.dragText",
      Math.round(v.x*1e5)/1e5+", "+Math.round(v.y*1e5)/1e5+", "+Math.round(v.z*1e5)/1e5));

      //System.out.println(" --- " + v.x + " " + v.y + " " + v.z);
  }

  public void mouseReleased(WidgetMouseEvent e, ViewerCanvas view)
  {
	//System.out.println("MoveObjectTool.mouseReleased()"); // JDT
    theWindow.getScene().applyTracksAfterModification(toMove);
    theWindow.setHelpText(Translate.text("moveObjectTool.helpText"));
    toMove = null;
    objectPos = null;
    theWindow.updateImage();
  }

    /**
     * keyPressed
     *
     * Description:
     */
  public void keyPressed(KeyPressedEvent e, ViewerCanvas view)
  {
	LayoutModeling layout = new LayoutModeling();
    Scene theScene = theWindow.getScene();
    //layout.setBaseDir(theScene.getDirectory() + "\\" + theScene.getName() + "_layout_data" );
    Camera cam = view.getCamera();
    CoordinateSystem c;
    UndoRecord undo;
    int i, sel[];
    double dx, dy;
    Vec3 v = null;
    int key = e.getKeyCode();
      
    //System.out.println("MoveObjectTool.keyPressed() " + key);

    // Pressing an arrow key is equivalent to dragging the first selected object by one pixel.

      dx = 0;
      dy = 0;
      
    if (key == KeyPressedEvent.VK_UP)
    {
      dx = 0;
      dy = -1;
    }
    else if (key == KeyPressedEvent.VK_DOWN)
    {
      dx = 0;
      dy = 1;
    }
    else if (key == KeyPressedEvent.VK_LEFT)
    {
      dx = -1;
      dy = 0;
    }
    else if (key == KeyPressedEvent.VK_RIGHT)
    {
      dx = 1;
      dy = 0;
    }
    else {
        
      return;
    }
    e.consume();
    if (applyToChildren)
      sel = theScene.getSelectionWithChildren();
    else
      sel = theScene.getSelection();
      if (sel.length == 0){
          //return;  // No objects are selected.
      }
    if (view.getSnapToGrid())
    {
      double scale = view.getGridSpacing()*view.getScale();
      if (!e.isAltDown())
        scale /= view.getSnapToSubdivisions();
      dx *= scale;
      dy *= scale;
    }
    else if (e.isAltDown())
    {
      dx *= 10;
      dy *= 10;
    }
    CoordinateSystem cameraCoords = cam.getCameraCoordinates();
    if (e.isControlDown()) {
      v = cameraCoords.getZDirection().times(-dy*0.01);
    } else if( sel.length > 0 )
    {
      Vec3 origin = theScene.getObject(sel[0]).getCoords().getOrigin();
      if (Math.abs(origin.minus(cameraCoords.getOrigin()).dot(cameraCoords.getZDirection())) < 1e-10)
      {
        // The object being moved is in the plane of the camera, so use a slightly
        // different point to avoid dividing by zero.

        origin = origin.plus(cameraCoords.getZDirection().times(cam.getClipDistance()));
      }
      v = cam.findDragVector(origin, dx, dy);
    }
    theWindow.setUndoRecord(undo = new UndoRecord(theWindow, false));
    toMove = new Vector<ObjectInfo>();
    for (i = 0; i < sel.length; i++){
      toMove.addElement(theScene.getObject(sel[i]));
    }
      
      
    for (i = 0; i < toMove.size(); i++)
    {
      ObjectInfo info = toMove.elementAt(i);
      c = info.getCoords();
      if(info.getLayoutView() == false){ // JDT
        c = layout.getCoords(info); // Read cutting coord from file
      }
      if(info.getTubeLayoutView() == true){
        c = layout.getCoords(info); // Read cutting coord from file
      }

      undo.addCommand(UndoRecord.COPY_COORDS, new Object [] {c, c.duplicate()});

      c.setOrigin(c.getOrigin().plus(v));

      // JDT
      if(info.getLayoutView() == false){
        layout.saveLayout(info, c);
        info.resetLayoutCoords(c);
      }
      if(info.getTubeLayoutView() == true){
        layout.saveLayout(info, c);
        info.resetLayoutCoords(c);
      }
      // getWindow().getScene().objectModified(objects.get(i).getObject());

    }
      
    // If an object vertex is selected, apply move transform.
    PointJoinObject pointJoin = theScene.getCreatePointJoinObject();
    
    if(pointJoin.objectA > 0){
        //System.out.println(" objectA " + pointJoin.objectA );
        
        int count = theScene.getNumObjects();
        for( i = 0; i < count; i++){
            ObjectInfo obj = theScene.getObject(i);
            if( obj.getId() == pointJoin.objectA ){
                Mesh o3d = (Mesh)obj.getObject();
                MeshVertex[] verts = o3d.getVertices();
                if(pointJoin.objectAPoint < verts.length){
                    
                    if(v == null){
                        Vec3 origin = obj.getCoords().getOrigin();
                        if (Math.abs(origin.minus(cameraCoords.getOrigin()).dot(cameraCoords.getZDirection())) < 1e-10)
                        {
                            // The object being moved is in the plane of the camera, so use a slightly
                            // different point to avoid dividing by zero.
                            
                            origin = origin.plus(cameraCoords.getZDirection().times(cam.getClipDistance()));
                        }
                        v = cam.findDragVector(origin, dx, dy);
                    }
                    
                    Vec3 vr[] = new Vec3[verts.length];
                    for(int vrx = 0; vrx < verts.length; vrx++){
                        vr[vrx] = verts[vrx].r;
                    }
                    
                    MeshVertex vm = verts[pointJoin.objectAPoint];
                    Vec3 vec = vm.r;
                    //Vec3 vx[] = new Vec3[1];
                    
                    vr[pointJoin.objectAPoint] = new Vec3(vec.x + v.x, vec.y + v.y, vec.z + v.z);
                    
                    // Update object
                    o3d.setVertexPositions(vr); // clears cache mesh
                    obj.setObject((Object3D)o3d);
                    obj.clearCachedMeshes();
                    
                    if(o3d instanceof Curve){
                        //System.out.println(" XXXXXX ");
                        //((Curve)o3d).getBounds(); // force recalculate.
                    }
                    
                    // Refresh *** NOT WORKING
                    //theScene
                    // LayoutWindow.updateImage();
                    //theWindow.updateImage();
                    
                }
            }
        }
        
        
        
    }
      if(pointJoin.objectB > 0){
          System.out.println(" objectB " + pointJoin.objectB );
          
      }
      
      
    theWindow.getScene().applyTracksAfterModification(toMove);
    theWindow.updateImage();
  }
    
    
    public void moveVertex(int objectId){
        Scene theScene = theWindow.getScene();
        
        int count = theScene.getNumObjects();
        for(int i = 0; i < count; i++){
            ObjectInfo obj = theScene.getObject(i);
            //if( obj.getId() == this.objectA ){
                
            //}
        }
    }

  /* Allow the user to set options. */

  public void iconDoubleClicked()
  {
    BCheckBox childrenBox = new BCheckBox(Translate.text("applyToUnselectedChildren"), applyToChildren);
    ComponentsDialog dlg = new ComponentsDialog(theFrame, Translate.text("moveToolTitle"),
		new Widget [] {childrenBox}, new String [] {null});
    if (!dlg.clickedOk())
      return;
    applyToChildren = childrenBox.getState();
  }
}
