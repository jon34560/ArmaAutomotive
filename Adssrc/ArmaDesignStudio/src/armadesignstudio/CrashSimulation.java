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

/** This class implements the dialog box which is used for the "Object Layout" and
    "Transform Object" commands.  It allows the user enter values for the position,
    orientation, and size of an object.  The initial values are passed to the constructor
    in values[].  If this argument is omitted, all of the fields will initially be blank.
    If transformLabels is true, the rows will be labelled "Move", "Rotate", and "Scale".
    If it is false, they will be labelled "Position", "Orientation", and "Size". */

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

  public CrashSimulation(BFrame parent)
  {
    super(parent, "FEA Crash Simulation", true);


    fields = new ValueField [9];
    layoutDialog();
    pack();
    setResizable(false);
    UIUtilities.centerDialog(this, parent);
    //fields[0].requestFocus();
    //if (show)
    setVisible(true);
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


  /**
  * process
  */
  //public void process(ObjectInfo obj){
     
  //}
}

