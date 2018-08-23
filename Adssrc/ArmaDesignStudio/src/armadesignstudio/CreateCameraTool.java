/* Copyright (C) 1999-2008 by Peter Eastman

   This program is free software; you can redistribute it and/or modify it under the
   terms of the GNU General Public License as published by the Free Software
   Foundation; either version 2 of the License, or (at your option) any later version.

   This program is distributed in the hope that it will be useful, but WITHOUT ANY 
   WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
   PARTICULAR PURPOSE.  See the GNU General Public License for more details. */

package armadesignstudio;

import armadesignstudio.animation.*;
import armadesignstudio.math.*;
import armadesignstudio.object.*;
import armadesignstudio.ui.*;
import buoy.event.*;
import java.awt.*;

/** CreateCameraTool is an EditingTool used for creating SceneCamera objects. */

public class CreateCameraTool extends EditingTool
{
  static int counter = 2;
  Point clickPoint;
  
  public CreateCameraTool(LayoutWindow fr)
  {
    super(fr);
    initButton("camera");
  }

  public void activate()
  {
    super.activate();
    theWindow.setHelpText(Translate.text("createCameraTool.helpText"));
  }

  public int whichClicks()
  {
    return ALL_CLICKS;
  }

  public String getToolTipText()
  {
    return Translate.text("createCameraTool.tipText");
  }

  public void mousePressed(WidgetMouseEvent e, ViewerCanvas view)
  {
  }
  
  public void mouseReleased(WidgetMouseEvent e, ViewerCanvas view)
  {
    Scene theScene = ((LayoutWindow) theWindow).getScene();
    Camera cam = view.getCamera();
    Vec3 orig, ydir, zdir;
    
    orig = cam.convertScreenToWorld(e.getPoint(), Camera.DEFAULT_DISTANCE_TO_SCREEN);
    ydir = new Vec3(0.0, 1.0, 0.0);
    zdir = new Vec3(0.0, 0.0, 1.0);
    ObjectInfo info = new ObjectInfo(new SceneCamera(), new CoordinateSystem(orig, zdir, ydir), "Camera "+(counter++));
    info.addTrack(new PositionTrack(info), 0);
    info.addTrack(new RotationTrack(info), 1);
    UndoRecord undo = new UndoRecord(theWindow, false);
    int sel[] = ((LayoutWindow) theWindow).getSelectedIndices();
    ((LayoutWindow) theWindow).addObject(info, undo);
    undo.addCommand(UndoRecord.SET_SCENE_SELECTION, new Object [] {sel});
    theWindow.setUndoRecord(undo);
    ((LayoutWindow) theWindow).setSelection(theScene.getNumObjects()-1);
    theWindow.updateImage();
  }
}
