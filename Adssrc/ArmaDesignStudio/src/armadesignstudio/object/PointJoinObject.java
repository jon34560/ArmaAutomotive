/**
 * PointJoinObject
 *
 *
 */

package armadesignstudio.object;

import armadesignstudio.*;
import armadesignstudio.texture.*;
import armadesignstudio.animation.*;
import armadesignstudio.math.*;
import armadesignstudio.ui.*;
import buoy.widget.*;
import java.io.*;

import armadesignstudio.view.*;
import armadesignstudio.texture.*;


// java.lang.NoSuchMethodException: armadesignstudio.object.PointJoinObject.<init>(java.io.DataInputStream, armadesignstudio.Scene)

// Curve extends Object3D

public class PointJoinObject extends Object3D // extends Curve implements Mesh
{
    public int objectA;
    public int objectB;
    public int objectAPoint;
    public int objectBPoint;
    protected MeshVertex vertex[];  // 2 vertex max
    protected float smoothness[];
    protected boolean closed;
    protected int smoothingMethod;
    protected WireframeMesh cachedWire; // ???
    protected BoundingBox bounds;
    double halfx, halfy, halfz;
    
    
    private static final Property PROPERTIES[] = new Property [] {
    new Property(Translate.text("menu.smoothingMethod"),
                 new Object[] {
                     Translate.text("menu.none"),
                     Translate.text("menu.interpolating"),
                     Translate.text("menu.approximating")
                 }, Translate.text("menu.shading")),
    new Property(Translate.text("menu.closedEnds"), true)
    };
    
    
    public PointJoinObject(Vec3 v[], float smoothness[], int smoothingMethod, boolean isClosed)
    {
        //super(v, smoothness, smoothingMethod, isClosed);
        
        int i;
        
        
        vertex = new MeshVertex [v.length];
        for (i = 0; i < v.length; i++)
            vertex[i] = new MeshVertex(v[i]);
        this.smoothness = smoothness;
        this.smoothingMethod = smoothingMethod;
        closed = isClosed;
        
        
        objectA = 0;
        objectB = 0;
        objectAPoint = 0;
        objectBPoint = 0;
    }
    
    
    public PointJoinObject()
    {
        objectA = 0;
        objectB = 0;
        objectAPoint = 0;
        objectBPoint = 0;
    }
    
    public void setVertex(Vec3 v[])
    {
        vertex = new MeshVertex [v.length];
        for (int i = 0; i < v.length; i++){
            vertex[i] = new MeshVertex(v[i]);
        }
    }
    
    public Object3D duplicate()
    {
        Vec3 v[] = new Vec3 [vertex.length];
        float s[] = new float [vertex.length];
        
        for (int i = 0; i < vertex.length; i++)
        {
            v[i] = new Vec3(vertex[i].r);
            s[i] = smoothness[i];
        }
        return new PointJoinObject(v, s, smoothingMethod, closed);
    }
    
    public void copyObject(Object3D obj)
    {
        PointJoinObject cv = (PointJoinObject) obj;
        MeshVertex v[] = cv.getVertices();
        
        vertex = new MeshVertex [v.length];
        smoothness = new float [v.length];
        for (int i = 0; i < vertex.length; i++)
        {
            vertex[i] = new MeshVertex(new Vec3(v[i].r));
            smoothness[i] = cv.smoothness[i];
        }
        smoothingMethod = cv.smoothingMethod;
        setClosed(cv.closed);
        //clearCachedMesh();
    }
    
    /*
    protected void findBounds()
    {
        double minx, miny, minz, maxx, maxy, maxz;
        Vec3 v, points[];
        int i;
        
        //getWireframeMesh();
        points = cachedWire.vert;
        minx = maxx = points[0].x;
        miny = maxy = points[0].y;
        minz = maxz = points[0].z;
        for (i = 1; i < points.length; i++)
        {
            v = points[i];
            if (v.x < minx) minx = v.x;
            if (v.x > maxx) maxx = v.x;
            if (v.y < miny) miny = v.y;
            if (v.y > maxy) maxy = v.y;
            if (v.z < minz) minz = v.z;
            if (v.z > maxz) maxz = v.z;
        }
        bounds = new BoundingBox(minx, maxx, miny, maxy, minz, maxz);
    }
     
    
    public BoundingBox getBounds()
    {
        if (bounds == null)
            findBounds();
        return bounds;
    }
     */
    
    public MeshVertex[] getVertices()
    {
        return vertex;
    }
    
    public float[] getSmoothness()
    {
        return smoothness;
    }
    
    
    
    
    public void movePoint(int which, Vec3 pos)
    {
        vertex[which].r = pos;
        //clearCachedMesh();
    }
    
    public Vec3 [] getVertexPositions()
    {
        Vec3 v[] = new Vec3 [vertex.length];
        for (int i = 0; i < v.length; i++)
            v[i] = new Vec3(vertex[i].r);
        return v;
    }
    
    public void setVertexPositions(Vec3 v[])
    {
        for (int i = 0; i < v.length; i++)
            vertex[i].r = v[i];
        //clearCachedMesh();
    }
    
    
    public void renderObject(ObjectInfo obj, ViewerCanvas canvas, Vec3 viewDir)
    {
        
        
        // canvas.renderDimensionObject( obj, theCamera );
    }
    
    public void setShape(Vec3 v[], float smoothness[])
    {
        if (v.length != vertex.length)
            vertex = new MeshVertex [v.length];
        for (int i = 0; i < v.length; i++)
            vertex[i] = new MeshVertex(v[i]);
        this.smoothness = smoothness;
        //clearCachedMesh();
    }
    
    public void setClosed(boolean isClosed)
    {
        closed = isClosed;
        //clearCachedMesh();
    }
    
    public boolean isClosed()
    {
        return closed;
    }
    
    /*
    public void setSize(double xsize, double ysize, double zsize)
    {
        Vec3 size = getBounds().getSize();
        double xscale, yscale, zscale;
        
        if (size.x == 0.0)
            xscale = 1.0;
        else
            xscale = xsize / size.x;
        if (size.y == 0.0)
            yscale = 1.0;
        else
            yscale = ysize / size.y;
        if (size.z == 0.0)
            zscale = 1.0;
        else
            zscale = zsize / size.z;
        for (int i = 0; i < vertex.length; i++)
        {
            vertex[i].r.x *= xscale;
            vertex[i].r.y *= yscale;
            vertex[i].r.z *= zscale;
        }
        clearCachedMesh();
    }
     */
    
   
    
    
    /**
     * writeToFile
     *
     * Description:
     */
    public void writeToFile(DataOutputStream out, Scene theScene) throws IOException
    {
        super.writeToFile(out, theScene);
        
        //System.out.println("  DimensionObject writeToFile ( outstream, scene ) ");
        
        int i;
        
        out.writeShort(0); // is this a delimiter or object id
        out.writeInt(vertex.length); // should always be 3
        for (i = 0; i < vertex.length; i++)
        {
            vertex[i].r.writeToFile(out);
            //out.writeFloat(smoothness[i]);
        }
        out.writeBoolean(closed);
        out.writeInt(smoothingMethod); // maybe use this method ID as a designation for dimension object
        
        
    }
    
    
    /**
     * PointJoinObject
     *
     * Descirption: Read from file.
     */
    public PointJoinObject(DataInputStream in, Scene theScene) throws IOException, InvalidObjectException
    {
        super(in, theScene);
        
        //System.out.println("  PointJoinObject( instream, scene ) ");
        
        int i;
        short version = in.readShort();
        
        if (version != 0){
            throw new InvalidObjectException("");
        }
        vertex = new MeshVertex [in.readInt()];
        //smoothness = new float [vertex.length];
        for (i = 0; i < vertex.length; i++)
        {
            vertex[i] = new MeshVertex(new Vec3(in));
            //smoothness[i] = in.readFloat();
        }
        closed = in.readBoolean();
        smoothingMethod = in.readInt();
    }
    
    
    
    /* Return a Keyframe which describes the current pose of this object. */
    
    public Keyframe getPoseKeyframe()
    {
        return new VectorKeyframe(2.0, 2.0, 2.0);
    }
    
    public void applyPoseKeyframe(Keyframe k)
    {
        VectorKeyframe key = (VectorKeyframe) k;
        
        setSize(key.x, key.y, key.z);
    }
    
    public WireframeMesh getWireframeMesh()
    {
        
        int i, from[], to[];
        PointJoinObject subdiv;
        Vec3 vert[];
        
        if (cachedWire != null)
            return cachedWire;
        if (true) //smoothingMethod == NO_SMOOTHING)
            subdiv = this;
        else
            subdiv = subdividePointJoin().subdividePointJoin();
        vert = new Vec3 [subdiv.vertex.length];
        for (i = 0; i < vert.length; i++)
            vert[i] = subdiv.vertex[i].r;
        if (closed)
        {
            from = new int [vert.length];
            to = new int [vert.length];
            from[vert.length-1] = vert.length-1;
            to[vert.length-1] = 0;
        }
        else
        {
            from = new int [vert.length-1];
            to = new int [vert.length-1];
        }
        for (i = 0; i < vert.length-1; i++)
        {
            from[i] = i;
            to[i] = i+1;
        }
        return (cachedWire = new WireframeMesh(vert, from, to));
    }
    
    public PointJoinObject subdividePointJoin()
    {
        //System.out.println("  DimensionObject.subdivideDimension() ");
        if (vertex.length < 2)
            return (PointJoinObject) duplicate();
        if (vertex.length == 2)
        {
            Vec3 newpos[] = new Vec3 [] {new Vec3(vertex[0].r), vertex[0].r.plus(vertex[1].r).times(0.5), new Vec3(vertex[1].r)};
            float news[] = new float [] {smoothness[0], (smoothness[0]+smoothness[1])*0.5f, smoothness[1]};
            return new PointJoinObject(newpos, news, smoothingMethod, closed);
        }
        Vec3 v[] = new Vec3 [vertex.length];
        for (int i = 0; i < v.length; i++)
            v[i] = new Vec3(vertex[i].r);
        Vec3 newpos[];
        float news[];
        int i, j;
        if (closed)
        {
            newpos = new Vec3 [v.length*2];
            news = new float [smoothness.length*2];
            if (true) // smoothingMethod == INTERPOLATING)
            {
                newpos[0] = v[0];
                newpos[1] = calcInterpPoint(v, smoothness, v.length-1, 0, 1, 2);
                for (i = 2, j = 1; i < newpos.length; i++)
                {
                    if (i%2 == 0)
                        newpos[i] = v[j];
                    else
                    {
                        newpos[i] = calcInterpPoint(v, smoothness, j-1, j, (j+1)%v.length, (j+2)%v.length);
                        j++;
                    }
                }
            }
            else
            {
                newpos[0] = calcApproxPoint(v, smoothness, v.length-1, 0, 1);
                for (i = 1; i < v.length-1; i++)
                {
                    newpos[i*2-1] = v[i].plus(v[i-1]).times(0.5);
                    newpos[i*2] = calcApproxPoint(v, smoothness, i-1, i, i+1);
                }
                newpos[i*2-1] = v[i].plus(v[i-1]).times(0.5);
                newpos[i*2] = calcApproxPoint(v, smoothness, i-1, i, 0);
                newpos[i*2+1] = v[0].plus(v[i]).times(0.5);
            }
            for (i = 0; i < smoothness.length; i++)
            {
                news[i*2] = Math.min(smoothness[i]*2.0f, 1.0f);
                news[i*2+1] = 1.0f;
            }
        }
        else
        {
            newpos = new Vec3 [v.length*2-1];
            news = new float [smoothness.length*2-1];
            if (true ) // smoothingMethod == INTERPOLATING)
            {
                newpos[0] = v[0];
                newpos[1] = calcInterpPoint(v, smoothness, 0, 0, 1, 2);
                for (i = 2, j = 1; i < newpos.length-2; i++)
                {
                    if (i%2 == 0)
                        newpos[i] = v[j];
                    else
                    {
                        newpos[i] = calcInterpPoint(v, smoothness, j-1, j, j+1, j+2);
                        j++;
                    }
                }
                newpos[i] = calcInterpPoint(v, smoothness, j-1, j, j+1, j+1);
                newpos[i+1] = v[j+1];
            }
            else
            {
                newpos[0] = v[0];
                for (i = 1; i < v.length-1; i++)
                {
                    newpos[i*2-1] = v[i].plus(v[i-1]).times(0.5);
                    newpos[i*2] = calcApproxPoint(v, smoothness, i-1, i, i+1);
                }
                newpos[i*2-1] = v[i].plus(v[i-1]).times(0.5);
                newpos[i*2] = v[i];
            }
            for (i = 0; i < smoothness.length-1; i++)
            {
                news[i*2] = Math.min(smoothness[i]*2.0f, 1.0f);
                news[i*2+1] = 1.0f;
            }
            news[i*2] = Math.min(smoothness[i]*2.0f, 1.0f);
        }
        return new PointJoinObject(newpos, news, smoothingMethod, closed);
    }
    
    /** Return a new Dimension object which has been subdivided the specified number of times to give a finer approximation of
     the curve shape. */
    
    public PointJoinObject subdividePointJoin(int times)
    {
        PointJoinObject c = this;
        for (int i = 0; i < times; i++)
            c = c.subdividePointJoin();
        return c;
    }
    
    /** The following two routines are used by subdivideDimension to calculate new point positions
     for interpolating and approximating subdivision.  v is the array of current points, s is
     the array of smoothness values for them, and i, j, k, and m are the indices of the points
     from which the new point will be calculated. */
    
    public static Vec3 calcInterpPoint(Vec3 v[], float s[], int i, int j, int k, int m)
    {
        double w1, w2, w3, w4;
        
        w1 = -0.0625*s[j];
        w2 = 0.5-w1;
        w4 = -0.0625*s[k];
        w3 = 0.5-w4;
        
        return new Vec3 (w1*v[i].x + w2*v[j].x + w3*v[k].x + w4*v[m].x,
                         w1*v[i].y + w2*v[j].y + w3*v[k].y + w4*v[m].y,
                         w1*v[i].z + w2*v[j].z + w3*v[k].z + w4*v[m].z);
    }
    
    public static Vec3 calcApproxPoint(Vec3 v[], float s[], int i, int j, int k)
    {
        double w1 = 0.125*s[j], w2 = 1.0-2.0*w1;
        
        return new Vec3 (w1*v[i].x + w2*v[j].x + w1*v[k].x,
                         w1*v[i].y + w2*v[j].y + w1*v[k].y,
                         w1*v[i].z + w2*v[j].z + w1*v[k].z);
    }
    
    public void setSize(double xsize, double ysize, double zsize)
    {
        Vec3 size = getBounds().getSize();
        double xscale, yscale, zscale;
        
        if (size.x == 0.0)
            xscale = 1.0;
        else
            xscale = xsize / size.x;
        if (size.y == 0.0)
            yscale = 1.0;
        else
            yscale = ysize / size.y;
        if (size.z == 0.0)
            zscale = 1.0;
        else
            zscale = zsize / size.z;
        for (int i = 0; i < vertex.length; i++)
        {
            vertex[i].r.x *= xscale;
            vertex[i].r.y *= yscale;
            vertex[i].r.z *= zscale;
        }
        clearCachedMesh();
    }
    
    protected void clearCachedMesh()
    {
        cachedWire = null;
        bounds = null;
    }
    
    public BoundingBox getBounds()
    {
        if (bounds == null)
            findBounds();
        return bounds;
    }
    
    protected void findBounds()
    {
        double minx, miny, minz, maxx, maxy, maxz;
        Vec3 v, points[];
        int i;
        
        getWireframeMesh();
        points = cachedWire.vert;
        minx = maxx = points[0].x;
        miny = maxy = points[0].y;
        minz = maxz = points[0].z;
        for (i = 1; i < points.length; i++)
        {
            v = points[i];
            if (v.x < minx) minx = v.x;
            if (v.x > maxx) maxx = v.x;
            if (v.y < miny) miny = v.y;
            if (v.y > maxy) maxy = v.y;
            if (v.z < minz) minz = v.z;
            if (v.z > maxz) maxz = v.z;
        }
        bounds = new BoundingBox(minx, maxx, miny, maxy, minz, maxz);
    }
    
    
   
    
}
