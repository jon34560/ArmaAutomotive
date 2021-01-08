/* Copyright (C) 2021 Jon Taylor

   This program is free software; you can redistribute it and/or modify it under the
   terms of the GNU General Public License as published by the Free Software
   Foundation; either version 2 of the License, or (at your option) any later version.

   This program is distributed in the hope that it will be useful, but WITHOUT ANY 
   WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
   PARTICULAR PURPOSE.  See the GNU General Public License for more details. */

package armadesignstudio.view;

import buoy.widget.*;
import buoy.event.*;
import armadesignstudio.*;
import armadesignstudio.ui.*;

/**
 * This is a ViewerControl for adjusting the scale of the view.
 */

public class ViewerTopButtonControl implements ViewerControl
{
  public Widget createWidget(final ViewerCanvas view)
  {
      //Button btnFront = new Button("F");
      final BButton envButton = new BButton("Top");
      
      envButton.addEventLink(CommandEvent.class, new Object()
      {
        void processEvent()
        {
            System.out.println("Top Button");
            
            // How do I know which view this is from???
            
            view.setOrientation(4); // 2 is left
            
        }
      });
      
    
      view.addEventLink(ViewChangedEvent.class, new Object() {
      void processEvent()
      {
        //if (view.isPerspective() || view.getBoundCamera() != null)
          //scaleField.setEnabled(false);
        //else
        //{
          //scaleField.setEnabled(true);
        //  if (view.getScale() != scaleField.getValue())
            //scaleField.setValue(view.getScale());
        //}
      }
    });
    
    return envButton;
  }


  public String getName()
  {
    return Translate.text("View Top");
  }
    
}
