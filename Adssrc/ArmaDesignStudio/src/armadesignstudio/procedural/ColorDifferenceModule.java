/* This is a Module which outputs the difference between two colors. */

/* Copyright (C) 2000 by Peter Eastman

   This program is free software; you can redistribute it and/or modify it under the
   terms of the GNU General Public License as published by the Free Software
   Foundation; either version 2 of the License, or (at your option) any later version.

   This program is distributed in the hope that it will be useful, but WITHOUT ANY 
   WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
   PARTICULAR PURPOSE.  See the GNU General Public License for more details. */

package armadesignstudio.procedural;

import armadesignstudio.*;
import armadesignstudio.math.*;
import armadesignstudio.ui.*;
import java.awt.*;

public class ColorDifferenceModule extends Module
{
  RGBColor color;
  boolean colorOk;
  double lastBlur;
  
  public ColorDifferenceModule(Point position)
  {
    super("-", new IOPort [] {new IOPort(IOPort.COLOR, IOPort.INPUT, IOPort.TOP, new String [] {"Color 1", '('+Translate.text("black")+')'}),
      new IOPort(IOPort.COLOR, IOPort.INPUT, IOPort.BOTTOM, new String [] {"Color 2", '('+Translate.text("black")+')'})}, 
      new IOPort [] {new IOPort(IOPort.COLOR, IOPort.OUTPUT, IOPort.RIGHT, new String [] {"Difference"})}, 
      position);
    color = new RGBColor(0.0f, 0.0f, 0.0f);
  }

  /* New point, so the color will need to be recalculated. */

  public void init(PointInfo p)
  {
    colorOk = false;
  }

  /* Calculate the difference color. */
  
  public void getColor(int which, RGBColor c, double blur)
  {
    if (colorOk && blur == lastBlur)
      {
        c.copy(color);
        return;
      }
    colorOk = true;
    lastBlur = blur;
    if (linkFrom[0] == null)
      color.setRGB(0.0f, 0.0f, 0.0f);
    else
      linkFrom[0].getColor(linkFromIndex[0], color, blur);
    if (linkFrom[1] == null)
      c.setRGB(0.0f, 0.0f, 0.0f);
    else
      linkFrom[1].getColor(linkFromIndex[1], c, blur);
    color.subtract(c);
    c.copy(color);
  }
}