package ass2.spec;

import java.nio.FloatBuffer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

public class MyObject {
    GLU glu;
    
    float positions[] = {0,1,-1, -1,-1,-1,
            1,-1,-1, 0, 2,-4,
            -2,-2,-4, 2,-2,-4};
    float colors[] = {1,0,0, 0,1,0,
            1,1,1, 0,0,0,
            0,0,1, 1,1,0};
    
    FloatBuffer posData =
            Buffers.newDirectFloatBuffer(positions);
    
    FloatBuffer colorData =
            Buffers.newDirectFloatBuffer(colors); 
    
    public MyObject(GL2 gl) {
        glu = new GLU();
        int bufferIDs[] = new int[2];
        gl.glGenBuffers(2, bufferIDs,0);
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER
                ,bufferIDs[0]);
        
         // Upload data into the current VBO
         // For our example if we were only
         // loading positions we could use
        gl.glBufferData(GL2.GL_ARRAY_BUFFER,
          posData.capacity() * Float.BYTES,
          posData,
          GL2.GL_STATIC_DRAW);
        
         // Upload data into the current VBO
         // For our example if we were wanting
         // to load position and color data
         // we could create an empty buffer of the
         // desired size and then load in each
         // section of data using glBufferSubData
        gl.glBufferData(GL2.GL_ARRAY_BUFFER,
          positions.length*Float.BYTES +
          colors.length*Float.BYTES,
          null, GL2.GL_STATIC_DRAW);
        
        
        gl.glBufferSubData(GL2.GL_ARRAY_BUFFER,
                0,positions.length*Float.BYTES,posData);
                gl.glBufferSubData(GL2.GL_ARRAY_BUFFER,
                 positions.length*Float.BYTES, //offset
                colors.length*Float.BYTES,colorData);
                
        //Enable client state
        gl.glEnableClientState( GL2.GL_VERTEX_ARRAY);
        gl.glEnableClientState( GL2.GL_COLOR_ARRAY);
        //For other types of data
        gl.glEnableClientState( GL2.GL_NORMAL_ARRAY);//etc
        
        
    }
    public void draw(GL2 gl) {
        gl.glPushMatrix();
             // Tell OpenGL where to find data
             // In our example each position has 3
             // float co-ordinates. Positions are not
             // interleaved with other data and are
             // at the start of the buffer
             gl.glVertexPointer(3,GL.GL_FLOAT,0, 0);
             // In our example color data is found
             // after all the position data
             gl.glColorPointer(3,GL.GL_FLOAT,0,
             positions.length*Float.BYTES );
             
               //In our example we have data for 2
               //triangles, so 6 vertices
               //and we are starting at the
               //vertex at index 0
             gl.glDrawArrays(GL2.GL_TRIANGLES,0,6);
               //This would just draw the second triangle
             gl.glDrawArrays(GL2.GL_TRIANGLES,3,6); 
        gl.glPopMatrix();
    }
}
