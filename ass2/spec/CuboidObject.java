package ass2.spec;

import com.jogamp.opengl.GL2;

public class CuboidObject {
    public static void drawCuboid (GL2 gl, double height) {
        gl.glBegin(GL2.GL_QUADS);
        // front   
        
        gl.glNormal3d(0,0,1);
        
        gl.glTexCoord2d(0.0, 0.0);
        gl.glVertex3d(0, 0, 0); 
        gl.glTexCoord2d(1.0, 0.0);
        gl.glVertex3d(1, 0, 0); 
        gl.glTexCoord2d(1.0, 1.0);
        gl.glVertex3d(1, height, 0);
        gl.glTexCoord2d(0.0, 1.0);
        gl.glVertex3d(0, height, 0); 
        // back 
        
        gl.glColor3f(1, 1, 0); 
        gl.glNormal3d(0,0,-1);
        
        gl.glTexCoord2d(0.0, 0.0);
        gl.glVertex3d(0, 0, -1);
        gl.glTexCoord2d(1.0, 0.0);
        gl.glVertex3d(0, height, -1); 
        gl.glTexCoord2d(1.0, 1.0);
        gl.glVertex3d(1, height, -1);    
        gl.glTexCoord2d(0.0, 1.0);
        gl.glVertex3d(1, 0, -1); 
        
        
        // top
        gl.glColor3f(1, 0, 0);
        gl.glNormal3d(0,1,0);
        
        gl.glTexCoord2d(0.0, 0.0);
        gl.glVertex3d(0, height, 0); 
        gl.glTexCoord2d(1.0, 0.0);
        gl.glVertex3d(1, height, 0); 
        gl.glTexCoord2d(1.0, 1.0);
        gl.glVertex3d(1, height, -1);  
        gl.glTexCoord2d(0.0, 1.0);
        gl.glVertex3d(0, height, -1);  
        
        // bottom  
        gl.glColor3f(0, 1, 0); 
        gl.glNormal3d(0,-1,0);
        
        gl.glTexCoord2d(0.0, 0.0);
        gl.glVertex3d(0, 0, 0);
        gl.glTexCoord2d(1.0, 0.0);
        gl.glVertex3d(0, 0, -1); 
        gl.glTexCoord2d(1.0, 1.0);
        gl.glVertex3d(1, 0, -1);    
        gl.glTexCoord2d(0.0, 1.0);
        gl.glVertex3d(1, 0, 0); 
        
        //left
        gl.glColor3f(0, 1, 1); 
        gl.glNormal3d(-1,0,0);
        
        gl.glTexCoord2d(0.0, 0.0);
        gl.glVertex3d(0, height, -1);
        gl.glTexCoord2d(1.0, 0.0);
        gl.glVertex3d(0, 0, -1);
        gl.glTexCoord2d(1.0, 1.0);
        gl.glVertex3d(0, 0, 0);
        gl.glTexCoord2d(0.0, 1.0);
        gl.glVertex3d(0, height, 0);
        
        //right
        gl.glColor3f(0, 0, 1); 
        gl.glNormal3d(1,0,0);
        
        gl.glTexCoord2d(0.0, 0.0);
        gl.glVertex3d(1, 0, -1);
        gl.glTexCoord2d(1.0, 0.0);
        gl.glVertex3d(1, height, -1);
        gl.glTexCoord2d(1.0, 1.0);
        gl.glVertex3d(1, height, 0);
        gl.glTexCoord2d(0.0, 1.0);
        gl.glVertex3d(1, 0, 0);
        
        gl.glEnd();
    }
}
