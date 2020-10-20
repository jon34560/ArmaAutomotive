/* Copyright (C) 2020 by Jon Taylor

This program is free software; you can redistribute it and/or modify it under the
terms of the GNU General Public License as published by the Free Software
Foundation; either version 2 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE.  See the GNU General Public License for more details.

*/

package armadesignstudio.fea;

import armadesignstudio.ui.*;
import buoy.event.*;
import buoy.widget.*;
import java.awt.*;
import java.util.*;
import javax.swing.JComboBox;
import armadesignstudio.object.*;
import armadesignstudio.math.*;


public class FeaPoint
{
    public int pointId;
    public Vec3 preLocation;
    public Vec3 postLocation;
    public Vector<Vec3> inputForces;
    public Vector<FeaConnection> connections;
    
    public FeaPoint()
    {
        
    }
}

