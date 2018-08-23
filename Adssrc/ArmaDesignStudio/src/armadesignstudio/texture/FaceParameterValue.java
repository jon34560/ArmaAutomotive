/* Copyright (C) 2003 by Peter Eastman

   This program is free software; you can redistribute it and/or modify it under the
   terms of the GNU General Public License as published by the Free Software
   Foundation; either version 2 of the License, or (at your option) any later version.

   This program is distributed in the hope that it will be useful, but WITHOUT ANY 
   WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
   PARTICULAR PURPOSE.  See the GNU General Public License for more details. */

package armadesignstudio.texture;

import armadesignstudio.*;
import armadesignstudio.object.*;
import java.io.*;

/** This class defines a scalar parameter who value is defined on each face of a mesh. */

public class FaceParameterValue implements ParameterValue
{
  private double value[];
  
  /** Create a new FaceParameterValue object. */
  
  public FaceParameterValue(double val[])
  {
    value = val;
  }
  
  /** Create a new FaceParameterValue for a mesh, and initialize it to appropriate default values. */
  
  public FaceParameterValue(FacetedMesh mesh, TextureParameter param)
  {
    value = new double[mesh.getFaceCount()];
    for (int i = 0; i < value.length; i++)
      value[i] = param.defaultVal;
  }
  
  /** Get the list of parameter values. */
  
  public double [] getValue()
  {
    return value;
  }
  
  /** Set the list of parameter values. */
  
  public void setValue(double val[])
  {
    value = val;
  }
  
  /** Get the value of the parameter at a particular point in a particular triangle. */
  
  public double getValue(int tri, int v1, int v2, int v3, double u, double v, double w)
  {
    return value[tri];
  }
  
  /** Get the average value of the parameter over the entire surface. */
  
  public double getAverageValue()
  {
    double avg = 0.0;
    for (int i = 0; i < value.length; i++)
      avg += value[i];
    return (avg/value.length);
  }
  
  /** Create a duplicate of this object. */
  
  public ParameterValue duplicate()
  {
    double d[] = new double [value.length];
    System.arraycopy(value, 0, d, 0, value.length);
    return new FaceParameterValue(d);
  }
  
  /** Determine whether this object represents the same set of values as another one. */
  
  public boolean equals(Object o)
  {
    if (!(o instanceof FaceParameterValue))
      return false;
    FaceParameterValue v = (FaceParameterValue) o;
    if (v.value.length != value.length)
      return false;
    for (int i = 0; i < value.length; i++)
      if (v.value[i] != value[i])
        return false;
    return true;
  }
  
  /** Write out a serialized representation of this object to a stream. */
  
  public void writeToStream(DataOutputStream out) throws IOException
  {
    out.writeInt(value.length);
    for (int i = 0; i < value.length; i++)
      out.writeDouble(value[i]);
  }
  
  /** Reconstruct a serialized object. */
  
  public FaceParameterValue(DataInputStream in) throws IOException
  {
    value = new double [in.readInt()];
    for (int i = 0; i < value.length; i++)
      value[i] = in.readDouble();
  }
}
