package ass2.spec;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

public class MyObject {
    GLU glu;
    private static final int FloatByteSize = 4;//Float.SIZE / 8;
    int bufferIds[] = new int[2];
    private final String faceTex = "creeper.png";
    private final String faceTexExt = "png";
    private static final String VERTEX_SHADER = "ass2/spec/AttributeVertex.glsl";
    private static final String FRAGMENT_SHADER = "ass2/spec/AttributeFragment.glsl";

    private float positions[] =
        {
            0, 0, 0,
            1, 0, 0,
            1, 1, 0,
            0, 1, 0,
            
            0, 0, -1,
            0, 1, -1,
            1, 1, -1,
            1, 0, -1,
            
            0, 1, 0,
            1, 1, 0,
            1, 1, -1,
            0, 1, -1,
            
            0, 0, 0,
            0, 0, -1,
            1, 0, -1,
            1, 0, 0,
            
            0, 1, -1,
            0, 0, -1,
            0, 0, 0,
            0, 1, 0,
            
            1, 0, -1,
            1, 1, -1,
            1, 1, 0,
            1, 0, 0
        };
    
    
    private float normals[] =
        {
            1, 1, 1,
            1, 1, 1,
            1, 1, 1,
            1, 1, 1,
            
            1, 1, 1,
            1, 1, 1,
            1, 1, 1,
            1, 1, 1,
            
            1, 1, 1,
            1, 1, 1,
            1, 1, 1,
            1, 1, 1,
            
            1, 1, 1,
            1, 1, 1,
            1, 1, 1,
            1, 1, 1,
            
            1, 1, 1,
            1, 1, 1,
            1, 1, 1,
            1, 1, 1
        };

    //There should be a matching entry in this array for each entry in
    //the positions array
    
    private float colors[] = 
        {
            1,0,0, 
            0,1,0,
            1,1,1,
            0,0,0,
            
            0,0,1, 
            1,1,0,
            1,0,0, 
            0,1,0,
            
            1,1,1,
            0,0,0,
            0,0,1, 
            1,1,0,
            
            1,0,0, 
            0,1,0,
            1,1,1,
            0,0,0,
            
            0,0,1, 
            1,1,0,
            1,0,0, 
            0,1,0,
            
            1,1,1,
            0,0,0,
            0,0,1, 
            1,1,0,
        }; 
    
    private float texCoords[] = {
            0, 0,
            1, 0,
            1, 1,
            0, 1,
            
            0, 0,
            1, 0,
            1, 1,
            0, 1,
            
            0, 0,
            1, 0,
            1, 1,
            0, 1,
            
            0, 0,
            1, 0,
            1, 1,
            0, 1,
    };


    //Best to use smallest data type possible for indexes 
    //We could even use byte here...
    private short indexes[] = {0,1,5,3,4,2};
    
    private FloatBuffer posData = Buffers.newDirectFloatBuffer(positions);
    private FloatBuffer colorData = Buffers.newDirectFloatBuffer(colors);
    private FloatBuffer normalData = Buffers.newDirectFloatBuffer(normals);
    private ShortBuffer indexData = Buffers.newDirectShortBuffer(indexes);
    private FloatBuffer texData = Buffers.newDirectFloatBuffer(texCoords);
    
    LevelTexture faceTexture;
    private int texUnitLoc;
    private int shaderprogram;
    
    public MyObject(GL2 gl) {
        faceTexture = new LevelTexture(gl, faceTex, faceTexExt, true);
        glu = new GLU();
        //Generate 2 VBO buffer and get their IDs
        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
        gl.glGenBuffers(1,bufferIds,0);
       
        //This buffer is now the current array buffer
        //array buffers hold vertex attribute data
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER,bufferIds[0]);
        
        //This is just setting aside enough empty space
        //for all our data
        gl.glBufferData(GL2.GL_ARRAY_BUFFER,    //Type of buffer  
                   (positions.length + texCoords.length) * FloatByteSize, //size needed
                   null,    //We are not actually loading data here yet
                   GL2.GL_STATIC_DRAW); //We expect once we load this data we will not modify it
        
        gl.glBufferSubData(GL.GL_ARRAY_BUFFER, 0, positions.length * FloatByteSize, posData);
        gl.glBufferSubData(GL.GL_ARRAY_BUFFER, positions.length * FloatByteSize, 
                texCoords.length * FloatByteSize, texData);
        
        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
        
        try {
            shaderprogram = Shader.initShaders(gl, VERTEX_SHADER, FRAGMENT_SHADER);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        texUnitLoc = gl.glGetUniformLocation(shaderprogram, "texUnit");

    }
    public void draw(GL2 gl) {
        gl.glPushMatrix();
            //gl.glScaled(0.5, 0.5, 0.5);
            //gl.glTranslated(-0.5, 0, 0.5);
            
            //Use the shader
            gl.glUseProgram(shaderprogram);
            
            gl.glUniform1i(texUnitLoc,0);
            gl.glBindTexture(GL2.GL_TEXTURE_2D, faceTexture.getTextureId());
            //Set wrap mode for texture in S direction
            //gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT); 
            //Set wrap mode for texture in T direction
            //gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP_TO_EDGE);
           
            gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
            gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, bufferIds[0]);
            
            gl.glVertexPointer(3, GL.GL_FLOAT, 0, 0);
            gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, positions.length * FloatByteSize);
            
            gl.glDrawArrays(GL2.GL_QUADS, 0, 4);
            
            
            gl.glUseProgram(0);
            gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
            gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
            //Un-bind the buffer. 
            //This is not needed in this simple example but good practice
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER,0);
        gl.glPopMatrix();
    }
    /*
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
                   (positions.length + colors.length + normals.length) * FloatByteSize, //size needed
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
        
        //Actually load the normal data
        gl.glBufferSubData(GL2.GL_ARRAY_BUFFER, (positions.length + colors.length) *FloatByteSize,  //Load after the position data
                normals.length*FloatByteSize,
                normalData);
        
        /*
         Uncomment if we want to use indexes
        //Now for the element array
        //Element arrays hold indexes to an array buffer
        gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, bufferIds[1]);

        //We can load it all at once this time since there are not
        //two separate parts like there was with color and position.
        gl.glBufferData(GL2.GL_ELEMENT_ARRAY_BUFFER,      
               indexes.length *Short.BYTES,
               indexData, GL2.GL_STATIC_DRAW);
        
        
        //Enable client state
        gl.glEnableClientState( GL2.GL_VERTEX_ARRAY);
        gl.glEnableClientState( GL2.GL_COLOR_ARRAY);
        gl.glEnableClientState( GL2.GL_NORMAL_ARRAY);
        
        try {
            shaderprogram = Shader.initShaders(gl, VERTEX_SHADER, FRAGMENT_SHADER);   
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

    }
    */
    /*
    public void draw(GL2 gl) {
        gl.glPushMatrix();
            gl.glScaled(0.5, 0.5, 0.5);
            gl.glTranslated(-0.5, 0, 0.5);
            
            //Use the shader
            gl.glUseProgram(shaderprogram);
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER,bufferIds[0]);
               
            int vertexColLoc = gl.glGetAttribLocation(shaderprogram,"vertexCol");
            int vertexPosLoc = gl.glGetAttribLocation(shaderprogram,"vertexPos");
            //int vertexNorLoc = gl.glGetAttribLocation(shaderprogram,"vertexNor");
                   
            // Specify locations for the co-ordinates and color arrays.
            gl.glEnableVertexAttribArray(vertexPosLoc);
            gl.glEnableVertexAttribArray(vertexColLoc);
            //gl.glEnableVertexAttribArray(vertexNorLoc);
            gl.glVertexAttribPointer(vertexPosLoc,3, GL.GL_FLOAT, false,0, 0); //last num is the offset
            gl.glVertexAttribPointer(vertexColLoc,3, GL.GL_FLOAT, false,0, positions.length*FloatByteSize);
            //gl.glVertexAttribPointer(vertexNorLoc,3, GL.GL_FLOAT, false,0, (positions.length + colors.length) * FloatByteSize);
            
            //Comment below out if we want to use indexes
            
            // This tells OpenGL the locations for the co-ordinates and color arrays.
            gl.glVertexPointer(3, //3 coordinates per vertex 
                               GL.GL_FLOAT, //each co-ordinate is a float 
                               0, //There are no gaps in data between co-ordinates 
                               0); //Co-ordinates are at the start of the current array buffer
            gl.glColorPointer(3, GL.GL_FLOAT, 0, 
                              positions.length*FloatByteSize); //colors are found after the position
                                                             //co-ordinates in the current array buffer
            
            //gl.glNormalPointer(GL.GL_FLOAT, 0, (positions.length + colors.length) * FloatByteSize);
            
            //Draw triangles, using 4 vertices, starting at vertex index 0 
            gl.glDrawArrays(GL2.GL_QUADS,0, positions.length / 3);
            
            
            //Comment above out of we want to use indexes
            
            /* Uncomment if we want to use indexes
            //gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, bufferIds[1]);
                 
            //gl.glDrawElements(GL2.GL_TRIANGLES, 6, GL2.GL_UNSIGNED_SHORT,0);    
            
            
            
            //Unbind shader
            gl.glUseProgram(0);
               
            //Un-bind the buffer. 
            //This is not needed in this simple example but good practice
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER,0);
            gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER,0);
        gl.glPopMatrix();
    }
    */
}
