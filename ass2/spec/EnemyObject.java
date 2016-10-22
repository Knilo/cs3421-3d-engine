package ass2.spec;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

public class EnemyObject {
    GLU glu;
    private static final int FloatByteSize = 4;//Float.SIZE / 8;
    int bufferIds[] = new int[2];
    private String headTex = "steve.png"; //default value
    private String headTexExt = "png"; //default value
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

    //There should be a matching entry in this array for each entry in
    //the positions array
    
    private float colors[] = 
        {
            //front
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
            //front
            0, 0,
            0.5f, 0,
            0.5f, 1,
            0, 1,
            
            //back
            1, 0,
            1, 1,
            0.45f, 1,
            0.45f, 0,
            
            
            
            
            //top
            0, 0,
            0.5f, 0,
            0.5f, 1,
            0, 1,
            
            //bottom
            0, 0,
            0.5f, 0,
            0.5f, 1,
            0, 1,
            
            //right
            0, 0,
            0.5f, 0,
            0.5f, 1,
            0, 1,
            
            //left
            0, 0,
            0.5f, 0,
            0.5f, 1,
            0, 1
    };
    
    private FloatBuffer posData = Buffers.newDirectFloatBuffer(positions);
    private FloatBuffer normalData = Buffers.newDirectFloatBuffer(normals);
    private FloatBuffer texData = Buffers.newDirectFloatBuffer(texCoords);

    LevelTexture faceTexture;
    private int texUnitLoc;
    private int shaderprogram;
    
    public EnemyObject(GL2 gl, String headTex, String headTexExt) {
        if (headTex != null && headTexExt != null) {
            this.headTex = headTex;
            this.headTexExt = headTexExt;
        }   
        
        faceTexture = new LevelTexture(gl, headTex, headTexExt, true);
        glu = new GLU();
        //Generate a VBO buffer and get their IDs
        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
        gl.glGenBuffers(1,bufferIds,0);
       
        //This buffer is now the current array buffer
        //array buffers hold vertex attribute data
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER,bufferIds[0]);
        
        //This is just setting aside enough empty space
        //for all our data
        gl.glBufferData(GL2.GL_ARRAY_BUFFER,    //Type of buffer  
                   (positions.length + texCoords.length + normals.length) * FloatByteSize, //size needed
                   null,    //We are not actually loading data here yet
                   GL2.GL_STATIC_DRAW); //We expect once we load this data we will not modify it
        
        gl.glBufferSubData(GL.GL_ARRAY_BUFFER, 
                            0, 
                            positions.length * FloatByteSize, 
                            posData); //buffer in vertex position data
        gl.glBufferSubData(GL.GL_ARRAY_BUFFER, 
                            positions.length * FloatByteSize,  //buffer in texture coordinate data
                            texCoords.length * FloatByteSize, 
                            texData);
        gl.glBufferSubData(GL.GL_ARRAY_BUFFER, 
                            (positions.length + texCoords.length) * FloatByteSize,  //buffer in normal vertex data
                            normals.length * FloatByteSize, 
                            normalData);
        
        
        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
        gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);
        
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
            gl.glScaled(0.5, 0.5, 0.5);
            gl.glTranslated(-0.5, 0, 0.5);
            
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
            gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER, bufferIds[0]);
            
            gl.glVertexPointer(3, GL.GL_FLOAT, 0, 0);
            gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, positions.length * FloatByteSize);
            gl.glNormalPointer(GL.GL_FLOAT, 0, (positions.length + texCoords.length) * FloatByteSize);
            
            gl.glDrawArrays(GL2.GL_QUADS, 0, positions.length / 3); //3 float numbers per vertex.
            
            gl.glUseProgram(0);
            gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
            gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
            gl.glDisableClientState(GL2.GL_NORMAL_ARRAY);
            
            //Un-bind the buffer. 
            //This is not needed in this simple example but good practice
            gl.glBindBuffer(GL.GL_ARRAY_BUFFER,0);
        gl.glPopMatrix();
    }

}
