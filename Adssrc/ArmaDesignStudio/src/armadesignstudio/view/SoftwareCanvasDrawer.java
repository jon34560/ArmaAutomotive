/* Copyright (C) 1999-2009 by Peter Eastman

   This program is free software; you can redistribute it and/or modify it under the
   terms of the GNU General Public License as published by the Free Software
   Foundation; either version 2 of the License, or (at your option) any later version.

   This program is distributed in the hope that it will be useful, but WITHOUT ANY 
   WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
   PARTICULAR PURPOSE.  See the GNU General Public License for more details. */

package armadesignstudio.view;

import armadesignstudio.*;
import armadesignstudio.texture.*;
import armadesignstudio.math.*;
import buoy.event.*;

import java.awt.*;
import java.awt.image.*;
import java.util.*;
import java.lang.ref.*;
import armadesignstudio.object.*; // JDT for drawing dimensionobjects.

/** This is a CanvasDrawer which implements a software renderer for generating the contents of a ViewerCanvas. */

public class SoftwareCanvasDrawer implements CanvasDrawer
{
  protected ViewerCanvas view;
  protected BufferedImage theImage;
  protected Graphics2D imageGraphics;
  protected int pixel[], zbuffer[];
  protected boolean hideBackfaces;
  protected int templatePixel[];
  protected Rectangle bounds;

  private static Vec2 reuseVec2[];
  private static WeakHashMap<Image, SoftReference<ImageRecord>> imageMap = new WeakHashMap<Image, SoftReference<ImageRecord>>();
  private static WeakHashMap<Image, SoftReference<RenderingMesh>> imageMeshMap = new WeakHashMap<Image, SoftReference<RenderingMesh>>();

  private static final int MODE_COPY = 0;
  private static final int MODE_ADD = 1;
  private static final int MODE_SUBTRACT = 2;

  public SoftwareCanvasDrawer(ViewerCanvas view)
  {
    this.view = view;
    hideBackfaces = true;
    view.addEventLink(RepaintEvent.class, this, "paint");
    if (reuseVec2 == null)
    {
      reuseVec2 = new Vec2 [10000];
      for (int i = 0; i < reuseVec2.length; i++)
        reuseVec2[i] = new Vec2();
    }
  }

  /** Set the template image. */

  public void setTemplateImage(Image im)
  {
    try
    {
      PixelGrabber pg = new PixelGrabber(im, 0, 0, -1, -1, true);
      pg.grabPixels();
      templatePixel = (int []) pg.getPixels();
    }
    catch (InterruptedException ex)
    {
      ex.printStackTrace();
    }
  }

  /** Show feedback to the user in response to a mouse drag, by drawing a Shape over the image.
      Unlike the other methods of this class, this method may be called at arbitrary times
      (though always from the event dispatch thread), not during the process of rendering
      the image. */

  public void drawDraggedShape(Shape shape)
  {
    Graphics2D g = (Graphics2D) view.getComponent().getGraphics();
    g.drawImage(theImage, 0, 0, null);
    g.setColor(ViewerCanvas.lineColor);
    g.draw(shape);
    g.dispose();
  }

  /** Get the most recent rendered image. */

  public BufferedImage getImage()
  {
    return theImage;
  }

  public void paint(RepaintEvent ev)
  {
    bounds = view.getBounds();
    prepareToRender();
    view.updateImage();
    view.getCurrentTool().drawOverlay(view);
    ev.getGraphics().drawImage(theImage, 0, 0, null);
  }

  /** Subclasses should override this to draw the contents of the canvas, but should begin
      by calling super.updateImage() to make sure the Image exists and is the right size. */

  private void prepareToRender()
  {
    if (bounds.height <= 0)
      return;
    view.prepareCameraForRendering();
    if (theImage == null || theImage.getWidth(null) != bounds.width || theImage.getHeight(null) != bounds.height)
    {
      if (bounds.width < 0 || bounds.height < 0)
        bounds.width = bounds.height = 0;
      theImage = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB_PRE);
      pixel = ((DataBufferInt) ((BufferedImage) theImage).getRaster().getDataBuffer()).getData();
      zbuffer = new int [bounds.width*bounds.height];
      if (imageGraphics != null)
        imageGraphics.dispose();
      imageGraphics = theImage.createGraphics();
      imageGraphics.setFont(view.getComponent().getFont());
    }
    int rgb = ViewerCanvas.backgroundColor.getRGB();
    for (int i = 0; i < pixel.length; i++)
    {
      pixel[i] = rgb;
      zbuffer[i] = Integer.MAX_VALUE;
    }

    // Draw the template image, if necessary.

    if (view.getTemplateShown())
    {
      int width = view.getTemplateImage().getWidth(null), height = view.getTemplateImage().getHeight(null);
      int maxi = (height < bounds.height ? height : bounds.height);
      int maxj = (width < bounds.width ? width : bounds.width);
      for (int i = 0; i < maxi; i++)
        System.arraycopy(templatePixel, width*i, pixel, bounds.width*i, maxj);
    }
  }

  /** Draw a border around the rendered image. */

  public void drawBorder()
  {
    boolean drawFocus = view.getDrawFocus();
    int i, index1, index2;

    index1 = 0;
    index2 = bounds.width-1;
    int black = 0xFF000000;
    int line = ViewerCanvas.lineColor.getRGB();
    for (i = 0; i < bounds.height; i++, index1 += bounds.width, index2 += bounds.width)
    {
      pixel[index1] = pixel[index2] = black;
      if (drawFocus)
        pixel[index1+1] = pixel[index2-1] = line;
    }
    index1 = bounds.width*(bounds.height-1);
    for (i = 1; i < bounds.width-1; i++)
    {
      pixel[i] = pixel[index1+i] = black;
      if (drawFocus)
        pixel[i+bounds.width] = pixel[index1+i-bounds.width] = line;
    }
  }

  /** Draw a horizontal line across the rendered image.  The parameters are the y coordinate
      of the line and the line color. */

  public void drawHRule(int y, Color color)
  {
    int index = y*bounds.width;
    int col = color.getRGB();
    for (int i = 0; i < bounds.width; i++, index++)
      pixel[index] = col;
  }

  /** Draw a vertical line across the rendered image.  The parameters are the x coordinate
      of the line and the line color. */

  public void drawVRule(int x, Color color)
  {
    int index = x;
    int col = color.getRGB();
    for (int i = 0; i < bounds.height; i++, index += bounds.width)
      pixel[index] = col;
  }

  /** Draw a filled box in the rendered image. */

  public void drawBox(int x, int y, int width, int height, Color color)
  {
    int col = color.getRGB();
    int maxx = x+width;
    int maxy = y+height;
    if (x < 0) x = 0;
    if (y < 0) y = 0;
    if (maxx > bounds.width) maxx = bounds.width;
    if (maxy > bounds.height) maxy = bounds.height;
    width = maxx-x;
    for (int i = y, index = y*bounds.width+x; i < maxy; i++, index += bounds.width)
      for (int j = 0; j < width; j++)
        pixel[index+j] = col;
  }

  /** Draw a set of filled boxes in the rendered image. */

  public void drawBoxes(java.util.List<Rectangle> box, Color color)
  {
    for (int i = 0; i < box.size(); i++)
    {
      Rectangle r = box.get(i);
      drawBox(r.x, r.y, r.width, r.height, color);
    }
  }

  /** Render a filled box at a specified depth in the rendered image. */

  public void renderBox(int x, int y, int width, int height, double depth, Color color)
  {
    int i, j, index, maxx, maxy, col, z;

    col = color.getRGB();
    z = (int) (depth*65535.0);
    maxx = x+width;
    maxy = y+height;
    if (x < 0) x = 0;
    if (y < 0) y = 0;
    if (maxx > bounds.width) maxx = bounds.width;
    if (maxy > bounds.height) maxy = bounds.height;
    width = maxx-x;
    for (i = y, index = y*bounds.width+x; i < maxy; i++, index += bounds.width)
      for (j = 0; j < width; j++)
        if (z <= zbuffer[index+j])
          {
            pixel[index+j] = col;
            zbuffer[index+j] = z;
          }
  }

  /** Render a set of filled boxes at specified depths in the rendered image. */

  public void renderBoxes(java.util.List<Rectangle> box, java.util.List<Double>depth, Color color)
  {
    for (int i = 0; i < box.size(); i++)
    {
      Rectangle r = box.get(i);
      renderBox(r.x, r.y, r.width, r.height, depth.get(i), color);
    }
  }

  /** Draw a line into the rendered image. */

  public void drawLine(Point p1, Point p2, Color color)
  {
    int x1, y1, x2, y2, col;
    int x, y, dx, dy, end, index, edge;

    col = color.getRGB();
    x1 = p1.x;
    y1 = p1.y;
    x2 = p2.x;
    y2 = p2.y;
    if (x1 < 0 && x2 < 0)
      return;
    if (y1 < 0 && y2 < 0)
      return;
    if (x1 >= bounds.width && x2 >= bounds.width)
      return;
    if (y1 >= bounds.height && y2 >= bounds.height)
      return;
    dx = x2 - x1;
    dy = y2 - y1;
    if (dx == 0 && dy == 0)
      return;
    if (Math.abs(dx) > Math.abs(dy))
      {
        // x is the major axis.

        if (dx > 0)
          {
            x = x1;
            y = y1<<16+32768;
            dy = (dy<<16)/dx;
            end = x2 < bounds.width ? x2 : bounds.width;
          }
        else
          {
            x = x2;
            y = y2<<16+32768;
            dy = (dy<<16)/dx;
            end = x1 < bounds.width ? x1 : bounds.width;
          }
        if (x < 0)
          {
            y -= dy*x;
            x = 0;
          }
        edge = bounds.height<<16;
        while (x < end)
          {
            if (y >= 0 && y < edge)
              {
                index = bounds.width*(y>>16)+x;
                pixel[index] = col;
              }
            x++;
            y += dy;
          }
      }
    else
      {
        // y is the major axis.

        if (dy > 0)
          {
            x = x1<<16+32768;
            y = y1;
            dx = (dx<<16)/dy;
            end = y2 < bounds.height ? y2 : bounds.height;
          }
        else
          {
            x = x2<<16+32768;
            y = y2;
            dx = (dx<<16)/dy;
            end = y1 < bounds.height ? y1 : bounds.height;
          }
        if (y < 0)
          {
            x -= dx*y;
            y = 0;
          }
        edge = bounds.width<<16;
        while (y < end)
          {
            if (x >= 0 && x < edge)
              {
                index = y*bounds.width+(x>>16);
                pixel[index] = col;
              }
            x += dx;
            y++;
          }
      }
  }

  /** Render a line into the image.
      @param p1     the first endpoint of the line
      @param p2     the second endpoint of the line
      @param cam    the camera from which to draw the line
      @param color  the line color
  */

  public void renderLine(Vec3 p1, Vec3 p2, Camera cam, Color color)
  {
    if (cam.isPerspective())
    {
      double z1 = cam.getObjectToView().timesZ(p1);
      double z2 = cam.getObjectToView().timesZ(p2);
      double clip = cam.getClipDistance();
      if (z1 < clip)
      {
        if (z2 < clip)
          return;
        double f = ((double) (clip-z1))/(z2-z1);
        p1 = new Vec3(p1.x+f*(p2.x-p1.x), p1.y+f*(p2.y-p1.y), p1.z+f*(p2.z-p1.z));
      }
      else if (z2 < clip)
      {
        double f = ((double) (clip-z2))/(z1-z2);
        p2 = new Vec3(p2.x+f*(p1.x-p2.x), p2.y+f*(p1.y-p2.y), p2.z+f*(p1.z-p2.z));
      }
    }
    renderLine(cam.getObjectToScreen().timesXY(p1), cam.getObjectToView().timesZ(p1),
        cam.getObjectToScreen().timesXY(p2), cam.getObjectToView().timesZ(p2),
        cam, color);
  }

  /** Render a line into the image.
      @param p1     the first endpoint of the line, in screen coordinates
      @param zf1    the z coordinate of the first endpoint, in view coordinates
      @param p2     the second endpoint of the line, in screen coordinates
      @param zf2    the z coordinate of the second endpoint, in view coordinates
      @param cam    the camera from which to draw the line
      @param color  the line color
  */

  public void renderLine(Vec2 p1, double zf1, Vec2 p2, double zf2, Camera cam, Color color)
  {
    int x1, y1, z1, x2, y2, z2;
    int x, y, z, dx, dy, dz, end, index, edge;
    int clip = (int) (cam.isPerspective() ? cam.getClipDistance()*65535.0 : Integer.MIN_VALUE);
    int rgb = color.getRGB();

    x1 = (int) p1.x;
    y1 = (int) p1.y;
    z1 = (int) (zf1*65535.0);
    x2 = (int) p2.x;
    y2 = (int) p2.y;
    z2 = (int) (zf2*65535.0);
    if (x1 < 0 && x2 < 0)
      return;
    if (y1 < 0 && y2 < 0)
      return;
    if (x1 >= bounds.width && x2 >= bounds.width)
      return;
    if (y1 >= bounds.height && y2 >= bounds.height)
      return;
    if (z1 < clip && z2 < clip)
      return;
    if (view.getRenderMode() == ViewerCanvas.RENDER_WIREFRAME)
    {
      drawLine(new Point(x1, y1), new Point(x2, y2), color);
      return;
    }
    dx = x2 - x1;
    dy = y2 - y1;
    dz = z2 - z1;
    if (dx == 0 && dy == 0)
      return;
    if (Math.abs(dx) > Math.abs(dy))
      {
        // x is the major axis.

        if (dx > 0)
          {
            x = x1;
            y = y1<<16+32768;
            z = z1;
            dy = (dy<<16)/dx;
            dz = dz/dx;
            end = x2 < bounds.width ? x2 : bounds.width;
          }
        else
          {
            x = x2;
            y = y2<<16+32768;
            z = z2;
            dy = (dy<<16)/dx;
            dz = dz/dx;
            end = x1 < bounds.width ? x1 : bounds.width;
          }
        if (x < 0)
          {
            y -= dy*x;
            z -= dz*x;
            x = 0;
          }
        edge = bounds.height<<16;
        while (x < end)
          {
            if (y >= 0 && y < edge && z > clip)
              {
                index = bounds.width*(y>>16)+x;
                if (z <= zbuffer[index])
                  {
                    pixel[index] = rgb;
                    zbuffer[index] = z;
                  }
              }
            x++;
            y += dy;
            z += dz;
          }
      }
    else
      {
        // y is the major axis.

        if (dy > 0)
          {
            x = x1<<16+32768;
            y = y1;
            z = z1;
            dx = (dx<<16)/dy;
            dz = dz/dy;
            end = y2 < bounds.height ? y2 : bounds.height;
          }
        else
          {
            x = x2<<16+32768;
            y = y2;
            z = z2;
            dx = (dx<<16)/dy;
            dz = dz/dy;
            end = y1 < bounds.height ? y1 : bounds.height;
          }
        if (y < 0)
          {
            x -= dx*y;
            z -= dz*y;
            y = 0;
          }
        edge = bounds.width<<16;
        while (y < end)
          {
            if (x >= 0 && x < edge && z > clip)
              {
                index = y*bounds.width+(x>>16);
                if (z <= zbuffer[index])
                  {
                    pixel[index] = rgb;
                    zbuffer[index] = z;
                  }
              }
            x += dx;
            y++;
            z += dz;
          }
      }
  }

  /** Clip a triangle to the region in front of the z clipping plane. */

  private Vec2 [] clipTriangle(Vec3 v1, Vec3 v2, Vec3 v3, double z1, double z2, double z3, Camera cam, double newz[])
  {
    double clip = cam.getClipDistance();
    Mat4 toScreen = cam.getObjectToScreen();
    boolean c1 = z1 < clip, c2 = z2 < clip, c3 = z3 < clip;
    Vec3 u1, u2, u3, u4;
    int clipCount = 0;

    if (c1) clipCount++;
    if (c2) clipCount++;
    if (c3) clipCount++;
    if (clipCount == 2)
      {
        // Two vertices need to be clipped.

        if (!c1)
          {
            u1 = v1;
            newz[0] = z1;
            double f2 = (z1-clip)/(z1-z2), f1 = 1.0-f2;
            u2 = new Vec3(f1*v1.x+f2*v2.x, f1*v1.y+f2*v2.y, f1*v1.z+f2*v2.z);
            newz[1] = f1*z1 + f2*z2;
            f2 = (z1-clip)/(z1-z3);
            f1 = 1.0-f2;
            u3 = new Vec3(f1*v1.x+f2*v3.x, f1*v1.y+f2*v3.y, f1*v1.z+f2*v3.z);
            newz[2] = f1*z1 + f2*z3;
          }
        else if (!c2)
          {
            u2 = v2;
            newz[1] = z2;
            double f2 = (z2-clip)/(z2-z3), f1 = 1.0-f2;
            u3 = new Vec3(f1*v2.x+f2*v3.x, f1*v2.y+f2*v3.y, f1*v2.z+f2*v3.z);
            newz[2] = f1*z2 + f2*z3;
            f2 = (z2-clip)/(z2-z1);
            f1 = 1.0-f2;
            u1 = new Vec3(f1*v2.x+f2*v1.x, f1*v2.y+f2*v1.y, f1*v2.z+f2*v1.z);
            newz[0] = f1*z2 + f2*z1;
          }
        else
          {
            u3 = v3;
            newz[2] = z3;
            double f2 = (z3-clip)/(z3-z1), f1 = 1.0-f2;
            u1 = new Vec3(f1*v3.x+f2*v1.x, f1*v3.y+f2*v1.y, f1*v3.z+f2*v1.z);
            newz[0] = f1*z3 + f2*z1;
            f2 = (z3-clip)/(z3-z2);
            f1 = 1.0-f2;
            u2 = new Vec3(f1*v3.x+f2*v2.x, f1*v3.y+f2*v2.y, f1*v3.z+f2*v2.z);
            newz[1] = f1*z3 + f2*z2;
          }
        return new Vec2 [] {toScreen.timesXY(u1), toScreen.timesXY(u2), toScreen.timesXY(u3)};
      }

    // Only one vertex needs to be clipped, resulting in a quad.

    if (c1)
      {
        u1 = v2;
        newz[0] = z2;
        u2 = v3;
        newz[1] = z3;
        double f1 = (z2-clip)/(z2-z1), f2 = 1.0-f1;
        u3 = new Vec3(f1*v1.x+f2*v2.x, f1*v1.y+f2*v2.y, f1*v1.z+f2*v2.z);
        newz[2] = f1*z1 + f2*z2;
        f1 = (z3-clip)/(z3-z1);
        f2 = 1.0-f1;
        u4 = new Vec3(f1*v1.x+f2*v3.x, f1*v1.y+f2*v3.y, f1*v1.z+f2*v3.z);
        newz[3] = f1*z1 + f2*z3;
      }
    else if (c2)
      {
        u1 = v3;
        newz[0] = z3;
        u2 = v1;
        newz[1] = z1;
        double f1 = (z3-clip)/(z3-z2), f2 = 1.0-f1;
        u3 = new Vec3(f1*v2.x+f2*v3.x, f1*v2.y+f2*v3.y, f1*v2.z+f2*v3.z);
        newz[2] = f1*z2 + f2*z3;
        f1 = (z1-clip)/(z1-z2);
        f2 = 1.0-f1;
        u4 = new Vec3(f1*v2.x+f2*v1.x, f1*v2.y+f2*v1.y, f1*v2.z+f2*v1.z);
        newz[3] = f1*z2 + f2*z1;
      }
    else
      {
        u1 = v1;
        newz[0] = z1;
        u2 = v2;
        newz[1] = z2;
        double f1 = (z1-clip)/(z1-z3), f2 = 1.0-f1;
        u3 = new Vec3(f1*v3.x+f2*v1.x, f1*v3.y+f2*v1.y, f1*v3.z+f2*v1.z);
        newz[2] = f1*z3 + f2*z1;
        f1 = (z2-clip)/(z2-z3);
        f2 = 1.0-f1;
        u4 = new Vec3(f1*v3.x+f2*v2.x, f1*v3.y+f2*v2.y, f1*v3.z+f2*v2.z);
        newz[3] = f1*z3 + f2*z2;
      }
    return new Vec2 [] {toScreen.timesXY(u1), toScreen.timesXY(u2), toScreen.timesXY(u3), toScreen.timesXY(u4)};
  }

  /** Render a wireframe object. */

  public void renderWireframe(WireframeMesh mesh, Camera cam, Color color)
  {
    Vec3 vert[] = mesh.vert;
    int from[] = mesh.from;
    int to[] = mesh.to;
    for (int i = 0; i < from.length; i++)
      renderLine(vert[from[i]], vert[to[i]], cam, color);
  }
    
  /**
   * renderDimensionObject
   *
   * Description: Draw the dimension based on
   *    Horizontal or vertical measurment.
   */
  public void renderDimensionObject( ObjectInfo obj, Camera theCamera ){
      //System.out.println("    software renderDimensionObject");
      // Draw dimension
      
      // Calculate distance between points 0 and 1.
      double distance = 0;
      
      
      // renderLine(vert[from[i]], vert[to[i]], cam, color);
      
      // TODO **********
      ObjectInfo objClone = obj.duplicate();
      LayoutModeling layout = new LayoutModeling();
      CoordinateSystem c;
      c = layout.getCoords(objClone);
      double scale = 1.0;
      Mesh mesh = (Mesh) objClone.getObject(); // Object3D
      Vec3 [] verts = mesh.getVertexPositions();
      
      if(verts.length >= 2){
          distance = verts[0].distance2(verts[1]);
          //System.out.println("distance: " + distance);
          //double d = dv3.vert.distance(midv1.vert);
          
          //renderNumber(2, verts[2], theCamera);
          double width = (Math.max(verts[0].x, verts[1].x) - Math.min(verts[0].x, verts[1].x));
          double height = (Math.max(verts[0].y, verts[1].y) - Math.min(verts[0].y, verts[1].y));
          double distanceScale = width / 3; // one third
          boolean vertical = false;
          if(height > width){
              vertical = true;
              distanceScale = height / 3;
          }
          
          // line 1
          Vec3 vert1 = new Vec3( verts[1].x, verts[1].y, verts[1].z); // point 2
          Vec3 vert2 = new Vec3( verts[2].x, verts[2].y, verts[2].z); // point 3
          renderLine(vert1, vert2, theCamera, new Color(0.2f, 0.2f, 0.2f));
          
          // Line 2
          double line2Width = verts[2].x - verts[1].x;
          double line2Height = verts[2].y - verts[1].y;
          Vec3 vert3 = new Vec3( verts[0].x, verts[0].y, verts[0].z); // point 1
          Vec3 vert4 = new Vec3( verts[0].x + line2Width, verts[0].y + line2Height, verts[0].z); // constructed point
          renderLine(vert3, vert4, theCamera, new Color(0.2f, 0.2f, 0.2f));
          
          // Line 3
          renderLine(vert2, vert4, theCamera, new Color(0.2f, 0.2f, 0.2f));
          
          // Anotation
          double annotationX = ( ( Math.max(verts[0].x, verts[1].x) - Math.min(verts[0].x, verts[1].x)) / 2 ) + Math.min(verts[0].x, verts[1].x);
          //double x = verts[0].x - verts[1].x;
          Vec3 annotationLocation = new Vec3(annotationX, verts[2].y, verts[2].z );
          renderDouble(distance, annotationLocation, distanceScale, theCamera);
          
      }
      /*
      for (Vec3 vert : verts){
          // Transform vertex points around object loc/rot.
          Mat4 mat4 = c.duplicate().fromLocal();
          mat4.transform(vert);
          
          // Apply scale
          vert.x = vert.x * scale;
          vert.y = vert.y * scale;
          vert.z = vert.z * scale;
          
          double x = vert.x; // + origin.x;
          double y = vert.y; // + origin.y;
          double z = vert.z; // + origin.z;
          
          Vec3 vert1 = new Vec3( x, y, z);
          Vec3 vert2 = new Vec3( x + 0.2, y + 0.2, z + 0.0);
          
          //renderLine(vert1, vert2, theCamera, new Color(0.0f, 0.0f, 0.9f) );
          
          //System.out.println("         x " + x + " y: " + y + " z: " + z );
          
      }
       */
          
  }

  
  // 
  // Angled 
  //  
  public void renderDimensionLinearObject( ObjectInfo obj, Camera theCamera ){
    // Calculate distance between points 0 and 1.
      double distance = 0;

      // TODO **********
      ObjectInfo objClone = obj.duplicate();
      LayoutModeling layout = new LayoutModeling();
      CoordinateSystem c;
      c = layout.getCoords(objClone);
      double scale = 1.0;
      Mesh mesh = (Mesh) objClone.getObject(); // Object3D
      Vec3 [] verts = mesh.getVertexPositions();

      if(verts.length >= 2){
          distance = verts[0].distance2(verts[1]);
          //System.out.println("distance: " + distance);
          //double d = dv3.vert.distance(midv1.vert);

          //renderNumber(2, verts[2], theCamera);
          double width = (Math.max(verts[0].x, verts[1].x) - Math.min(verts[0].x, verts[1].x));
          double height = (Math.max(verts[0].y, verts[1].y) - Math.min(verts[0].y, verts[1].y));
          double distanceScale = width / 3; // one third
          boolean vertical = false;
          if(height > width){
              vertical = true;
              distanceScale = height / 3;
          }

          // line 1
          Vec3 vert1 = new Vec3( verts[1].x, verts[1].y, verts[1].z); // point 2
          Vec3 vert2 = new Vec3( verts[2].x, verts[2].y, verts[2].z); // point 3
          renderLine(vert1, vert2, theCamera, new Color(0.2f, 0.2f, 0.2f));

          // Line 2
          double line2Width = verts[2].x - verts[1].x;
          double line2Height = verts[2].y - verts[1].y;
          Vec3 vert3 = new Vec3( verts[0].x, verts[0].y, verts[0].z); // point 1
          Vec3 vert4 = new Vec3( verts[0].x + line2Width, verts[0].y + line2Height, verts[0].z); // constructed point
          renderLine(vert3, vert4, theCamera, new Color(0.2f, 0.2f, 0.2f));

          // Line 3
          renderLine(vert2, vert4, theCamera, new Color(0.2f, 0.2f, 0.2f));

          // Anotation
          double annotationX = ( ( Math.max(verts[0].x, verts[1].x) - Math.min(verts[0].x, verts[1].x)) / 2 ) + Math.min(verts[0].x, verts[1].x);
          //double x = verts[0].x - verts[1].x;
          Vec3 annotationLocation = new Vec3(annotationX, verts[2].y, verts[2].z );
          renderDouble(distance, annotationLocation, distanceScale, theCamera);

      } 
  }
    
    public void renderLabelObject(ObjectInfo obj, Camera theCamera){
        double distance = 0;
        
        
        // renderLine(vert[from[i]], vert[to[i]], cam, color);
        
        // TODO **********
        ObjectInfo objClone = obj.duplicate();
        LayoutModeling layout = new LayoutModeling();
        CoordinateSystem c;
        c = layout.getCoords(objClone);
        double scale = 1.0;
        Mesh mesh = (Mesh) objClone.getObject(); // Object3D
        Vec3 [] verts = mesh.getVertexPositions();
        
        if(verts.length >= 2){
            distance = verts[0].distance2(verts[1]);
            //System.out.println("distance: " + distance);
            //double d = dv3.vert.distance(midv1.vert);
            
            //renderNumber(2, verts[2], theCamera);
            double width = (Math.max(verts[0].x, verts[1].x) - Math.min(verts[0].x, verts[1].x));
            double height = (Math.max(verts[0].y, verts[1].y) - Math.min(verts[0].y, verts[1].y));
            double distanceScale = width / 1.1; //
            boolean vertical = false;
            if(height > width){
                vertical = true;
                distanceScale = height / 1.1;
            }
            
            // line 1
            Vec3 vert3 = new Vec3( verts[0].x, verts[0].y, verts[0].z); // point 1
            Vec3 vert1 = new Vec3( verts[1].x, verts[1].y, verts[1].z); // point 2
            Vec3 vert2 = new Vec3( verts[2].x, verts[2].y, verts[2].z); // point 3
            renderLine(vert1, vert3, theCamera, new Color(0.2f, 0.2f, 0.2f));
            renderLine(vert1, vert2, theCamera, new Color(0.2f, 0.2f, 0.2f));
            
            
            // Line 2
            //double line2Width = verts[2].x - verts[1].x;
            //double line2Height = verts[2].y - verts[1].y;
            
            //Vec3 vert4 = new Vec3( verts[0].x + line2Width, verts[0].y + line2Height, verts[0].z); // constructed point
            //renderLine(vert3, vert4, theCamera, new Color(0.2f, 0.2f, 0.2f));
            
            // Line 3
            //renderLine(vert2, vert4, theCamera, new Color(0.2f, 0.2f, 0.2f));
            
            LabelObject labelObject = (LabelObject)mesh;
            
            // Anotation
            double annotationX = ( ( Math.max(verts[1].x, verts[2].x) - Math.min(verts[1].x, verts[2].x)) / 2 ) + Math.min(verts[0].x, verts[1].x);
            //double x = verts[0].x - verts[1].x;
            Vec3 annotationLocation = new Vec3(annotationX, verts[2].y, verts[2].z );
            renderString( labelObject.labelText , annotationLocation, distanceScale, theCamera);
            
        }
    }
    
    /**
     * renderDouble
     *
     * Description:
     */
    public void renderDouble(double number, Vec3 location, double scale, Camera theCamera){
        double xOffset = 0;
        String numberAsString = Double.toString(number);
        for(int i = 0; i < numberAsString.length() && i < 5 ; i++){
            char digit = numberAsString.charAt(i);
            //System.out.println(" digit " + digit);
            
            if( Character.isDigit(digit) ){
                int digitNumber = Character.getNumericValue(digit);
                //System.out.println("number  " + digitNumber );
                
                Vec3 offsetLocation = new Vec3(location.x + xOffset - (0.3*2*scale), location.y, location.z);
                renderNumber( digitNumber, offsetLocation, scale, theCamera);
            } else {
                // Decimal point
                Vec3 offsetLocation = new Vec3(location.x + xOffset - (0.3*2*scale), location.y, location.z);
                renderNumber( 99, offsetLocation, scale, theCamera);
            }
            
            xOffset += 0.3;
        }
    }
   
    /**
    * renderString
    *
    * Description: render string to screen.
    */ 
    public void renderString(String text, Vec3 location, double scale, Camera theCamera){
        if(text == null){
            return;
        }
        double xOffset = 0;
        //String numberAsString = Double.toString(number);
        for(int i = 0; i < text.length(); i++){
            char digit = text.charAt(i);
            //System.out.println(" digit " + digit);
            
            //if( Character.isDigit(digit) ){
                //int digitNumber = Character.getNumericValue(digit);
                //System.out.println("number  " + digitNumber );
                
                Vec3 offsetLocation = new Vec3(location.x + xOffset - (0.3*2*scale), location.y, location.z);
                renderLetter( digit, offsetLocation, scale, theCamera);
            //} else {
            //    // Decimal point
            //    Vec3 offsetLocation = new Vec3(location.x + xOffset - (0.3*2*scale), location.y, location.z);
            //    renderNumber( 99, offsetLocation, scale, theCamera);
            //}
            // ******
            
            xOffset += 0.3;
        }
    }
    
    /**
     * renderNumber
     *
     * Description: render a number at a specified location.
     *
     * TODO: set scale based on screen scale zoomlevel.
     */
    public void renderNumber(int number, Vec3 location, double scale, Camera theCamera){
        Vec3[] vert;
        
        if(number == 0){
            vert = new Vec3[9];
            vert[0] = new Vec3(location.x + 0.001232328231617008, location.y + 0.1865244068166367,  location.z + 0.0);
            vert[1] = new Vec3(location.x + -0.09654041028952115, location.y + 0.16537109953360635,  location.z + 0.0);
            vert[2] = new Vec3(location.x + -0.11939983935707606, location.y + 0.0013190187780338651,  location.z + 0.0);
            vert[3] = new Vec3(location.x + -0.10511069575574364, location.y + -0.15814183762063375,  location.z + 0.0);
            vert[4] = new Vec3(location.x + -0.003064817106157663, location.y + -0.1934755931833633,  location.z + 0.0);
            vert[5] = new Vec3(location.x + 0.09933849017687268, location.y + -0.15969991967853797,  location.z + 0.0);
            vert[6] = new Vec3(location.x + 0.12204587864958609, location.y + 0.003254201671876076,  location.z + 0.0);
            vert[7] = new Vec3(location.x + 0.09143571583610936, location.y + 0.16483195593227395,  location.z + 0.0);
            vert[8] = new Vec3(location.x + 0.0032702051447607463, location.y + 0.18631901877803386,  location.z + 0.0);
        } else if( number == 1 ) {
            vert = new Vec3[3];
            vert[0] = new Vec3(location.x + -0.023328960135426565, location.y + 0.13752913324483362,  location.z + 0.0);
            vert[1] = new Vec3(location.x + 0.0031536869302545324, location.y + 0.18466114694878205,  location.z + 0.0);
            vert[2] = new Vec3(location.x + -0.0012614747721018237, location.y + -0.18687080858707442,  location.z + 0.0);
        } else if( number == 2 ) {
            // 2
            vert = new Vec3[7];
            vert[0] = new Vec3(location.x +-0.07428885348479253, location.y + 0.052351699750206544,   location.z + 0.0);
            vert[1] = new Vec3(location.x +-0.04384024242853486,  location.y + 0.1091701153778919,  location.z + 0.0);
            vert[2] = new Vec3(location.x +0.05816460363660987,  location.y + 0.11464620743260236, location.z + 0.0);
            vert[3] = new Vec3(location.x +0.0974080474425363, location.y + 0.03924706217355956, location.z + 0.0);
            vert[4] = new Vec3(location.x +-0.07244766306878149,  location.y + -0.10455745365633481,  location.z + 0.0);
            vert[5] = new Vec3(location.x +-0.06808218266954454, location.y +-0.12750458204177792,   location.z + 0.0);
            vert[6] = new Vec3(location.x +0.07547089370795024,  location.y +-0.12306036752488851,  location.z + 0.0);
        
        } else if( number == 3 ) {
            // 3
            vert = new Vec3[15];
            vert[0] = new Vec3(location.x + -0.11393887536345805 , location.y + 0.10927219681853773 , location.z + 0.0);
            vert[1] = new Vec3(location.x + -0.07528427041309263 , location.y + 0.17159114410725165 , location.z + 0.0);
            vert[2] = new Vec3(location.x + 0.007621329162712173 , location.y + 0.18891623039569275 , location.z + 0.0);
            vert[3] = new Vec3(location.x + 0.07402037110608053 , location.y + 0.17563995176356773 , location.z + 0.0);
            vert[4] = new Vec3(location.x + 0.11416361595197853 , location.y + 0.13930243801543066 , location.z + 0.0);
            vert[5] = new Vec3(location.x + 0.11310421230662628 , location.y + 0.051076137744275316 , location.z + 0.0);
            vert[6] = new Vec3(location.x + 0.023978865311616453 , location.y + 0.0050711565767534135 , location.z + 0.0);
            vert[7] = new Vec3(location.x + -0.03230426528022766 , location.y + -0.005026980884105515 , location.z + 0.0);
            vert[8] = new Vec3(location.x + 0.020239305954960526 , location.y + -0.015235015319287866 , location.z + 0.0);
            vert[9] = new Vec3(location.x + 0.11581585659272241 , location.y + -0.049060602659947446 , location.z + 0.0);
            vert[10] = new Vec3(location.x + 0.1168530301693806 , location.y + -0.13784733491765647 , location.z + 0.0);
            vert[11] = new Vec3(location.x + 0.06956594255442959 , location.y + -0.16916230176399746 , location.z + 0.0);
            vert[12] = new Vec3(location.x + -0.0036677225279931423 , location.y + -0.18706006898642874 , location.z + 0.0);
            vert[13] = new Vec3(location.x + -0.07469977354709814 , location.y + -0.1727309483310295 , location.z + 0.0);
            vert[14] = new Vec3(location.x + -0.11342817137684026 , location.y + -0.11151997306473801 , location.z + 0.0);
        
        } else if( number == 4 ) {
            // 4
        
            vert = new Vec3[4];
            vert[0] = new Vec3(location.x + -0.11266916341413132 , location.y + 0.17635047008893945 , location.z + 0.0);
            vert[1] = new Vec3(location.x + -0.1067585326090301 , location.y + 0.010518873263086384 , location.z + 0.0);
            vert[2] = new Vec3(location.x + -0.08018147138244289 , location.y + -0.02152545103411188 , location.z + 0.0);
            vert[3] = new Vec3(location.x + 0.11339586920158826 , location.y + -0.015197776360518616 , location.z + 0.0);
            
            vert = scaleVec3(vert, scale);
            int i, from[], to[];
            from = new int [vert.length-1];
            to = new int [vert.length-1];
            for (i = 0; i < vert.length-1; i++)
            {
                from[i] = i;
                to[i] = i+1;
            }
            WireframeMesh mesh = new WireframeMesh(vert, from, to);
            renderWireframe(mesh, theCamera, new Color(0.0f, 0.0f, 0.0f));
            
            vert = new Vec3[2];
            vert[0] = new Vec3(location.x + 0.0035128950004507387 , location.y + 0.1862298734272005 , location.z + 0.0);
            vert[1] = new Vec3(location.x + -0.0012983771280621315 , location.y + -0.18793624813380028 , location.z + 0.0);
        
        } else if( number == 5 ) {
            // 5
            vert = new Vec3[10];
            vert[0] = new Vec3(location.x + 0.11167324007317081 , location.y + 0.1829270981292792 , location.z + 0.0);
            vert[1] = new Vec3(location.x + -0.1165899228123699 , location.y + 0.17892155759213146 , location.z + 0.0);
            vert[2] = new Vec3(location.x + -0.11446314867717344 , location.y + 0.0024824139562920067 , location.z + 0.0);
            vert[3] = new Vec3(location.x + -0.018826191252065527 , location.y + 0.0018887184891820397 , location.z + 0.0);
            vert[4] = new Vec3(location.x + 0.08148092362744208 , location.y + -0.018614304830630424 , location.z + 0.0);
            vert[5] = new Vec3(location.x + 0.11529610590137462 , location.y + -0.08936882421504055 , location.z + 0.0);
            vert[6] = new Vec3(location.x + 0.08413116508831298 , location.y + -0.16253756998491564 , location.z + 0.0);
            vert[7] = new Vec3(location.x + 0.0039812564826444685 , location.y + -0.18642215135780837 , location.z + 0.0);
            vert[8] = new Vec3(location.x + -0.07878529544269242 , location.y + -0.17254765487808044 , location.z + 0.0);
            vert[9] = new Vec3(location.x + -0.11446314867717344 , location.y + -0.10870701401692706 , location.z + 0.0);
        
        } else if( number == 6 ) {
            // 6
            vert = new Vec3[13];
            vert[0] = new Vec3(location.x + 0.06178620906742023 , location.y + 0.18471689178987533 , location.z + 0.0);
            vert[1] = new Vec3(location.x + -0.07191404990920423 , location.y + 0.06694046581123916 , location.z + 0.0);
            vert[2] = new Vec3(location.x + -0.11808268451976946 , location.y + -0.03240973006436721 , location.z + 0.0);
            vert[3] = new Vec3(location.x + -0.1190438134907837 , location.y + -0.07162904468390674 , location.z + 0.0);
            vert[4] = new Vec3(location.x + -0.09592846824857218 , location.y + -0.15692743298588077 , location.z + 0.0);
            vert[5] = new Vec3(location.x + -0.023363568737642785 , location.y + -0.18329595370368992 , location.z + 0.0);
            vert[6] = new Vec3(location.x + 0.05358464699582054 , location.y + -0.16848510560698654 , location.z + 0.0);
            vert[7] = new Vec3(location.x + 0.08822335316783501 , location.y + -0.13218550000587045 , location.z + 0.0);
            vert[8] = new Vec3(location.x + 0.11727997898777365 , location.y + -0.07994490556111467 , location.z + 0.0);
            vert[9] = new Vec3(location.x + 0.10416434201147469 , location.y + -0.023886065267293137 , location.z + 0.0);
            vert[10] = new Vec3(location.x + 0.051958059258021716 , location.y + 0.011317711278189542 , location.z + 0.0);
            vert[11] = new Vec3(location.x + -0.04637597890594588 , location.y + 0.006369324682187458 , location.z + 0.0);
            vert[12] = new Vec3(location.x + -0.11402680351217602 , location.y + -0.039644131922718956 , location.z + 0.0);
        
        } else if( number == 7 ) {
            // 7
            vert = new Vec3[5];
            vert[0] = new Vec3(location.x + -0.1162870850060486 , location.y + 0.18309030405207652 , location.z + 0.0);
            vert[1] = new Vec3(location.x + 0.11083466156047186 , location.y + 0.18554916703345345 , location.z + 0.0);
            vert[2] = new Vec3(location.x + -0.003770461974042353 , location.y + 0.0012217663323763964 , location.z + 0.0);
            vert[3] = new Vec3(location.x + -0.05194272894139759 , location.y + -0.10166328300958152 , location.z + 0.0);
            vert[4] = new Vec3(location.x + -0.07868547900308254 , location.y + -0.18750399316525562 , location.z + 0.0);
        
        } else if( number == 8 ) {
            // 8
            vert = new Vec3[22];
            vert[0] = new Vec3(location.x + 0.001498590577098514 , location.y + 0.18278064960194715 , location.z + 0.0);
            vert[1] = new Vec3(location.x + -0.07057766950290731 , location.y + 0.16986979671217656 , location.z + 0.0);
            vert[2] = new Vec3(location.x + -0.10337772067923164 , location.y + 0.13744599988041328 , location.z + 0.0);
            vert[3] = new Vec3(location.x + -0.11850309144139083 , location.y + 0.08730606006954113 , location.z + 0.0);
            vert[4] = new Vec3(location.x + -0.10216603251566116 , location.y + 0.027305219060296513 , location.z + 0.0);
            vert[5] = new Vec3(location.x + -0.06196991903304895 , location.y + 0.0029869850289655437 , location.z + 0.0);
            vert[6] = new Vec3(location.x + 0.029493351664084587 , location.y + -0.007667887423472741 , location.z + 0.0);
            vert[7] = new Vec3(location.x + 0.10140221585428172 , location.y + -0.04092711807424544 , location.z + 0.0);
            vert[8] = new Vec3(location.x + 0.11585954868724016 , location.y + -0.112794519699499 , location.z + 0.0);
            vert[9] = new Vec3(location.x + 0.06952206925688814 , location.y + -0.17204285201962158 , location.z + 0.0);
            vert[10] = new Vec3(location.x + 0 , location.y + -0.19013524934329143 , location.z + 0.0);
            vert[11] = new Vec3(location.x + -0.07308397095993564 , location.y + -0.17317161505330467 , location.z + 0.0);
            vert[12] = new Vec3(location.x + -0.1175002617325727 , location.y + -0.11960546920647347 , location.z + 0.0);
            vert[13] = new Vec3(location.x + -0.10738903951450413 , location.y + -0.05459047965313818 , location.z + 0.0);
            vert[14] = new Vec3(location.x + -0.061301881103848176 , location.y + -0.012514640077754671 , location.z + 0.0);
            vert[15] = new Vec3(location.x + -0.005938934294133063 , location.y + 0 , location.z + 0.0);
            vert[16] = new Vec3(location.x + 0.06208454438565656 , location.y + 0.007374558208799073 , location.z + 0.0);
            vert[17] = new Vec3(location.x + 0.10558093057936287 , location.y + 0.0326956219489482 , location.z + 0.0);
            vert[18] = new Vec3(location.x + 0.11991233008745633 , location.y + 0.08241784485031552 , location.z + 0.0);
            vert[19] = new Vec3(location.x + 0.09814340570813132 , location.y + 0.14630407136996768 , location.z + 0.0);
            vert[20] = new Vec3(location.x + 0.05527359487868208 , location.y + 0.17208431458456516 , location.z + 0.0);
            vert[21] = new Vec3(location.x + 0 , location.y + 0.18399233776551763 , location.z + 0.0);
        
        } else if( number == 9 ) {
            // 9
            vert = new Vec3[16];
            vert[0] = new Vec3(location.x + 0.11598537356635315, location.y + 0.1813832608803004, location.z + 0.0);
            vert[1] = new Vec3(location.x + 0.003509805198556543, location.y + 0.18290051406665567, location.z + 0.0);
            vert[2] = new Vec3(location.x + -0.05896153835234658, location.y + 0.16862235867713526, location.z + 0.0);
            vert[3] = new Vec3(location.x + -0.09713454721411936, location.y + 0.13639563874649493, location.z + 0.0);
            vert[4] = new Vec3(location.x + -0.11881488281398317, location.y + 0.08715004052277914, location.z + 0.0);
            vert[5] = new Vec3(location.x + -0.11805625622080554, location.y + 0.04851218853698413, location.z + 0.0);
            vert[6] = new Vec3(location.x + -0.08775027069339843, location.y + 0.0064363489616006565, location.z + 0.0);
            vert[7] = new Vec3(location.x + -0.04345991324562636, location.y + -0.004636240400342353, location.z + 0.0);
            vert[8] = new Vec3(location.x + -0.01251802493933018, location.y + -0.00760938486590859, location.z + 0.0);
            vert[9] = new Vec3(location.x + 0.02221699633285413, location.y + -0.0016630959347761157, location.z + 0.0);
            vert[10] = new Vec3(location.x + 0.040165560873974084, location.y + 0.015294420451155075, location.z + 0.0);
            vert[11] = new Vec3(location.x + 0.06505146250141526, location.y + 0.037439599175041104, location.z + 0.0);
            vert[12] = new Vec3(location.x + 0.08171519541819107, location.y + 0.08126511349879095, location.z + 0.0);
            vert[13] = new Vec3(location.x + 0.10444794108038787, location.y + 0.1423419276776274, location.z + 0.0);
            vert[14] = new Vec3(location.x + 0.11819989143874172, location.y + 0.1791687430079118, location.z + 0.0);
            vert[15] = new Vec3(location.x + 0.007473997819311637, location.y + -0.18844122380859646, location.z + 0.0);
        } else {
            vert = new Vec3[2];
            vert[0] = new Vec3(location.x + -0.03, location.y + -0.18, location.z + 0.0);
            vert[1] = new Vec3(location.x +0.03, location.y + -0.18, location.z + 0.0);
        }
        
        vert = scaleVec3(vert, scale);
        int i, from[], to[];
        from = new int [vert.length-1];
        to = new int [vert.length-1];
        for (i = 0; i < vert.length-1; i++)
        {
            from[i] = i;
            to[i] = i+1;
        }
        WireframeMesh mesh = new WireframeMesh(vert, from, to);
        renderWireframe(mesh, theCamera, new Color(0.0f, 0.0f, 0.0f));
    }
  
    /**
    * renderLetter
    *
    */ 
    public void renderLetter(char letter, Vec3 location, double scale, Camera theCamera){
        Vec3[] vert = new Vec3[0];
        //System.out.println("Letter " + letter);
        if(letter == 'a'){
            vert = new Vec3[9];
            vert[0] = new Vec3(location.x + 0.10694793250723758, location.y + 0.02436788335607945,  location.z + 0.0);
            vert[1] = new Vec3(location.x + 0.004061313892679906, location.y + 0.04332068152191902,  location.z + 0.0);
            vert[2] = new Vec3(location.x + -0.09882530472187778, location.y + 0.01895279816583957,  location.z + 0.0);
            vert[3] = new Vec3(location.x + -0.1204856454828373, location.y + -0.09476399082919786,  location.z + 0.0);
            vert[4] = new Vec3(location.x + -0.06362725098531857, location.y + -0.17869781127791595,  location.z + 0.0);
            vert[5] = new Vec3(location.x + 0.060919708390198626, location.y + -0.17599026868279602,  location.z + 0.0);
            vert[6] = new Vec3(location.x + 0.09611776212675782, location.y + -0.12725450197063712,  location.z + 0.0);
            vert[7] = new Vec3(location.x + 0.10153284731699772, location.y + 0.01895279816583957,  location.z + 0.0);
            vert[8] = new Vec3(location.x + 0.1150705602925974, location.y + -0.17869781127791595,  location.z + 0.0);
            vert = scaleVec3(vert, scale);
            drawPoints(vert, theCamera);
        } else if( letter == 'b' ) {
            vert = new Vec3[9];
            vert[0] = new Vec3(location.x + -0.12048564548283727, location.y + 0.18411289646815585,  location.z + 0.0);
            vert[1] = new Vec3(location.x + -0.12048564548283727, location.y + -0.1814053538730359,  location.z + 0.0);
            vert[2] = new Vec3(location.x + -0.09070267693651793, location.y + -0.02436788335607945,  location.z + 0.0);
            vert[3] = new Vec3(location.x + -0.014891484273159665, location.y + 0.0054150851902398794,  location.z + 0.0);
            vert[4] = new Vec3(location.x + 0.08258004915115813, location.y + -0.010830170380479759,  location.z + 0.0);
            vert[5] = new Vec3(location.x + 0.10424038991211765, location.y + -0.09205644823407792,  location.z + 0.0);
            vert[6] = new Vec3(location.x + 0.07716496396091826, location.y + -0.16786764089743622,  location.z + 0.0);
            vert[7] = new Vec3(location.x + 0.0013537712975599664, location.y + -0.1895279816583957,  location.z + 0.0);
            vert[8] = new Vec3(location.x + -0.12048564548283727, location.y + -0.1814053538730359,  location.z + 0.0);
            vert = scaleVec3(vert, scale);
            drawPoints(vert, theCamera); 
        } else if( letter == 'c' ){
            vert = new Vec3[7];
            vert[0] = new Vec3(location.x + 0.11236301769747746, location.y + 0.0054150851902398794,  location.z + 0.0);
            vert[1] = new Vec3(location.x + 0.020306569463399538, location.y + 0.04061313892679909,  location.z + 0.0);
            vert[2] = new Vec3(location.x + -0.09070267693651794, location.y + 0.01624525557071964,  location.z + 0.0);
            vert[3] = new Vec3(location.x + -0.11777810288771735, location.y + -0.06227347968775859,  location.z + 0.0);
            vert[4] = new Vec3(location.x + -0.09611776212675784, location.y + -0.1543299279218365,  location.z + 0.0);
            vert[5] = new Vec3(location.x + -0.009476399082919786, location.y + -0.18682043906327578,  location.z + 0.0);
            vert[6] = new Vec3(location.x + 0.10694793250723758, location.y + -0.15162238532671657,  location.z + 0.0);
            vert = scaleVec3(vert, scale);
            drawPoints(vert, theCamera);
        } else if( letter == 'd' ){
            vert = new Vec3[8];
            vert[0] = new Vec3(location.x + 0.11236301769747745, location.y + 0.18411289646815582,  location.z + 0.0);
            vert[1] = new Vec3(location.x + 0.11236301769747745, location.y + -0.1814053538730359,  location.z + 0.0);
            vert[2] = new Vec3(location.x + 0.09070267693651796, location.y + -0.02978296854631933,  location.z + 0.0);
            vert[3] = new Vec3(location.x + -0.0013537712975599664, location.y + 0.027075425951199397,  location.z + 0.0);
            vert[4] = new Vec3(location.x + -0.09882530472187778, location.y + -0.0027075425951199328,  location.z + 0.0);
            vert[5] = new Vec3(location.x + -0.06091970839019863, location.y + -0.17057518349255615,  location.z + 0.0);
            vert[6] = new Vec3(location.x + 0.10694793250723758, location.y + -0.17599026868279602,  location.z + 0.0);
            vert[7] = new Vec3(location.x + 0.10694793250723758, location.y + -0.17599026868279602,  location.z + 0.0);
            vert = scaleVec3(vert, scale);
            drawPoints(vert, theCamera);

        } else if( letter == 'e' ){

            vert = new Vec3[9];
            vert[0] = new Vec3(location.x + -0.11507056029259739, location.y + -0.06768856487799847,  location.z + 0.0);
            vert[1] = new Vec3(location.x + 0.10424038991211765, location.y + -0.08664136304383804,  location.z + 0.0);
            vert[2] = new Vec3(location.x + 0.08528759174627808, location.y + -0.005415085190239879,  location.z + 0.0);
            vert[3] = new Vec3(location.x + 0.006768856487799846, location.y + 0.035198053736559196,  location.z + 0.0);
            vert[4] = new Vec3(location.x + -0.08528759174627808, location.y + 0.008122627785359812,  location.z + 0.0);
            vert[5] = new Vec3(location.x + -0.11236301769747746, location.y + -0.05956593709263865,  location.z + 0.0);
            vert[6] = new Vec3(location.x + -0.11236301769747746, location.y + -0.13808467235111688,  location.z + 0.0);
            vert[7] = new Vec3(location.x + -0.036551825034119176, location.y + -0.1814053538730359,  location.z + 0.0);
            vert[8] = new Vec3(location.x + 0.09611776212675782, location.y + -0.14891484273159664,  location.z + 0.0);
            vert = scaleVec3(vert, scale);
            drawPoints(vert, theCamera);
        } else if( letter == 'f' ){
        
            vert = new Vec3[7];
            vert[0] = new Vec3(location.x + 0.08799513434139802, location.y + 0.0974715334243178,  location.z + 0.0);
            vert[1] = new Vec3(location.x + 0.02030656946339954, location.y + 0.08664136304383804,  location.z + 0.0);
            vert[2] = new Vec3(location.x + -0.006768856487799846, location.y + 0.02166034076095951,  location.z + 0.0);
            vert[3] = new Vec3(location.x + -0.017599026868279605, location.y + -0.17599026868279602,  location.z + 0.0);
            vert[4] = new Vec3(location.x + -0.009476399082919786, location.y + -0.0351980537365592,  location.z + 0.0);
            vert[5] = new Vec3(location.x + 0.05821216579507869, location.y + -0.03249051114143927,  location.z + 0.0);
            vert[6] = new Vec3(location.x + -0.07174987877067837, location.y + -0.03249051114143927,  location.z + 0.0);
            vert = scaleVec3(vert, scale);
            drawPoints(vert, theCamera);
        } else if( letter == 'g' ){
            vert = new Vec3[11];
            vert[0] = new Vec3(location.x + 0.10424038991211765, location.y + -0.008122627785359826,  location.z + 0.0);
            vert[1] = new Vec3(location.x + 0.041966910224359055, location.y + 0.029782968546319316,  location.z + 0.0);
            vert[2] = new Vec3(location.x + -0.07716496396091825, location.y + 0.016245255570719624,  location.z + 0.0);
            vert[3] = new Vec3(location.x + -0.12319318807795722, location.y + -0.08393382044871811,  location.z + 0.0);
            vert[4] = new Vec3(location.x + -0.06904233617555844, location.y + -0.1732827260876761,  location.z + 0.0);
            vert[5] = new Vec3(location.x + 0.036551825034119176, location.y + -0.17869781127791595,  location.z + 0.0);
            vert[6] = new Vec3(location.x + 0.10965547510235753, location.y + -0.08934890563895798,  location.z + 0.0);
            vert[7] = new Vec3(location.x + 0.10153284731699772, location.y + -0.24097129096567454,  location.z + 0.0);
            vert[8] = new Vec3(location.x + 0.05550462319995875, location.y + -0.2815844298924736,  location.z + 0.0);
            vert[9] = new Vec3(location.x + -0.036551825034119176, location.y + -0.2788768872973537,  location.z + 0.0);
            vert[10] = new Vec3(location.x + -0.0798725065560382, location.y + -0.25180146134615433,  location.z + 0.0);
            vert = scaleVec3(vert, scale);
            drawPoints(vert, theCamera);
        } else if( letter == 'h' ){
            vert = new Vec3[7];
            vert[0] = new Vec3(location.x + -0.12048564548283729, location.y + 0.17599026868279602,  location.z + 0.0);
            vert[1] = new Vec3(location.x + -0.12319318807795722, location.y + -0.16786764089743622,  location.z + 0.0);
            vert[2] = new Vec3(location.x + -0.1069479325072376, location.y + -0.04061313892679908,  location.z + 0.0);
            vert[3] = new Vec3(location.x + -0.023014112058519484, location.y + 0.00812262778535982,  location.z + 0.0);
            vert[4] = new Vec3(location.x + 0.05550462319995875, location.y + -0.013537712975599695,  location.z + 0.0);
            vert[5] = new Vec3(location.x + 0.08799513434139801, location.y + -0.06227347968775859,  location.z + 0.0);
            vert[6] = new Vec3(location.x + 0.09611776212675782, location.y + -0.16516009830231626,  location.z + 0.0);
            vert = scaleVec3(vert, scale);
            drawPoints(vert, theCamera);

        } else if( letter == 'i' ){

            vert = new Vec3[2];
            vert[0] = new Vec3(location.x + 0.014891484273159665, location.y + 0.010830170380479759,  location.z + 0.0);
            vert[1] = new Vec3(location.x + -0.014891484273159665, location.y + -0.17869781127791595,  location.z + 0.0);
            vert = scaleVec3(vert, scale);
            drawPoints(vert, theCamera);

            vert = new Vec3[2];
            vert[0] = new Vec3(location.x + 0.014891484273159665, location.y + 0.12996204456575708,  location.z + 0.0);
            vert[1] = new Vec3(location.x + 0.012183941678039725, location.y + 0.10559416120967761,  location.z + 0.0);
            vert = scaleVec3(vert, scale);
            drawPoints(vert, theCamera);

        } else if( letter == 'j' ){

            vert = new Vec3[4];
            vert[0] = new Vec3(location.x + 0.10153284731699772, location.y + 0.010830170380479759,  location.z + 0.0);
            vert[1] = new Vec3(location.x + 0.09611776212675782, location.y + -0.1814053538730359,  location.z + 0.0);
            vert[2] = new Vec3(location.x + 0.060919708390198626, location.y + -0.25992408913151416,  location.z + 0.0);
            vert[3] = new Vec3(location.x + -0.012183941678039725, location.y + -0.26804671691687393,  location.z + 0.0);
            vert = scaleVec3(vert, scale);
            drawPoints(vert, theCamera);

            vert = new Vec3[2];
            vert[0] = new Vec3(location.x + 0.10424038991211765, location.y + 0.132669587160877,  location.z + 0.0);
            vert[1] = new Vec3(location.x + 0.10424038991211765, location.y + 0.10288661861455768,  location.z + 0.0);
            vert = scaleVec3(vert, scale);
            drawPoints(vert, theCamera);

        } else if( letter == 'k' ){

            vert = new Vec3[6];
            vert[0] = new Vec3(location.x + -0.11507056029259739, location.y + 0.17599026868279602,  location.z + 0.0);
            vert[1] = new Vec3(location.x + -0.12048564548283729, location.y + -0.17599026868279602,  location.z + 0.0);
            vert[2] = new Vec3(location.x + -0.11777810288771735, location.y + -0.05415085190239878,  location.z + 0.0);
            vert[3] = new Vec3(location.x + 0.017599026868279605, location.y + 0.027075425951199383,  location.z + 0.0);
            vert[4] = new Vec3(location.x + -0.11236301769747746, location.y + -0.05415085190239878,  location.z + 0.0);
            vert[5] = new Vec3(location.x + 0.03384428243899923, location.y + -0.1814053538730359,  location.z + 0.0);
            vert = scaleVec3(vert, scale);
            drawPoints(vert, theCamera);

        } else if( letter == 'l' ){

            vert = new Vec3[2];
            vert[0] = new Vec3(location.x + -0.10965547510235753, location.y + 0.1597450131120764,  location.z + 0.0);
            vert[1] = new Vec3(location.x + -0.11777810288771735, location.y + -0.1732827260876761,  location.z + 0.0);
            vert = scaleVec3(vert, scale);
            drawPoints(vert, theCamera);

        } else if( letter == 'm' ){

            vert = new Vec3[11];
            vert[0] = new Vec3(location.x + -0.11777810288771735, location.y + 0.0,  location.z + 0.0);
            vert[1] = new Vec3(location.x + -0.11507056029259739, location.y + -0.17869781127791595,  location.z + 0.0);
            vert[2] = new Vec3(location.x + -0.0934102195316379, location.y + -0.01895279816583957,  location.z + 0.0);
            vert[3] = new Vec3(location.x + -0.03384428243899923, location.y + -0.0027075425951199397,  location.z + 0.0);
            vert[4] = new Vec3(location.x + -0.006768856487799846, location.y + -0.0351980537365592,  location.z + 0.0);
            vert[5] = new Vec3(location.x + -0.006768856487799846, location.y + -0.16516009830231626,  location.z + 0.0);
            vert[6] = new Vec3(location.x + -0.014891484273159665, location.y + -0.029782968546319327,  location.z + 0.0);
            vert[7] = new Vec3(location.x + 0.009476399082919786, location.y + -0.0054150851902398794,  location.z + 0.0);
            vert[8] = new Vec3(location.x + 0.06362725098531856, location.y + -0.0027075425951199397,  location.z + 0.0);
            vert[9] = new Vec3(location.x + 0.09882530472187777, location.y + -0.029782968546319327,  location.z + 0.0);
            vert[10] = new Vec3(location.x + 0.10965547510235753, location.y + -0.17057518349255615,  location.z + 0.0); 
            vert = scaleVec3(vert, scale);
            drawPoints(vert, theCamera);

        } else if( letter == 'n' ){

            vert = new Vec3[7];
            vert[0] = new Vec3(location.x + -0.11777810288771735, location.y + 0.021660340760959518,  location.z + 0.0);
            vert[1] = new Vec3(location.x + -0.11777810288771735, location.y + -0.1732827260876761,  location.z + 0.0);
            vert[2] = new Vec3(location.x + -0.0798725065560382, location.y + 0.0027075425951199397,  location.z + 0.0);
            vert[3] = new Vec3(location.x + -0.014891484273159665, location.y + 0.021660340760959518,  location.z + 0.0);
            vert[4] = new Vec3(location.x + 0.041966910224359055, location.y + 0.01895279816583957,  location.z + 0.0);
            vert[5] = new Vec3(location.x + 0.09611776212675782, location.y + -0.02436788335607945,  location.z + 0.0);
            vert[6] = new Vec3(location.x + 0.10153284731699772, location.y + -0.16516009830231626,  location.z + 0.0);
            vert = scaleVec3(vert, scale);
            drawPoints(vert, theCamera);

        } else if( letter == 'o' ){

            vert = new Vec3[11];
            vert[0] = new Vec3(location.x + -0.012183941678039725, location.y + 0.0,  location.z + 0.0);
            vert[1] = new Vec3(location.x + -0.06904233617555844, location.y + 0.0054150851902398794,  location.z + 0.0);
            vert[2] = new Vec3(location.x + -0.10424038991211763, location.y + -0.03249051114143927,  location.z + 0.0);
            vert[3] = new Vec3(location.x + -0.11236301769747746, location.y + -0.06768856487799847,  location.z + 0.0);
            vert[4] = new Vec3(location.x + -0.11236301769747746, location.y + -0.1191318741852773,  location.z + 0.0);
            vert[5] = new Vec3(location.x + -0.060919708390198626, location.y + -0.1732827260876761,  location.z + 0.0);
            vert[6] = new Vec3(location.x + 0.041966910224359055, location.y + -0.18682043906327578,  location.z + 0.0);
            vert[7] = new Vec3(location.x + 0.09611776212675782, location.y + -0.11642433159015737,  location.z + 0.0);
            vert[8] = new Vec3(location.x + 0.10153284731699772, location.y + -0.05144330930727884,  location.z + 0.0);
            vert[9] = new Vec3(location.x + 0.060919708390198626, location.y + 0.0,  location.z + 0.0);
            vert[10] = new Vec3(location.x + 0.0013537712975599664, location.y + 0.01895279816583957,  location.z + 0.0);
            vert = scaleVec3(vert, scale);
            drawPoints(vert, theCamera);

        } else if( letter == 'p' ){

            vert = new Vec3[10];
            vert[0] = new Vec3(location.x + -0.12319318807795722, location.y + 0.0054150851902398794,  location.z + 0.0);
            vert[1] = new Vec3(location.x + -0.12590073067307717, location.y + -0.36551825034119173,  location.z + 0.0);
            vert[2] = new Vec3(location.x + -0.12048564548283729, location.y + -0.0027075425951199328,  location.z + 0.0);
            vert[3] = new Vec3(location.x + -0.041966910224359055, location.y + 0.027075425951199383,  location.z + 0.0);
            vert[4] = new Vec3(location.x + 0.0527970806048388, location.y + 0.02436788335607945,  location.z + 0.0);
            vert[5] = new Vec3(location.x + 0.10965547510235751, location.y + -0.027075425951199383,  location.z + 0.0);
            vert[6] = new Vec3(location.x + 0.10965547510235751, location.y + -0.1191318741852773,  location.z + 0.0);
            vert[7] = new Vec3(location.x + 0.05550462319995875, location.y + -0.17599026868279602,  location.z + 0.0);
            vert[8] = new Vec3(location.x + -0.052797080604838814, location.y + -0.17057518349255615,  location.z + 0.0);
            vert[9] = new Vec3(location.x + -0.10965547510235753, location.y + -0.16245255570719633,  location.z + 0.0); 
            vert = scaleVec3(vert, scale);
            drawPoints(vert, theCamera);

        } else if( letter == 'q' ){

            vert = new Vec3[11];
            vert[0] = new Vec3(location.x + 0.10153284731699772, location.y + 0.0027075425951199328,  location.z + 0.0);
            vert[1] = new Vec3(location.x + -0.0013537712975599664, location.y + 0.018952798165839557,  location.z + 0.0);
            vert[2] = new Vec3(location.x + -0.09882530472187777, location.y + 0.008122627785359812,  location.z + 0.0);
            vert[3] = new Vec3(location.x + -0.12048564548283729, location.y + -0.05144330930727884,  location.z + 0.0);
            vert[4] = new Vec3(location.x + -0.09611776212675784, location.y + -0.1407922149462368,  location.z + 0.0);
            vert[5] = new Vec3(location.x + -0.0013537712975599664, location.y + -0.18411289646815585,  location.z + 0.0);
            vert[6] = new Vec3(location.x + 0.07716496396091826, location.y + -0.1732827260876761,  location.z + 0.0);
            vert[7] = new Vec3(location.x + 0.10153284731699772, location.y + 0.0,  location.z + 0.0);
            vert[8] = new Vec3(location.x + 0.10153284731699772, location.y + -0.265339174321754,  location.z + 0.0);
            vert[9] = new Vec3(location.x + 0.15839124181451641, location.y + -0.24097129096567454,  location.z + 0.0);
            vert[10] = new Vec3(location.x + 0.19358929555107562, location.y + -0.20577323722911536,  location.z + 0.0); 
            vert = scaleVec3(vert, scale);
            drawPoints(vert, theCamera);

        } else if( letter == 'r' ){

            vert = new Vec3[5];
            vert[0] = new Vec3(location.x + -0.12319318807795722, location.y + 0.02707542595119939,  location.z + 0.0);
            vert[1] = new Vec3(location.x + -0.12860827326819707, location.y + -0.17599026868279602,  location.z + 0.0);
            vert[2] = new Vec3(location.x + -0.0934102195316379, location.y + -0.01895279816583957,  location.z + 0.0);
            vert[3] = new Vec3(location.x + -0.020306569463399538, location.y + 0.01624525557071963,  location.z + 0.0);
            vert[4] = new Vec3(location.x + 0.017599026868279605, location.y + 0.01895279816583957,  location.z + 0.0);
            vert = scaleVec3(vert, scale);
            drawPoints(vert, theCamera);

        } else if( letter == 's' ){ 

            vert = new Vec3[12];
            vert[0] = new Vec3(location.x + 0.10424038991211765, location.y + 0.027075425951199383,  location.z + 0.0);
            vert[1] = new Vec3(location.x + 0.028429197248759357, location.y + 0.040613138926799075,  location.z + 0.0);
            vert[2] = new Vec3(location.x + -0.1015328473169977, location.y + 0.03519805373655921,  location.z + 0.0);
            vert[3] = new Vec3(location.x + -0.12319318807795722, location.y + -0.01624525557071964,  location.z + 0.0);
            vert[4] = new Vec3(location.x + -0.09611776212675784, location.y + -0.06498102228287854,  location.z + 0.0);
            vert[5] = new Vec3(location.x + -0.023014112058519484, location.y + -0.0703961074731184,  location.z + 0.0);
            vert[6] = new Vec3(location.x + 0.039259367629239116, location.y + -0.0703961074731184,  location.z + 0.0);
            vert[7] = new Vec3(location.x + 0.07716496396091826, location.y + -0.09476399082919786,  location.z + 0.0);
            vert[8] = new Vec3(location.x + 0.0798725065560382, location.y + -0.1462073001364767,  location.z + 0.0);
            vert[9] = new Vec3(location.x + 0.036551825034119176, location.y + -0.16516009830231626,  location.z + 0.0);
            vert[10] = new Vec3(location.x + -0.03384428243899923, location.y + -0.1732827260876761,  location.z + 0.0);
            vert[11] = new Vec3(location.x + -0.10424038991211763, location.y + -0.15162238532671657,  location.z + 0.0);
            vert = scaleVec3(vert, scale);
            drawPoints(vert, theCamera);

        } else if( letter == 't' ){ 

            vert = new Vec3[7];
            vert[0] = new Vec3(location.x + -0.004061313892679906, location.y + 0.1028866186145577,  location.z + 0.0);
            vert[1] = new Vec3(location.x + -0.03925936762923911, location.y + -0.11642433159015736,  location.z + 0.0);
            vert[2] = new Vec3(location.x + -0.006768856487799846, location.y + -0.16516009830231626,  location.z + 0.0);
            vert[3] = new Vec3(location.x + -0.05008953800971887, location.y + -0.0974715334243178,  location.z + 0.0);
            vert[4] = new Vec3(location.x + -0.03384428243899923, location.y + 0.01895279816583957,  location.z + 0.0);
            vert[5] = new Vec3(location.x + 0.060919708390198626, location.y + 0.0054150851902398794,  location.z + 0.0);
            vert[6] = new Vec3(location.x + -0.09882530472187777, location.y + 0.01624525557071963,  location.z + 0.0);
            vert = scaleVec3(vert, scale);
            drawPoints(vert, theCamera);

        } else if( letter == 'u' ){ 

            vert = new Vec3[8];
            vert[0] = new Vec3(location.x + -0.10965547510235753, location.y + 0.02978296854631933,  location.z + 0.0);
            vert[1] = new Vec3(location.x + -0.12860827326819707, location.y + -0.11642433159015737,  location.z + 0.0);
            vert[2] = new Vec3(location.x + -0.07716496396091825, location.y + -0.16516009830231626,  location.z + 0.0);
            vert[3] = new Vec3(location.x + 0.014891484273159665, location.y + -0.1895279816583957,  location.z + 0.0);
            vert[4] = new Vec3(location.x + 0.05550462319995875, location.y + -0.1597450131120764,  location.z + 0.0);
            vert[5] = new Vec3(location.x + 0.10965547510235753, location.y + 0.010830170380479745,  location.z + 0.0);
            vert[6] = new Vec3(location.x + 0.10694793250723758, location.y + -0.17869781127791595,  location.z + 0.0);
            vert[7] = new Vec3(location.x + 0.1286082732681971, location.y + -0.2030656946339954,  location.z + 0.0);
            vert = scaleVec3(vert, scale);
            drawPoints(vert, theCamera);
        } else if( letter == 'v' ){

            vert = new Vec3[3];
            vert[0] = new Vec3(location.x + -0.11777810288771735, location.y + 0.040613138926799075,  location.z + 0.0);
            vert[1] = new Vec3(location.x + -0.004061313892679906, location.y + -0.17599026868279602,  location.z + 0.0);
            vert[2] = new Vec3(location.x + 0.10153284731699772, location.y + 0.027075425951199383,  location.z + 0.0);
            vert = scaleVec3(vert, scale);
            drawPoints(vert, theCamera);
        } else if( letter == 'w' ){ 

            vert = new Vec3[5];
            vert[0] = new Vec3(location.x + -0.12590073067307717, location.y + 0.02978296854631933,  location.z + 0.0);
            vert[1] = new Vec3(location.x + -0.07174987877067839, location.y + -0.17057518349255615,  location.z + 0.0);
            vert[2] = new Vec3(location.x + -0.025721654653639417, location.y + -0.05956593709263865,  location.z + 0.0);
            vert[3] = new Vec3(location.x + 0.04467445281947899, location.y + -0.17599026868279602,  location.z + 0.0);
            vert[4] = new Vec3(location.x + 0.10424038991211765, location.y + 0.01895279816583957,  location.z + 0.0);
            vert = scaleVec3(vert, scale);
            drawPoints(vert, theCamera);

        } else if( letter == 'x' ){

            vert = new Vec3[5];
            vert[0] = new Vec3(location.x + -0.11507056029259739, location.y + 0.02436788335607945,  location.z + 0.0);
            vert[1] = new Vec3(location.x + 0.09341021953163789, location.y + -0.1814053538730359,  location.z + 0.0);
            vert[2] = new Vec3(location.x + -0.009476399082919786, location.y + -0.08122627785359816,  location.z + 0.0);
            vert[3] = new Vec3(location.x + 0.10424038991211765, location.y + 0.01624525557071964,  location.z + 0.0);
            vert[4] = new Vec3(location.x + -0.12319318807795722, location.y + -0.1597450131120764,  location.z + 0.0);
            vert = scaleVec3(vert, scale);
            drawPoints(vert, theCamera);
        } else if( letter == 'y' ){ 

            vert = new Vec3[7];
            vert[0] = new Vec3(location.x + -0.11777810288771734, location.y + 0.029782968546319316,  location.z + 0.0);
            vert[1] = new Vec3(location.x + -0.10424038991211762, location.y + -0.09476399082919786,  location.z + 0.0);
            vert[2] = new Vec3(location.x + -0.07445742136579832, location.y + -0.1543299279218365,  location.z + 0.0);
            vert[3] = new Vec3(location.x + -0.004061313892679906, location.y + -0.1732827260876761,  location.z + 0.0);
            vert[4] = new Vec3(location.x + 0.06091970839019863, location.y + -0.12996204456575708,  location.z + 0.0);
            vert[5] = new Vec3(location.x + 0.10694793250723758, location.y + 0.0027075425951199328,  location.z + 0.0);
            vert[6] = new Vec3(location.x + -0.012183941678039725, location.y + -0.308659855843673,  location.z + 0.0);
            vert = scaleVec3(vert, scale);
            drawPoints(vert, theCamera);

        } else if( letter == 'z' ){

            vert = new Vec3[4];
            vert[0] = new Vec3(location.x + -0.11236301769747746, location.y + 0.021660340760959518,  location.z + 0.0);
            vert[1] = new Vec3(location.x + 0.1150705602925974, location.y + 0.01624525557071964,  location.z + 0.0);
            vert[2] = new Vec3(location.x + -0.11507056029259739, location.y + -0.15162238532671657,  location.z + 0.0);
            vert[3] = new Vec3(location.x + 0.10694793250723758, location.y + -0.16516009830231626,  location.z + 0.0);
            vert = scaleVec3(vert, scale);
            drawPoints(vert, theCamera);

        } else if( letter == '.' ){ 

        }

    }

    public void drawPoints(Vec3[] vert, Camera theCamera){
       if( vert.length > 0 ){
            //vert = scaleVec3(vert, scale);
            int i, from[], to[];
            from = new int [vert.length-1];
            to = new int [vert.length-1];
            for (i = 0; i < vert.length-1; i++)
            {
                from[i] = i;
                to[i] = i+1;
            }
            WireframeMesh mesh = new WireframeMesh(vert, from, to);
            renderWireframe(mesh, theCamera, new Color(0.0f, 0.0f, 0.0f));
        } 
    }
 
    public Vec3[] scaleVec3(Vec3[] in, double scale){
        Vec3[] out = new Vec3[in.length];
        for(int i = 0; i < in.length; i++){
            out[i] = new Vec3( in[i].x * scale, in[i].y * scale, in[i].z * scale );
        }
        return out;
    }
    

  /** Render an object with flat shading in subtractive (transparent) mode. */

  public void renderMeshTransparent(RenderingMesh mesh, VertexShader shader, Camera cam, Vec3 viewDir, boolean hideFace[])
  {
    Vec3 vert[] = mesh.vert;
    Vec2 pos[] = new Vec2 [vert.length];
    double z[] = new double [vert.length], clip = cam.getClipDistance();
    double clipz[] = new double [4];
    Mat4 toView = cam.getObjectToView(), toScreen = cam.getObjectToScreen();
    float dot;
    RGBColor faceColor = new RGBColor(0.0f, 0.0f, 0.0f);
    int mode = (ViewerCanvas.backgroundColor.getGreen() > 127 ? MODE_SUBTRACT : MODE_ADD);
    RenderingTriangle tri;
    int i, v1, v2, v3;

    int numToReuse = Math.min(vert.length, reuseVec2.length);
    for (i = 0; i < numToReuse; i++)
      {
        pos[i] = toScreen.timesXY(vert[i], reuseVec2[i]);
        z[i] = toView.timesZ(vert[i]);
      }
    for (i = numToReuse; i < vert.length; i++)
      {
        pos[i] = toScreen.timesXY(vert[i]);
        z[i] = toView.timesZ(vert[i]);
      }
    for (i = 0; i < mesh.triangle.length; i++)
      {
        if (hideFace != null && hideFace[i])
          continue;
        tri = mesh.triangle[i];
        v1 = tri.v1;
        v2 = tri.v2;
        v3 = tri.v3;
        if (z[v1] < clip && z[v2] < clip && z[v3] < clip)
          continue;
        dot = (float) viewDir.dot(mesh.faceNorm[i]);
        shader.getColor(i, 0, faceColor);
        if (mode == MODE_SUBTRACT)
          faceColor.setRGB(1.0f-faceColor.getRed(), 1.0f-faceColor.getGreen(), 1.0f-faceColor.getBlue());
        else
          faceColor.setRGB(faceColor.getRed(), faceColor.getGreen(), faceColor.getBlue());
        faceColor.scale(1.0f-0.8f*Math.abs(dot));
        if (z[v1] < clip || z[v2] < clip || z[v3] < clip)
          {
            Vec2 clipPos[] = clipTriangle(vert[v1], vert[v2], vert[v3], z[v1], z[v2], z[v3], cam, clipz);
            boolean inside = true;
            for (int j = 0; j < clipPos.length; j++)
              if (clipPos[j].x < -32767.0 || clipPos[j].x > 32767.0 || clipPos[j].y < -32767.0 || clipPos[j].y > 32767.0)
                inside = false;
            if (!inside)
              continue;
            for (int j = 0; j < clipPos.length-2; j++)
              renderFlatTriangle(clipPos[j], clipz[j], clipPos[j+1], clipz[j+1], clipPos[j+2], clipz[j+2], bounds.width, bounds.height, clip, mode, faceColor);
          }
        else
          renderFlatTriangle(pos[v1], z[v1], pos[v2], z[v2], pos[v3], z[v3], bounds.width, bounds.height, clip, mode, faceColor);
      }
  }

  /** Render a mesh to the canvas. */

  public void renderMesh(RenderingMesh mesh, VertexShader shader, Camera cam, boolean closed, boolean hideFace[])
  {
    Vec3 vert[] = mesh.vert;
    Vec2 pos[] = new Vec2 [vert.length];
    double z[] = new double [vert.length], clip = cam.getClipDistance();
    double clipz[] = new double [4];
    Mat4 toView = cam.getObjectToView(), toScreen = cam.getObjectToScreen();
    RGBColor color1 = new RGBColor(), color2 = new RGBColor(), color3 = new RGBColor();
    RGBColor color4 = new RGBColor(), color5 = new RGBColor(), color6 = new RGBColor(), color7 = new RGBColor();
    RenderingTriangle tri;
    int i, v1, v2, v3;
    boolean backface, needClipping;

    if (hideFace != null)
      closed = false;
    int numToReuse = Math.min(vert.length, reuseVec2.length);
    for (i = 0; i < numToReuse; i++)
      {
        pos[i] = toScreen.timesXY(vert[i], reuseVec2[i]);
        z[i] = toView.timesZ(vert[i]);
      }
    for (i = numToReuse; i < vert.length; i++)
      {
        pos[i] = toScreen.timesXY(vert[i]);
        z[i] = toView.timesZ(vert[i]);
      }
    for (i = 0; i < mesh.triangle.length; i++)
      {
        if (hideFace != null && hideFace[i])
          continue;
        tri = mesh.triangle[i];
        v1 = tri.v1;
        v2 = tri.v2;
        v3 = tri.v3;
        if (z[v1] < clip && z[v2] < clip && z[v3] < clip)
          continue;
        backface = ((closed && hideBackfaces) && ((pos[v2].x-pos[v1].x)*(pos[v3].y-pos[v1].y) - (pos[v2].y-pos[v1].y)*(pos[v3].x-pos[v1].x) > 0.0));
        needClipping = (z[v1] < clip || z[v2] < clip || z[v3] < clip);
        if (backface && !needClipping)
          continue;
        shader.getColor(i, 0, color1);
        if (shader.isUniformFace(i))
          {
            // This triangle is solid colored, so use the faster rendering method.

            if (needClipping)
              {
                Vec2 clipPos[] = clipTriangle(vert[v1], vert[v2], vert[v3], z[v1], z[v2], z[v3], cam, clipz);
                boolean inside = true;
                for (int j = 0; j < clipPos.length; j++)
                  if (clipPos[j].x < -32767.0 || clipPos[j].x > 32767.0 || clipPos[j].y < -32767.0 || clipPos[j].y > 32767.0)
                    inside = false;
                if (!inside)
                  continue;
                for (int j = 0; j < clipPos.length-2; j++)
                  renderFlatTriangle(clipPos[j], clipz[j], clipPos[j+1], clipz[j+1], clipPos[j+2], clipz[j+2], bounds.width, bounds.height, clip, MODE_COPY, color1);
              }
            else
              renderFlatTriangle(pos[v1], z[v1], pos[v2], z[v2], pos[v3], z[v3], bounds.width, bounds.height, clip, MODE_COPY, color1);
            continue;
          }
        shader.getColor(i, 1, color2);
        shader.getColor(i, 2, color3);
        if (needClipping)
          {
            Vec2 clipPos[] = clipSmoothTriangle(vert[v1], vert[v2], vert[v3], z[v1], z[v2], z[v3], cam, color1, color2, color3, color4, color5, color6, color7, clipz);
            boolean inside = true;
            for (int j = 0; j < clipPos.length; j++)
              if (clipPos[j].x < -32767.0 || clipPos[j].x > 32767.0 || clipPos[j].y < -32767.0 || clipPos[j].y > 32767.0)
                inside = false;
            if (!inside)
              continue;
            renderSmoothTriangle(clipPos[0], clipz[0], clipPos[1], clipz[1], clipPos[2], clipz[2], bounds.width, bounds.height, clip, color4, color5, color6);
            if (clipPos.length == 4)
              renderSmoothTriangle(clipPos[1], clipz[1], clipPos[2], clipz[2], clipPos[3], clipz[3], bounds.width, bounds.height, clip, color5, color6, color7);
          }
        else
          renderSmoothTriangle(pos[v1], z[v1], pos[v2], z[v2], pos[v3], z[v3], bounds.width, bounds.height, clip, color1, color2, color3);
      }
  }

  /** Clip a smooth shaded triangle to the region in front of the z clipping plane. */

  private Vec2 [] clipSmoothTriangle(Vec3 v1, Vec3 v2, Vec3 v3, double z1, double z2, double z3, Camera cam, RGBColor col1, RGBColor col2, RGBColor col3, RGBColor newc1, RGBColor newc2, RGBColor newc3, RGBColor newc4, double newz[])
  {
    double clip = cam.getClipDistance();
    Mat4 toScreen = cam.getObjectToScreen();
    boolean c1 = z1 < clip, c2 = z2 < clip, c3 = z3 < clip;
    Vec3 u1, u2, u3, u4;
    int clipCount = 0;

    if (c1) clipCount++;
    if (c2) clipCount++;
    if (c3) clipCount++;
    if (clipCount == 2)
      {
        // Two vertices need to be clipped.

        if (!c1)
          {
            u1 = v1;
            newz[0] = z1;
            newc1.copy(col1);
            double f2 = (z1-clip)/(z1-z2), f1 = 1.0-f2;
            u2 = new Vec3(f1*v1.x+f2*v2.x, f1*v1.y+f2*v2.y, f1*v1.z+f2*v2.z);
            newc2.setRGB(f1*col1.getRed()+f2*col2.getRed(), f1*col1.getGreen()+f2*col2.getGreen(), f1*col1.getBlue()+f2*col2.getBlue());
            newz[1] = f1*z1 + f2*z2;
            f2 = (z1-clip)/(z1-z3);
            f1 = 1.0-f2;
            u3 = new Vec3(f1*v1.x+f2*v3.x, f1*v1.y+f2*v3.y, f1*v1.z+f2*v3.z);
            newc3.setRGB(f1*col1.getRed()+f2*col3.getRed(), f1*col1.getGreen()+f2*col3.getGreen(), f1*col1.getBlue()+f2*col3.getBlue());
            newz[2] = f1*z1 + f2*z3;
          }
        else if (!c2)
          {
            u2 = v2;
            newz[1] = z2;
            newc2.copy(col2);
            double f2 = (z2-clip)/(z2-z3), f1 = 1.0-f2;
            u3 = new Vec3(f1*v2.x+f2*v3.x, f1*v2.y+f2*v3.y, f1*v2.z+f2*v3.z);
            newc3.setRGB(f1*col2.getRed()+f2*col3.getRed(), f1*col2.getGreen()+f2*col3.getGreen(), f1*col2.getBlue()+f2*col3.getBlue());
            newz[2] = f1*z2 + f2*z3;
            f2 = (z2-clip)/(z2-z1);
            f1 = 1.0-f2;
            u1 = new Vec3(f1*v2.x+f2*v1.x, f1*v2.y+f2*v1.y, f1*v2.z+f2*v1.z);
            newc1.setRGB(f1*col2.getRed()+f2*col3.getRed(), f1*col2.getGreen()+f2*col3.getGreen(), f1*col2.getBlue()+f2*col3.getBlue());
            newz[0] = f1*z2 + f2*z1;
          }
        else
          {
            u3 = v3;
            newz[2] = z3;
            newc3.copy(col3);
            double f2 = (z3-clip)/(z3-z1), f1 = 1.0-f2;
            u1 = new Vec3(f1*v3.x+f2*v1.x, f1*v3.y+f2*v1.y, f1*v3.z+f2*v1.z);
            newc1.setRGB(f1*col3.getRed()+f2*col1.getRed(), f1*col3.getGreen()+f2*col1.getGreen(), f1*col3.getBlue()+f2*col1.getBlue());
            newz[0] = f1*z3 + f2*z1;
            f2 = (z3-clip)/(z3-z2);
            f1 = 1.0-f2;
            u2 = new Vec3(f1*v3.x+f2*v2.x, f1*v3.y+f2*v2.y, f1*v3.z+f2*v2.z);
            newc2.setRGB(f1*col3.getRed()+f2*col2.getRed(), f1*col3.getGreen()+f2*col2.getGreen(), f1*col3.getBlue()+f2*col2.getBlue());
            newz[1] = f1*z3 + f2*z2;
          }
        return new Vec2 [] {toScreen.timesXY(u1), toScreen.timesXY(u2), toScreen.timesXY(u3)};
      }

    // Only one vertex needs to be clipped, resulting in a quad.

    if (c1)
      {
        u1 = v2;
        newz[0] = z2;
        newc1.copy(col2);
        u2 = v3;
        newz[1] = z3;
        newc2.copy(col3);
        double f1 = (z2-clip)/(z2-z1), f2 = 1.0-f1;
        u3 = new Vec3(f1*v1.x+f2*v2.x, f1*v1.y+f2*v2.y, f1*v1.z+f2*v2.z);
        newc3.setRGB(f1*col1.getRed()+f2*col2.getRed(), f1*col1.getGreen()+f2*col2.getGreen(), f1*col1.getBlue()+f2*col2.getBlue());
        newz[2] = f1*z1 + f2*z2;
        f1 = (z3-clip)/(z3-z1);
        f2 = 1.0-f1;
        u4 = new Vec3(f1*v1.x+f2*v3.x, f1*v1.y+f2*v3.y, f1*v1.z+f2*v3.z);
        newc4.setRGB(f1*col1.getRed()+f2*col3.getRed(), f1*col1.getGreen()+f2*col3.getGreen(), f1*col1.getBlue()+f2*col3.getBlue());
        newz[3] = f1*z1 + f2*z3;
      }
    else if (c2)
      {
        u1 = v3;
        newz[0] = z3;
        newc1.copy(col3);
        u2 = v1;
        newz[1] = z1;
        newc2.copy(col1);
        double f1 = (z3-clip)/(z3-z2), f2 = 1.0-f1;
        u3 = new Vec3(f1*v2.x+f2*v3.x, f1*v2.y+f2*v3.y, f1*v2.z+f2*v3.z);
        newc3.setRGB(f1*col2.getRed()+f2*col3.getRed(), f1*col2.getGreen()+f2*col3.getGreen(), f1*col2.getBlue()+f2*col3.getBlue());
        newz[2] = f1*z2 + f2*z3;
        f1 = (z1-clip)/(z1-z2);
        f2 = 1.0-f1;
        u4 = new Vec3(f1*v2.x+f2*v1.x, f1*v2.y+f2*v1.y, f1*v2.z+f2*v1.z);
        newc4.setRGB(f1*col2.getRed()+f2*col1.getRed(), f1*col2.getGreen()+f2*col1.getGreen(), f1*col2.getBlue()+f2*col1.getBlue());
        newz[3] = f1*z2 + f2*z1;
      }
    else
      {
        u1 = v1;
        newz[0] = z1;
        newc1.copy(col1);
        u2 = v2;
        newz[1] = z2;
        newc2.copy(col2);
        double f1 = (z1-clip)/(z1-z3), f2 = 1.0-f1;
        u3 = new Vec3(f1*v3.x+f2*v1.x, f1*v3.y+f2*v1.y, f1*v3.z+f2*v1.z);
        newc3.setRGB(f1*col3.getRed()+f2*col1.getRed(), f1*col3.getGreen()+f2*col1.getGreen(), f1*col3.getBlue()+f2*col1.getBlue());
        newz[2] = f1*z3 + f2*z1;
        f1 = (z2-clip)/(z2-z3);
        f2 = 1.0-f1;
        u4 = new Vec3(f1*v3.x+f2*v2.x, f1*v3.y+f2*v2.y, f1*v3.z+f2*v2.z);
        newc4.setRGB(f1*col3.getRed()+f2*col2.getRed(), f1*col3.getGreen()+f2*col2.getGreen(), f1*col3.getBlue()+f2*col2.getBlue());
        newz[3] = f1*z3 + f2*z2;
      }
    return new Vec2 [] {toScreen.timesXY(u1), toScreen.timesXY(u2), toScreen.timesXY(u3), toScreen.timesXY(u4)};
  }

  /** Render a solid colored triangle. */

  private void renderFlatTriangle(Vec2 pos1, double zf1, Vec2 pos2, double zf2, Vec2 pos3, double zf3, int width, int height, double clip, int mode, RGBColor color)
  {
    int x1, y1, z1, x2, y2, z2, x3, y3, z3;
    int dx1, dx2, dy1, dy2, dz1, dz2, mx1, mx2, mz1, mz2;
    int xstart, xend, yend, zstart, zend, y, z, dz, left, right, i, index;
    int clipDist = (int) (clip*65535.0), r, g, b, red, green, blue, col;

    if (mode == MODE_COPY)
      {
        col = color.getARGB();
        red = green = blue = 0;
      }
    else
      {
        col = 0;
        red = (int) (color.getRed()*255.0f);
        green = (int) (color.getGreen()*255.0f);
        blue = (int) (color.getBlue()*255.0f);
      }
    if (pos1.y <= pos2.y && pos1.y <= pos3.y)
      {
        x1 = ((int) pos1.x) << 16;
        y1 = ((int) pos1.y);
        z1 = (int) (zf1*65535.0);
        if (pos2.y < pos3.y)
          {
            x2 = ((int) pos2.x) << 16;
            y2 = ((int) pos2.y);
            z2 = (int) (zf2*65535.0);
            x3 = ((int) pos3.x) << 16;
            y3 = ((int) pos3.y);
            z3 = (int) (zf3*65535.0);
          }
        else
          {
            x2 = ((int) pos3.x) << 16;
            y2 = ((int) pos3.y);
            z2 = (int) (zf3*65535.0);
            x3 = ((int) pos2.x) << 16;
            y3 = ((int) pos2.y);
            z3 = (int) (zf2*65535.0);
          }
      }
    else if (pos2.y <= pos1.y && pos2.y <= pos3.y)
      {
        x1 = ((int) pos2.x) << 16;
        y1 = ((int) pos2.y);
        z1 = (int) (zf2*65535.0);
        if (pos1.y < pos3.y)
          {
            x2 = ((int) pos1.x) << 16;
            y2 = ((int) pos1.y);
            z2 = (int) (zf1*65535.0);
            x3 = ((int) pos3.x) << 16;
            y3 = ((int) pos3.y);
            z3 = (int) (zf3*65535.0);
          }
        else
          {
            x2 = ((int) pos3.x) << 16;
            y2 = ((int) pos3.y);
            z2 = (int) (zf3*65535.0);
            x3 = ((int) pos1.x) << 16;
            y3 = ((int) pos1.y);
            z3 = (int) (zf1*65535.0);
          }
      }
    else
      {
        x1 = ((int) pos3.x) << 16;
        y1 = ((int) pos3.y);
        z1 = (int) (zf3*65535.0);
        if (pos1.y < pos2.y)
          {
            x2 = ((int) pos1.x) << 16;
            y2 = ((int) pos1.y);
            z2 = (int) (zf1*65535.0);
            x3 = ((int) pos2.x) << 16;
            y3 = ((int) pos2.y);
            z3 = (int) (zf2*65535.0);
          }
        else
          {
            x2 = ((int) pos2.x) << 16;
            y2 = ((int) pos2.y);
            z2 = (int) (zf2*65535.0);
            x3 = ((int) pos1.x) << 16;
            y3 = ((int) pos1.y);
            z3 = (int) (zf1*65535.0);
          }
      }
    dx1 = x3-x1;
    dy1 = y3-y1;
    dz1 = z3-z1;
    if (dy1 == 0)
      return;
    dx2 = x2-x1;
    dy2 = y2-y1;
    dz2 = z2-z1;
    mx1 = dx1/dy1;
    mz1 = dz1/dy1;
    xstart = xend = x1;
    zstart = zend = z1;
    y = y1;
    if (dy2 != 0)
      {
        mx2 = dx2/dy2;
        mz2 = dz2/dy2;
        if (y2 < 0)
          {
            xstart += mx1*dy2;
            xend += mx2*dy2;
            zstart += mz1*dy2;
            zend += mz2*dy2;
            y = y2;
          }
        else if (y < 0)
          {
            xstart -= mx1*y;
            xend -= mx2*y;
            zstart -= mz1*y;
            zend -= mz2*y;
            y = 0;
          }
        yend = (y2 < height ? y2 : height);
        index = y*width;
        while (y < yend)
          {
            if (xstart < xend)
              {
                left = xstart >> 16;
                right = xend >> 16;
                z = zstart;
                dz = zend-zstart;
              }
            else
              {
                left = xend >> 16;
                right = xstart >> 16;
                z = zend;
                dz = zstart-zend;
              }
            if (left != right)
              {
                dz /= (right-left);
                if (left < 0)
                  {
                    z -= left*dz;
                    left = 0;
                  }
                if (right > width)
                  right = width;
                if (mode == MODE_COPY)
                {
                  for (i = left; i < right; i++)
                    {
                      if (z < zbuffer[index+i] && z > clipDist)
                        {
                          pixel[index+i] = col;
                          zbuffer[index+i] = z;
                        }
                      z += dz;
                    }
                }
                else if (mode == MODE_ADD)
                {
                  for (i = left; i < right; i++)
                    {
                      if (z > clipDist)
                        {
                          r = ((pixel[index+i] & 0xFF0000) >> 16) + red;
                          g = ((pixel[index+i] & 0xFF00) >> 8) + green;
                          b = (pixel[index+i] & 0xFF) + blue;
                          if (r > 255) r = 255;
                          if (g > 255) g = 255;
                          if (b > 255) b = 255;
                          pixel[index+i] = 0xFF000000 + (r<<16) + (g<<8) + b;
                        }
                      z += dz;
                    }
                }
                else
                {
                  for (i = left; i < right; i++)
                    {
                      if (z > clipDist)
                        {
                          r = ((pixel[index+i] & 0xFF0000) >> 16) - red;
                          g = ((pixel[index+i] & 0xFF00) >> 8) - green;
                          b = (pixel[index+i] & 0xFF) - blue;
                          if (r < 0) r = 0;
                          if (g < 0) g = 0;
                          if (b < 0) b = 0;
                          pixel[index+i] = 0xFF000000 + (r<<16) + (g<<8) + b;
                        }
                      z += dz;
                    }
                }
              }
            xstart += mx1;
            zstart += mz1;
            xend += mx2;
            zend += mz2;
            index += width;
            y++;
          }
      }
    dx2 = x3-x2;
    dy2 = y3-y2;
    dz2 = z3-z2;
    if (dy2 != 0)
      {
        mx2 = dx2/dy2;
        mz2 = dz2/dy2;
        xend = x2;
        zend = z2;
        if (y < 0)
          {
            xstart -= mx1*y;
            xend -= mx2*y;
            zstart -= mz1*y;
            zend -= mz2*y;
            y = 0;
          }
        yend = (y3 < height ? y3 : height);
        index = y*width;
        while (y < yend)
          {
            if (xstart < xend)
              {
                left = xstart >> 16;
                right = xend >> 16;
                z = zstart;
                dz = zend-zstart;
              }
            else
              {
                left = xend >> 16;
                right = xstart >> 16;
                z = zend;
                dz = zstart-zend;
              }
            if (left != right)
              {
                dz /= (right-left);
                if (left < 0)
                  {
                    z -= left*dz;
                    left = 0;
                  }
                if (right > width)
                  right = width;
                if (mode == MODE_COPY)
                {
                  for (i = left; i < right; i++)
                    {
                      if (z < zbuffer[index+i] && z > clipDist)
                        {
                          pixel[index+i] = col;
                          zbuffer[index+i] = z;
                        }
                      z += dz;
                    }
                }
                else if (mode == MODE_ADD)
                {
                  for (i = left; i < right; i++)
                    {
                      if (z > clipDist)
                        {
                          r = ((pixel[index+i] & 0xFF0000) >> 16) + red;
                          g = ((pixel[index+i] & 0xFF00) >> 8) + green;
                          b = (pixel[index+i] & 0xFF) + blue;
                          if (r > 255) r = 255;
                          if (g > 255) g = 255;
                          if (b > 255) b = 255;
                          pixel[index+i] = 0xFF000000 + (r<<16) + (g<<8) + b;
                        }
                      z += dz;
                    }
                }
                else
                {
                  for (i = left; i < right; i++)
                    {
                      if (z > clipDist)
                        {
                          r = ((pixel[index+i] & 0xFF0000) >> 16) - red;
                          g = ((pixel[index+i] & 0xFF00) >> 8) - green;
                          b = (pixel[index+i] & 0xFF) - blue;
                          if (r < 0) r = 0;
                          if (g < 0) g = 0;
                          if (b < 0) b = 0;
                          pixel[index+i] = 0xFF000000 + (r<<16) + (g<<8) + b;
                        }
                      z += dz;
                    }
                }
              }
            xstart += mx1;
            zstart += mz1;
            xend += mx2;
            zend += mz2;
            index += width;
            y++;
          }
      }
  }

  /** Render a triangle with smooth (interpolated) shading. */

  private void renderSmoothTriangle(Vec2 pos1, double zf1, Vec2 pos2, double zf2, Vec2 pos3, double zf3, int width, int height, double clip, RGBColor color1, RGBColor color2, RGBColor color3)
  {
    int x1, y1, z1, x2, y2, z2, x3, y3, z3;
    int dx1, dx2, dy1, dy2, dz1, dz2, mx1, mx2, mz1, mz2;
    int xstart, xend, yend, zstart, zend, y, z, dz, left, right, i, index;
    int red1, green1, blue1, red2, green2, blue2, red3, green3, blue3;
    int dred1, dred2, dgreen1, dgreen2, dblue1, dblue2, mred1, mred2, mgreen1, mgreen2, mblue1, mblue2;
    int redstart, redend, greenstart, greenend, bluestart, blueend, red, green, blue, dred, dgreen, dblue;
    int clipDist = (int) (clip*65535.0);

    if (pos1.y <= pos2.y && pos1.y <= pos3.y)
      {
        x1 = ((int) pos1.x) << 16;
        y1 = ((int) pos1.y);
        z1 = (int) (zf1*65535.0);
        red1 = (int) (color1.getRed()*65535.0f);
        green1 = (int) (color1.getGreen()*65535.0f);
        blue1 = (int) (color1.getBlue()*65535.0f);
        if (pos2.y < pos3.y)
          {
            x2 = ((int) pos2.x) << 16;
            y2 = ((int) pos2.y);
            z2 = (int) (zf2*65535.0);
            red2 = (int) (color2.getRed()*65535.0f);
            green2 = (int) (color2.getGreen()*65535.0f);
            blue2 = (int) (color2.getBlue()*65535.0f);
            x3 = ((int) pos3.x) << 16;
            y3 = ((int) pos3.y);
            z3 = (int) (zf3*65535.0);
            red3 = (int) (color3.getRed()*65535.0f);
            green3 = (int) (color3.getGreen()*65535.0f);
            blue3 = (int) (color3.getBlue()*65535.0f);
          }
        else
          {
            x2 = ((int) pos3.x) << 16;
            y2 = ((int) pos3.y);
            z2 = (int) (zf3*65535.0);
            red2 = (int) (color3.getRed()*65535.0f);
            green2 = (int) (color3.getGreen()*65535.0f);
            blue2 = (int) (color3.getBlue()*65535.0f);
            x3 = ((int) pos2.x) << 16;
            y3 = ((int) pos2.y);
            z3 = (int) (zf2*65535.0);
            red3 = (int) (color2.getRed()*65535.0f);
            green3 = (int) (color2.getGreen()*65535.0f);
            blue3 = (int) (color2.getBlue()*65535.0f);
          }
      }
    else if (pos2.y <= pos1.y && pos2.y <= pos3.y)
      {
        x1 = ((int) pos2.x) << 16;
        y1 = ((int) pos2.y);
        z1 = (int) (zf2*65535.0);
        red1 = (int) (color2.getRed()*65535.0f);
        green1 = (int) (color2.getGreen()*65535.0f);
        blue1 = (int) (color2.getBlue()*65535.0f);
        if (pos1.y < pos3.y)
          {
            x2 = ((int) pos1.x) << 16;
            y2 = ((int) pos1.y);
            z2 = (int) (zf1*65535.0);
            red2 = (int) (color1.getRed()*65535.0f);
            green2 = (int) (color1.getGreen()*65535.0f);
            blue2 = (int) (color1.getBlue()*65535.0f);
            x3 = ((int) pos3.x) << 16;
            y3 = ((int) pos3.y);
            z3 = (int) (zf3*65535.0);
            red3 = (int) (color3.getRed()*65535.0f);
            green3 = (int) (color3.getGreen()*65535.0f);
            blue3 = (int) (color3.getBlue()*65535.0f);
          }
        else
          {
            x2 = ((int) pos3.x) << 16;
            y2 = ((int) pos3.y);
            z2 = (int) (zf3*65535.0);
            red2 = (int) (color3.getRed()*65535.0f);
            green2 = (int) (color3.getGreen()*65535.0f);
            blue2 = (int) (color3.getBlue()*65535.0f);
            x3 = ((int) pos1.x) << 16;
            y3 = ((int) pos1.y);
            z3 = (int) (zf1*65535.0);
            red3 = (int) (color1.getRed()*65535.0f);
            green3 = (int) (color1.getGreen()*65535.0f);
            blue3 = (int) (color1.getBlue()*65535.0f);
          }
      }
    else
      {
        x1 = ((int) pos3.x) << 16;
        y1 = ((int) pos3.y);
        z1 = (int) (zf3*65535.0);
        red1 = (int) (color3.getRed()*65535.0f);
        green1 = (int) (color3.getGreen()*65535.0f);
        blue1 = (int) (color3.getBlue()*65535.0f);
        if (pos1.y < pos2.y)
          {
            x2 = ((int) pos1.x) << 16;
            y2 = ((int) pos1.y);
            z2 = (int) (zf1*65535.0);
            red2 = (int) (color1.getRed()*65535.0f);
            green2 = (int) (color1.getGreen()*65535.0f);
            blue2 = (int) (color1.getBlue()*65535.0f);
            x3 = ((int) pos2.x) << 16;
            y3 = ((int) pos2.y);
            z3 = (int) (zf2*65535.0);
            red3 = (int) (color2.getRed()*65535.0f);
            green3 = (int) (color2.getGreen()*65535.0f);
            blue3 = (int) (color2.getBlue()*65535.0f);
          }
        else
          {
            x2 = ((int) pos2.x) << 16;
            y2 = ((int) pos2.y);
            z2 = (int) (zf2*65535.0);
            red2 = (int) (color2.getRed()*65535.0f);
            green2 = (int) (color2.getGreen()*65535.0f);
            blue2 = (int) (color2.getBlue()*65535.0f);
            x3 = ((int) pos1.x) << 16;
            y3 = ((int) pos1.y);
            z3 = (int) (zf1*65535.0);
            red3 = (int) (color1.getRed()*65535.0f);
            green3 = (int) (color1.getGreen()*65535.0f);
            blue3 = (int) (color1.getBlue()*65535.0f);
          }
      }
    dx1 = x3-x1;
    dy1 = y3-y1;
    dz1 = z3-z1;
    if (dy1 == 0)
      return;
    dred1 = red3-red1;
    dgreen1 = green3-green1;
    dblue1 = blue3-blue1;
    dx2 = x2-x1;
    dy2 = y2-y1;
    dz2 = z2-z1;
    dred2 = red2-red1;
    dgreen2 = green2-green1;
    dblue2 = blue2-blue1;
    mx1 = dx1/dy1;
    mz1 = dz1/dy1;
    mred1 = dred1/dy1;
    mgreen1 = dgreen1/dy1;
    mblue1 = dblue1/dy1;
    xstart = xend = x1;
    zstart = zend = z1;
    redstart = redend = red1;
    greenstart = greenend = green1;
    bluestart = blueend = blue1;
    y = y1;
    if (dy2 != 0)
      {
        mx2 = dx2/dy2;
        mz2 = dz2/dy2;
        mred2 = dred2/dy2;
        mgreen2 = dgreen2/dy2;
        mblue2 = dblue2/dy2;
        if (y2 < 0)
          {
            xstart += mx1*dy2;
            xend += mx2*dy2;
            zstart += mz1*dy2;
            zend += mz2*dy2;
            redstart += mred1*dy2;
            redend += mred2*dy2;
            greenstart += mgreen1*dy2;
            greenend += mgreen2*dy2;
            bluestart += mblue1*dy2;
            blueend += mblue2*dy2;
            y = y2;
          }
        else if (y < 0)
          {
            xstart -= mx1*y;
            xend -= mx2*y;
            zstart -= mz1*y;
            zend -= mz2*y;
            redstart -= mred1*y;
            redend -= mred2*y;
            greenstart -= mgreen1*y;
            greenend -= mgreen2*y;
            bluestart -= mblue1*y;
            blueend -= mblue2*y;
            y = 0;
          }
        yend = (y2 < height ? y2 : height);
        index = y*width;
        while (y < yend)
          {
            if (xstart < xend)
              {
                left = xstart >> 16;
                right = xend >> 16;
                z = zstart;
                dz = zend-zstart;
                red = redstart;
                dred = redend-redstart;
                green = greenstart;
                dgreen = greenend-greenstart;
                blue = bluestart;
                dblue = blueend-bluestart;
              }
            else
              {
                left = xend >> 16;
                right = xstart >> 16;
                z = zend;
                dz = zstart-zend;
                red = redend;
                dred = redstart-redend;
                green = greenend;
                dgreen = greenstart-greenend;
                blue = blueend;
                dblue = bluestart-blueend;
              }
            if (left != right)
              {
                dz /= (right-left);
                dred /= (right-left);
                dgreen /= (right-left);
                dblue /= (right-left);
                if (left < 0)
                  {
                    z -= left*dz;
                    red -= left*dred;
                    green -= left*dgreen;
                    blue -= left*dblue;
                    left = 0;
                  }
                if (right > width)
                  right = width;
                for (i = left; i < right; i++)
                  {
                    if (z < zbuffer[index+i] && z > clipDist)
                      {
                        pixel[index+i] = 0xFF000000 + ((red & 0xFF00)<<8) + (green & 0xFF00) + (blue >> 8);
                        zbuffer[index+i] = z;
                      }
                    z += dz;
                    red += dred;
                    green += dgreen;
                    blue += dblue;
                  }
              }
            xstart += mx1;
            zstart += mz1;
            redstart += mred1;
            greenstart += mgreen1;
            bluestart += mblue1;
            xend += mx2;
            zend += mz2;
            redend += mred2;
            greenend += mgreen2;
            blueend += mblue2;
            index += width;
            y++;
          }
      }
    dx2 = x3-x2;
    dy2 = y3-y2;
    dz2 = z3-z2;
    dred2 = red3-red2;
    dgreen2 = green3-green2;
    dblue2 = blue3-blue2;
    if (dy2 != 0)
      {
        mx2 = dx2/dy2;
        mz2 = dz2/dy2;
        mred2 = dred2/dy2;
        mgreen2 = dgreen2/dy2;
        mblue2 = dblue2/dy2;
        xend = x2;
        zend = z2;
        redend = red2;
        greenend = green2;
        blueend = blue2;
        if (y < 0)
          {
            xstart -= mx1*y;
            xend -= mx2*y;
            zstart -= mz1*y;
            zend -= mz2*y;
            redstart -= mred1*y;
            redend -= mred2*y;
            greenstart -= mgreen1*y;
            greenend -= mgreen2*y;
            bluestart -= mblue1*y;
            blueend -= mblue2*y;
            y = 0;
          }
        yend = (y3 < height ? y3 : height);
        index = y*width;
        while (y < yend)
          {
            if (xstart < xend)
              {
                left = xstart >> 16;
                right = xend >> 16;
                z = zstart;
                dz = zend-zstart;
                red = redstart;
                dred = redend-redstart;
                green = greenstart;
                dgreen = greenend-greenstart;
                blue = bluestart;
                dblue = blueend-bluestart;
              }
            else
              {
                left = xend >> 16;
                right = xstart >> 16;
                z = zend;
                dz = zstart-zend;
                red = redend;
                dred = redstart-redend;
                green = greenend;
                dgreen = greenstart-greenend;
                blue = blueend;
                dblue = bluestart-blueend;
              }
            if (left != right)
              {
                dz /= (right-left);
                dred /= (right-left);
                dgreen /= (right-left);
                dblue /= (right-left);
                if (left < 0)
                  {
                    z -= left*dz;
                    red -= left*dred;
                    green -= left*dgreen;
                    blue -= left*dblue;
                    left = 0;
                  }
                if (right > width)
                  right = width;
                for (i = left; i < right; i++)
                  {
                    if (z < zbuffer[index+i] && z > clipDist)
                      {
                        pixel[index+i] = 0xFF000000 + ((red & 0xFF00)<<8) + (green & 0xFF00) + (blue >> 8);
                        zbuffer[index+i] = z;
                      }
                    z += dz;
                    red += dred;
                    green += dgreen;
                    blue += dblue;
                  }
              }
            xstart += mx1;
            zstart += mz1;
            redstart += mred1;
            greenstart += mgreen1;
            bluestart += mblue1;
            xend += mx2;
            zend += mz2;
            redend += mred2;
            greenend += mgreen2;
            blueend += mblue2;
            index += width;
            y++;
          }
      }
  }

  /** Draw a piece of text onto the canvas. */

  public void drawString(String text, int x, int y, Color color)
  {
    imageGraphics.setColor(color);
    imageGraphics.drawString(text, x, y);
  }

  /** Draw the outline of a Shape into the canvas. */

  public void drawShape(Shape shape, Color color)
  {
    imageGraphics.setColor(color);
    imageGraphics.draw(shape);
  }

  /** Draw a filled Shape onto the canvas. */

  public void fillShape(Shape shape, Color color)
  {
    imageGraphics.setColor(color);
    imageGraphics.fill(shape);
  }

  /** Draw an image onto the canvas. */

  public void drawImage(Image image, int x, int y)
  {
    ImageRecord record = getCachedImage(image);
    if (record == null)
      return;

    // Draw the image onto the canvas.

    int[] imagePixel = record.pixel;
    int width = record.width, height = record.height;
    int starti = Math.max(0, -x);
    int startj = Math.max(0, -y);
    int endi = Math.min(width, bounds.width-x);
    int endj = Math.min(height, bounds.height-y);
    for (int j = startj; j < endj; j++)
    {
      int srcOffset = j*width;
      int dstOffset = (j+y)*bounds.width+x;
      for (int i = starti; i < endi; i++)
      {
        int pix = imagePixel[srcOffset+i];
        if ((pix&0xFF000000) != 0)
          pixel[dstOffset+i] = pix;
      }
    }
  }

  /**
   * Render an image onto the canvas.  This method uses a ridiculously inefficient method for
   * rendering the image.  So why does it work that way?  First, because I was lazy and didn't
   * want to take the trouble to actually write a proper rasterizer.  Second, because most
   * people will be using the OpenGL renderer instead.  And third, because renderImage() is currently
   * only used for reference images, all of which means that the speed of this method won't be very
   * important for very many people.
   *
   * @param image  the image to render
   * @param p1     the coordinates of the first corner of the image
   * @param p2     the coordinates of the second corner of the image
   * @param p3     the coordinates of the third corner of the image
   * @param p4     the coordinates of the fourth corner of the image
   * @param camera the camera from which to draw the image
   */

  public void renderImage(Image image, Vec3 p1, Vec3 p2, Vec3 p3, Vec3 p4, Camera camera)
  {
    // Get a cached ImageRecord for this image.

    final ImageRecord record = getCachedImage(image);
    if (record == null)
      return;

    // Get a cached RenderingMesh for this image.

    RenderingMesh mesh = null;
    SoftReference ref = (SoftReference) imageMeshMap.get(image);
    if (ref != null)
      mesh = (RenderingMesh) ref.get();
    int width = record.width+1;
    int height = record.height+1;
    Vec3 dx = p2.minus(p1).times(1.0/record.width);
    Vec3 dy = p4.minus(p1).times(1.0/record.height);
    if (mesh == null)
    {
      // We don't have a cached one, so we need to create a new one.

      Vec3 vert[] = new Vec3[width*height];
      Vec3 norm[] = new Vec3[] {new Vec3()};
      RenderingTriangle tri[] = new RenderingTriangle[2*record.width*record.height];
      for (int i = 0; i < width; i++)
      {
        for (int j = 0; j < height; j++)
          vert[i+j*width] = p1.plus(dx.times(i)).plus(dy.times(j));
      }
      for (int i = 0; i < record.width; i++)
        for (int j = 0; j < record.height; j++)
        {
          int index = 2*(i+(record.height-j-1)*record.width);
          tri[index] = new UniformTriangle(i+j*width, i+1+j*width, i+1+(j+1)*width, 0, 0, 0);
          tri[index+1] = new UniformTriangle(i+j*width, i+1+(j+1)*width, i+(j+1)*width, 0, 0, 0);
        }
      mesh = new RenderingMesh(vert, norm, tri, null, null);
      imageMeshMap.put(image, new SoftReference<RenderingMesh>(mesh));
    }
    else
    {
      // Just position the vertices correctly.

      for (int i = 0; i < width; i++)
      {
        for (int j = 0; j < height; j++)
          mesh.vert[i+j*width] = p1.plus(dx.times(i)).plus(dy.times(j));
      }
    }

    // Render the image.

    renderMesh(mesh, new VertexShader() {
      public void getColor(int face, int vertex, RGBColor color)
      {
        color.setARGB(record.pixel[face/2]);
      }
      public boolean isUniformFace(int face)
      {
        return true;
      }
      public boolean isUniformTexture()
      {
        return false;
      }
      public void getTextureSpec(TextureSpec spec)
      {
      }
    }, camera, false, null);
  }

  public void imageChanged(Image image)
  {
    imageMap.remove(image);
  }

  /** Get an ImageRecord for an Image, attempting to cache objects for efficiency. */

  private ImageRecord getCachedImage(Image image)
  {
    ImageRecord record = null;
    SoftReference ref = (SoftReference) imageMap.get(image);
    if (ref != null)
      record = (ImageRecord) ref.get();
    if (record == null)
    {
      // Grab the pixels from the image and cache them.

      try
      {
        record = new ImageRecord(image);
        imageMap.put(image, new SoftReference<ImageRecord>(record));
      }
      catch (InterruptedException ex)
      {
        ex.printStackTrace();
        return null;
      }
    }
    return record;
  }

  /** This inner class represents an image to be drawn on the canvas. */

  private static class ImageRecord
  {
    int pixel[], width, height;

    ImageRecord(Image image) throws InterruptedException
    {
      try
      {
        PixelGrabber pg = new PixelGrabber(image, 0, 0, -1, -1, true);
        pg.grabPixels();
        pixel = (int []) pg.getPixels();
        width = image.getWidth(null);
        height = image.getHeight(null);
      }
      catch (InterruptedException ex)
      {
        ex.printStackTrace();
      }
    }
  }
}
