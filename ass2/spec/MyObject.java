package ass2.spec;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

public class MyObject {
    GLU glu;
    private static final int FloatByteSize = Float.SIZE / 8;
    int bufferIds[] = new int[2];
    private static final String VERTEX_SHADER = "ass2/spec/AttributeVertex.glsl";
    private static final String FRAGMENT_SHADER = "ass2/spec/AttributeFragment.glsl";
    
    private float spositions[] = 
        {
            0,1,-1, 
            -1,-1,-1,
            1,-1,-1, 
            0, 2,-4,
            -2,-2,-4, 
            2,-2,-4
        };
    
    //render cube as a set of triangles, credit to: http://www.opengl-tutorial.org/beginners-tutorials/tutorial-4-a-colored-cube/
    private float positions[] =
        {
            -1.0f,-1.0f,-1.0f, // triangle 1 : begin
            -1.0f,-1.0f, 1.0f,
            -1.0f, 1.0f, 1.0f, // triangle 1 : end
            1.0f, 1.0f,-1.0f, // triangle 2 : begin
            -1.0f,-1.0f,-1.0f,
            -1.0f, 1.0f,-1.0f, // triangle 2 : end
            1.0f,-1.0f, 1.0f,
            -1.0f,-1.0f,-1.0f,
            1.0f,-1.0f,-1.0f,
            1.0f, 1.0f,-1.0f,
            1.0f,-1.0f,-1.0f,
            -1.0f,-1.0f,-1.0f,
            -1.0f,-1.0f,-1.0f,
            -1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f,-1.0f,
            1.0f,-1.0f, 1.0f,
            -1.0f,-1.0f, 1.0f,
            -1.0f,-1.0f,-1.0f,
            -1.0f, 1.0f, 1.0f,
            -1.0f,-1.0f, 1.0f,
            1.0f,-1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            1.0f,-1.0f,-1.0f,
            1.0f, 1.0f,-1.0f,
            1.0f,-1.0f,-1.0f,
            1.0f, 1.0f, 1.0f,
            1.0f,-1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            1.0f, 1.0f,-1.0f,
            -1.0f, 1.0f,-1.0f,
            1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f,-1.0f,
            -1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f, 1.0f,
            1.0f,-1.0f, 1.0f
        };

    //There should be a matching entry in this array for each entry in
    //the positions array
    private float scolors[] = 
        {
            1,0,0, 
            0,1,0,
            1,1,1,
            0,0,0,
            0,0,1, 
            1,1,0
        }; 
    
    private float colors[] =
        {
            0.583f,  0.771f,  0.014f,
            0.609f,  0.115f,  0.436f,
            0.327f,  0.483f,  0.844f,
            0.822f,  0.569f,  0.201f,
            0.435f,  0.602f,  0.223f,
            0.310f,  0.747f,  0.185f,
            0.597f,  0.770f,  0.761f,
            0.559f,  0.436f,  0.730f,
            0.359f,  0.583f,  0.152f,
            0.483f,  0.596f,  0.789f,
            0.559f,  0.861f,  0.639f,
            0.195f,  0.548f,  0.859f,
            0.014f,  0.184f,  0.576f,
            0.771f,  0.328f,  0.970f,
            0.406f,  0.615f,  0.116f,
            0.676f,  0.977f,  0.133f,
            0.971f,  0.572f,  0.833f,
            0.140f,  0.616f,  0.489f,
            0.997f,  0.513f,  0.064f,
            0.945f,  0.719f,  0.592f,
            0.543f,  0.021f,  0.978f,
            0.279f,  0.317f,  0.505f,
            0.167f,  0.620f,  0.077f,
            0.347f,  0.857f,  0.137f,
            0.055f,  0.953f,  0.042f,
            0.714f,  0.505f,  0.345f,
            0.783f,  0.290f,  0.734f,
            0.722f,  0.645f,  0.174f,
            0.302f,  0.455f,  0.848f,
            0.225f,  0.587f,  0.040f,
            0.517f,  0.713f,  0.338f,
            0.053f,  0.959f,  0.120f,
            0.393f,  0.621f,  0.362f,
            0.673f,  0.211f,  0.457f,
            0.820f,  0.883f,  0.371f,
            0.982f,  0.099f,  0.879f
        };

    //Best to use smallest data type possible for indexes 
    //We could even use byte here...
    private short indexes[] = {0,1,5,3,4,2};
    
    private FloatBuffer posData = Buffers.newDirectFloatBuffer(positions);
    private FloatBuffer colorData = Buffers.newDirectFloatBuffer(colors);
    
    private ShortBuffer indexData = Buffers.newDirectShortBuffer(indexes);
    
    private int shaderprogram;
    
    public MyObject(GL2 gl) {
        glu = new GLU();
        //Generate 2 VBO buffer and get their IDs
        gl.glGenBuffers(2,bufferIds,0);
       
        //This buffer is now the current array buffer
        //array buffers hold vertex attribute data
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER,bufferIds[0]);
        
        //This is just setting aside enough empty space
        //for all our data
        gl.glBufferData(GL2.GL_ARRAY_BUFFER,    //Type of buffer  
                   positions.length * FloatByteSize +  colors.length* FloatByteSize, //size needed
                   null,    //We are not actually loading data here yet
                   GL2.GL_STATIC_DRAW); //We expect once we load this data we will not modify it
        
        
        //Actually load the positions data
        gl.glBufferSubData(GL2.GL_ARRAY_BUFFER, 0, //From byte offset 0
                positions.length*FloatByteSize,
                posData);

        //Actually load the color data
        gl.glBufferSubData(GL2.GL_ARRAY_BUFFER, positions.length*FloatByteSize,  //Load after the position data
                colors.length*FloatByteSize,
                colorData);
        
        /* Uncomment if we want to use indexes
        //Now for the element array
        //Element arrays hold indexes to an array buffer
        gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, bufferIds[1]);

        //We can load it all at once this time since there are not
        //two separate parts like there was with color and position.
        gl.glBufferData(GL2.GL_ELEMENT_ARRAY_BUFFER,      
               indexes.length *Short.BYTES,
               indexData, GL2.GL_STATIC_DRAW);
        */
        
        //Enable client state
        gl.glEnableClientState( GL2.GL_VERTEX_ARRAY);
        gl.glEnableClientState( GL2.GL_COLOR_ARRAY);
        
        try {
            shaderprogram = Shader.initShaders(gl, VERTEX_SHADER, FRAGMENT_SHADER);   
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

    }
    public void draw(GL2 gl) {
        gl.glPushMatrix();
            gl.glScaled(0.1, 0.1, 0.1);
            gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
            
            //Use the shader
            gl.glUseProgram(shaderprogram);
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER,bufferIds[0]);
               
            int vertexColLoc = gl.glGetAttribLocation(shaderprogram,"vertexCol");
            int vertexPosLoc = gl.glGetAttribLocation(shaderprogram,"vertexPos");
                   
            // Specify locations for the co-ordinates and color arrays.
            gl.glEnableVertexAttribArray(vertexPosLoc);
            gl.glEnableVertexAttribArray(vertexColLoc);
            gl.glVertexAttribPointer(vertexPosLoc,3, GL.GL_FLOAT, false,0, 0); //last num is the offset
            gl.glVertexAttribPointer(vertexColLoc,3, GL.GL_FLOAT, false,0, positions.length*FloatByteSize);
            
            //Comment below out if we want to use indexes
            
            // This tells OpenGL the locations for the co-ordinates and color arrays.
            gl.glVertexPointer(3, //3 coordinates per vertex 
                               GL.GL_FLOAT, //each co-ordinate is a float 
                               0, //There are no gaps in data between co-ordinates 
                               0); //Co-ordinates are at the start of the current array buffer
            gl.glColorPointer(3, GL.GL_FLOAT, 0, 
                              positions.length*FloatByteSize); //colors are found after the position
                                                             //co-ordinates in the current array buffer
             
            //Draw triangles, using 6 vertices, starting at vertex index 0 
            gl.glDrawArrays(GL2.GL_TRIANGLES,0, positions.length / 3);  
            
            //Comment above out of we want to use indexes
            
            /* Uncomment if we want to use indexes
            //gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, bufferIds[1]);
                 
            //gl.glDrawElements(GL2.GL_TRIANGLES, 6, GL2.GL_UNSIGNED_SHORT,0);    
            
            */
            
            //Unbind shader
            gl.glUseProgram(0);
               
            //Un-bind the buffer. 
            //This is not needed in this simple example but good practice
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER,0);
            gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER,0);
        gl.glPopMatrix();
    }
}
