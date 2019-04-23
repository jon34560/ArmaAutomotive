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

public class PointJoinObject
{
    public int objectA;
    public int objectB;
    public int objectAPoint;
    public int objectBPoint;
    protected MeshVertex vertex[];  // 3 vertex max???
    protected float smoothness[];
    protected boolean closed;
    protected int smoothingMethod;
    //protected WireframeMesh cachedWire; // ???
    protected BoundingBox bounds;
    
    public PointJoinObject()
    {
        objectA = 0;
        objectB = 0;
        objectAPoint = 0;
        objectBPoint = 0;
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
    
    
    /**
     * writeToFile
     *
     * Description:
     */
    public void writeToFile(DataOutputStream out, Scene theScene) throws IOException
    {
        /*
        super.writeToFile(out, theScene);
        
        //System.out.println("  DimensionObject writeToFile ( outstream, scene ) ");
        
        int i;
        
        out.writeShort(0); // is this a delimiter or object id
        out.writeInt(vertex.length); // should always be 3
        for (i = 0; i < vertex.length; i++)
        {
            vertex[i].r.writeToFile(out);
            out.writeFloat(smoothness[i]);
        }
        out.writeBoolean(closed);
        out.writeInt(smoothingMethod); // maybe use this method ID as a designation for dimension object
         */
    }
    
    
    
}
